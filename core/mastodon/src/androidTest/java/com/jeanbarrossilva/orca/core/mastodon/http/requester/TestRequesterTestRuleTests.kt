package com.jeanbarrossilva.orca.core.mastodon.http.requester

import assertk.assertThat
import assertk.assertions.isNotInstanceOf
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.DelayedRequester
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequesterTestRule
import kotlin.time.Duration
import org.junit.Test

internal class TestRequesterTestRuleTests {
  @Test
  fun resetsRequesterAfterTest() {
    assertThat(
        TestRequesterTestRule()
          .delayedBy(Duration.INFINITE)
          .apply(TestRequesterTestRule::after)
          .requester
      )
      .isNotInstanceOf<DelayedRequester>()
  }
}
