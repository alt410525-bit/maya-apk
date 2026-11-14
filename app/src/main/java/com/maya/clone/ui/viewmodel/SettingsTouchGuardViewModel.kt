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
class SettingsTouchGuardViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _enabled = MutableStateFlow<Boolean>(false)
    val enabled = _enabled.asStateFlow()

    private val _sensitivity = MutableStateFlow<Float>(0.5f)
    val sensitivity = _sensitivity.asStateFlow()

    private val _protectedZones = MutableStateFlow<List<String>>(emptyList())
    val protectedZones = _protectedZones.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "touch_guard_enabled")?.let { s -> _enabled.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "touch_guard_sensitivity")?.let { s -> _sensitivity.value = s.valueJson.trim('"').toFloat() }
                settingRepository.getSettingByKey(userId, "touch_guard_zones")?.let { s -> _protectedZones.value = parseJsonArray(s.valueJson) }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setEnabled(enabled: Boolean) { _enabled.value = enabled; saveBoolean("touch_guard_enabled", enabled) }
    fun setSensitivity(sensitivity: Float) { _sensitivity.value = sensitivity; saveFloat("touch_guard_sensitivity", sensitivity) }
    fun addProtectedZone(zone: String) {
        val newList = _protectedZones.value + zone
        _protectedZones.value = newList
        saveJsonArray("touch_guard_zones", newList)
    }
    fun removeProtectedZone(zone: String) {
        val newList = _protectedZones.value.filter { it != zone }
        _protectedZones.value = newList
        saveJsonArray("touch_guard_zones", newList)
    }

    private fun saveBoolean(key: String, value: Boolean) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
    private fun saveFloat(key: String, value: Float) { saveBoolean(key, value != 0f) }
    private fun saveJsonArray(key: String, list: List<String>) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = toJsonArray(list), createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
    private fun parseJsonArray(json: String): List<String> = try { json.trim().let { if (it.startsWith("[")) it.substring(1, it.length - 1).split(",").map { it.trim().trim('"') }.filter { it.isNotBlank() } else emptyList() } } catch (e: Exception) { emptyList() }
    private fun toJsonArray(list: List<String>): String = "[${list.map { "\"$it\"" }.joinToString(",")}]"
}