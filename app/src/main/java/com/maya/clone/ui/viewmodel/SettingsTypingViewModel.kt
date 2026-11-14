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
class SettingsTypingViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _showTypingIndicator = MutableStateFlow<Boolean>(true)
    val showTypingIndicator = _showTypingIndicator.asStateFlow()

    private val _keyboardSounds = MutableStateFlow<Boolean>(true)
    val keyboardSounds = _keyboardSounds.asStateFlow()

    private val _autoCapitalize = MutableStateFlow<Boolean>(true)
    val autoCapitalize = _autoCapitalize.asStateFlow()

    private val _autoCorrect = MutableStateFlow<Boolean>(true)
    val autoCorrect = _autoCorrect.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "typing_indicator")?.let { s -> _showTypingIndicator.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "typing_keyboard_sounds")?.let { s -> _keyboardSounds.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "typing_auto_capitalize")?.let { s -> _autoCapitalize.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "typing_auto_correct")?.let { s -> _autoCorrect.value = s.valueJson.trim('"').toBoolean() }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setShowTypingIndicator(enabled: Boolean) { _showTypingIndicator.value = enabled; saveBoolean("typing_indicator", enabled) }
    fun setKeyboardSounds(enabled: Boolean) { _keyboardSounds.value = enabled; saveBoolean("typing_keyboard_sounds", enabled) }
    fun setAutoCapitalize(enabled: Boolean) { _autoCapitalize.value = enabled; saveBoolean("typing_auto_capitalize", enabled) }
    fun setAutoCorrect(enabled: Boolean) { _autoCorrect.value = enabled; saveBoolean("typing_auto_correct", enabled) }

    private fun saveBoolean(key: String, value: Boolean) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
}