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
class KeySetupViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _encryptionKey = MutableStateFlow<String>("")
    val encryptionKey = _encryptionKey.asStateFlow()

    private val _keyRotationInterval = MutableStateFlow<Int>(90) // days
    val keyRotationInterval = _keyRotationInterval.asStateFlow()

    private val _autoRotate = MutableStateFlow<Boolean>(true)
    val autoRotate = _autoRotate.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "key_setup_encryption_key")?.let { s -> _encryptionKey.value = s.valueJson.trim('"') }
                settingRepository.getSettingByKey(userId, "key_setup_rotation_interval")?.let { s -> _keyRotationInterval.value = s.valueJson.trim('"').toInt() }
                settingRepository.getSettingByKey(userId, "key_setup_auto_rotate")?.let { s -> _autoRotate.value = s.valueJson.trim('"').toBoolean() }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setEncryptionKey(key: String) { _encryptionKey.value = key; saveString("key_setup_encryption_key", key) }
    fun setKeyRotationInterval(days: Int) { _keyRotationInterval.value = days; saveInt("key_setup_rotation_interval", days) }
    fun setAutoRotate(enabled: Boolean) { _autoRotate.value = enabled; saveBoolean("key_setup_auto_rotate", enabled) }

    fun generateNewKey() {
        // Generate a new 256-bit encryption key
        val secureRandom = java.security.SecureRandom()
        val keyBytes = ByteArray(32)
        secureRandom.nextBytes(keyBytes)
        val newKey = android.util.Base64.encodeToString(keyBytes, android.util.Base64.NO_WRAP)
        setEncryptionKey(newKey)
    }

    private fun saveString(key: String, value: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
    private fun saveBoolean(key: String, value: Boolean) { saveString(key, value.toString()) }
    private fun saveInt(key: String, value: Int) { saveString(key, value.toString()) }
}