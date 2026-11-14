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
class SettingsFaceAttemptsViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _maxAttempts = MutableStateFlow<Int>(5)
    val maxAttempts = _maxAttempts.asStateFlow()

    private val _lockoutDuration = MutableStateFlow<Int>(30) // minutes
    val lockoutDuration = _lockoutDuration.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "face_attempts_max")?.let { s -> _maxAttempts.value = s.valueJson.trim('"').toInt() }
                settingRepository.getSettingByKey(userId, "face_attempts_lockout")?.let { s -> _lockoutDuration.value = s.valueJson.trim('"').toInt() }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setMaxAttempts(attempts: Int) { _maxAttempts.value = attempts; saveInt("face_attempts_max", attempts) }
    fun setLockoutDuration(minutes: Int) { _lockoutDuration.value = minutes; saveInt("face_attempts_lockout", minutes) }

    private fun saveInt(key: String, value: Int) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = value.toString(), createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
}