package com.jeanbarrossilva.orca.core.mastodon.feed.profile.toot.stat

import com.jeanbarrossilva.orca.core.feed.profile.Profile
import com.jeanbarrossilva.orca.core.feed.profile.toot.stat.toggleable.ToggleableStat
import com.jeanbarrossilva.orca.core.mastodon.feed.profile.toot.MastodonToot
import com.jeanbarrossilva.orca.core.mastodon.http.authenticateAndPost
import com.jeanbarrossilva.orca.core.mastodon.instance.SomeHttpInstance
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.core.module.instanceProvider
import com.jeanbarrossilva.orca.std.injector.Injector

/**
 * Builds a [ToggleableStat] for an [MastodonToot]'s favorites that obtains them from the API.
 *
 * @param id ID of the [MastodonToot] for which the [ToggleableStat] is.
 * @param count Amount of times that the [MastodonToot] has been marked as favorite.
 */
@Suppress("FunctionName")
internal fun FavoriteStat(id: String, count: Int): ToggleableStat<Profile> {
  return ToggleableStat(count) {
    setEnabled { isEnabled ->
      val route =
        if (isEnabled) {
          "/api/v1/statuses/$id/favourite"
        } else {
          @Suppress("SpellCheckingInspection") "/api/v1/statuses/$id/unfavourite"
        }
      (Injector.from<CoreModule>().instanceProvider().provide() as SomeHttpInstance)
        .client
        .authenticateAndPost(route)
    }
  }
}

/**
 * Builds a [ToggleableStat] for an [MastodonToot]'s reblogs that obtains them from the API.
 *
 * @param id ID of the [MastodonToot] for which the [ToggleableStat] is.
 * @param count Amount of times that the [MastodonToot] has been reblogged.
 */
@Suppress("FunctionName")
internal fun ReblogStat(id: String, count: Int): ToggleableStat<Profile> {
  return ToggleableStat(count) {
    setEnabled { isEnabled ->
      val route = if (isEnabled) "/api/v1/statuses/$id/reblog" else "/api/v1/statuses/$id/unreblog"
      (Injector.from<CoreModule>().instanceProvider().provide() as SomeHttpInstance)
        .client
        .authenticateAndPost(route)
    }
  }
}
