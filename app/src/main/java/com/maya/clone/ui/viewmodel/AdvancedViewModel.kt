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
class AdvancedViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _debugMode = MutableStateFlow<Boolean>(false)
    val debugMode = _debugMode.asStateFlow()

    private val _showFps = MutableStateFlow<Boolean>(false)
    val showFps = _showFps.asStateFlow()

    private val _logLevel = MutableStateFlow<String>("info")
    val logLevel = _logLevel.asStateFlow()

    private val _experimentalFeatures = MutableStateFlow<Boolean>(false)
    val experimentalFeatures = _experimentalFeatures.asStateFlow()

    private val _networkLogging = MutableStateFlow<Boolean>(false)
    val networkLogging = _networkLogging.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "advanced_debug_mode")?.let { s -> _debugMode.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "advanced_show_fps")?.let { s -> _showFps.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "advanced_log_level")?.let { s -> _logLevel.value = s.valueJson.trim('"') }
                settingRepository.getSettingByKey(userId, "advanced_experimental")?.let { s -> _experimentalFeatures.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "advanced_network_logging")?.let { s -> _networkLogging.value = s.valueJson.trim('"').toBoolean() }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setDebugMode(enabled: Boolean) { _debugMode.value = enabled; saveBoolean("advanced_debug_mode", enabled) }
    fun setShowFps(enabled: Boolean) { _showFps.value = enabled; saveBoolean("advanced_show_fps", enabled) }
    fun setLogLevel(level: String) { _logLevel.value = level; saveString("advanced_log_level", level) }
    fun setExperimentalFeatures(enabled: Boolean) { _experimentalFeatures.value = enabled; saveBoolean("advanced_experimental", enabled) }
    fun setNetworkLogging(enabled: Boolean) { _networkLogging.value = enabled; saveBoolean("advanced_network_logging", enabled) }

    private fun saveString(key: String, value: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
    private fun saveBoolean(key: String, value: Boolean) { saveString(key, value.toString()) }
}