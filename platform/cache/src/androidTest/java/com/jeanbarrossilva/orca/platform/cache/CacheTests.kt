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

package com.jeanbarrossilva.orca.platform.cache

import com.jeanbarrossilva.orca.platform.cache.test.CacheTestRule
import com.jeanbarrossilva.orca.platform.cache.test.TestFetcher
import com.jeanbarrossilva.orca.platform.cache.test.TestStorage
import io.mockk.coVerify
import io.mockk.spyk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule

internal class CacheTests {
  @OptIn(ExperimentalCoroutinesApi::class)
  private val coroutineScope = TestScope(UnconfinedTestDispatcher())

  @get:Rule val cacheRule = CacheTestRule(coroutineScope)

  @Test
  fun fetchesWhenValueIsObtainedForTheFirstTime() {
    val fetcher = spyk(TestFetcher())
    coroutineScope.runTest {
      cacheRule.cache.fetchingWith(fetcher).get("0")
      coVerify { fetcher.fetch("0") }
    }
  }

  @Test
  fun remembersValueWhenItIsObtainedForTheFirstTime() {
    val storage = TestStorage()
    coroutineScope.runTest {
      val value = cacheRule.cache.storingTo(storage).get("0")
      assertEquals(value, storage.get("0"))
    }
  }

  @Test
  fun obtainsRememberedValueWhenItIsReadBeforeTimeToIdle() {
    val fetcher = spyk(TestFetcher())
    val storage = spyk(TestStorage())
    val cache =
      cacheRule.cache.storingTo(storage).fetchingWith(fetcher).idlingFor(1.days).livingFor(1.days)
    coroutineScope.runTest {
      cache.get("0")

      @OptIn(ExperimentalCoroutinesApi::class) advanceTimeBy(23.hours)

      cache.get("0")
      coVerify { fetcher.fetch("0") }
      coVerify { storage.get("0") }
    }
  }

  @Test
  fun remembersValueAgainWhenItIsObtainedAfterTimeToIdle() {
    val fetcher = spyk(TestFetcher())
    val storage = spyk(TestStorage())
    val cache = cacheRule.cache.storingTo(storage).fetchingWith(fetcher).idlingFor(1.days)
    coroutineScope.runTest {
      cache.get("0")

      @OptIn(ExperimentalCoroutinesApi::class) advanceTimeBy(25.hours)

      cache.get("0")
      coVerify(exactly = 2) { fetcher.fetch("0") }
      coVerify(exactly = 2) { storage.store("0", TestFetcher.FETCHED.first()) }
    }
  }

  @Test
  fun obtainsRememberedValueWhenItIsReadBeforeTimeToLive() {
    val fetcher = spyk(TestFetcher())
    val storage = spyk(TestStorage())
    val cache = cacheRule.cache.storingTo(storage).fetchingWith(fetcher).livingFor(1.days)
    coroutineScope.runTest {
      cache.get("0")

      @OptIn(ExperimentalCoroutinesApi::class) advanceTimeBy(23.hours)

      cache.get("0")
      coVerify { fetcher.fetch("0") }
      coVerify { storage.get("0") }
    }
  }

  @Test
  fun remembersValueAgainWhenItIsObtainedAfterTimeToLive() {
    val fetcher = spyk(TestFetcher())
    val storage = spyk(TestStorage())
    val cache = cacheRule.cache.storingTo(storage).fetchingWith(fetcher).livingFor(1.days)
    coroutineScope.runTest {
      cache.get("0")

      @OptIn(ExperimentalCoroutinesApi::class) advanceTimeBy(25.hours)

      cache.get("0")
      coVerify(exactly = 2) { fetcher.fetch("0") }
      coVerify(exactly = 2) { storage.store("0", TestFetcher.FETCHED.first()) }
    }
  }
}
