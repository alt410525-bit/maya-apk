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
class SettingsBehaviourViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _alwaysListening = MutableStateFlow<Boolean>(true)
    val alwaysListening = _alwaysListening.asStateFlow()

    private val _interruptible = MutableStateFlow<Boolean>(true)
    val interruptible = _interruptible.asStateFlow()

    private val _autoAwaken = MutableStateFlow<Boolean>(false)
    val autoAwaken = _autoAwaken.asStateFlow()

    private val _memoryLimit = MutableStateFlow<Int>(75)
    val memoryLimit = _memoryLimit.asStateFlow()

    private val _forgetAfterDays = MutableStateFlow<Int>(30)
    val forgetAfterDays = _forgetAfterDays.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "behaviour_always_listening")?.let { s -> _alwaysListening.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "behaviour_interruptible")?.let { s -> _interruptible.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "behaviour_auto_awaken")?.let { s -> _autoAwaken.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "behaviour_memory_limit")?.let { s -> _memoryLimit.value = s.valueJson.trim('"').toInt() }
                settingRepository.getSettingByKey(userId, "behaviour_forget_after")?.let { s -> _forgetAfterDays.value = s.valueJson.trim('"').toInt() }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setAlwaysListening(enabled: Boolean) { _alwaysListening.value = enabled; saveBoolean("behaviour_always_listening", enabled) }
    fun setInterruptible(enabled: Boolean) { _interruptible.value = enabled; saveBoolean("behaviour_interruptible", enabled) }
    fun setAutoAwaken(enabled: Boolean) { _autoAwaken.value = enabled; saveBoolean("behaviour_auto_awaken", enabled) }
    fun setMemoryLimit(limit: Int) { _memoryLimit.value = limit; saveInt("behaviour_memory_limit", limit) }
    fun setForgetAfterDays(days: Int) { _forgetAfterDays.value = days; saveInt("behaviour_forget_after", days) }

    private fun saveBoolean(key: String, value: Boolean) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
    private fun saveInt(key: String, value: Int) { saveBoolean(key, value != 0) } // simplified
}