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

package com.jeanbarrossilva.orca.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jeanbarrossilva.loadable.flow.loadableFlow
import com.jeanbarrossilva.loadable.list.toSerializableList
import com.jeanbarrossilva.orca.core.feed.profile.search.ProfileSearchResult
import com.jeanbarrossilva.orca.core.feed.profile.search.ProfileSearcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

internal class SearchViewModel private constructor(private val searcher: ProfileSearcher) :
  ViewModel() {
  private val queryMutableFlow = MutableStateFlow("")

  val queryFlow = queryMutableFlow.asStateFlow()

  @OptIn(ExperimentalCoroutinesApi::class)
  val resultsFlow =
    loadableFlow(viewModelScope) {
      queryFlow
        .flatMapLatest(searcher::search)
        .map(List<ProfileSearchResult>::toSerializableList)
        .collect(::load)
    }

  fun setQuery(query: String) {
    queryMutableFlow.value = query
  }

  companion object {
    fun createFactory(searcher: ProfileSearcher): ViewModelProvider.Factory {
      return viewModelFactory { initializer { SearchViewModel(searcher) } }
    }
  }
}
