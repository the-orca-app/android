package com.jeanbarrossilva.mastodonte.core.mastodon.profile

import com.jeanbarrossilva.mastodonte.core.account.Account
import com.jeanbarrossilva.mastodonte.core.mastodon.toot.status.TootPaginateSource
import com.jeanbarrossilva.mastodonte.core.profile.Profile
import com.jeanbarrossilva.mastodonte.core.toot.Toot
import java.net.URL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal data class MastodonProfile(
    private val tootPaginateSource: TootPaginateSource,
    override val id: String,
    override val account: Account,
    override val avatarURL: URL,
    override val name: String,
    override val bio: String,
    override val followerCount: Int,
    override val followingCount: Int,
    override val url: URL
) : Profile {
    private val tootsFlow = tootPaginateSource.loadPages(TootPaginateSource.DEFAULT_COUNT).map {
        it.items
    }

    override suspend fun getToots(page: Int): Flow<List<Toot>> {
        tootPaginateSource.paginateTo(page)
        return tootsFlow
    }
}
