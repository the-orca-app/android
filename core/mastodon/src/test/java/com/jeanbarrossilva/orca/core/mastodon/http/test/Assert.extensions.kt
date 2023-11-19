package com.jeanbarrossilva.orca.core.mastodon.http.test

import assertk.Assert
import assertk.assertThat
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders

/**
 * Creates an [Assert] on the [response]'s authorization [HttpHeader].
 *
 * @param response [HttpResponse] whose header will be the basis of the [Assert].
 */
internal fun assertThatRequestAuthorizationHeaderOf(response: HttpResponse): Assert<String?> {
  return assertThat(response.request.headers[HttpHeaders.Authorization])
}
