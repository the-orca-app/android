package com.jeanbarrossilva.orca.core.mastodon.http.requester

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import assertk.assertions.prop
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.DelayedRequester
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequesterTestRule
import io.ktor.client.call.body
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Rule

internal class TestRequesterTests {
  @get:Rule val requesterRule = TestRequesterTestRule()

  @Test
  fun requestIsOngoing() {
    runTest {
      with(
        requesterRule
          .on(coroutineContext)
          .respond { respondOk("🥐") }
          .delayedBy(Duration.INFINITE)
          .requester
      ) {
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) { get<String>("api/v1") }
        assertThat(isRequestOngoing("api/v1")).isTrue()
      }
    }
  }

  @Test
  fun requestIsNotOngoing() {
    runTest {
      with(requesterRule.respond { respondOk("🍞") }.requester) {
        get<String>("api/v1")
        assertThat(isRequestOngoing("api/v1")).isFalse()
      }
    }
  }

  @Test
  fun createsDelayedRequester() {
    val initialRequester = requesterRule.requester
    assertThat(requesterRule.delayedBy(2.minutes).requester).all {
      given {
        isInstanceOf<DelayedRequester>().all {
          prop(DelayedRequester::delegate).isEqualTo(initialRequester)
          prop(DelayedRequester::delay).isEqualTo(2.minutes)
        }
      }
    }
  }

  @Test
  fun respondsWithChangedResponseWhenSendingGetRequest() {
    runTest {
      assertThat(
          requesterRule
            .respond { respondBadRequest() }
            .respond { respondOk("👍🏽") }
            .requester
            .get<String>("api/v1")
        )
        .isEqualTo("👍🏽")
    }
  }

  @Test
  fun respondsWithChangedResponseWhenSendingPostRequest() {
    runTest {
      val request =
        requesterRule
          .respond { respondBadRequest() }
          .respond { respondOk("🤙🏽") }
          .requester
          .post("api/v1")
      assertThat(request.status).isEqualTo(HttpStatusCode.OK)
      assertThat(request.body<String>()).isEqualTo("🤙🏽")
    }
  }
}
