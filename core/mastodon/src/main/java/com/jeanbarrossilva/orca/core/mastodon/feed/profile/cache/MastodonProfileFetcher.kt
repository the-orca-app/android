package com.jeanbarrossilva.orca.core.mastodon.feed.profile.cache

import com.jeanbarrossilva.orca.core.feed.profile.Profile
import com.jeanbarrossilva.orca.core.feed.profile.toot.Toot
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.MastodonProfile
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.MastodonProfileTootPaginator
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.account.MastodonAccount
import com.jeanbarrossilva.orca.core.mastodon.http.client.authenticateAndGet
import com.jeanbarrossilva.orca.core.mastodon.instance.SomeHttpInstance
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.core.module.instanceProvider
import com.jeanbarrossilva.orca.platform.cache.Fetcher
import com.jeanbarrossilva.orca.std.imageloader.ImageLoader
import com.jeanbarrossilva.orca.std.injector.Injector
import io.ktor.client.call.body
import java.net.URL

/**
 * [Fetcher] for [MastodonProfile]s.
 *
 * @param avatarLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which
 *   [Profile]s' avatars will be loaded from a [URL].
 * @param tootPaginatorProvider [MastodonProfileTootPaginator.Provider] by which a
 *   [MastodonProfileTootPaginator] for paginating through a [MastodonProfile]'s [Toot]s will be
 *   provided.
 */
internal class MastodonProfileFetcher(
  private val avatarLoaderProvider: ImageLoader.Provider<URL>,
  private val tootPaginatorProvider: MastodonProfileTootPaginator.Provider
) : Fetcher<Profile>() {
  override suspend fun onFetch(key: String): Profile {
    return (Injector.from<CoreModule>().instanceProvider().provide() as SomeHttpInstance)
      .client
      .authenticateAndGet("/api/v1/accounts/$key")
      .body<MastodonAccount>()
      .toProfile(avatarLoaderProvider, tootPaginatorProvider)
  }
}
