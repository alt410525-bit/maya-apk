package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.AwarenessEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AwarenessEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: AwarenessEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<AwarenessEventEntity>)

    @Update
    suspend fun update(event: AwarenessEventEntity)

    @Query("DELETE FROM awareness_events WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM awareness_events WHERE user_id = :userId AND created_at < :before")
    suspend fun deleteOldForUser(userId: String, before: Long)

    @Query("SELECT * FROM awareness_events WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    suspend fun getRecentForUser(userId: String, limit: Int): List<AwarenessEventEntity>

    @Query("SELECT * FROM awareness_events WHERE user_id = :userId AND type = :type ORDER BY created_at DESC LIMIT :limit")
    suspend fun getByTypeForUser(userId: String, type: String, limit: Int): List<AwarenessEventEntity>

    @Query("SELECT * FROM awareness_events WHERE user_id = :userId AND processed = 0 ORDER BY created_at ASC")
    fun getUnprocessedForUser(userId: String): Flow<List<AwarenessEventEntity>>

    @Query("SELECT COUNT(*) FROM awareness_events WHERE user_id = :userId AND created_at > :since")
    suspend fun countSince(userId: String, since: Long): Int
}