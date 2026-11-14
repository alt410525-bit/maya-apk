package com.maya.clone.data.repository

import com.maya.clone.data.dao.AutoReplyRuleDao
import com.maya.clone.data.entity.AutoReplyRuleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoReplyRuleRepository @Inject constructor(
    private val autoReplyRuleDao: AutoReplyRuleDao
) {
    suspend fun saveRule(rule: AutoReplyRuleEntity) {
        autoReplyRuleDao.insert(rule)
    }

    suspend fun updateRule(rule: AutoReplyRuleEntity) {
        autoReplyRuleDao.update(rule)
    }

    suspend fun deleteRule(id: String) {
        autoReplyRuleDao.deleteById(id)
    }

    suspend fun getRule(id: String): AutoReplyRuleEntity? {
        return autoReplyRuleDao.getById(id)
    }

    fun getEnabledRules(userId: String): Flow<List<AutoReplyRuleEntity>> {
        return autoReplyRuleDao.getEnabledForUser(userId)
    }

    fun getAllRules(userId: String): Flow<List<AutoReplyRuleEntity>> {
        return autoReplyRuleDao.getAllForUser(userId)
    }
}