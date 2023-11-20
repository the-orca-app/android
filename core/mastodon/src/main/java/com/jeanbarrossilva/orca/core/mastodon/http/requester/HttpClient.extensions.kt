package com.jeanbarrossilva.orca.core.mastodon.http.requester

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.content.PartData

/**
 * Sends a POST request either with or without the [form] depending on its emptiness.
 *
 * @param route [String] that's the path to which the request will be sent.
 * @param form Multipart form data.
 * @param build Configuration to be made to the request.
 */
internal suspend fun HttpClient.post(
  route: String,
  form: List<PartData>,
  build: suspend HttpRequestBuilder.() -> Unit = {}
): HttpResponse {
  return if (form.isEmpty()) {
    post {
      url(route)
      build(this)
    }
  } else {
    submitFormWithBinaryData(form) {
      url(route)
      build(this)
    }
  }
}
