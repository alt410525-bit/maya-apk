package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.ThemeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThemeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(theme: ThemeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(themes: List<ThemeEntity>)

    @Update
    suspend fun update(theme: ThemeEntity)

    @Query("DELETE FROM themes WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM themes WHERE id = :id")
    suspend fun getById(id: String): ThemeEntity?

    @Query("SELECT * FROM themes WHERE user_id = :userId ORDER BY is_active DESC, created_at DESC")
    fun getAllForUser(userId: String): Flow<List<ThemeEntity>>

    @Query("SELECT * FROM themes WHERE user_id = :userId AND is_active = 1 LIMIT 1")
    suspend fun getActiveForUser(userId: String): ThemeEntity?

    @Query("UPDATE themes SET is_active = 0 WHERE user_id = :userId AND is_active = 1")
    suspend fun deactivateAllForUser(userId: String)
}