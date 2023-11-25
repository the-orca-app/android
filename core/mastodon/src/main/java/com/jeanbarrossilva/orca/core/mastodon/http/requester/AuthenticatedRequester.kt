package com.jeanbarrossilva.orca.core.mastodon.http.requester

import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.auth.SomeAuthenticationLock
import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database.RequestDatabase
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.Parameters
import io.ktor.http.content.PartData

/**
 * [Requester] that sends HTTP requests as an [authenticated][Actor.Authenticated] [Actor].
 *
 * @param lock [AuthenticationLock] by which authentication will be required.
 */
internal class AuthenticatedRequester(
  private val lock: SomeAuthenticationLock,
  override val database: RequestDatabase,
  override val client: HttpClient
) : Requester() {
  override suspend fun onGet(
    route: String,
    parameters: Parameters,
    headers: Headers
  ): HttpResponse {
    return client.get(route) { authenticate() }
  }

  override suspend fun onPost(
    route: String,
    parameters: Parameters,
    headers: Headers,
    form: List<PartData>
  ): HttpResponse {
    return client.post(route, form) {
      parameters(parameters)
      this.headers.appendAll(headers)
      authenticate()
    }
  }

  /**
   * Provides the [authenticated][Actor.Authenticated] [Actor]'s access token to the
   * [Authorization][HttpHeaders.Authorization] header through the [lock].
   */
  private suspend fun HttpMessageBuilder.authenticate() {
    lock.scheduleUnlock { bearerAuth(it.accessToken) }
  }
}
