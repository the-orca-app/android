package com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/** Performs SQL transactions regarding [request entities][RequestEntity]. */
@Dao
internal interface RequestDao {
  /**
   * Inserts the [entity].
   *
   * @param entity [RequestEntity] to be inserted.
   */
  @Insert suspend fun insert(entity: RequestEntity)

  /**
   * Selects the inserted [RequestEntity] whose [route][RequestEntity.route] is the same as the
   * given one.
   *
   * @param route Route of the [RequestEntity] to be selected.
   */
  @Query("SELECT * FROM requests WHERE route = :route")
  suspend fun selectByRoute(route: String): RequestEntity?

  /**
   * Deletes the [entity].
   *
   * @param entity [RequestEntity] to be deleted.
   */
  @Delete suspend fun delete(entity: RequestEntity)
}
