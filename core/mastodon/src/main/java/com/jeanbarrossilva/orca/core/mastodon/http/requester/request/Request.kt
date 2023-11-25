package com.jeanbarrossilva.orca.core.mastodon.http.requester.request

import com.jeanbarrossilva.orca.core.mastodon.http.requester.Requester
import com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database.RequestEntity
import io.ktor.http.Headers
import io.ktor.http.Parameters

/** Anatomy of an HTTP request made by the [Requester]. */
sealed class Request {
  /** Name of the method to which this [Request] refers to. */
  internal abstract val methodName: String

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
    override val methodName = METHOD_NAME

    override suspend fun sendTo(requester: Requester) {
      requester.get(route, parameters, headers)
    }

    companion object {
      /** Name of the HTTP method of a [Get]. */
      const val METHOD_NAME = "GET"
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
    override val headers: Headers
  ) : Request() {
    override val methodName = METHOD_NAME

    override suspend fun sendTo(requester: Requester) {
      requester.post(route, parameters, headers)
    }

    companion object {
      /** Name of the HTTP method of a [Post]. */
      const val METHOD_NAME = "POST"
    }
  }

  /**
   * Sends this [Request] to the [requester].
   *
   * @param requester [Requester] to which this [Request] will be sent.
   */
  internal abstract suspend fun sendTo(requester: Requester)

  /** Converts this [Request] into a [RequestEntity]. */
  internal fun toRequestEntity(): RequestEntity {
    return RequestEntity(methodName, route, "$parameters", "$headers")
  }
}
