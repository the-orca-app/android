package com.jeanbarrossilva.orca.app

import androidx.test.core.app.launchActivity
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsRule
import com.jeanbarrossilva.orca.core.mastodon.auth.authorization.MastodonAuthorizationActivity
import org.junit.Rule
import org.junit.Test

internal class MainOrcaActivityTests {
  @get:Rule val intentsRule = IntentsRule()

  @Test
  fun navigatesToAuthorization() {
    launchActivity<MainOrcaActivity>().use {
      intended(hasComponent(MastodonAuthorizationActivity::class.qualifiedName))
    }
  }
}
