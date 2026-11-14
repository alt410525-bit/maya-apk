package com.maya.clone.data.repository

import com.maya.clone.data.dao.FaceLockAttemptDao
import com.maya.clone.data.entity.FaceLockAttemptEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceLockAttemptRepository @Inject constructor(
    private val faceLockAttemptDao: FaceLockAttemptDao
) {
    suspend fun recordAttempt(attempt: FaceLockAttemptEntity) {
        faceLockAttemptDao.insert(attempt)
    }

    suspend fun getRecentAttempts(userId: String, limit: Int): List<FaceLockAttemptEntity> {
        return faceLockAttemptDao.getRecentForUser(userId, limit)
    }

    suspend fun countRecentFailures(userId: String, since: Long): Int {
        return faceLockAttemptDao.countRecentFailures(userId, since)
    }
}