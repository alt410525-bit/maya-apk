package com.maya.clone.data.repository

import com.maya.clone.data.dao.EncryptionKeyDao
import com.maya.clone.data.entity.EncryptionKeyEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionKeyRepository @Inject constructor(
    private val encryptionKeyDao: EncryptionKeyDao
) {
    suspend fun saveKey(key: EncryptionKeyEntity) {
        encryptionKeyDao.insert(key)
    }

    suspend fun updateKey(key: EncryptionKeyEntity) {
        encryptionKeyDao.update(key)
    }

    suspend fun getLatestKey(userId: String, purpose: String): EncryptionKeyEntity? {
        return encryptionKeyDao.getLatestForPurpose(userId, purpose)
    }

    fun getAllKeys(userId: String): Flow<List<EncryptionKeyEntity>> {
        return encryptionKeyDao.getAllForUser(userId)
    }
}