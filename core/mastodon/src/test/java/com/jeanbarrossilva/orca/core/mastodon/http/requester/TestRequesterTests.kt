package com.jeanbarrossilva.orca.core.mastodon.http.requester

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.DelayedRequester
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequester
import io.ktor.client.call.body
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.test.runTest

internal class TestRequesterTests {
  @Test
  fun createsDelayedRequester() {
    val requester = TestRequester()
    assertThat(requester.delayedBy(2.minutes)).all {
      prop(DelayedRequester::delegate).isEqualTo(requester)
      prop(DelayedRequester::delay).isEqualTo(2.minutes)
    }
  }

  @Test
  fun respondsWithChangedResponseWhenSendingGetRequest() {
    val requester =
      TestRequester().apply {
        respond { respondBadRequest() }
        respond { respondOk("👍🏽") }
      }
    runTest { assertThat(requester.get<String>("api/v1")).isEqualTo("👍🏽") }
  }

  @Test
  fun respondsWithChangedResponseWhenSendingPostRequest() {
    val requester =
      TestRequester().apply {
        respond { respondBadRequest() }
        respond { respondOk("🤙🏽") }
      }
    runTest {
      val request = requester.post("api/v1")
      assertThat(request.status).isEqualTo(HttpStatusCode.OK)
      assertThat(request.body<String>()).isEqualTo("🤙🏽")
    }
  }
}
