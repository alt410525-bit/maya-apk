package com.maya.clone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.SettingEntity
import com.maya.clone.data.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsThemeViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _selectedTheme = MutableStateFlow<String>("dark")
    val selectedTheme = _selectedTheme.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadSettings(userId)
    }

    private fun loadSettings(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                settingRepository.getSettingByKey(userId, "theme_selected")?.let { s ->
                    _selectedTheme.value = s.valueJson.trim('"')
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setTheme(theme: String) {
        _selectedTheme.value = theme
        saveString("theme_selected", theme)
    }

    private fun saveString(key: String, value: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
}