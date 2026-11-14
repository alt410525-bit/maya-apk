package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.FaceProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: FaceProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<FaceProfileEntity>)

    @Update
    suspend fun update(profile: FaceProfileEntity)

    @Query("DELETE FROM face_profiles WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM face_profiles WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM face_profiles WHERE id = :id")
    suspend fun getById(id: String): FaceProfileEntity?

    @Query("SELECT * FROM face_profiles WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllForUser(userId: String): Flow<List<FaceProfileEntity>>
}