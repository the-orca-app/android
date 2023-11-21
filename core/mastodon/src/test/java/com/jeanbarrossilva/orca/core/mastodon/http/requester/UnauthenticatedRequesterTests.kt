package com.jeanbarrossilva.orca.core.mastodon.http.requester

import assertk.assertThat
import assertk.assertions.isNotSameAs
import assertk.assertions.isSameAs
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequesterTestRule
import com.jeanbarrossilva.orca.core.test.TestAuthenticationLock
import kotlin.test.AfterTest
import kotlin.test.Test
import org.junit.BeforeClass

internal class UnauthenticatedRequesterTests {
  private val lock = TestAuthenticationLock()

  @AfterTest
  fun tearDown() {
    unauthenticatedRequester.clear()
  }

  @Test
  fun retrievesAuthenticatedRequester() {
    val authenticated = unauthenticatedRequester.authenticated(lock)
    assertThat(unauthenticatedRequester.authenticated(lock)).isSameAs(authenticated)
  }

  @Test
  fun clears() {
    val authenticatedRequester = unauthenticatedRequester.authenticated(lock)
    unauthenticatedRequester.clear()
    assertThat(unauthenticatedRequester.authenticated(lock)).isNotSameAs(authenticatedRequester)
  }

  companion object {
    lateinit var unauthenticatedRequester: UnauthenticatedRequester

    @BeforeClass
    @JvmStatic
    fun setUp() {
      val client = TestRequesterTestRule().requester.client
      unauthenticatedRequester = UnauthenticatedRequester(client)
    }
  }
}
