package com.jeanbarrossilva.orca.core.mastodon.feed.profile.account

import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.feed.profile.Profile
import com.jeanbarrossilva.orca.core.feed.profile.account.Account
import com.jeanbarrossilva.orca.core.feed.profile.toot.Author
import com.jeanbarrossilva.orca.core.feed.profile.type.followable.Follow
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.MastodonProfile
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.MastodonProfileTootPaginator
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.toot.MastodonToot
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.type.editable.MastodonEditableProfile
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.type.followable.MastodonFollowableProfile
import com.jeanbarrossilva.orca.core.mastodon.http.authenticateAndGet
import com.jeanbarrossilva.orca.core.mastodon.instance.SomeHttpInstance
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.core.module.instanceProvider
import com.jeanbarrossilva.orca.platform.ui.core.style.fromHtml
import com.jeanbarrossilva.orca.std.imageloader.ImageLoader
import com.jeanbarrossilva.orca.std.injector.Injector
import com.jeanbarrossilva.orca.std.styledstring.StyledString
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import java.net.URL
import kotlinx.serialization.Serializable

/**
 * Structure returned by the API when requesting a user's account.
 *
 * @param id Unique global identifier.
 * @param username Unique identifier within the instance from which this [Account] is.
 * @param acct Relative identifier based on the instance of this [Account].
 * @param url URL [String] that leads to this [Account] within its instance.
 * @param displayName Name that's publicly displayed.
 * @param locked Whether its contents are private, meaning that they can only be seen by accepted
 *   followers.
 * @param note Description provided by the owner.
 * @param avatar URL [String] that leads to the avatar image.
 * @param followersCount Amount of followers that this [Account] has.
 * @param followingCount Amount of other [Account]s that this one is following.
 */
@Serializable
internal data class MastodonAccount(
  val id: String,
  val username: String,
  val acct: String,
  val url: String,
  val displayName: String,
  val locked: Boolean,
  val note: String,
  val avatar: String,
  val followersCount: Int,
  val followingCount: Int
) {
  /**
   * Converts this [MastodonAccount] into an [Author].
   *
   * @param avatarLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which the
   *   [Author]'s avatar will be loaded from a [URL].
   */
  fun toAuthor(avatarLoaderProvider: ImageLoader.Provider<URL>): Author {
    val avatarURL = URL(avatar)
    val avatarLoader = avatarLoaderProvider.provide(avatarURL)
    val account = toAccount()
    val profileURL = URL(url)
    return Author(id, avatarLoader, displayName, account, profileURL)
  }

  /**
   * Converts this [MastodonAccount] into a [Profile].
   *
   * @param avatarLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which the
   *   [Profile]'s avatar will be loaded from a [URL].
   * @param tootPaginatorProvider [MastodonProfileTootPaginator.Provider] by which a
   *   [MastodonProfileTootPaginator] for paginating through the resulting [MastodonProfile]'s
   *   [MastodonToot]s will be provided.
   */
  suspend fun toProfile(
    avatarLoaderProvider: ImageLoader.Provider<URL>,
    tootPaginatorProvider: MastodonProfileTootPaginator.Provider
  ): Profile {
    return if (isOwner()) {
      toEditableProfile(avatarLoaderProvider, tootPaginatorProvider)
    } else {
      toFollowableProfile(avatarLoaderProvider, tootPaginatorProvider)
    }
  }

  /** Converts this [MastodonAccount] into an [Account]. */
  private fun toAccount(): Account {
    return Account.of(acct, fallbackDomain = "mastodon.social")
  }

  /**
   * Whether the currently [authenticated][Actor.Authenticated] [Actor] is the owner of this
   * [Account].
   */
  private suspend fun isOwner(): Boolean {
    return Injector.from<CoreModule>()
      .instanceProvider()
      .provide()
      .authenticationLock
      .requestUnlock { it.id == id }
  }

  /**
   * Converts this [MastodonAccount] into an [MastodonEditableProfile].
   *
   * @param avatarLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which the
   *   [MastodonEditableProfile]'s avatar will be loaded from a [URL].
   * @param tootPaginatorProvider [MastodonProfileTootPaginator.Provider] by which a
   *   [MastodonProfileTootPaginator] for paginating through the resulting
   *   [MastodonEditableProfile]'s [MastodonToot]s will be provided.
   */
  private fun toEditableProfile(
    avatarLoaderProvider: ImageLoader.Provider<URL>,
    tootPaginatorProvider: MastodonProfileTootPaginator.Provider
  ): MastodonEditableProfile {
    val account = toAccount()
    val avatarURL = URL(avatar)
    val avatarLoader = avatarLoaderProvider.provide(avatarURL)
    val bio = StyledString.fromHtml(note)
    val url = URL(url)
    return MastodonEditableProfile(
      tootPaginatorProvider,
      id,
      account,
      avatarLoader,
      displayName,
      bio,
      followersCount,
      followingCount,
      url
    )
  }

  /**
   * Converts this [MastodonAccount] into an [MastodonFollowableProfile].
   *
   * @param avatarLoaderProvider [ImageLoader.Provider] that provides the [ImageLoader] by which the
   *   [MastodonFollowableProfile]'s avatar will be loaded from a [URL].
   * @param tootPaginatorProvider [MastodonProfileTootPaginator.Provider] by which a
   *   [MastodonProfileTootPaginator] for paginating through the resulting
   *   [MastodonFollowableProfile]'s [MastodonToot]s will be provided.
   */
  private suspend fun toFollowableProfile(
    avatarLoaderProvider: ImageLoader.Provider<URL>,
    tootPaginatorProvider: MastodonProfileTootPaginator.Provider
  ): MastodonFollowableProfile<Follow> {
    val account = toAccount()
    val avatarURL = URL(avatar)
    val avatarLoader = avatarLoaderProvider.provide(avatarURL)
    val bio = StyledString.fromHtml(note)
    val url = URL(url)
    val follow =
      (Injector.from<CoreModule>().instanceProvider().provide() as SomeHttpInstance)
        .client
        .authenticateAndGet("/api/v1/accounts/relationships") { parameter("id", id) }
        .body<List<MastodonRelationship>>()
        .first()
        .toFollow(this)
    return MastodonFollowableProfile(
      tootPaginatorProvider,
      id,
      account,
      avatarLoader,
      displayName,
      bio,
      follow,
      followersCount,
      followingCount,
      url
    )
  }
}
