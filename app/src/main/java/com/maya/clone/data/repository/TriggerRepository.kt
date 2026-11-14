package com.maya.clone.data.repository

import com.maya.clone.data.dao.TriggerDao
import com.maya.clone.data.entity.TriggerEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TriggerRepository @Inject constructor(
    private val triggerDao: TriggerDao
) {
    suspend fun saveTrigger(trigger: TriggerEntity) {
        triggerDao.insert(trigger)
    }

    suspend fun updateTrigger(trigger: TriggerEntity) {
        triggerDao.update(trigger)
    }

    suspend fun deleteTrigger(id: String) {
        triggerDao.deleteById(id)
    }

    suspend fun getTrigger(id: String): TriggerEntity? {
        return triggerDao.getById(id)
    }

    fun getEnabledTriggers(userId: String): Flow<List<TriggerEntity>> {
        return triggerDao.getEnabledForUser(userId)
    }

    fun getAllTriggers(userId: String): Flow<List<TriggerEntity>> {
        return triggerDao.getAllForUser(userId)
    }

    suspend fun getTriggersByEventType(userId: String, eventType: String): List<TriggerEntity> {
        return triggerDao.getByEventType(userId, eventType)
    }
}