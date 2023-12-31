/*
 * Copyright © 2023 Orca
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see https://www.gnu.org/licenses.
 */

package com.jeanbarrossilva.orca.core.mastodon.feed.profile.post.pagination

import com.jeanbarrossilva.orca.core.feed.profile.post.Post
import com.jeanbarrossilva.orca.core.mastodon.client.CoreHttpClient
import com.jeanbarrossilva.orca.core.mastodon.client.authenticateAndGet
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.post.status.MastodonStatus
import com.jeanbarrossilva.orca.core.mastodon.instance.SomeHttpInstance
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.core.module.instanceProvider
import com.jeanbarrossilva.orca.ext.coroutines.mapEach
import com.jeanbarrossilva.orca.std.image.ImageLoader
import com.jeanbarrossilva.orca.std.image.SomeImageLoaderProvider
import com.jeanbarrossilva.orca.std.injector.Injector
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse
import java.net.URL
import kotlin.jvm.optionals.getOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

/** Requests and paginates through [Post]s. */
internal abstract class MastodonPostPaginator {
  /** Last [HttpResponse] that's been received. */
  private var lastResponse: HttpResponse? = null

  /** [MutableStateFlow] with the index of the page that's the current one. */
  private var pageFlow = MutableStateFlow(0)

  /**
   * [Flow] to which the [Post]s within the current page are emitted.
   *
   * @see page
   */
  private val postFlow =
    pageFlow
      .compareNotNull { previous, current -> previous.getOrNull()?.compareTo(current) ?: 0 }
      .map { it == 0 }
      .associateWith { lastResponse?.headers?.links?.firstOrNull()?.uri }
      .map({ (url, isRefreshing) -> isRefreshing || url == null }) { route to it.second }
      .mapNotNull { (url, _) -> url?.let { client.authenticateAndGet(it) } }
      .onEach { lastResponse = it }
      .map { it.body<List<MastodonStatus>>() }
      .mapEach { it.toPost(imageLoaderProvider) }

  /** [CoreHttpClient] through which the [HttpRequest]s will be performed. */
  private val client
    get() = (Injector.from<CoreModule>().instanceProvider().provide() as SomeHttpInstance).client

  /**
   * Index of the page that's the current one.
   *
   * @see pageFlow
   */
  private var page
    get() = pageFlow.value
    private set(page) {
      pageFlow.value = page
    }

  /**
   * [ImageLoader.Provider] that provides the [ImageLoader] by which images will be loaded from a
   * [URL].
   */
  protected abstract val imageLoaderProvider: SomeImageLoaderProvider<URL>

  /** URL [String] to which the initial [HttpRequest] should be sent. */
  protected abstract val route: String

  /**
   * Iterates from the current page to the given one.
   *
   * @param page Page until which pagination should be performed.
   * @return [Flow] that receives the [Post]s of all the pages through which we've been through in
   *   the pagination process.
   * @throws IllegalArgumentException If the given page is before the current one.
   */
  @Throws(IllegalArgumentException::class)
  fun paginateTo(page: Int): Flow<List<Post>> {
    iterate(page)
    return postFlow
  }

  /**
   * Goes through each page between the current and the given one.
   *
   * @param page Destination page.
   * @throws IllegalArgumentException If the given [page] is before the current one.
   */
  @Throws(IllegalArgumentException::class)
  private fun iterate(page: Int) {
    if (page < this.page) {
      throw IllegalArgumentException(
        "Cannot iterate backwards (current page is ${this.page} and the given one is $page)."
      )
    }
    while (page > this.page) {
      this.page++
    }
  }
}
