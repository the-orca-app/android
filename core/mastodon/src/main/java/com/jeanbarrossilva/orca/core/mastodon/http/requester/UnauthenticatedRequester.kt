package com.jeanbarrossilva.orca.core.mastodon.http.requester

import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.auth.SomeAuthenticationLock
import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database.RequestDatabase
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.Parameters
import io.ktor.http.content.PartData

/** [Requester] that sends HTTP requests as an [unauthenticated][Actor.Unauthenticated] [Actor]. */
class UnauthenticatedRequester
internal constructor(override val database: RequestDatabase, override val client: HttpClient) :
  Requester() {
  /**
   * [AuthenticatedRequester]s that have been created, associated to their
   * [lock][AuthenticatedRequester.lock].
   */
  private val authenticated = hashMapOf<SomeAuthenticationLock, AuthenticatedRequester>()

  override suspend fun onGet(
    route: String,
    parameters: Parameters,
    headers: Headers
  ): HttpResponse {
    return client.get(route) {
      parameters(parameters)
      this.headers.appendAll(headers)
    }
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
    }
  }

  /**
   * Creates or retrieves a version of this [Requester] that sends requests as an
   * [authenticated][Actor.Authenticated] [Actor].
   *
   * @param lock [AuthenticationLock] by which authentication will be required.
   * @see AuthenticatedRequester
   */
  fun authenticated(lock: SomeAuthenticationLock): Requester {
    return authenticated.getOrPut(lock) { AuthenticatedRequester(lock, database, client) }
  }

  /** Removes all [AuthenticatedRequester]s that have been created. */
  override fun clear() {
    super.clear()
    authenticated.values.forEach(AuthenticatedRequester::clear)
    authenticated.clear()
  }
}
