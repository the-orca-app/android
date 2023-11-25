package com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database

import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.isSameAs
import org.junit.Test

internal class RequestDatabaseTests {
  @Test
  fun isBuiltOnce() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val firstDatabase = RequestDatabase.get(context)
    val secondDatabase = RequestDatabase.get(context)
    assertThat(secondDatabase).isSameAs(firstDatabase)
    secondDatabase.close()
    firstDatabase.close()
  }
}
