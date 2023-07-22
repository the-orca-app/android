package com.jeanbarrossilva.mastodonte.core.auth.actor

import com.jeanbarrossilva.mastodonte.core.test.TestActorProvider
import com.jeanbarrossilva.mastodonte.core.test.TestAuthenticator
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class ActorProviderTests {
    @Test
    fun `GIVEN a provider WHEN authenticating THEN it provides the resulting actor`() {
        val actorProvider = TestActorProvider()
        val authenticator = TestAuthenticator(actorProvider = actorProvider)
        runTest {
            val actor = authenticator.authenticate()
            assertEquals(actor, actorProvider.provide())
        }
    }
}
