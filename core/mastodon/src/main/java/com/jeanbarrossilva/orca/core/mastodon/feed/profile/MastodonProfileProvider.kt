package com.jeanbarrossilva.orca.core.mastodon.feed.profile

import com.jeanbarrossilva.orca.core.feed.profile.Profile
import com.jeanbarrossilva.orca.core.feed.profile.ProfileProvider
import com.jeanbarrossilva.orca.platform.cache.Cache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * [ProfileProvider] that either requests [MastodonProfile]s to the API or retrieves cached ones if
 * they're available.
 *
 * @param cache [Cache] of [MastodonProfile] by which [MastodonProfile]s will be obtained.
 */
class MastodonProfileProvider internal constructor(private val cache: Cache<Profile>) :
  ProfileProvider() {
  override suspend fun contains(id: String): Boolean {
    return true
  }

  override suspend fun onProvide(id: String): Flow<Profile> {
    val profile = cache.get(id)
    return flowOf(profile)
  }
}