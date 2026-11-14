package com.maya.clone.data.repository

import com.maya.clone.data.dao.AwarenessEventDao
import com.maya.clone.data.entity.AwarenessEventEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AwarenessEventRepository @Inject constructor(
    private val awarenessEventDao: AwarenessEventDao
) {
    suspend fun recordEvent(event: AwarenessEventEntity) {
        awarenessEventDao.insert(event)
    }

    suspend fun recordEvents(events: List<AwarenessEventEntity>) {
        awarenessEventDao.insertAll(events)
    }

    suspend fun markProcessed(event: AwarenessEventEntity) {
        awarenessEventDao.update(event.copy(processed = true))
    }

    suspend fun getRecentEvents(userId: String, limit: Int): List<AwarenessEventEntity> {
        return awarenessEventDao.getRecentForUser(userId, limit)
    }

    suspend fun getEventsByType(userId: String, type: String, limit: Int): List<AwarenessEventEntity> {
        return awarenessEventDao.getByTypeForUser(userId, type, limit)
    }

    fun getUnprocessedEvents(userId: String): Flow<List<AwarenessEventEntity>> {
        return awarenessEventDao.getUnprocessedForUser(userId)
    }

    suspend fun countEventsSince(userId: String, since: Long): Int {
        return awarenessEventDao.countSince(userId, since)
    }

    suspend fun deleteOldEvents(userId: String, before: Long) {
        awarenessEventDao.deleteOldForUser(userId, before)
    }
}