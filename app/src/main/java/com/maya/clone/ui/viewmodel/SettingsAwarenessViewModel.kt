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
class SettingsAwarenessViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _enabled = MutableStateFlow<Boolean>(false)
    val enabled = _enabled.asStateFlow()

    private val _locationEnabled = MutableStateFlow<Boolean>(true)
    val locationEnabled = _locationEnabled.asStateFlow()

    private val _sensorEnabled = MutableStateFlow<Boolean>(true)
    val sensorEnabled = _sensorEnabled.asStateFlow()

    private val _appUsageEnabled = MutableStateFlow<Boolean>(true)
    val appUsageEnabled = _appUsageEnabled.asStateFlow()

    private val _notificationEnabled = MutableStateFlow<Boolean>(true)
    val notificationEnabled = _notificationEnabled.asStateFlow()

    private val _callEnabled = MutableStateFlow<Boolean>(true)
    val callEnabled = _callEnabled.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "awareness_enabled")?.let { s -> _enabled.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "awareness_location")?.let { s -> _locationEnabled.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "awareness_sensor")?.let { s -> _sensorEnabled.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "awareness_app_usage")?.let { s -> _appUsageEnabled.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "awareness_notification")?.let { s -> _notificationEnabled.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "awareness_call")?.let { s -> _callEnabled.value = s.valueJson.trim('"').toBoolean() }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setEnabled(enabled: Boolean) { _enabled.value = enabled; saveBoolean("awareness_enabled", enabled) }
    fun setLocationEnabled(enabled: Boolean) { _locationEnabled.value = enabled; saveBoolean("awareness_location", enabled) }
    fun setSensorEnabled(enabled: Boolean) { _sensorEnabled.value = enabled; saveBoolean("awareness_sensor", enabled) }
    fun setAppUsageEnabled(enabled: Boolean) { _appUsageEnabled.value = enabled; saveBoolean("awareness_app_usage", enabled) }
    fun setNotificationEnabled(enabled: Boolean) { _notificationEnabled.value = enabled; saveBoolean("awareness_notification", enabled) }
    fun setCallEnabled(enabled: Boolean) { _callEnabled.value = enabled; saveBoolean("awareness_call", enabled) }

    private fun saveBoolean(key: String, value: Boolean) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
}