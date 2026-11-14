package com.maya.clone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.ApiKeyEntity
import com.maya.clone.data.repository.ApiKeyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsApiKeysViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository
) : ViewModel() {

    private val _apiKeys = MutableStateFlow<List<ApiKeyEntity>>(emptyList())
    val apiKeys = _apiKeys.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadApiKeys(userId)
    }

    private fun loadApiKeys(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                apiKeyRepository.getApiKeysForUser(userId)
                    .collect { keys ->
                        _apiKeys.value = keys
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun addApiKey(provider: String, key: String, label: String? = null, scopesJson: String = "[]") {
        currentUserId?.let { userId ->
            val apiKey = ApiKeyEntity(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                provider = provider,
                keyHash = hashKey(key),
                keyEncrypted = encryptKey(key),
                label = label,
                scopesJson = scopesJson,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            viewModelScope.launch {
                apiKeyRepository.saveApiKey(apiKey)
            }
        }
    }

    fun deleteApiKey(apiKeyId: String) {
        viewModelScope.launch {
            apiKeyRepository.deleteApiKey(apiKeyId)
        }
    }

    private fun hashKey(key: String): String {
        // TODO: Use proper Argon2id hashing via Security-Crypto
        return "hash_${key.hashCode()}"
    }

    private fun encryptKey(key: String): String {
        // TODO: Use AES-256-GCM encryption via Security-Crypto MasterKey
        return "encrypted_$key"
    }
}