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
class SettingsWhatsAppViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _isConnected = MutableStateFlow<Boolean>(false)
    val isConnected = _isConnected.asStateFlow()

    private val _phoneNumber = MutableStateFlow<String>("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _groups = MutableStateFlow<List<String>>(emptyList())
    val groups = _groups.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow<Boolean>(true)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "whatsapp_connected")?.let { s ->
                    _isConnected.value = s.valueJson.trim('"').toBoolean()
                }
                settingRepository.getSettingByKey(userId, "whatsapp_phone")?.let { s ->
                    _phoneNumber.value = s.valueJson.trim('"')
                }
                settingRepository.getSettingByKey(userId, "whatsapp_groups")?.let { s ->
                    _groups.value = parseJsonArray(s.valueJson)
                }
                settingRepository.getSettingByKey(userId, "whatsapp_notifications")?.let { s ->
                    _notificationsEnabled.value = s.valueJson.trim('"').toBoolean()
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun connectWhatsApp(phone: String) {
        _phoneNumber.value = phone
        _isConnected.value = true
        saveString("whatsapp_phone", phone)
        saveBoolean("whatsapp_connected", true)
    }

    fun disconnectWhatsApp() {
        _isConnected.value = false
        _groups.value = emptyList()
        saveBoolean("whatsapp_connected", false)
        saveJsonArray("whatsapp_groups", emptyList())
    }

    fun addGroup(groupName: String) {
        val newList = _groups.value + groupName
        _groups.value = newList
        saveJsonArray("whatsapp_groups", newList)
    }

    fun removeGroup(groupName: String) {
        val newList = _groups.value.filter { it != groupName }
        _groups.value = newList
        saveJsonArray("whatsapp_groups", newList)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        saveBoolean("whatsapp_notifications", enabled)
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

    private fun saveString(key: String, value: String) {
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

    private fun saveJsonArray(key: String, list: List<String>) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val entity = SettingEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    key = key,
                    valueJson = toJsonArray(list),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                settingRepository.saveSetting(entity)
            }
        }
    }

    private fun parseJsonArray(json: String): List<String> {
        return try {
            json.trim().let {
                if (it.startsWith("[")) {
                    it.substring(1, it.length - 1)
                        .split(",")
                        .map { it.trim().trim('"') }
                        .filter { it.isNotBlank() }
                } else emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun toJsonArray(list: List<String>): String {
        return "[${list.map { "\"$it\"" }.joinToString(",")}]"
    }
}