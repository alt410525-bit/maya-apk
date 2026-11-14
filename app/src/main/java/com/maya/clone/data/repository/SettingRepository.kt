package com.maya.clone.data.repository

import com.maya.clone.data.dao.SettingDao
import com.maya.clone.data.entity.SettingEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    private val settingDao: SettingDao
) {
    suspend fun saveSetting(setting: SettingEntity) {
        settingDao.insert(setting)
    }

    suspend fun updateSetting(setting: SettingEntity) {
        settingDao.update(setting)
    }

    suspend fun deleteSetting(id: String) {
        settingDao.deleteById(id)
    }

    suspend fun getSetting(id: String): SettingEntity? {
        return settingDao.getById(id)
    }

    suspend fun getSettingByKey(userId: String, key: String): SettingEntity? {
        return settingDao.getByKey(userId, key)
    }

    fun getAllSettings(userId: String): Flow<List<SettingEntity>> {
        return settingDao.getAllForUser(userId)
    }
}