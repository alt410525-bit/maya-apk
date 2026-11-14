package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maya.clone.data.entity.FaceLockAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceLockAttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: FaceLockAttemptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attempts: List<FaceLockAttemptEntity>)

    @Query("DELETE FROM face_lock_attempts WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM face_lock_attempts WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM face_lock_attempts WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    suspend fun getRecentForUser(userId: String, limit: Int): List<FaceLockAttemptEntity>

    @Query("SELECT COUNT(*) FROM face_lock_attempts WHERE user_id = :userId AND success = 0 AND created_at > :since")
    suspend fun countRecentFailures(userId: String, since: Long): Int
}