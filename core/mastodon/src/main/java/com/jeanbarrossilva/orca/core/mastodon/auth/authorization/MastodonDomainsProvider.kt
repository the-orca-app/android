package com.jeanbarrossilva.orca.core.mastodon.auth.authorization

import com.jeanbarrossilva.orca.core.instance.domain.Domain
import com.jeanbarrossilva.orca.core.mastodon.BuildConfig
import com.jeanbarrossilva.orca.core.mastodon.auth.authorization.MastodonDomainsProvider.provide
import com.jeanbarrossilva.orca.core.mastodon.http.client.CoreHttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import kotlinx.serialization.Serializable

/** Provides [Domain]s through [provide]. */
internal object MastodonDomainsProvider {
  /** [CoreHttpClient] through which the [HttpRequest]s will be sent. */
  private val client = CoreHttpClient { defaultRequest { url("https://instances.social") } }

  /**
   * Structure returned by the API containing the [instances].
   *
   * @param instances [NamedInstance]s that have been found.
   */
  @Serializable private data class PaginatingInstanceList(val instances: List<NamedInstance>)

  /**
   * Structure returned by the API with a [String] version of the server [Domain].
   *
   * @param name [String] that represents the [Domain].
   */
  @Serializable
  private data class NamedInstance(val name: String) {
    /** Converts this [NamedInstance] into a [Domain]. */
    fun toDomain(): Domain {
      return Domain(name)
    }
  }

  /** Provides the available Mastodon server [Domain]s. */
  suspend fun provide(): List<Domain> {
    return client
      .request("/api/1.0/instances/list") {
        bearerAuth(BuildConfig.instancesSocialtoken)
        parameter("sort_by", "users")
      }
      .body<PaginatingInstanceList>()
      .instances
      .map(NamedInstance::toDomain)
  }

  /**
   * Provides the available Mastodon server [Domain]s matching the given [query].
   *
   * @param query Query to provide the [Domain]s with.
   */
  suspend fun provide(query: String): List<Domain> {
    return client
      .request("/api/1.0/instances/search") {
        bearerAuth(BuildConfig.instancesSocialtoken)
        parameter("name", true)
        parameter("q", query)
      }
      .body<PaginatingInstanceList>()
      .instances
      .map(NamedInstance::toDomain)
  }
}
