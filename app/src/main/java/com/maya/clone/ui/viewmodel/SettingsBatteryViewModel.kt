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
class SettingsBatteryViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _isOptimized = MutableStateFlow<Boolean>(true)
    val isOptimized = _isOptimized.asStateFlow()

    private val _showExemptionDialog = MutableStateFlow<Boolean>(false)
    val showExemptionDialog = _showExemptionDialog.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "battery_optimized")?.let { s ->
                    _isOptimized.value = s.valueJson.trim('"').toBoolean()
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setOptimized(optimized: Boolean) {
        _isOptimized.value = optimized
        saveBoolean("battery_optimized", optimized)
    }

    fun requestExemption() {
        _showExemptionDialog.value = true
        // In real app: startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:$packageName")))
    }

    fun onExemptionResult(granted: Boolean) {
        _showExemptionDialog.value = false
        if (granted) {
            _isOptimized.value = false
            saveBoolean("battery_optimized", false)
        }
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
}