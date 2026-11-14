package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.BackupJobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupJobDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: BackupJobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(jobs: List<BackupJobEntity>)

    @Update
    suspend fun update(job: BackupJobEntity)

    @Query("DELETE FROM backup_jobs WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM backup_jobs WHERE id = :id")
    suspend fun getById(id: String): BackupJobEntity?

    @Query("SELECT * FROM backup_jobs WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllForUser(userId: String): Flow<List<BackupJobEntity>>

    @Query("SELECT * FROM backup_jobs WHERE user_id = :userId AND status = 'pending' ORDER BY created_at ASC")
    suspend fun getPendingForUser(userId: String): List<BackupJobEntity>
}