package com.jeanbarrossilva.orca.core.mastodon.http.requester

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotSameAs
import assertk.assertions.isSameAs
import assertk.assertions.isTrue
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequesterTestRule
import io.ktor.client.call.body
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.time.Duration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule

internal class RequesterTests {
  @get:Rule val requesterRule = TestRequesterTestRule()

  @Test
  fun createsRequesterOnce() {
    val client = requesterRule.requester.client
    val requester = Requester.through(client)
    assertThat(Requester.through(client)).isSameAs(requester)
  }

  @Test
  fun clearsRequesters() {
    val client = requesterRule.requester.client
    val requester = Requester.through(client)
    Requester.clear()
    assertThat(Requester.through(client)).isNotSameAs(requester)
  }

  @Test
  fun gets() {
    runTest {
      assertThat(requesterRule.respond { respondOk("✨") }.requester.get("api/v1").body<String>())
        .isEqualTo("✨")
    }
  }

  @Test
  fun posts() {
    runTest {
      assertThat(requesterRule.respond { respondOk() }.requester.post("api/v1").status)
        .isEqualTo(HttpStatusCode.OK)
    }
  }

  @Test
  fun resumes() {
    runTest(@OptIn(ExperimentalCoroutinesApi::class) UnconfinedTestDispatcher()) {
      with(requesterRule.on(coroutineContext).delayedBy(Duration.INFINITE).requester) {
        launch { get("api/v1") }.cancelAndJoin()
        resume()
        assertThat(isRequestOngoing("api/v1")).isTrue()
      }
    }
  }

  @Test
  fun doesNotResumeWhenRequestHasBeenCancelled() {
    runTest(@OptIn(ExperimentalCoroutinesApi::class) UnconfinedTestDispatcher()) {
      with(requesterRule.on(coroutineContext).delayedBy(Duration.INFINITE).requester) {
        launch { get("api/v1") }
        cancel("api/v1")
        resume()
        assertThat(isRequestOngoing("api/v1")).isFalse()
      }
    }
  }

  @Test
  fun cancels() {
    runTest(@OptIn(ExperimentalCoroutinesApi::class) UnconfinedTestDispatcher()) {
      with(requesterRule.on(coroutineContext).delayedBy(Duration.INFINITE).requester) {
        launch { get("api/v1") }
        launch { post("api/v2") }
        cancel("api/v2")
        assertThat(isRequestOngoing("api/v2")).isFalse()
      }
    }
  }
}
