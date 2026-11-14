package com.maya.clone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.ThemeEntity
import com.maya.clone.data.repository.ThemeRepository
import com.maya.clone.data.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsAppearanceViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _themes = MutableStateFlow<List<ThemeEntity>>(emptyList())
    val themes = _themes.asStateFlow()

    private val _activeTheme = MutableStateFlow<ThemeEntity?>(null)
    val activeTheme = _activeTheme.asStateFlow()

    private val _fontScale = MutableStateFlow<Float>(1.0f)
    val fontScale = _fontScale.asStateFlow()

    private val _animationScale = MutableStateFlow<Float>(1.0f)
    val animationScale = _animationScale.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadThemes(userId)
        loadSettings(userId)
    }

    private fun loadThemes(userId: String) {
        viewModelScope.launch {
            themeRepository.getAllThemes(userId)
                .collect { themes ->
                    _themes.value = themes
                }
            themeRepository.getActiveTheme(userId)?.let { theme ->
                _activeTheme.value = theme
            }
        }
    }

    private fun loadSettings(userId: String) {
        viewModelScope.launch {
            settingRepository.getSettingByKey(userId, "font_scale")?.let { setting ->
                try {
                    _fontScale.value = setting.valueJson.trim('"').toFloat()
                } catch (e: Exception) {
                    _fontScale.value = 1.0f
                }
            }
            settingRepository.getSettingByKey(userId, "animation_scale")?.let { setting ->
                try {
                    _animationScale.value = setting.valueJson.trim('"').toFloat()
                } catch (e: Exception) {
                    _animationScale.value = 1.0f
                }
            }
        }
    }

    fun setActiveTheme(themeId: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                themeRepository.setActiveTheme(userId, themeId)
                themeRepository.getTheme(themeId)?.let { theme ->
                    _activeTheme.value = theme
                }
            }
        }
    }

    fun saveFontScale(scale: Float) {
        _fontScale.value = scale
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val entity = com.maya.clone.data.entity.SettingEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    key = "font_scale",
                    valueJson = "\"$scale\"",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                settingRepository.saveSetting(entity)
            }
        }
    }

    fun saveAnimationScale(scale: Float) {
        _animationScale.value = scale
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val entity = com.maya.clone.data.entity.SettingEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    key = "animation_scale",
                    valueJson = "\"$scale\"",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                settingRepository.saveSetting(entity)
            }
        }
    }

    fun createCustomTheme(name: String, colorsJson: String, typographyJson: String, spacingJson: String, shapeJson: String) {
        currentUserId?.let { userId ->
            val theme = ThemeEntity(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                name = name,
                isCustom = true,
                colorsJson = colorsJson,
                typographyJson = typographyJson,
                spacingJson = spacingJson,
                shapeJson = shapeJson,
                isActive = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            viewModelScope.launch {
                themeRepository.saveTheme(theme)
            }
        }
    }
}