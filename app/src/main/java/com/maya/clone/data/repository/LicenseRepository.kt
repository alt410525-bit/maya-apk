package com.maya.clone.data.repository

import com.maya.clone.data.dao.LicenseDao
import com.maya.clone.data.entity.LicenseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LicenseRepository @Inject constructor(
    private val licenseDao: LicenseDao
) {
    suspend fun saveLicense(license: LicenseEntity) {
        licenseDao.insert(license)
    }

    suspend fun updateLicense(license: LicenseEntity) {
        licenseDao.update(license)
    }

    suspend fun getLicense(id: String): LicenseEntity? {
        return licenseDao.getById(id)
    }

    fun getLicensesForUser(userId: String): Flow<List<LicenseEntity>> {
        return licenseDao.getAllForUser(userId)
    }

    suspend fun getLatestLicense(userId: String): LicenseEntity? {
        return licenseDao.getLatestForUser(userId)
    }
}