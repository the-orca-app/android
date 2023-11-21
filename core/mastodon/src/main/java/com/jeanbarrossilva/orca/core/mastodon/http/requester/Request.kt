package com.jeanbarrossilva.orca.core.mastodon.http.requester

import io.ktor.http.Headers
import io.ktor.http.Parameters
import io.ktor.http.content.PartData

/** Anatomy of an HTTP request made by the [Requester]. */
sealed class Request {
  /** Path to which this [Request] was made. */
  internal abstract val route: String

  /** [Parameters] that have been added to the final URL. */
  internal abstract val parameters: Parameters

  /** [Headers] with which this [Request] has been sent. */
  internal abstract val headers: Headers

  /** GET HTTP request. */
  internal data class Get(
    override val route: String,
    override val parameters: Parameters,
    override val headers: Headers
  ) : Request() {
    override suspend fun sendTo(requester: Requester) {
      requester.get(route, parameters, headers)
    }
  }

  /**
   * POST HTTP request.
   *
   * @param form Multipart form data.
   */
  internal data class Post(
    override val route: String,
    override val parameters: Parameters,
    override val headers: Headers,
    val form: List<PartData>
  ) : Request() {
    override suspend fun sendTo(requester: Requester) {
      requester.post(route, parameters, headers, form)
    }
  }

  /**
   * Sends this [Request] to the [requester].
   *
   * @param requester [Requester] to which this [Request] will be sent.
   */
  internal abstract suspend fun sendTo(requester: Requester)
}
