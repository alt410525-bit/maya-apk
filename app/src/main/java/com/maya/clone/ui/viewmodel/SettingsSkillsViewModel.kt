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
class SettingsSkillsViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _installedSkills = MutableStateFlow<List<String>>(emptyList())
    val installedSkills = _installedSkills.asStateFlow()

    private val _availableSkills = MutableStateFlow<List<SkillInfo>>(listOf(
        SkillInfo("web_search", "Web Search", "Search the web for real-time information", "search", true),
        SkillInfo("code_execution", "Code Execution", "Run Python/JavaScript code in sandbox", "code", true),
        SkillInfo("file_operations", "File Operations", "Read, write, and manage files", "file", true),
        SkillInfo("image_generation", "Image Generation", "Generate images from text prompts", "image", false),
        SkillInfo("data_analysis", "Data Analysis", "Analyze CSV/JSON data with pandas", "analytics", false),
        SkillInfo("email_sender", "Email Sender", "Send emails via SMTP", "email", false),
        SkillInfo("calendar", "Calendar", "Manage calendar events", "calendar", false),
        SkillInfo("translation", "Translation", "Translate between languages", "translate", false),
        SkillInfo("ocr", "OCR", "Extract text from images", "ocr", false),
        SkillInfo("speech_recognition", "Speech Recognition", "Convert speech to text", "mic", false),
        SkillInfo("text_to_speech", "Text-to-Speech", "Convert text to natural speech", "volume_up", false),
        SkillInfo("web_scraping", "Web Scraping", "Extract data from web pages", "spider", false),
    ))
    val availableSkills = _availableSkills.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadSkills(userId)
    }

    private fun loadSkills(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                settingRepository.getSettingByKey(userId, "skills_installed")?.let { s ->
                    val skills = parseJsonArray(s.valueJson)
                    _installedSkills.value = skills
                    // Update available skills installed status
                    _availableSkills.value = _availableSkills.value.map { skill ->
                        skill.copy(isInstalled = skills.contains(skill.id))
                    }
                }
            } catch (e: Exception) {
                // Use defaults
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun installSkill(skillId: String) {
        if (skillId in _installedSkills.value) return
        val newList = _installedSkills.value + skillId
        _installedSkills.value = newList
        _availableSkills.value = _availableSkills.value.map { skill ->
            if (skill.id == skillId) skill.copy(isInstalled = true) else skill
        }
        saveInstalledSkills(newList)
    }

    fun uninstallSkill(skillId: String) {
        if (skillId !in _installedSkills.value) return
        val newList = _installedSkills.value.filter { it != skillId }
        _installedSkills.value = newList
        _availableSkills.value = _availableSkills.value.map { skill ->
            if (skill.id == skillId) skill.copy(isInstalled = false) else skill
        }
        saveInstalledSkills(newList)
    }

    private fun saveInstalledSkills(skills: List<String>) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val entity = SettingEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    userId = userId,
                    key = "skills_installed",
                    valueJson = toJsonArray(skills),
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

    data class SkillInfo(
        val id: String,
        val name: String,
        val description: String,
        val icon: String,
        var isInstalled: Boolean
    )
}