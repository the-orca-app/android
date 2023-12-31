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

package com.jeanbarrossilva.orca.feature.postdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jeanbarrossilva.loadable.flow.loadable
import com.jeanbarrossilva.loadable.list.flow.listLoadable
import com.jeanbarrossilva.orca.core.feed.profile.post.PostProvider
import com.jeanbarrossilva.orca.ext.coroutines.notifier.notifierFlow
import com.jeanbarrossilva.orca.ext.coroutines.notifier.notify
import com.jeanbarrossilva.orca.feature.postdetails.toPostDetailsFlow
import com.jeanbarrossilva.orca.platform.autos.theme.AutosTheme
import com.jeanbarrossilva.orca.platform.ui.component.timeline.post.figure.gallery.disposition.Disposition
import com.jeanbarrossilva.orca.platform.ui.component.timeline.post.toPostPreviewFlow
import com.jeanbarrossilva.orca.platform.ui.core.await
import com.jeanbarrossilva.orca.platform.ui.core.context.ContextProvider
import com.jeanbarrossilva.orca.platform.ui.core.context.share
import com.jeanbarrossilva.orca.platform.ui.core.flatMapEach
import java.net.URL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

internal class PostDetailsViewModel
private constructor(
  private val contextProvider: ContextProvider,
  private val postProvider: PostProvider,
  private val id: String,
  private val onLinkClick: (URL) -> Unit,
  private val onThumbnailClickListener: Disposition.OnThumbnailClickListener
) : ViewModel() {
  private val notifierFlow = notifierFlow()

  @OptIn(ExperimentalCoroutinesApi::class)
  private val postFlow = notifierFlow.flatMapLatest { postProvider.provide(id) }

  private val commentsIndexFlow = MutableStateFlow(0)

  private val colors
    get() = AutosTheme.getColors(context)

  private val context
    get() = contextProvider.provide()

  @OptIn(ExperimentalCoroutinesApi::class)
  val detailsLoadableFlow =
    postFlow
      .flatMapLatest { it.toPostDetailsFlow(colors, onLinkClick, onThumbnailClickListener) }
      .loadable(viewModelScope)

  val commentsLoadableFlow =
    flatMapCombine(commentsIndexFlow, postFlow) { commentsIndex, post ->
        post.comment.get(commentsIndex).flatMapEach {
          it.toPostPreviewFlow(colors, onLinkClick, onThumbnailClickListener)
        }
      }
      .listLoadable(viewModelScope, SharingStarted.WhileSubscribed())

  fun requestRefresh(onRefresh: () -> Unit) {
    viewModelScope.launch {
      notifierFlow.notify()
      postFlow.await()
      onRefresh()
    }
  }

  fun favorite(id: String) {
    viewModelScope.launch { postProvider.provide(id).first().favorite.toggle() }
  }

  fun repost(id: String) {
    viewModelScope.launch { postProvider.provide(id).first().repost.toggle() }
  }

  fun share(url: URL) {
    context.share("$url")
  }

  fun loadCommentsAt(index: Int) {
    commentsIndexFlow.value = index
  }

  companion object {
    fun createFactory(
      contextProvider: ContextProvider,
      postProvider: PostProvider,
      id: String,
      onLinkClick: (URL) -> Unit,
      onThumbnailClickListener: Disposition.OnThumbnailClickListener
    ): ViewModelProvider.Factory {
      return viewModelFactory {
        addInitializer(PostDetailsViewModel::class) {
          PostDetailsViewModel(
            contextProvider,
            postProvider,
            id,
            onLinkClick,
            onThumbnailClickListener
          )
        }
      }
    }
  }
}
