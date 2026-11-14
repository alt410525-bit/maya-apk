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
class SettingsSOSViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _emergencyContacts = MutableStateFlow<List<String>>(emptyList())
    val emergencyContacts = _emergencyContacts.asStateFlow()

    private val _autoSendLocation = MutableStateFlow<Boolean>(true)
    val autoSendLocation = _autoSendLocation.asStateFlow()

    private val _autoCallEmergency = MutableStateFlow<Boolean>(false)
    val autoCallEmergency = _autoCallEmergency.asStateFlow()

    private val _messageTemplate = MutableStateFlow<String>("Emergency! I need help. My location: {location}")
    val messageTemplate = _messageTemplate.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "sos_contacts")?.let { s -> _emergencyContacts.value = parseJsonArray(s.valueJson) }
                settingRepository.getSettingByKey(userId, "sos_auto_location")?.let { s -> _autoSendLocation.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "sos_auto_call")?.let { s -> _autoCallEmergency.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "sos_message_template")?.let { s -> _messageTemplate.value = s.valueJson.trim('"') }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addContact(contact: String) {
        val newList = _emergencyContacts.value + contact
        _emergencyContacts.value = newList
        saveJsonArray("sos_contacts", newList)
    }

    fun removeContact(contact: String) {
        val newList = _emergencyContacts.value.filter { it != contact }
        _emergencyContacts.value = newList
        saveJsonArray("sos_contacts", newList)
    }

    fun setAutoSendLocation(enabled: Boolean) { _autoSendLocation.value = enabled; saveBoolean("sos_auto_location", enabled) }
    fun setAutoCallEmergency(enabled: Boolean) { _autoCallEmergency.value = enabled; saveBoolean("sos_auto_call", enabled) }
    fun setMessageTemplate(template: String) { _messageTemplate.value = template; saveString("sos_message_template", template) }

    private fun saveBoolean(key: String, value: Boolean) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
    private fun saveString(key: String, value: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
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