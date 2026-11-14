package com.maya.clone.data.repository

import com.maya.clone.data.dao.ApiKeyDao
import com.maya.clone.data.entity.ApiKeyEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRepository @Inject constructor(
    private val apiKeyDao: ApiKeyDao
) {
    suspend fun saveApiKey(apiKey: ApiKeyEntity) {
        apiKeyDao.insert(apiKey)
    }

    suspend fun updateApiKey(apiKey: ApiKeyEntity) {
        apiKeyDao.update(apiKey)
    }

    suspend fun deleteApiKey(id: String) {
        apiKeyDao.deleteById(id)
    }

    suspend fun getApiKey(id: String): ApiKeyEntity? {
        return apiKeyDao.getById(id)
    }

    fun getApiKeysForUser(userId: String): Flow<List<ApiKeyEntity>> {
        return apiKeyDao.getAllForUser(userId)
    }

    fun getApiKeysByProvider(userId: String, provider: String): Flow<List<ApiKeyEntity>> {
        return apiKeyDao.getByProvider(userId, provider)
    }
}