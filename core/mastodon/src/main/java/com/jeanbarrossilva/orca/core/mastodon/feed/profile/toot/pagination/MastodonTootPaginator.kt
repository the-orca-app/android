package com.jeanbarrossilva.orca.core.mastodon.feed.profile.toot.pagination

import com.jeanbarrossilva.orca.core.feed.profile.toot.Toot
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.toot.status.MastodonStatus
import com.jeanbarrossilva.orca.core.mastodon.http.CoreHttpClient
import com.jeanbarrossilva.orca.core.mastodon.http.authenticateAndGet
import com.jeanbarrossilva.orca.core.mastodon.instance.SomeHttpInstance
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.core.module.instanceProvider
import com.jeanbarrossilva.orca.platform.ui.core.mapEach
import com.jeanbarrossilva.orca.std.imageloader.Image
import com.jeanbarrossilva.orca.std.imageloader.ImageLoader
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

/** Requests and paginates through [Toot]s. */
internal abstract class MastodonTootPaginator {
  /** Last [HttpResponse] that's been received. */
  private var lastResponse: HttpResponse? = null

  /** [MutableStateFlow] with the index of the page that's the current one. */
  private var pageFlow = MutableStateFlow(0)

  /**
   * [Flow] to which the [Toot]s within the current page are emitted.
   *
   * @see page
   */
  private val tootsFlow =
    pageFlow
      .compareNotNull { previous, current -> previous.getOrNull()?.compareTo(current) ?: 0 }
      .map { it == 0 }
      .associateWith { lastResponse?.headers?.links?.firstOrNull()?.uri }
      .map({ (url, isRefreshing) -> isRefreshing || url == null }) { route to it.second }
      .mapNotNull { (url, _) -> url?.let { client.authenticateAndGet(it) } }
      .onEach { lastResponse = it }
      .map { it.body<List<MastodonStatus>>() }
      .mapEach { it.toToot(imageLoaderProvider) }

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
   * [ImageLoader.Provider] that provides the [ImageLoader] by which [Image]s will be loaded from a
   * [URL].
   */
  protected abstract val imageLoaderProvider: ImageLoader.Provider<URL>

  /** URL [String] to which the initial [HttpRequest] should be sent. */
  protected abstract val route: String

  /**
   * Iterates from the current page to the given one.
   *
   * @param page Page until which pagination should be performed.
   * @return [Flow] that receives the [Toot]s of all the pages through which we've been through in
   *   the pagination process.
   * @throws IllegalArgumentException If the given page is before the current one.
   */
  @Throws(IllegalArgumentException::class)
  fun paginateTo(page: Int): Flow<List<Toot>> {
    iterate(page)
    return tootsFlow
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
