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
class SettingsSocialViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _shareOnTwitter = MutableStateFlow<Boolean>(false)
    val shareOnTwitter = _shareOnTwitter.asStateFlow()

    private val _shareOnFacebook = MutableStateFlow<Boolean>(false)
    val shareOnFacebook = _shareOnFacebook.asStateFlow()

    private val _shareOnInstagram = MutableStateFlow<Boolean>(false)
    val shareOnInstagram = _shareOnInstagram.asStateFlow()

    private val _referralCode = MutableStateFlow<String>("MAYA-REF-001")
    val referralCode = _referralCode.asStateFlow()

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
                settingRepository.getSettingByKey(userId, "social_twitter")?.let { s -> _shareOnTwitter.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "social_facebook")?.let { s -> _shareOnFacebook.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "social_instagram")?.let { s -> _shareOnInstagram.value = s.valueJson.trim('"').toBoolean() }
                settingRepository.getSettingByKey(userId, "social_referral_code")?.let { s -> _referralCode.value = s.valueJson.trim('"') }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setShareOnTwitter(enabled: Boolean) { _shareOnTwitter.value = enabled; saveBoolean("social_twitter", enabled) }
    fun setShareOnFacebook(enabled: Boolean) { _shareOnFacebook.value = enabled; saveBoolean("social_facebook", enabled) }
    fun setShareOnInstagram(enabled: Boolean) { _shareOnInstagram.value = enabled; saveBoolean("social_instagram", enabled) }

    private fun saveBoolean(key: String, value: Boolean) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                settingRepository.saveSetting(SettingEntity(id = java.util.UUID.randomUUID().toString(), userId = userId, key = key, valueJson = "\"$value\"", createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()))
            }
        }
    }
}