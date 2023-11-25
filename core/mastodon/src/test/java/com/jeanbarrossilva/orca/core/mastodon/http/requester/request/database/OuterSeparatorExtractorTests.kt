package com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database

import assertk.assertThat
import assertk.assertions.containsExactly
import kotlin.test.Test

internal class OuterSeparatorExtractorTests {
  @Test
  fun extracts() {
    assertThat(
        OuterSeparatorExtractor.extract(
          "[[0, 1], [2, 3], [4, 5]]",
          prefix = "[",
          suffix = "]",
          separator = ", "
        )
      )
      .containsExactly(7, 15)
  }
}
