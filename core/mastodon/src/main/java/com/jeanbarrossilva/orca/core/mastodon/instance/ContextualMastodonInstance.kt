package com.jeanbarrossilva.orca.core.mastodon.instance

import android.content.Context
import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.auth.actor.ActorProvider
import com.jeanbarrossilva.orca.core.feed.profile.search.ProfileSearchResult
import com.jeanbarrossilva.orca.core.feed.profile.toot.Toot
import com.jeanbarrossilva.orca.core.feed.profile.toot.content.TermMuter
import com.jeanbarrossilva.orca.core.instance.domain.Domain
import com.jeanbarrossilva.orca.core.mastodon.MastodonDatabase
import com.jeanbarrossilva.orca.core.mastodon.auth.authentication.MastodonAuthenticator
import com.jeanbarrossilva.orca.core.mastodon.auth.authorization.MastodonAuthorizer
import com.jeanbarrossilva.orca.core.mastodon.feed.MastodonFeedPaginator
import com.jeanbarrossilva.orca.core.mastodon.feed.MastodonFeedProvider
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.MastodonProfile
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.MastodonProfileProvider
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.MastodonProfileTootPaginator
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.cache.MastodonProfileFetcher
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.cache.storage.MastodonProfileStorage
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.search.MastodonProfileSearcher
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.search.cache.MastodonProfileSearchResultsFetcher
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.search.cache.storage.MastodonProfileSearchResultsStorage
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.toot.MastodonToot
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.toot.MastodonTootProvider
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.toot.cache.MastodonTootFetcher
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.toot.cache.storage.MastodonTootStorage
import com.jeanbarrossilva.orca.core.mastodon.http.requester.Requester
import com.jeanbarrossilva.orca.platform.cache.Cache
import com.jeanbarrossilva.orca.std.imageloader.Image
import com.jeanbarrossilva.orca.std.imageloader.ImageLoader
import java.net.URL

/**
 * [MastodonInstance] that authorizes the user and caches all fetched structures through the given
 * [Context].
 *
 * @param context [Context] with which the [authenticator] will be created.
 * @param domain Unique identifier of the server.
 * @param authorizer [MastodonAuthorizer] by which the user will be authorized.
 * @param actorProvider [ActorProvider] that will provide [Actor]s to the [authenticator], the
 *   [authenticationLock] and the [feedProvider].
 * @param termMuter [TermMuter] by which [Toot]s with muted terms will be filtered out.
 * @param imageLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which
 *   [Image]s will be loaded.
 */
class ContextualMastodonInstance(
  context: Context,
  domain: Domain,
  authorizer: MastodonAuthorizer,
  override val authenticator: MastodonAuthenticator,
  actorProvider: ActorProvider,
  override val authenticationLock: AuthenticationLock<MastodonAuthenticator>,
  termMuter: TermMuter,
  imageLoaderProvider: ImageLoader.Provider<URL>
) : MastodonInstance<MastodonAuthorizer, MastodonAuthenticator>(domain, authorizer) {
  /** [MastodonDatabase] in which cached structures will be persisted. */
  private val database = MastodonDatabase.getInstance(context)

  /** [MastodonTootFetcher] by which [Toot]s will be fetched from the API. */
  private val tootFetcher = MastodonTootFetcher(imageLoaderProvider)

  /**
   * [MastodonFeedPaginator] with which pagination through the feed's [Toot]s that have been fetched
   * from the API will be performed.
   */
  private val feedTootPaginator = MastodonFeedPaginator(imageLoaderProvider)

  /**
   * [MastodonProfileTootPaginator.Provider] that provides the [MastodonProfileTootPaginator] to be
   * used by [profileFetcher], [profileStorage] and [profileSearchResultsFetcher].
   */
  private val profileTootPaginatorProvider =
    MastodonProfileTootPaginator.Provider { MastodonProfileTootPaginator(imageLoaderProvider, it) }

  /** [MastodonProfileFetcher] by which [MastodonProfile]s will be fetched from the API. */
  private val profileFetcher =
    MastodonProfileFetcher(imageLoaderProvider, profileTootPaginatorProvider)

  /** [MastodonProfileStorage] that will store fetched [MastodonProfile]s. */
  private val profileStorage =
    MastodonProfileStorage(
      imageLoaderProvider,
      profileTootPaginatorProvider,
      database.profileEntityDao
    )

  /** [Cache] that decides how to obtain [MastodonProfile]s. */
  private val profileCache =
    Cache.of(context, name = "profile-cache", profileFetcher, profileStorage)

  /**
   * [MastodonProfileSearchResultsFetcher] by which [ProfileSearchResult]s will be fetched from the
   * API.
   */
  private val profileSearchResultsFetcher =
    MastodonProfileSearchResultsFetcher(imageLoaderProvider, profileTootPaginatorProvider)

  /** [MastodonProfileSearchResultsStorage] that will store fetched [ProfileSearchResult]s. */
  private val profileSearchResultsStorage =
    MastodonProfileSearchResultsStorage(imageLoaderProvider, database.profileSearchResultEntityDao)

  /** [Cache] that decides how to obtain [ProfileSearchResult]s. */
  private val profileSearchResultsCache =
    Cache.of(
      context,
      name = "profile-search-results-cache",
      profileSearchResultsFetcher,
      profileSearchResultsStorage
    )

  /** [MastodonTootStorage] that will store fetched [MastodonToot]s. */
  private val tootStorage =
    MastodonTootStorage(
      profileCache,
      database.tootEntityDao,
      database.styleEntityDao,
      imageLoaderProvider
    )

  /** [Cache] that decides how to obtain [MastodonToot]s. */
  private val tootCache = Cache.of(context, name = "toot-cache", tootFetcher, tootStorage)

  override val feedProvider = MastodonFeedProvider(actorProvider, termMuter, feedTootPaginator)
  override val profileProvider = MastodonProfileProvider(profileCache)
  override val profileSearcher = MastodonProfileSearcher(profileSearchResultsCache)
  override val tootProvider = MastodonTootProvider(tootCache)

  /**
   * [Requester] that will mediate the sending of HTTP requests to the [client] and deal with
   * performing, retrying, cancelling and resuming them.
   */
  val requester = Requester.of(context, client)
}
