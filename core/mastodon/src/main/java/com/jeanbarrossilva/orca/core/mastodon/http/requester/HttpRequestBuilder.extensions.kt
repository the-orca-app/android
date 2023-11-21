package com.jeanbarrossilva.orca.core.mastodon.http.requester

import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.util.flattenEntries

/**
 * Sets [parameters] as the [Parameters] of the final [Url] of the [HttpRequest] to be built.
 *
 * @param parameters [Parameters] to be set as the [Url]'s.
 */
internal fun HttpRequestBuilder.parameters(parameters: Parameters) {
  parameters.flattenEntries().forEach { (key, value) -> parameter(key, value) }
}
