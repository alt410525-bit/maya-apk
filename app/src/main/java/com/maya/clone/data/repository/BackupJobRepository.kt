package com.maya.clone.data.repository

import com.maya.clone.data.dao.BackupJobDao
import com.maya.clone.data.entity.BackupJobEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupJobRepository @Inject constructor(
    private val backupJobDao: BackupJobDao
) {
    suspend fun createJob(job: BackupJobEntity) {
        backupJobDao.insert(job)
    }

    suspend fun updateJob(job: BackupJobEntity) {
        backupJobDao.update(job)
    }

    suspend fun getJob(id: String): BackupJobEntity? {
        return backupJobDao.getById(id)
    }

    fun getJobsForUser(userId: String): Flow<List<BackupJobEntity>> {
        return backupJobDao.getAllForUser(userId)
    }

    suspend fun getPendingJobs(userId: String): List<BackupJobEntity> {
        return backupJobDao.getPendingForUser(userId)
    }
}