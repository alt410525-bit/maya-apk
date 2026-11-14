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
class SettingsPersonalViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _displayName = MutableStateFlow<String>("")
    val displayName = _displayName.asStateFlow()

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl = _avatarUrl.asStateFlow()

    private val _email = MutableStateFlow<String>("")
    val email = _email.asStateFlow()

    private val _age = MutableStateFlow<Int?>(null)
    val age = _age.asStateFlow()

    private val _gender = MutableStateFlow<String>("")
    val gender = _gender.asStateFlow()

    private val _timezone = MutableStateFlow<String>("UTC")
    val timezone = _timezone.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "personal_display_name")?.let { s ->
                    _displayName.value = s.valueJson.trim('"')
                }
                settingRepository.getSettingByKey(userId, "personal_avatar_url")?.let { s ->
                    _avatarUrl.value = s.valueJson.trim('"').takeIf { it != "null" }
                }
                settingRepository.getSettingByKey(userId, "personal_email")?.let { s ->
                    _email.value = s.valueJson.trim('"')
                }
                settingRepository.getSettingByKey(userId, "personal_age")?.let { s ->
                    _age.value = s.valueJson.trim('"').toIntOrNull()
                }
                settingRepository.getSettingByKey(userId, "personal_gender")?.let { s ->
                    _gender.value = s.valueJson.trim('"')
                }
                settingRepository.getSettingByKey(userId, "personal_timezone")?.let { s ->
                    _timezone.value = s.valueJson.trim('"')
                }
            } catch (e: Exception) {
                // Use defaults
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveDisplayName(name: String) {
        _displayName.value = name
        saveSetting("personal_display_name", "\"$name\"")
    }

    fun saveAvatarUrl(url: String?) {
        _avatarUrl.value = url
        saveSetting("personal_avatar_url", url?.let { "\"$it\"" } ?: "null")
    }

    fun saveEmail(email: String) {
        _email.value = email
        saveSetting("personal_email", "\"$email\"")
    }

    fun saveAge(age: Int?) {
        _age.value = age
        age?.let { saveSetting("personal_age", "\"$it\"") } ?: saveSetting("personal_age", "null")
    }

    fun saveGender(gender: String) {
        _gender.value = gender
        saveSetting("personal_gender", "\"$gender\"")
    }

    fun saveTimezone(timezone: String) {
        _timezone.value = timezone
        saveSetting("personal_timezone", "\"$timezone\"")
    }

    private fun saveSetting(key: String, valueJson: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val entity = SettingEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    key = key,
                    valueJson = valueJson,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                settingRepository.saveSetting(entity)
            }
        }
    }
}