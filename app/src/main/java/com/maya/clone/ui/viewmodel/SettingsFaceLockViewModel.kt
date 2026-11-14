package com.maya.clone.ui.viewmodel

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.maya.clone.data.entity.FaceProfileEntity
import com.maya.clone.data.entity.FaceLockAttemptEntity
import com.maya.clone.data.repository.FaceProfileRepository
import com.maya.clone.data.repository.FaceLockAttemptRepository
import com.maya.clone.data.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsFaceLockViewModel @Inject constructor(
    private val faceProfileRepository: FaceProfileRepository,
    private val faceLockAttemptRepository: FaceLockAttemptRepository,
    private val settingRepository: SettingRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // ML Kit Face Detector
    private var faceDetector: FaceDetector? = null

    // State
    private val _faceProfiles = MutableStateFlow<List<FaceProfileEntity>>(emptyList())
    val faceProfiles = _faceProfiles.asStateFlow()

    private val _recentAttempts = MutableStateFlow<List<FaceLockAttemptEntity>>(emptyList())
    val recentAttempts = _recentAttempts.asStateFlow()

    private val _failureCount = MutableStateFlow<Int>(0)
    val failureCount = _failureCount.asStateFlow()

    private val _isFaceUnlockEnabled = MutableStateFlow<Boolean>(false)
    val isFaceUnlockEnabled = _isFaceUnlockEnabled.asStateFlow()

    private val _confidenceThreshold = MutableStateFlow<Float>(0.7f)
    val confidenceThreshold = _confidenceThreshold.asStateFlow()

    private val _pinFallbackEnabled = MutableStateFlow<Boolean>(true)
    val pinFallbackEnabled = _pinFallbackEnabled.asStateFlow()

    private val _patternFallbackEnabled = MutableStateFlow<Boolean>(false)
    val patternFallbackEnabled = _patternFallbackEnabled.asStateFlow()

    private val _isEnrolling = MutableStateFlow<Boolean>(false)
    val isEnrolling = _isEnrolling.asStateFlow()

    private val _enrollProgress = MutableStateFlow<Int>(0)
    val enrollProgress = _enrollProgress.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null

    init {
        initializeFaceDetector()
    }

    private fun initializeFaceDetector() {
        try {
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setMinFaceSize(0.15f)
                .enableTracking()
                .build()
            faceDetector = FaceDetection.getClient(options)
        } catch (e: Exception) {
            _error.value = "Face detector init failed: ${e.message}"
        }
    }

    fun init(userId: String) {
        currentUserId = userId
        loadFaceProfiles(userId)
        loadFaceLockAttempts(userId)
        loadSettings(userId)
    }

    private fun loadFaceProfiles(userId: String) {
        viewModelScope.launch {
            faceProfileRepository.getProfilesForUser(userId)
                .collect { profiles ->
                    _faceProfiles.value = profiles
                }
        }
    }

    private fun loadFaceLockAttempts(userId: String) {
        viewModelScope.launch {
            val attempts = faceLockAttemptRepository.getRecentAttempts(userId, 20)
            _recentAttempts.value = attempts
            val failures = faceLockAttemptRepository.countRecentFailures(userId, System.currentTimeMillis() - 86400000)
            _failureCount.value = failures
        }
    }

    private fun loadSettings(userId: String) {
        viewModelScope.launch {
            try {
                settingRepository.getSettingByKey(userId, "face_unlock_enabled")?.let { s ->
                    _isFaceUnlockEnabled.value = s.valueJson.trim('"').toBoolean()
                }
                settingRepository.getSettingByKey(userId, "face_unlock_confidence")?.let { s ->
                    _confidenceThreshold.value = s.valueJson.trim('"').toFloat()
                }
                settingRepository.getSettingByKey(userId, "face_unlock_pin_fallback")?.let { s ->
                    _pinFallbackEnabled.value = s.valueJson.trim('"').toBoolean()
                }
                settingRepository.getSettingByKey(userId, "face_unlock_pattern_fallback")?.let { s ->
                    _patternFallbackEnabled.value = s.valueJson.trim('"').toBoolean()
                }
            } catch (e: Exception) {
                // Use defaults
            }
        }
    }

    fun setFaceUnlockEnabled(enabled: Boolean) {
        _isFaceUnlockEnabled.value = enabled
        saveBooleanSetting("face_unlock_enabled", enabled)
    }

    fun setConfidenceThreshold(threshold: Float) {
        _confidenceThreshold.value = threshold
        saveFloatSetting("face_unlock_confidence", threshold)
    }

    fun setPinFallbackEnabled(enabled: Boolean) {
        _pinFallbackEnabled.value = enabled
        saveBooleanSetting("face_unlock_pin_fallback", enabled)
    }

    fun setPatternFallbackEnabled(enabled: Boolean) {
        _patternFallbackEnabled.value = enabled
        saveBooleanSetting("face_unlock_pattern_fallback", enabled)
    }

    private fun saveBooleanSetting(key: String, value: Boolean) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val entity = com.maya.clone.data.entity.SettingEntity(
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

    private fun saveFloatSetting(key: String, value: Float) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val entity = com.maya.clone.data.entity.SettingEntity(
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

    fun startEnrollment(name: String) {
        _isEnrolling.value = true
        _enrollProgress.value = 0
        // TODO: Implement actual face enrollment using ML Kit face detection
        // This would capture multiple face images and create embeddings
        viewModelScope.launch {
            for (i in 1..10) {
                _enrollProgress.value = i * 10
                delay(200)
            }
            // Simulate enrollment completion
            val profile = FaceProfileEntity(
                id = java.util.UUID.randomUUID().toString(),
                userId = currentUserId ?: "default_user",
                name = name,
                embedding = ByteArray(512), // Placeholder - real implementation uses ML Kit embeddings
                photoUrlsJson = "[]",
                qualityScore = 0.85f
            )
            faceProfileRepository.saveProfile(profile)
            _isEnrolling.value = false
            _enrollProgress.value = 100
            loadFaceProfiles(currentUserId ?: "default_user")
        }
    }

    fun deleteFaceProfile(profileId: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                faceProfileRepository.deleteProfile(profileId)
                loadFaceProfiles(userId)
            }
        }
    }

    fun verifyFace(imageUri: String): Boolean {
        // TODO: Implement actual face verification using ML Kit
        // Compare face embedding from image with stored profiles
        faceDetector?.let { detector ->
            // detector.process(inputImage).addOnSuccessListener { faces ->
            //     // Compare embeddings
            // }
        }
        return false // Placeholder
    }

    fun recordAttempt(success: Boolean, confidence: Float?, fallbackUsed: Boolean = false) {
        currentUserId?.let { userId ->
            val attempt = FaceLockAttemptEntity(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                success = success,
                confidence = confidence,
                fallbackUsed = fallbackUsed
            )
            viewModelScope.launch {
                faceLockAttemptRepository.recordAttempt(attempt)
                loadFaceLockAttempts(userId)
            }
        }
    }

    fun getFaceDetector(): FaceDetector? {
        return faceDetector
    }

    override fun onCleared() {
        super.onCleared()
        faceDetector?.close()
    }
}