package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.ConnectorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(connector: ConnectorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(connectors: List<ConnectorEntity>)

    @Update
    suspend fun update(connector: ConnectorEntity)

    @Query("DELETE FROM connectors WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM connectors WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM connectors WHERE id = :id")
    suspend fun getById(id: String): ConnectorEntity?

    @Query("SELECT * FROM connectors WHERE user_id = :userId ORDER BY status, created_at DESC")
    fun getAllForUser(userId: String): Flow<List<ConnectorEntity>>

    @Query("SELECT * FROM connectors WHERE user_id = :userId AND status = 'connected' ORDER BY last_sync_at DESC")
    fun getConnectedForUser(userId: String): Flow<List<ConnectorEntity>>
}