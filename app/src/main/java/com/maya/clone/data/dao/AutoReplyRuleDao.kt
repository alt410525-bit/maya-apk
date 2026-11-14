package com.maya.clone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maya.clone.data.entity.AutoReplyRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AutoReplyRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: AutoReplyRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<AutoReplyRuleEntity>)

    @Update
    suspend fun update(rule: AutoReplyRuleEntity)

    @Query("DELETE FROM auto_reply_rules WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM auto_reply_rules WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM auto_reply_rules WHERE id = :id")
    suspend fun getById(id: String): AutoReplyRuleEntity?

    @Query("SELECT * FROM auto_reply_rules WHERE user_id = :userId AND enabled = 1 ORDER BY priority DESC, created_at ASC")
    fun getEnabledForUser(userId: String): Flow<List<AutoReplyRuleEntity>>

    @Query("SELECT * FROM auto_reply_rules WHERE user_id = :userId ORDER BY priority DESC, created_at ASC")
    fun getAllForUser(userId: String): Flow<List<AutoReplyRuleEntity>>
}