package com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.jeanbarrossilva.orca.core.mastodon.http.requester.request.Request
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

internal class RequestDaoTests {
  @After
  fun tearDown() {
    database.clearAllTables()
    database.close()
  }

  @Test
  fun inserts() {
    val entity =
      RequestEntity(
        Request.Get.METHOD_NAME,
        route = "api/v1",
        "Parameters [id=[123]]",
        "Headers [${HttpHeaders.Authorization}=[Bearer 1234]]"
      )
    runTest {
      database.dao.insert(entity)
      assertThat(database.dao.selectByRoute("api/v1")).isEqualTo(entity)
    }
  }

  @Test
  fun deletes() {
    val entity =
      RequestEntity(
        Request.Get.METHOD_NAME,
        route = "api/v1",
        "Parameters [id=[123]]",
        "Headers [${HttpHeaders.Authorization}=[Bearer 1234]]"
      )
    runTest {
      database.dao.delete(entity)
      assertThat(database.dao.selectByRoute("api/v1")).isNull()
    }
  }

  companion object {
    private lateinit var database: RequestDatabase

    @BeforeClass
    @JvmStatic
    fun setUp() {
      val context = InstrumentationRegistry.getInstrumentation().targetContext
      database = Room.inMemoryDatabaseBuilder(context, RequestDatabase::class.java).build()
    }
  }
}
