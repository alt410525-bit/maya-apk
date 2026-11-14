package com.maya.clone.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.SettingEntity
import com.maya.clone.data.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

/**
 * Voice engine settings. Uses the Android platform engines:
 *  - STT: android.speech.SpeechRecognizer
 *  - TTS: android.speech.tts.TextToSpeech
 *
 * The earlier ML Kit speech and TTS clients do not exist in ML Kit, so they were removed.
 * Stored settings with old engine ids fall back to SYSTEM.
 */
@HiltViewModel
class SettingsVoiceEngineViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    enum class STTEngine(val id: String, val label: String) {
        SYSTEM("system", "System speech recognition")
    }

    enum class TTSEngine(val id: String, val label: String) {
        SYSTEM("system", "System text-to-speech")
    }

    private val defaultUserId = "default_user"

    private val _selectedSTTEngine = MutableStateFlow(STTEngine.SYSTEM)
    val selectedSTTEngine: StateFlow<STTEngine> = _selectedSTTEngine.asStateFlow()

    private val _selectedTTSEngine = MutableStateFlow(TTSEngine.SYSTEM)
    val selectedTTSEngine: StateFlow<TTSEngine> = _selectedTTSEngine.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("en-US")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _availableLanguages = MutableStateFlow(
        listOf("en-US", "es-ES", "fr-FR", "de-DE", "ja-JP", "ko-KR", "zh-CN", "hi-IN")
    )
    val availableLanguages: StateFlow<List<String>> = _availableLanguages.asStateFlow()

    // Language packs are managed by Android itself, so progress is not simulated.
    private val _isDownloadingLanguage = MutableStateFlow(false)
    val isDownloadingLanguage: StateFlow<Boolean> = _isDownloadingLanguage.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    private val _isTestingTTS = MutableStateFlow(false)
    val isTestingTTS: StateFlow<Boolean> = _isTestingTTS.asStateFlow()

    private val _ttsStatus = MutableStateFlow("")
    val ttsStatus: StateFlow<String> = _ttsStatus.asStateFlow()

    private val _sttStatus = MutableStateFlow("")
    val sttStatus: StateFlow<String> = _sttStatus.asStateFlow()

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var recognizer: SpeechRecognizer? = null

    init {
        loadSettings()
        initTts()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                settingRepository.getSettingByKey(defaultUserId, "voice_engine_stt")?.let {
                    val value = it.valueJson.trim('"')
                    STTEngine.values().find { engine -> engine.id == value }?.let { engine ->
                        _selectedSTTEngine.value = engine
                    }
                }
                settingRepository.getSettingByKey(defaultUserId, "voice_engine_tts")?.let {
                    val value = it.valueJson.trim('"')
                    TTSEngine.values().find { engine -> engine.id == value }?.let { engine ->
                        _selectedTTSEngine.value = engine
                    }
                }
                settingRepository.getSettingByKey(defaultUserId, "voice_engine_language")?.let {
                    _selectedLanguage.value = it.valueJson.trim('"')
                }
                applyLanguageToTts()
            } catch (e: Exception) {
                // Keep defaults if stored settings cannot be read
            }
        }
    }

    private fun initTts() {
        tts = TextToSpeech(context) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
            if (ttsReady) {
                applyLanguageToTts()
            } else {
                _ttsStatus.value = "System text-to-speech is unavailable on this device"
            }
        }
    }

    private fun applyLanguageToTts() {
        if (!ttsReady) return
        tts?.setLanguage(Locale.forLanguageTag(_selectedLanguage.value))
    }

    fun onSTTEngineSelected(engine: STTEngine) {
        _selectedSTTEngine.value = engine
        saveSetting("voice_engine_stt", engine.id)
    }

    fun onTTSEngineSelected(engine: TTSEngine) {
        _selectedTTSEngine.value = engine
        saveSetting("voice_engine_tts", engine.id)
    }

    fun onLanguageSelected(language: String) {
        _selectedLanguage.value = language
        saveSetting("voice_engine_language", language)
        applyLanguageToTts()
    }

    /** Checks whether the system TTS engine has voice data for [language]. */
    fun downloadLanguage(language: String) {
        val result = if (ttsReady) {
            tts?.isLanguageAvailable(Locale.forLanguageTag(language)) ?: TextToSpeech.LANG_NOT_SUPPORTED
        } else {
            TextToSpeech.LANG_NOT_SUPPORTED
        }
        _ttsStatus.value = when (result) {
            TextToSpeech.LANG_AVAILABLE,
            TextToSpeech.LANG_COUNTRY_AVAILABLE,
            TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE ->
                "$language is available on this device"
            TextToSpeech.LANG_MISSING_DATA ->
                "$language voice data is missing. Install it in Android Settings > Text-to-speech"
            else ->
                "$language is not supported by this device's text-to-speech engine"
        }
    }

    fun testTTSPlayback() {
        if (_isTestingTTS.value) return
        val engine = tts
        if (engine == null || !ttsReady) {
            _ttsStatus.value = "Text-to-speech is not ready yet"
            return
        }

        _isTestingTTS.value = true
        _ttsStatus.value = "Playing test..."

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                _isTestingTTS.value = false
                _ttsStatus.value = "Test playback complete"
            }

            override fun onError(utteranceId: String?) {
                _isTestingTTS.value = false
                _ttsStatus.value = "Text-to-speech playback failed"
            }
        })

        engine.speak(
            "Hello, this is a test of the system voice engine.",
            TextToSpeech.QUEUE_FLUSH,
            null,
            "test_utterance"
        )
    }

    /**
     * Starts a one-shot recognition session. Requires RECORD_AUDIO to be granted.
     */
    fun testSTTRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _sttStatus.value = "Speech recognition is not available on this device"
            return
        }

        recognizer?.destroy()
        val sr = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer = sr

        sr.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _sttStatus.value = "Listening..."
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}

            override fun onError(error: Int) {
                _sttStatus.value = "Speech recognition error (code $error)"
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                _sttStatus.value = if (text != null) {
                    "STT recognized: $text"
                } else {
                    "No speech recognized"
                }
            }
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, _selectedLanguage.value)
        }
        sr.startListening(intent)
    }

    private fun saveSetting(key: String, value: String) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val json = "\"$value\""
                val existing = settingRepository.getSettingByKey(defaultUserId, key)
                if (existing != null) {
                    settingRepository.updateSetting(existing.copy(valueJson = json, updatedAt = now))
                } else {
                    settingRepository.saveSetting(
                        SettingEntity(
                            id = UUID.randomUUID().toString(),
                            userId = defaultUserId,
                            key = key,
                            valueJson = json,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                }
            } catch (e: Exception) {
                _ttsStatus.value = "Could not save setting: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
        recognizer?.destroy()
    }
}
