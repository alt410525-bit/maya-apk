package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.ApiKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApiKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(apiKey: ApiKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apiKeys: List<ApiKeyEntity>)

    @Update
    suspend fun update(apiKey: ApiKeyEntity)

    @Query("DELETE FROM api_keys WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM api_keys WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM api_keys WHERE id = :id")
    suspend fun getById(id: String): ApiKeyEntity?

    @Query("SELECT * FROM api_keys WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllForUser(userId: String): Flow<List<ApiKeyEntity>>

    @Query("SELECT * FROM api_keys WHERE user_id = :userId AND provider = :provider ORDER BY created_at DESC")
    fun getByProvider(userId: String, provider: String): Flow<List<ApiKeyEntity>>
}