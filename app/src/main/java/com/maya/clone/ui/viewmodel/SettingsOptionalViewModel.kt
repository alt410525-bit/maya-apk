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
class SettingsOptionalViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _features = MutableStateFlow<Map<String, Boolean>>(mapOf(
        "voice_recognition" to true,
        "face_unlock" to false,
        "auto_backup" to true,
        "cloud_sync" to false,
        "beta_features" to false,
        "analytics" to true,
        "crash_reporting" to true,
        "personalization" to true,
    ))
    val features = _features.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "optional_features")?.let { s ->
                    _features.value = parseJsonMap(s.valueJson)
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFeature(featureId: String) {
        val newMap = _features.value.toMutableMap().also { it[featureId] = !it.getOrDefault(featureId, false) }
        _features.value = newMap
        saveFeatures(newMap)
    }

    private fun saveFeatures(map: Map<String, Boolean>) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val entity = SettingEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    key = "optional_features",
                    valueJson = toJsonMap(map),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                settingRepository.saveSetting(entity)
            }
        }
    }

    private fun parseJsonMap(json: String): Map<String, Boolean> {
        return try {
            json.trim().let {
                if (it.startsWith("{")) {
                    it.substring(1, it.length - 1)
                        .split(",")
                        .map { it.trim() }
                        .associate { pair ->
                            val parts = pair.split(":")
                            parts[0].trim().trim('"') to parts[1].trim().toBoolean()
                        }
                } else emptyMap()
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun toJsonMap(map: Map<String, Boolean>): String {
        return "{${map.map { "\"${it.key}\":${it.value}" }.joinToString(",")}}"
    }
}