package com.jeanbarrossilva.orca.core.mastodon.http.requester

import io.ktor.http.content.PartData
import kotlin.reflect.KClass

/** Anatomy of an HTTP request made by the [Requester]. */
internal sealed interface Request {
  /** Path to which this [Request] was made. */
  val route: String

  /**
   * GET HTTP request.
   *
   * @param T Resource to be obtained.
   * @param resourceClass [KClass] of the resource to be obtained.
   */
  data class Get<T>(override val route: String, private val resourceClass: KClass<T & Any>) :
    Request {
    override suspend fun sendTo(requester: Requester) {
      requester.get(route, resourceClass)
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
