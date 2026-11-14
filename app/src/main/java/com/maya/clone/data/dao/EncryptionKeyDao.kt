package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.EncryptionKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EncryptionKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: EncryptionKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<EncryptionKeyEntity>)

    @Update
    suspend fun update(key: EncryptionKeyEntity)

    @Query("DELETE FROM encryption_keys WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM encryption_keys WHERE id = :id")
    suspend fun getById(id: String): EncryptionKeyEntity?

    @Query("SELECT * FROM encryption_keys WHERE user_id = :userId AND purpose = :purpose ORDER BY key_version DESC LIMIT 1")
    suspend fun getLatestForPurpose(userId: String, purpose: String): EncryptionKeyEntity?

    @Query("SELECT * FROM encryption_keys WHERE user_id = :userId ORDER BY purpose, key_version DESC")
    fun getAllForUser(userId: String): Flow<List<EncryptionKeyEntity>>
}