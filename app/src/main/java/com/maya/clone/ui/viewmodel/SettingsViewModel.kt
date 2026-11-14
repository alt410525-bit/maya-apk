package com.maya.clone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.AgentEntity
import com.maya.clone.data.entity.ApiKeyEntity
import com.maya.clone.data.entity.AutoReplyRuleEntity
import com.maya.clone.data.entity.AwarenessEventEntity
import com.maya.clone.data.entity.BackupJobEntity
import com.maya.clone.data.entity.ConversationEntity
import com.maya.clone.data.entity.ConnectorEntity
import com.maya.clone.data.entity.EncryptionKeyEntity
import com.maya.clone.data.entity.FaceLockAttemptEntity
import com.maya.clone.data.entity.FaceProfileEntity
import com.maya.clone.data.entity.LicenseEntity
import com.maya.clone.data.entity.MessageEntity
import com.maya.clone.data.entity.SettingEntity
import com.maya.clone.data.entity.ThemeEntity
import com.maya.clone.data.entity.TriggerEntity
import com.maya.clone.data.repository.AgentRepository
import com.maya.clone.data.repository.ApiKeyRepository
import com.maya.clone.data.repository.AutoReplyRuleRepository
import com.maya.clone.data.repository.AwarenessEventRepository
import com.maya.clone.data.repository.BackupJobRepository
import com.maya.clone.data.repository.ConnectorRepository
import com.maya.clone.data.repository.ConversationRepository
import com.maya.clone.data.repository.EncryptionKeyRepository
import com.maya.clone.data.repository.FaceLockAttemptRepository
import com.maya.clone.data.repository.FaceProfileRepository
import com.maya.clone.data.repository.LicenseRepository
import com.maya.clone.data.repository.MessageRepository
import com.maya.clone.data.repository.SettingRepository
import com.maya.clone.data.repository.ThemeRepository
import com.maya.clone.data.repository.TriggerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val themeRepository: ThemeRepository,
    private val licenseRepository: LicenseRepository,
    private val faceProfileRepository: FaceProfileRepository,
    private val faceLockAttemptRepository: FaceLockAttemptRepository,
    private val backupJobRepository: BackupJobRepository,
    private val autoReplyRuleRepository: AutoReplyRuleRepository,
    private val awarenessEventRepository: AwarenessEventRepository,
    private val triggerRepository: TriggerRepository,
    private val encryptionKeyRepository: EncryptionKeyRepository,
    private val connectorRepository: ConnectorRepository,
    private val apiKeyRepository: ApiKeyRepository,
    private val agentRepository: AgentRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    // General Settings
    private val _settings = MutableStateFlow<List<SettingEntity>>(emptyList())
    val settings = _settings.asStateFlow()

    // Appearance / Theme
    private val _themes = MutableStateFlow<List<ThemeEntity>>(emptyList())
    val themes = _themes.asStateFlow()

    private val _activeTheme = MutableStateFlow<ThemeEntity?>(null)
    val activeTheme = _activeTheme.asStateFlow()

    // Licenses
    private val _licenses = MutableStateFlow<List<LicenseEntity>>(emptyList())
    val licenses = _licenses.asStateFlow()

    private val _latestLicense = MutableStateFlow<LicenseEntity?>(null)
    val latestLicense = _latestLicense.asStateFlow()

    // Face Lock
    private val _faceProfiles = MutableStateFlow<List<FaceProfileEntity>>(emptyList())
    val faceProfiles = _faceProfiles.asStateFlow()

    private val _recentFaceLockAttempts = MutableStateFlow<List<FaceLockAttemptEntity>>(emptyList())
    val recentFaceLockAttempts = _recentFaceLockAttempts.asStateFlow()

    private val _recentFailureCount = MutableStateFlow<Int>(0)
    val recentFailureCount = _recentFailureCount.asStateFlow()

    // Backup
    private val _backupJobs = MutableStateFlow<List<BackupJobEntity>>(emptyList())
    val backupJobs = _backupJobs.asStateFlow()

    private val _pendingBackupJobs = MutableStateFlow<List<BackupJobEntity>>(emptyList())
    val pendingBackupJobs = _pendingBackupJobs.asStateFlow()

    // Auto Reply
    private val _enabledAutoReplyRules = MutableStateFlow<List<AutoReplyRuleEntity>>(emptyList())
    val enabledAutoReplyRules = _enabledAutoReplyRules.asStateFlow()

    private val _allAutoReplyRules = MutableStateFlow<List<AutoReplyRuleEntity>>(emptyList())
    val allAutoReplyRules = _allAutoReplyRules.asStateFlow()

    // Awareness
    private val _unprocessedAwarenessEvents = MutableStateFlow<List<AwarenessEventEntity>>(emptyList())
    val unprocessedAwarenessEvents = _unprocessedAwarenessEvents.asStateFlow()

    private val _recentAwarenessEvents = MutableStateFlow<List<AwarenessEventEntity>>(emptyList())
    val recentAwarenessEvents = _recentAwarenessEvents.asStateFlow()

    private val _awarenessEventCount = MutableStateFlow<Int>(0)
    val awarenessEventCount = _awarenessEventCount.asStateFlow()

    // Triggers
    private val _enabledTriggers = MutableStateFlow<List<TriggerEntity>>(emptyList())
    val enabledTriggers = _enabledTriggers.asStateFlow()

    private val _allTriggers = MutableStateFlow<List<TriggerEntity>>(emptyList())
    val allTriggers = _allTriggers.asStateFlow()

    // Encryption
    private val _encryptionKeys = MutableStateFlow<List<EncryptionKeyEntity>>(emptyList())
    val encryptionKeys = _encryptionKeys.asStateFlow()

    // Connectors
    private val _connectors = MutableStateFlow<List<ConnectorEntity>>(emptyList())
    val connectors = _connectors.asStateFlow()

    private val _connectedConnectors = MutableStateFlow<List<ConnectorEntity>>(emptyList())
    val connectedConnectors = _connectedConnectors.asStateFlow()

    // API Keys
    private val _apiKeys = MutableStateFlow<List<ApiKeyEntity>>(emptyList())
    val apiKeys = _apiKeys.asStateFlow()

    // Agents
    private val _agents = MutableStateFlow<List<AgentEntity>>(emptyList())
    val agents = _agents.asStateFlow()

    // Conversations
    private val _conversations = MutableStateFlow<List<ConversationEntity>>(emptyList())
    val conversations = _conversations.asStateFlow()

    // Messages
    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages = _messages.asStateFlow()

    // Loading & Error States
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadAllSettings(userId)
    }

    private fun loadAllSettings(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                loadSettings(userId)
                loadThemes(userId)
                loadLicenses(userId)
                loadFaceProfiles(userId)
                loadFaceLockAttempts(userId)
                loadBackupJobs(userId)
                loadAutoReplyRules(userId)
                loadAwarenessEvents(userId)
                loadTriggers(userId)
                loadEncryptionKeys(userId)
                loadConnectors(userId)
                loadApiKeys(userId)
                loadAgents(userId)
                loadConversations(userId)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    // General Settings
    private fun loadSettings(userId: String) {
        viewModelScope.launch {
            settingRepository.getAllSettings(userId)
                .collect { settings ->
                    _settings.value = settings
                }
        }
    }

    fun saveSetting(setting: SettingEntity) {
        viewModelScope.launch {
            settingRepository.saveSetting(setting)
        }
    }

    fun updateSetting(setting: SettingEntity) {
        viewModelScope.launch {
            settingRepository.updateSetting(setting)
        }
    }

    fun deleteSetting(id: String) {
        viewModelScope.launch {
            settingRepository.deleteSetting(id)
        }
    }

    // Themes
    private fun loadThemes(userId: String) {
        viewModelScope.launch {
            themeRepository.getAllThemes(userId)
                .collect { themes ->
                    _themes.value = themes
                }
            themeRepository.getActiveTheme(userId)?.let { theme ->
                _activeTheme.value = theme
            }
        }
    }

    fun setActiveTheme(userId: String, themeId: String) {
        viewModelScope.launch {
            themeRepository.setActiveTheme(userId, themeId)
            themeRepository.getTheme(themeId)?.let { theme ->
                _activeTheme.value = theme
            }
        }
    }

    fun saveTheme(theme: ThemeEntity) {
        viewModelScope.launch {
            themeRepository.saveTheme(theme)
        }
    }

    fun updateTheme(theme: ThemeEntity) {
        viewModelScope.launch {
            themeRepository.updateTheme(theme)
        }
    }

    fun deleteTheme(id: String) {
        viewModelScope.launch {
            themeRepository.deleteTheme(id)
        }
    }

    // Licenses
    private fun loadLicenses(userId: String) {
        viewModelScope.launch {
            licenseRepository.getLicensesForUser(userId)
                .collect { licenses ->
                    _licenses.value = licenses
                }
            licenseRepository.getLatestLicense(userId)?.let { license ->
                _latestLicense.value = license
            }
        }
    }

    fun saveLicense(license: LicenseEntity) {
        viewModelScope.launch {
            licenseRepository.saveLicense(license)
            licenseRepository.getLicensesForUser(license.userId)
                .collect { licenses ->
                    _licenses.value = licenses
                }
        }
    }

    fun updateLicense(license: LicenseEntity) {
        viewModelScope.launch {
            licenseRepository.updateLicense(license)
        }
    }

    // Face Profiles
    private fun loadFaceProfiles(userId: String) {
        viewModelScope.launch {
            faceProfileRepository.getProfilesForUser(userId)
                .collect { profiles ->
                    _faceProfiles.value = profiles
                }
        }
    }

    fun saveFaceProfile(profile: FaceProfileEntity) {
        viewModelScope.launch {
            faceProfileRepository.saveProfile(profile)
        }
    }

    fun updateFaceProfile(profile: FaceProfileEntity) {
        viewModelScope.launch {
            faceProfileRepository.updateProfile(profile)
        }
    }

    fun deleteFaceProfile(id: String) {
        viewModelScope.launch {
            faceProfileRepository.deleteProfile(id)
        }
    }

    // Face Lock Attempts
    private fun loadFaceLockAttempts(userId: String) {
        viewModelScope.launch {
            val attempts = faceLockAttemptRepository.getRecentAttempts(userId, 20)
            _recentFaceLockAttempts.value = attempts
            val failures = faceLockAttemptRepository.countRecentFailures(userId, System.currentTimeMillis() - 86400000) // Last 24 hours
            _recentFailureCount.value = failures
        }
    }

    fun recordFaceLockAttempt(attempt: FaceLockAttemptEntity) {
        viewModelScope.launch {
            faceLockAttemptRepository.recordAttempt(attempt)
            loadFaceLockAttempts(attempt.userId)
        }
    }

    // Backup Jobs
    private fun loadBackupJobs(userId: String) {
        viewModelScope.launch {
            backupJobRepository.getJobsForUser(userId)
                .collect { jobs ->
                    _backupJobs.value = jobs
                }
            val pending = backupJobRepository.getPendingJobs(userId)
            _pendingBackupJobs.value = pending
        }
    }

    fun createBackupJob(job: BackupJobEntity) {
        viewModelScope.launch {
            backupJobRepository.createJob(job)
        }
    }

    fun updateBackupJob(job: BackupJobEntity) {
        viewModelScope.launch {
            backupJobRepository.updateJob(job)
        }
    }

    // Auto Reply Rules
    private fun loadAutoReplyRules(userId: String) {
        viewModelScope.launch {
            autoReplyRuleRepository.getEnabledRules(userId)
                .collect { rules ->
                    _enabledAutoReplyRules.value = rules
                }
            autoReplyRuleRepository.getAllRules(userId)
                .collect { rules ->
                    _allAutoReplyRules.value = rules
                }
        }
    }

    fun saveAutoReplyRule(rule: AutoReplyRuleEntity) {
        viewModelScope.launch {
            autoReplyRuleRepository.saveRule(rule)
        }
    }

    fun updateAutoReplyRule(rule: AutoReplyRuleEntity) {
        viewModelScope.launch {
            autoReplyRuleRepository.updateRule(rule)
        }
    }

    fun deleteAutoReplyRule(id: String) {
        viewModelScope.launch {
            autoReplyRuleRepository.deleteRule(id)
        }
    }

    // Awareness Events
    private fun loadAwarenessEvents(userId: String) {
        viewModelScope.launch {
            awarenessEventRepository.getUnprocessedEvents(userId)
                .collect { events ->
                    _unprocessedAwarenessEvents.value = events
                }
            val recent = awarenessEventRepository.getRecentEvents(userId, 50)
            _recentAwarenessEvents.value = recent
            val count = awarenessEventRepository.countEventsSince(userId, System.currentTimeMillis() - 86400000) // Last 24 hours
            _awarenessEventCount.value = count
        }
    }

    fun recordAwarenessEvent(event: AwarenessEventEntity) {
        viewModelScope.launch {
            awarenessEventRepository.recordEvent(event)
        }
    }

    fun recordAwarenessEvents(events: List<AwarenessEventEntity>) {
        viewModelScope.launch {
            awarenessEventRepository.recordEvents(events)
        }
    }

    fun markAwarenessEventProcessed(event: AwarenessEventEntity) {
        viewModelScope.launch {
            awarenessEventRepository.markProcessed(event)
        }
    }

    fun deleteOldAwarenessEvents(userId: String, before: Long) {
        viewModelScope.launch {
            awarenessEventRepository.deleteOldEvents(userId, before)
        }
    }

    // Triggers
    private fun loadTriggers(userId: String) {
        viewModelScope.launch {
            triggerRepository.getEnabledTriggers(userId)
                .collect { triggers ->
                    _enabledTriggers.value = triggers
                }
            triggerRepository.getAllTriggers(userId)
                .collect { triggers ->
                    _allTriggers.value = triggers
                }
        }
    }

    fun saveTrigger(trigger: TriggerEntity) {
        viewModelScope.launch {
            triggerRepository.saveTrigger(trigger)
        }
    }

    fun updateTrigger(trigger: TriggerEntity) {
        viewModelScope.launch {
            triggerRepository.updateTrigger(trigger)
        }
    }

    fun deleteTrigger(id: String) {
        viewModelScope.launch {
            triggerRepository.deleteTrigger(id)
        }
    }

    fun getTriggersByEventType(userId: String, eventType: String) {
        viewModelScope.launch {
            val triggers = triggerRepository.getTriggersByEventType(userId, eventType)
            // Could expose via a separate flow if needed
        }
    }

    // Encryption Keys
    private fun loadEncryptionKeys(userId: String) {
        viewModelScope.launch {
            encryptionKeyRepository.getAllKeys(userId)
                .collect { keys ->
                    _encryptionKeys.value = keys
                }
        }
    }

    fun saveEncryptionKey(key: EncryptionKeyEntity) {
        viewModelScope.launch {
            encryptionKeyRepository.saveKey(key)
        }
    }

    fun updateEncryptionKey(key: EncryptionKeyEntity) {
        viewModelScope.launch {
            encryptionKeyRepository.updateKey(key)
        }
    }

    fun getLatestEncryptionKey(userId: String, purpose: String) {
        viewModelScope.launch {
            val key = encryptionKeyRepository.getLatestKey(userId, purpose)
            // Could expose via a separate flow if needed
        }
    }

    // Connectors
    private fun loadConnectors(userId: String) {
        viewModelScope.launch {
            connectorRepository.getAllConnectors(userId)
                .collect { connectors ->
                    _connectors.value = connectors
                }
            connectorRepository.getConnectedConnectors(userId)
                .collect { connectors ->
                    _connectedConnectors.value = connectors
                }
        }
    }

    fun saveConnector(connector: ConnectorEntity) {
        viewModelScope.launch {
            connectorRepository.saveConnector(connector)
        }
    }

    fun updateConnector(connector: ConnectorEntity) {
        viewModelScope.launch {
            connectorRepository.updateConnector(connector)
        }
    }

    fun deleteConnector(id: String) {
        viewModelScope.launch {
            connectorRepository.deleteConnector(id)
        }
    }

    // API Keys
    private fun loadApiKeys(userId: String) {
        viewModelScope.launch {
            apiKeyRepository.getApiKeysForUser(userId)
                .collect { keys ->
                    _apiKeys.value = keys
                }
        }
    }

    fun saveApiKey(apiKey: ApiKeyEntity) {
        viewModelScope.launch {
            apiKeyRepository.saveApiKey(apiKey)
        }
    }

    fun updateApiKey(apiKey: ApiKeyEntity) {
        viewModelScope.launch {
            apiKeyRepository.updateApiKey(apiKey)
        }
    }

    fun deleteApiKey(id: String) {
        viewModelScope.launch {
            apiKeyRepository.deleteApiKey(id)
        }
    }

    fun getApiKeysByProvider(userId: String, provider: String) {
        viewModelScope.launch {
            val keys = apiKeyRepository.getApiKeysByProvider(userId, provider)
            // Could expose via a separate flow if needed
        }
    }

    // Agents
    private fun loadAgents(userId: String) {
        viewModelScope.launch {
            agentRepository.getActiveAgents(userId)
                .collect { agents ->
                    _agents.value = agents
                }
        }
    }

    // Conversations
    private fun loadConversations(userId: String) {
        viewModelScope.launch {
            conversationRepository.getConversationsForUser(userId)
                .collect { conversations ->
                    _conversations.value = conversations
                }
        }
    }

    // Messages (for a specific conversation)
    fun observeMessagesForConversation(conversationId: String) {
        viewModelScope.launch {
            messageRepository.getMessagesForConversation(conversationId)
                .collect { messages ->
                    _messages.value = messages
                }
        }
    }

    fun clearError() {
        _error.value = null
    }
}