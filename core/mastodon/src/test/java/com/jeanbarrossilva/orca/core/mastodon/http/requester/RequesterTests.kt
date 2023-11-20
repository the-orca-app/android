package com.jeanbarrossilva.orca.core.mastodon.http.requester

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequesterTestRule
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.time.Duration
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Rule

internal class RequesterTests {
  @get:Rule val requesterRule = TestRequesterTestRule()

  @Test
  fun gets() {
    runTest {
      assertThat(requesterRule.respond { respondOk("✨") }.requester.get<String>("api/v1"))
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
    runTest {
      with(requesterRule.delayedBy(Duration.INFINITE).requester) {
        coroutineScope.launch { get<String>("api/v1") }.cancelAndJoin()
        resume()
        assertThat(coroutineScope.isActive).isTrue()
      }
    }
  }
}
