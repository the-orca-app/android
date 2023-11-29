package com.jeanbarrossilva.orca.core.sharedpreferences.actor.test

import androidx.test.platform.app.InstrumentationRegistry
import com.jeanbarrossilva.orca.core.feed.profile.post.content.TermMuter
import com.jeanbarrossilva.orca.core.sharedpreferences.actor.SharedPreferencesActorProvider
import com.jeanbarrossilva.orca.core.sharedpreferences.feed.profile.post.content.SharedPreferencesTermMuter
import org.junit.rules.ExternalResource

internal class SharedPreferencesCoreTestRule : ExternalResource() {
  private val context
    get() = InstrumentationRegistry.getInstrumentation().context

  lateinit var actorProvider: SharedPreferencesActorProvider
    private set

  lateinit var termMuter: TermMuter
    private set

  override fun before() {
    actorProvider = SharedPreferencesActorProvider(context)
    termMuter = SharedPreferencesTermMuter(context)
  }

  override fun after() {
    actorProvider.reset()
    SharedPreferencesTermMuter.reset(context)
  }
}
