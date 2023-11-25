package com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database

import androidx.annotation.StringDef
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jeanbarrossilva.orca.core.mastodon.http.requester.request.Request
import com.jeanbarrossilva.orca.core.mastodon.http.requester.request.valueOf
import io.ktor.http.Headers
import io.ktor.http.Parameters

/**
 * Primitive information to be persisted about a [Request].
 *
 * @param methodName Name of the HTTP method to which the [Request] refers.
 * @param route Path to which the [Request] is sent.
 * @param parameters [String] version of the [Parameters] that are appended to the final URL.
 * @param headers [String] version of the [Headers] with which the [Request] is sent.
 */
@Entity(tableName = "requests")
internal data class RequestEntity(
  @MethodName val methodName: String,
  @PrimaryKey val route: String,
  val parameters: String,
  val headers: String
) {
  /** Constrains the [methodName] to those of a [Request]. */
  @StringDef(Request.Get.METHOD_NAME, Request.Post.METHOD_NAME)
  annotation class MethodName {
    /**
     * [IllegalArgumentException] thrown if a [RequestEntity]'s [methodName] isn't a known one.
     *
     * @param methodName Name of the method that's unknown.
     */
    class UnknownException(methodName: String) : IllegalArgumentException(methodName)

    companion object {
      /**
       * Requires the [methodName] to be known, throwing an [UnknownException] if it isn't.
       *
       * @param methodName Name of the HTTP method to validate.
       * @throws UnknownException If the [methodName] is unknown.
       */
      fun requireToBeKnown(methodName: String) {
        fold(methodName, onGet = {}, onPost = {})
      }

      /**
       * Returns the result of one of the lambdas depending on the given [methodName] or throws.
       *
       * @param methodName Name of the HTTP method to validate.
       * @param onGet Run if the [methodName] is that of a [Request.Get].
       * @param onPost Run if the [methodName] is that of a [Request.Post].
       * @throws UnknownException If the [methodName] is unknown.
       */
      @Throws(UnknownException::class)
      fun <T> fold(methodName: String, onGet: () -> T, onPost: () -> T): T {
        return when (methodName) {
          Request.Get.METHOD_NAME -> onGet()
          Request.Post.METHOD_NAME -> onPost()
          else -> throw UnknownException(methodName)
        }
      }
    }
  }

  init {
    MethodName.requireToBeKnown(methodName)
  }

  /** Converts this [RequestEntity] into a [Request]. */
  fun toRequest(): Request {
    val parameters = Parameters.valueOf(parameters)
    val headers = Headers.valueOf(headers)
    return when (methodName) {
      Request.Get.METHOD_NAME -> Request.Get(route, parameters, headers)
      Request.Post.METHOD_NAME -> Request.Post(route, parameters, headers)
      else -> error("Unknown method name: \"$methodName\".")
    }
  }
}
