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
class SettingsGuardianViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _contentFilter = MutableStateFlow<Boolean>(true)
    val contentFilter = _contentFilter.asStateFlow()

    private val _parentalControls = MutableStateFlow<Boolean>(false)
    val parentalControls = _parentalControls.asStateFlow()

    private val _screenTimeLimit = MutableStateFlow<Int>(120) // minutes
    val screenTimeLimit = _screenTimeLimit.asStateFlow()

    private val _blockedKeywords = MutableStateFlow<List<String>>(emptyList())
    val blockedKeywords = _blockedKeywords.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "guardian_content_filter")?.let { s -> _contentFilter.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "guardian_parental_controls")?.let { s -> _parentalControls.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "guardian_screen_time")?.let { s -> _screenTimeLimit.value = s.valueJson.trim('"').toInt() }
                settingRepository.getSettingByKey(userId, "guardian_blocked_keywords")?.let { s -> _blockedKeywords.value = parseJsonArray(s.valueJson) }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setContentFilter(enabled: Boolean) { _contentFilter.value = enabled; saveBoolean("guardian_content_filter", enabled) }
    fun setParentalControls(enabled: Boolean) { _parentalControls.value = enabled; saveBoolean("guardian_parental_controls", enabled) }
    fun setScreenTimeLimit(minutes: Int) { _screenTimeLimit.value = minutes; saveInt("guardian_screen_time", minutes) }
    fun addBlockedKeyword(keyword: String) {
        val newList = _blockedKeywords.value + keyword
        _blockedKeywords.value = newList
        saveJsonArray("guardian_blocked_keywords", newList)
    }
    fun removeBlockedKeyword(keyword: String) {
        val newList = _blockedKeywords.value.filter { it != keyword }
        _blockedKeywords.value = newList
        saveJsonArray("guardian_blocked_keywords", newList)
    }

    private fun saveBoolean(key: String, value: Boolean) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
    private fun saveInt(key: String, value: Int) { saveBoolean(key, value != 0) }
    private fun saveJsonArray(key: String, list: List<String>) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = toJsonArray(list), createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
    private fun parseJsonArray(json: String): List<String> {
        return try {
            json.trim().let {
                if (it.startsWith("[")) it.substring(1, it.length - 1).split(",").map { it.trim().trim('"') }.filter { it.isNotBlank() }
                else emptyList()
            }
        } catch (e: Exception) { emptyList() }
    }
    private fun toJsonArray(list: List<String>): String = "[${list.map { "\"$it\"" }.joinToString(",")}]"
}