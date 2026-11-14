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
class SettingsAccessibilityViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _talkBackEnabled = MutableStateFlow<Boolean>(false)
    val talkBackEnabled = _talkBackEnabled.asStateFlow()

    private val _magnificationEnabled = MutableStateFlow<Boolean>(false)
    val magnificationEnabled = _magnificationEnabled.asStateFlow()

    private val _highContrastEnabled = MutableStateFlow<Boolean>(false)
    val highContrastEnabled = _highContrastEnabled.asStateFlow()

    private val _largeTextEnabled = MutableStateFlow<Boolean>(false)
    val largeTextEnabled = _largeTextEnabled.asStateFlow()

    private val _reduceMotionEnabled = MutableStateFlow<Boolean>(false)
    val reduceMotionEnabled = _reduceMotionEnabled.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "accessibility_talkback")?.let { s -> _talkBackEnabled.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "accessibility_magnification")?.let { s -> _magnificationEnabled.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "accessibility_high_contrast")?.let { s -> _highContrastEnabled.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "accessibility_large_text")?.let { s -> _largeTextEnabled.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "accessibility_reduce_motion")?.let { s -> _reduceMotionEnabled.value = s.valueJson.trim('"').toBoolean() }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setTalkBackEnabled(enabled: Boolean) { _talkBackEnabled.value = enabled; saveBoolean("accessibility_talkback", enabled) }
    fun setMagnificationEnabled(enabled: Boolean) { _magnificationEnabled.value = enabled; saveBoolean("accessibility_magnification", enabled) }
    fun setHighContrastEnabled(enabled: Boolean) { _highContrastEnabled.value = enabled; saveBoolean("accessibility_high_contrast", enabled) }
    fun setLargeTextEnabled(enabled: Boolean) { _largeTextEnabled.value = enabled; saveBoolean("accessibility_large_text", enabled) }
    fun setReduceMotionEnabled(enabled: Boolean) { _reduceMotionEnabled.value = enabled; saveBoolean("accessibility_reduce_motion", enabled) }

    private fun saveBoolean(key: String, value: Boolean) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
}