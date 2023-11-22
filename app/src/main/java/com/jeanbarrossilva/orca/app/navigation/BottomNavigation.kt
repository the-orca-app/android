package com.jeanbarrossilva.orca.app.navigation

import androidx.annotation.IdRes
import com.jeanbarrossilva.orca.app.R
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.core.module.authenticationLock
import com.jeanbarrossilva.orca.feature.feed.FeedFragment
import com.jeanbarrossilva.orca.feature.profiledetails.ProfileDetailsFragment
import com.jeanbarrossilva.orca.feature.profiledetails.navigation.BackwardsNavigationState
import com.jeanbarrossilva.orca.feature.settings.SettingsFragment
import com.jeanbarrossilva.orca.platform.ui.core.navigation.Navigator
import com.jeanbarrossilva.orca.platform.ui.core.navigation.duplication.disallowingDuplication
import com.jeanbarrossilva.orca.platform.ui.core.navigation.transition.suddenly
import com.jeanbarrossilva.orca.std.injector.Injector

internal enum class BottomNavigation {
  FEED {
    override val id = R.id.feed

    override suspend fun getDestination(): Navigator.Navigation.Destination<*> {
      return authenticationLock.scheduleUnlock {
        Navigator.Navigation.Destination("feed") { FeedFragment(it.id) }
      }
    }
  },
  PROFILE_DETAILS {
    override val id = R.id.profile_details

    override suspend fun getDestination(): Navigator.Navigation.Destination<*> {
      return authenticationLock.scheduleUnlock {
        Navigator.Navigation.Destination(ProfileDetailsFragment.createRoute(it.id)) {
          ProfileDetailsFragment(BackwardsNavigationState.Unavailable, it.id)
        }
      }
    }
  },
  SETTINGS {
    override val id = R.id.settings

    override suspend fun getDestination(): Navigator.Navigation.Destination<*> {
      return Navigator.Navigation.Destination("settings", ::SettingsFragment)
    }
  };

  @get:IdRes protected abstract val id: Int

  protected val authenticationLock
    get() = Injector.from<CoreModule>().authenticationLock()

  protected abstract suspend fun getDestination(): Navigator.Navigation.Destination<*>

  companion object {
    suspend fun navigate(navigator: Navigator, @IdRes id: Int) {
      val value = values().find { it.id == id } ?: throw NoSuchElementException("$id")
      val destination = value.getDestination()
      navigator.navigate(suddenly(), disallowingDuplication()) {
        to(destination.route, destination.target)
      }
    }
  }
}
