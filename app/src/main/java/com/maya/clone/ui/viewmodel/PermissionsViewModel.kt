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
class PermissionsViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _cameraPermission = MutableStateFlow<Boolean>(false)
    val cameraPermission = _cameraPermission.asStateFlow()

    private val _microphonePermission = MutableStateFlow<Boolean>(false)
    val microphonePermission = _microphonePermission.asStateFlow()

    private val _locationPermission = MutableStateFlow<Boolean>(false)
    val locationPermission = _locationPermission.asStateFlow()

    private val _storagePermission = MutableStateFlow<Boolean>(false)
    val storagePermission = _storagePermission.asStateFlow()

    private val _contactsPermission = MutableStateFlow<Boolean>(false)
    val contactsPermission = _contactsPermission.asStateFlow()

    private val _phonePermission = MutableStateFlow<Boolean>(false)
    val phonePermission = _phonePermission.asStateFlow()

    private val _smsPermission = MutableStateFlow<Boolean>(false)
    val smsPermission = _smsPermission.asStateFlow()

    private val _notificationPermission = MutableStateFlow<Boolean>(false)
    val notificationPermission = _notificationPermission.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "perm_camera")?.let { s -> _cameraPermission.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "perm_microphone")?.let { s -> _microphonePermission.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "perm_location")?.let { s -> _locationPermission.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "perm_storage")?.let { s -> _storagePermission.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "perm_contacts")?.let { s -> _contactsPermission.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "perm_phone")?.let { s -> _phonePermission.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "perm_sms")?.let { s -> _smsPermission.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "perm_notifications")?.let { s -> _notificationPermission.value = s.valueJson.trim('"').toBoolean() }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setCameraPermission(granted: Boolean) { _cameraPermission.value = granted; requestPermissionIfNeeded("camera", granted) }
    fun setMicrophonePermission(granted: Boolean) { _microphonePermission.value = granted; requestPermissionIfNeeded("microphone", granted) }
    fun setLocationPermission(granted: Boolean) { _locationPermission.value = granted; requestPermissionIfNeeded("location", granted) }
    fun setStoragePermission(granted: Boolean) { _storagePermission.value = granted; requestPermissionIfNeeded("storage", granted) }
    fun setContactsPermission(granted: Boolean) { _contactsPermission.value = granted; requestPermissionIfNeeded("contacts", granted) }
    fun setPhonePermission(granted: Boolean) { _phonePermission.value = granted; requestPermissionIfNeeded("phone", granted) }
    fun setSmsPermission(granted: Boolean) { _smsPermission.value = granted; requestPermissionIfNeeded("sms", granted) }
    fun setNotificationPermission(granted: Boolean) { _notificationPermission.value = granted; requestPermissionIfNeeded("notifications", granted) }

    private fun requestPermissionIfNeeded(permission: String, granted: Boolean) {
        if (granted) {
            // In real app: startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")))
        }
    }
}