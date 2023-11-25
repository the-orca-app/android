package com.jeanbarrossilva.orca.core.mastodon.http.requester

import assertk.assertThat
import assertk.assertions.isTrue
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.use
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class JobExtensionsTests {
  @Test
  fun uses() {
    var hasBeenUsed = false
    runTest { launch {}.use { hasBeenUsed = true } }
    assertThat(hasBeenUsed).isTrue()
  }

  @Test
  fun startsJobBeforeUsingIt() {
    runTest { launch(start = CoroutineStart.LAZY) {}.use { ensureActive() } }
  }

  @Test
  fun cancelsJobAfterUsingIt() {
    runTest { assertThat(launch {}.use { this }.isCancelled).isTrue() }
  }
}
