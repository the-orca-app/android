package com.jeanbarrossilva.orca.core.mastodon.auth.authentication

import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.mastodon.instance.SomeHttpInstance
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.core.module.instanceProvider
import com.jeanbarrossilva.orca.std.injector.Injector
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

/**
 * Structure returned by the Mastodon API that holds the access token that's been given when
 * authorization was successfully granted to the user.
 *
 * @param accessToken Token that gives Orca user-level access to the API resources.
 */
@Serializable
internal data class MastodonAuthenticationToken(val accessToken: String) {
  /**
   * Converts this [MastodonAuthenticationToken] into an [authenticated][Actor.Authenticated]
   * [Actor].
   */
  suspend fun toActor(): Actor.Authenticated {
    val id =
      (Injector.from<CoreModule>().instanceProvider().provide() as SomeHttpInstance)
        .client
        .get("/api/v1/accounts/verify_credentials") { bearerAuth(accessToken) }
        .body<MastodonAuthenticationVerification>()
        .id
    return Actor.Authenticated(id, accessToken)
  }
}