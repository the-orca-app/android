package com.jeanbarrossilva.orca.core.auth

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.jeanbarrossilva.orca.core.test.TestActorProvider
import com.jeanbarrossilva.orca.core.test.TestAuthenticationLock
import com.jeanbarrossilva.orca.core.test.TestAuthenticator
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

internal class AuthenticationLockTests {
  @Test
  fun `GIVEN an unauthenticated actor WHEN unlocking THEN it's authenticated`() {
    var hasBeenAuthenticated = false
    val authenticator = TestAuthenticator { hasBeenAuthenticated = true }
    runTest { TestAuthenticationLock(authenticator = authenticator).scheduleUnlock {} }
    assertTrue(hasBeenAuthenticated)
  }

  @Test
  fun `GIVEN an authenticated actor WHEN unlocking THEN the listener is notified`() {
    val actorProvider = TestActorProvider()
    val authenticator = TestAuthenticator(actorProvider = actorProvider)
    var hasListenerBeenNotified = false
    runTest {
      authenticator.authenticate()
      TestAuthenticationLock(actorProvider, authenticator).scheduleUnlock {
        hasListenerBeenNotified = true
      }
    }
    assertTrue(hasListenerBeenNotified)
  }

  @Test
  fun `GIVEN an ongoing unlock WHEN scheduling other ones THEN they're performed sequentially`() {
    val lock = TestAuthenticationLock()
    runTest {
      lock.scheduleUnlock { delay(32.seconds) }
      repeat(1_024) { lock.scheduleUnlock { delay(8.seconds) } }
      assertThat(
          @OptIn(ExperimentalCoroutinesApi::class)
          testScheduler.currentTime.milliseconds.inWholeSeconds
        )
        .isEqualTo(8_224)
    }
  }
}
