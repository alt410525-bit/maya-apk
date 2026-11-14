package com.maya.clone.data.repository

import com.maya.clone.data.dao.ThemeDao
import com.maya.clone.data.entity.ThemeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepository @Inject constructor(
    private val themeDao: ThemeDao
) {
    suspend fun saveTheme(theme: ThemeEntity) {
        themeDao.insert(theme)
    }

    suspend fun updateTheme(theme: ThemeEntity) {
        themeDao.update(theme)
    }

    suspend fun deleteTheme(id: String) {
        themeDao.deleteById(id)
    }

    suspend fun getTheme(id: String): ThemeEntity? {
        return themeDao.getById(id)
    }

    fun getAllThemes(userId: String): Flow<List<ThemeEntity>> {
        return themeDao.getAllForUser(userId)
    }

    suspend fun getActiveTheme(userId: String): ThemeEntity? {
        return themeDao.getActiveForUser(userId)
    }

    suspend fun setActiveTheme(userId: String, themeId: String) {
        themeDao.deactivateAllForUser(userId)
        getTheme(themeId)?.let { theme ->
            themeDao.update(theme.copy(isActive = true, updatedAt = System.currentTimeMillis()))
        }
    }
}