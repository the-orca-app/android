package com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/** [RoomDatabase] into which [request entities][RequestEntity] will be persisted. */
@Database(entities = [RequestEntity::class], version = 1)
internal abstract class RequestDatabase : RoomDatabase() {
  companion object {
    /**
     * [RequestDatabase] to be created once and used throughout the whole [Application] lifecycle.
     */
    private lateinit var instance: RequestDatabase

    /**
     * Builds or retrieves the previously instantiated [RequestDatabase].
     *
     * @param context [Context] to be used for building it.
     */
    fun get(context: Context): RequestDatabase {
      return if (::instance.isInitialized) {
        instance
      } else {
        instance = build(context)
        instance
      }
    }

    /**
     * Builds a [RequestDatabase].
     *
     * @param context [Context] from which it will be built.
     */
    private fun build(context: Context): RequestDatabase {
      return Room.databaseBuilder(context, RequestDatabase::class.java, "request-database").build()
    }
  }
}
