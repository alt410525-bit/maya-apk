package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.AgentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(agent: AgentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(agents: List<AgentEntity>)

    @Update
    suspend fun update(agent: AgentEntity)

    @Query("DELETE FROM agents WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM agents WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM agents WHERE id = :id")
    suspend fun getById(id: String): AgentEntity?

    @Query("SELECT * FROM agents WHERE user_id = :userId AND is_active = 1 ORDER BY updated_at DESC")
    fun getActiveForUser(userId: String): Flow<List<AgentEntity>>

    @Query("SELECT * FROM agents WHERE user_id = :userId ORDER BY updated_at DESC")
    fun getAllForUser(userId: String): Flow<List<AgentEntity>>

    @Query("SELECT COUNT(*) FROM agents WHERE user_id = :userId AND is_active = 1")
    suspend fun countActiveForUser(userId: String): Int
}