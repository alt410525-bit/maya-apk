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
class SettingsScreenLockViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _lockType = MutableStateFlow<String>("pin")
    val lockType = _lockType.asStateFlow()

    private val _pin = MutableStateFlow<String>("")
    val pin = _pin.asStateFlow()

    private val _autoLockDelay = MutableStateFlow<Int>(5) // minutes
    val autoLockDelay = _autoLockDelay.asStateFlow()

    private val _showLockScreen = MutableStateFlow<Boolean>(true)
    val showLockScreen = _showLockScreen.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "screen_lock_type")?.let { s -> _lockType.value = s.valueJson.trim('"') }
                settingRepository.getSettingByKey(userId, "screen_lock_pin")?.let { s -> _pin.value = s.valueJson.trim('"') }
                settingRepository.getSettingByKey(userId, "screen_lock_auto_delay")?.let { s -> _autoLockDelay.value = s.valueJson.trim('"').toInt() }
                settingRepository.getSettingByKey(userId, "screen_lock_show")?.let { s -> _showLockScreen.value = s.valueJson.trim('"').toBoolean() }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setLockType(type: String) { _lockType.value = type; saveString("screen_lock_type", type) }
    fun setPin(pin: String) { _pin.value = pin; saveString("screen_lock_pin", pin) }
    fun setAutoLockDelay(minutes: Int) { _autoLockDelay.value = minutes; saveInt("screen_lock_auto_delay", minutes) }
    fun setShowLockScreen(show: Boolean) { _showLockScreen.value = show; saveBoolean("screen_lock_show", show) }

    private fun saveString(key: String, value: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
    private fun saveBoolean(key: String, value: Boolean) { saveString(key, value.toString()) }
    private fun saveInt(key: String, value: Int) { saveString(key, value.toString()) }
}