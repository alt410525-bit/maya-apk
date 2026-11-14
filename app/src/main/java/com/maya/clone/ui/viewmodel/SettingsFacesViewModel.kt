package com.maya.clone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.FaceProfileEntity
import com.maya.clone.data.entity.SettingEntity
import com.maya.clone.data.repository.FaceProfileRepository
import com.maya.clone.data.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsFacesViewModel @Inject constructor(
    private val faceProfileRepository: FaceProfileRepository,
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _faceProfiles = MutableStateFlow<List<FaceProfileEntity>>(emptyList())
    val faceProfiles = _faceProfiles.asStateFlow()

    private val _isEnrolling = MutableStateFlow<Boolean>(false)
    val isEnrolling = _isEnrolling.asStateFlow()

    private val _enrollProgress = MutableStateFlow<Int>(0)
    val enrollProgress = _enrollProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadProfiles(userId)
    }

    private fun loadProfiles(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                faceProfileRepository.getProfilesForUser(userId)
                    .collect { profiles ->
                        _faceProfiles.value = profiles
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun deleteFaceProfile(profileId: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                faceProfileRepository.deleteProfile(profileId)
                loadProfiles(userId)
            }
        }
    }
}