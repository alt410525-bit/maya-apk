package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.SettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: SettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(settings: List<SettingEntity>)

    @Update
    suspend fun update(setting: SettingEntity)

    @Query("DELETE FROM settings WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM settings WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM settings WHERE id = :id")
    suspend fun getById(id: String): SettingEntity?

    @Query("SELECT * FROM settings WHERE user_id = :userId AND key = :key")
    suspend fun getByKey(userId: String, key: String): SettingEntity?

    @Query("SELECT * FROM settings WHERE user_id = :userId ORDER BY key ASC")
    fun getAllForUser(userId: String): Flow<List<SettingEntity>>
}