package com.maya.clone.data.repository

import com.maya.clone.data.dao.FaceProfileDao
import com.maya.clone.data.entity.FaceProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceProfileRepository @Inject constructor(
    private val faceProfileDao: FaceProfileDao
) {
    suspend fun saveProfile(profile: FaceProfileEntity) {
        faceProfileDao.insert(profile)
    }

    suspend fun updateProfile(profile: FaceProfileEntity) {
        faceProfileDao.update(profile)
    }

    suspend fun deleteProfile(id: String) {
        faceProfileDao.deleteById(id)
    }

    suspend fun getProfile(id: String): FaceProfileEntity? {
        return faceProfileDao.getById(id)
    }

    fun getProfilesForUser(userId: String): Flow<List<FaceProfileEntity>> {
        return faceProfileDao.getAllForUser(userId)
    }
}