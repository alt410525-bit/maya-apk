package com.maya.clone.ui.viewmodel

import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
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
class SettingsFacesCaptureViewModel @Inject constructor(
    private val faceProfileRepository: FaceProfileRepository,
    private val settingRepository: SettingRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var faceDetector: FaceDetector? = null

    private val _isEnrolling = MutableStateFlow<Boolean>(false)
    val isEnrolling = _isEnrolling.asStateFlow()

    private val _enrollProgress = MutableStateFlow<Int>(0)
    val enrollProgress = _enrollProgress.asStateFlow()

    private val _enrollMessage = MutableStateFlow<String>("Position your face in the frame")
    val enrollMessage = _enrollMessage.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null
    private var currentFaceName: String? = null

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
    }

    fun startEnrollment(faceName: String) {
        _isEnrolling.value = true
        _enrollProgress.value = 0
        _enrollMessage.value = "Position your face in the frame"
        currentFaceName = faceName

        viewModelScope.launch {
            // Simulate enrollment with camera frames
            for (i in 1..10) {
                _enrollProgress.value = i * 10
                when (i) {
                    1 -> _enrollMessage.value = "Look straight ahead"
                    3 -> _enrollMessage.value = "Turn head slightly left"
                    5 -> _enrollMessage.value = "Turn head slightly right"
                    7 -> _enrollMessage.value = "Look up slightly"
                    9 -> _enrollMessage.value = "Almost done..."
                }
                kotlinx.coroutines.delay(800)
            }
            // Simulate successful enrollment
            val profile = FaceProfileEntity(
                id = java.util.UUID.randomUUID().toString(),
                userId = currentUserId ?: "default_user",
                name = currentFaceName ?: "My Face",
                embedding = ByteArray(512), // Placeholder
                photoUrlsJson = "[]",
                qualityScore = 0.9f
            )
            faceProfileRepository.saveProfile(profile)
            _isEnrolling.value = false
            _enrollProgress.value = 100
            _enrollMessage.value = "Face registered successfully!"
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