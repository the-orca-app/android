package com.jeanbarrossilva.orca.core.mastodon.http.requester

import io.ktor.http.content.PartData

/** Anatomy of an HTTP request made by the [Requester]. */
internal sealed interface Request {
  /** Path to which this [Request] was made. */
  val route: String

  /** GET HTTP request. */
  data class Get(override val route: String) : Request {
    override suspend fun sendTo(requester: Requester) {
      requester.get(route)
    }
  }

  /**
   * POST HTTP request.
   *
   * @param form Multipart form data.
   */
  data class Post(override val route: String, val form: List<PartData>) : Request {
    override suspend fun sendTo(requester: Requester) {
      requester.post(route, form)
    }
  }

  /**
   * Sends this [Request] to the [requester].
   *
   * @param requester [Requester] to which this [Request] will be sent.
   */
  suspend fun sendTo(requester: Requester)
}
