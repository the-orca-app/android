package com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.http.Parameters
import kotlin.test.Test

internal class ParametersExtensionsTests {
  @Test
  fun createsParametersFromString() {
    assertThat(Parameters.valueOf("Parameters [id=[123], name=[John Doe], age=[30]]"))
      .isEqualTo(
        Parameters.build {
          append("id", "123")
          append("name", "John Doe")
          append("age", "30")
        }
      )
  }
}
