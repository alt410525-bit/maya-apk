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
class SettingsEmailViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _isEnabled = MutableStateFlow<Boolean>(false)
    val isEnabled = _isEnabled.asStateFlow()

    private val _emailAddress = MutableStateFlow<String>("")
    val emailAddress = _emailAddress.asStateFlow()

    private val _syncFrequency = MutableStateFlow<String>("15min")
    val syncFrequency = _syncFrequency.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow<Boolean>(true)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadSettings(userId)
    }

    private fun loadSettings(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                settingRepository.getSettingByKey(userId, "email_enabled")?.let { s ->
                    _isEnabled.value = s.valueJson.trim('"').toBoolean()
                }
                settingRepository.getSettingByKey(userId, "email_address")?.let { s ->
                    _emailAddress.value = s.valueJson.trim('"')
                }
                settingRepository.getSettingByKey(userId, "email_sync_frequency")?.let { s ->
                    _syncFrequency.value = s.valueJson.trim('"')
                }
                settingRepository.getSettingByKey(userId, "email_notifications")?.let { s ->
                    _notificationsEnabled.value = s.valueJson.trim('"').toBoolean()
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
        saveBoolean("email_enabled", enabled)
    }

    fun setEmailAddress(address: String) {
        _emailAddress.value = address
        saveString("email_address", address)
    }

    fun setSyncFrequency(frequency: String) {
        _syncFrequency.value = frequency
        saveString("email_sync_frequency", frequency)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        saveBoolean("email_notifications", enabled)
    }

    private fun saveBoolean(key: String, value: Boolean) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val entity = SettingEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    key = key,
                    valueJson = "\"$value\"",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                settingRepository.saveSetting(entity)
            }
        }
    }

    private fun saveString(key: String, value: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val entity = SettingEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    key = key,
                    valueJson = "\"$value\"",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                settingRepository.saveSetting(entity)
            }
        }
    }
}