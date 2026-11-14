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
class SettingsMayaViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _model = MutableStateFlow<String>("gpt-4o-mini")
    val model = _model.asStateFlow()

    private val _temperature = MutableStateFlow<Float>(0.7f)
    val temperature = _temperature.asStateFlow()

    private val _maxTokens = MutableStateFlow<Int>(2048)
    val maxTokens = _maxTokens.asStateFlow()

    private val _systemPrompt = MutableStateFlow<String>("")
    val systemPrompt = _systemPrompt.asStateFlow()

    private val _toolsJson = MutableStateFlow<String>("[]")
    val toolsJson = _toolsJson.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "maya_model")?.let { s -> _model.value = s.valueJson.trim('"') }
                settingRepository.getSettingByKey(userId, "maya_temperature")?.let { s -> _temperature.value = s.valueJson.trim('"').toFloat() }
                settingRepository.getSettingByKey(userId, "maya_max_tokens")?.let { s -> _maxTokens.value = s.valueJson.trim('"').toInt() }
                settingRepository.getSettingByKey(userId, "maya_system_prompt")?.let { s -> _systemPrompt.value = s.valueJson.trim('"') }
                settingRepository.getSettingByKey(userId, "maya_tools")?.let { s -> _toolsJson.value = s.valueJson }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setModel(model: String) { _model.value = model; saveString("maya_model", model) }
    fun setTemperature(temp: Float) { _temperature.value = temp; saveFloat("maya_temperature", temp) }
    fun setMaxTokens(tokens: Int) { _maxTokens.value = tokens; saveInt("maya_max_tokens", tokens) }
    fun setSystemPrompt(prompt: String) { _systemPrompt.value = prompt; saveString("maya_system_prompt", prompt) }
    fun setToolsJson(json: String) { _toolsJson.value = json; saveString("maya_tools", json) }

    private fun saveString(key: String, value: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
    private fun saveFloat(key: String, value: Float) { saveString(key, value.toString()) }
    private fun saveInt(key: String, value: Int) { saveString(key, value.toString()) }
}