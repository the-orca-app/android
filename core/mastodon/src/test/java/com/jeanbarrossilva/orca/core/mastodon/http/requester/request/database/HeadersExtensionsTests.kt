package com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlin.test.Test

internal class HeadersExtensionsTests {
  @Test
  fun createsHeadersFromString() {
    assertThat(
        Headers.valueOf(
          "Headers [${HttpHeaders.Authorization}=[Bearer 1234], " +
            "${HttpHeaders.SecWebSocketExtensions}=[client_max_window_bits; " +
            "server_no_context_takeover]]"
        )
      )
      .isEqualTo(
        Headers.build {
          append(HttpHeaders.Authorization, "Bearer 1234")
          appendAll(
            HttpHeaders.SecWebSocketExtensions,
            listOf("client_max_window_bits", "server_no_context_takeover")
          )
        }
      )
  }
}
