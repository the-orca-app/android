package com.jeanbarrossilva.orca.app.module.core

import android.content.Context
import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.auth.Authenticator
import com.jeanbarrossilva.orca.core.auth.Authorizer
import com.jeanbarrossilva.orca.core.feed.FeedProvider
import com.jeanbarrossilva.orca.core.feed.profile.ProfileProvider
import com.jeanbarrossilva.orca.core.feed.profile.search.ProfileSearcher
import com.jeanbarrossilva.orca.core.feed.profile.toot.TootProvider
import com.jeanbarrossilva.orca.core.mastodon.MastodonDatabase
import com.jeanbarrossilva.orca.core.mastodon.auth.authentication.MastodonAuthenticator
import com.jeanbarrossilva.orca.core.mastodon.auth.authorization.MastodonAuthorizer
import com.jeanbarrossilva.orca.core.mastodon.feed.MastodonFeedProvider
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.MastodonProfileProvider
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.cache.ProfileFetcher
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.cache.storage.ProfileStorage
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.search.MastodonProfileSearcher
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.search.cache.ProfileSearchResultsFetcher
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.search.cache.storage.ProfileSearchResultsStorage
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.toot.MastodonTootProvider
import com.jeanbarrossilva.orca.core.sharedpreferences.actor.SharedPreferencesActorProvider
import com.jeanbarrossilva.orca.std.cache.Cache
import org.koin.android.ext.koin.androidContext
import org.koin.core.definition.Definition
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent

@Suppress("FunctionName")
internal fun MainCoreModule(): Module {
    val context = KoinJavaComponent.get<Context>(Context::class.java)
    val actorProvider = SharedPreferencesActorProvider(context)
    val tootPaginateSource = MastodonFeedProvider.PaginateSource()
    val database = MastodonDatabase.getInstance(context)
    val profileFetcher = ProfileFetcher(tootPaginateSource)
    val profileStorage = ProfileStorage(tootPaginateSource, database.profileEntityDao)
    val profileCache = Cache.of(profileFetcher, profileStorage)
    val profileSearchResultsFetcher = ProfileSearchResultsFetcher(tootPaginateSource)
    val profileSearchResultsStorage =
        ProfileSearchResultsStorage(database.profileSearchResultEntityDao)
    val profileSearchResultsCache =
        Cache.of(profileSearchResultsFetcher, profileSearchResultsStorage)
    return CoreModule(
        { MastodonAuthorizer(androidContext()) },
        { MastodonAuthenticator(context, authorizer = get(), actorProvider) },
        { AuthenticationLock(authenticator = get(), actorProvider) },
        { MastodonFeedProvider(actorProvider, tootPaginateSource) },
        { MastodonProfileProvider(profileCache) },
        { MastodonProfileSearcher(profileSearchResultsCache) },
        { MastodonTootProvider() }
    )
}

@Suppress("FunctionName")
internal fun CoreModule(
    authenticator: Definition<Authenticator>,
    authenticationLock: Definition<AuthenticationLock>,
    feedProvider: Definition<FeedProvider>,
    profileProvider: Definition<ProfileProvider>,
    profileSearcher: Definition<ProfileSearcher>,
    tootProvider: Definition<TootProvider>
): Module {
    return CoreModule<Authorizer, Authenticator>(
        authorizer = null,
        authenticator,
        authenticationLock,
        feedProvider,
        profileProvider,
        profileSearcher,
        tootProvider
    )
}

@Suppress("FunctionName")
private inline fun <reified A1 : Authorizer, reified A2 : Authenticator> CoreModule(
    noinline authorizer: Definition<A1>?,
    noinline authenticator: Definition<A2>,
    noinline authenticationLock: Definition<AuthenticationLock>,
    noinline feedProvider: Definition<FeedProvider>,
    noinline profileProvider: Definition<ProfileProvider>,
    noinline profileSearcher: Definition<ProfileSearcher>,
    noinline tootProvider: Definition<TootProvider>
): Module {
    return module {
        authorizer?.let { single(definition = it) binds arrayOf(Authorizer::class, A1::class) }
        single(definition = authenticator) binds arrayOf(Authenticator::class, A2::class)
        single(definition = authenticationLock)
        single(definition = feedProvider)
        single(definition = profileProvider)
        single(definition = profileSearcher)
        single(definition = tootProvider)
    }
}
