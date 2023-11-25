package com.jeanbarrossilva.orca.core.mastodon.http.requester

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.isNotSameAs
import assertk.assertions.isSameAs
import com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database.RequestDatabase
import com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequesterTestRule
import com.jeanbarrossilva.orca.core.test.TestAuthenticationLock
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

internal class UnauthenticatedRequesterTests {
  private val lock = TestAuthenticationLock()

  @After
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
      val context = InstrumentationRegistry.getInstrumentation().targetContext
      val database = Room.inMemoryDatabaseBuilder(context, RequestDatabase::class.java).build()
      unauthenticatedRequester = UnauthenticatedRequester(database, client)
    }
  }
}
