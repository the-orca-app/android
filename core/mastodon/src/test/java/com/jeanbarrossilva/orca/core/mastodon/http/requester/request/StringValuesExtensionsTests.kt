package com.jeanbarrossilva.orca.core.mastodon.http.requester.request

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.util.StringValues
import kotlin.test.Test

internal class StringValuesExtensionsTests {
  @Test
  fun createsStringValuesFromString() {
    assertThat(
        StringValues.valueOf("StringValues(case=true) [id=[123], name=[John Doe], age=[30]]")
      )
      .isEqualTo(
        StringValues.build {
          append("id", "123")
          append("name", "John Doe")
          append("age", "30")
        }
      )
  }
}
