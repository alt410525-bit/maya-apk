package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.LicenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LicenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(license: LicenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(licenses: List<LicenseEntity>)

    @Update
    suspend fun update(license: LicenseEntity)

    @Query("DELETE FROM licenses WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM licenses WHERE id = :id")
    suspend fun getById(id: String): LicenseEntity?

    @Query("SELECT * FROM licenses WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllForUser(userId: String): Flow<List<LicenseEntity>>

    @Query("SELECT * FROM licenses WHERE user_id = :userId ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestForUser(userId: String): LicenseEntity?
}