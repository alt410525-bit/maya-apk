package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.TriggerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TriggerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trigger: TriggerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(triggers: List<TriggerEntity>)

    @Update
    suspend fun update(trigger: TriggerEntity)

    @Query("DELETE FROM triggers WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM triggers WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM triggers WHERE id = :id")
    suspend fun getById(id: String): TriggerEntity?

    @Query("SELECT * FROM triggers WHERE user_id = :userId AND enabled = 1 ORDER BY created_at ASC")
    fun getEnabledForUser(userId: String): Flow<List<TriggerEntity>>

    @Query("SELECT * FROM triggers WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllForUser(userId: String): Flow<List<TriggerEntity>>

    @Query("SELECT * FROM triggers WHERE user_id = :userId AND event_type = :eventType AND enabled = 1 ORDER BY cooldown_ms ASC")
    suspend fun getByEventType(userId: String, eventType: String): List<TriggerEntity>
}