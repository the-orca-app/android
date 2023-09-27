package com.jeanbarrossilva.orca.core.http.feed.profile.search.cache.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jeanbarrossilva.orca.core.feed.profile.account.Account
import com.jeanbarrossilva.orca.core.feed.profile.search.ProfileSearchResult
import java.net.URL

/**
 * Primitive information to be persisted about a [ProfileSearchResult].
 *
 * @param query Search query to which the [ProfileSearchResult] is associated.
 * @param id Unique identifier.
 * @param account [String] representation of the [HttpProfileSearchResultEntity]'s
 * [account][HttpProfileSearchResultEntity.account].
 * @param avatarURL URL [String] that leads to the avatar image.
 * @param name Name to be displayed.
 * @param url URL [String] that leads to the owner.
 **/
@Entity(tableName = "profile_search_results")
data class HttpProfileSearchResultEntity(
    @PrimaryKey internal val query: String,
    internal val id: String,
    internal val account: String,
    @ColumnInfo(name = "avatar_url") internal val avatarURL: String,
    internal val name: String,
    internal val url: String
) {
    /** Converts this [HttpProfileSearchResultEntity] into a [ProfileSearchResult]. **/
    internal fun toProfileSearchResult(): ProfileSearchResult {
        val account = Account.of(account, fallbackDomain = "mastodon.social")
        val avatarURL = URL(avatarURL)
        val url = URL(url)
        return ProfileSearchResult(id, account, avatarURL, name, url)
    }
}