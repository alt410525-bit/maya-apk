package com.maya.clone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.SettingEntity
import com.maya.clone.data.entity.ConnectorEntity
import com.maya.clone.data.repository.SettingRepository
import com.maya.clone.data.repository.ConnectorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsConnectorsViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val connectorRepository: ConnectorRepository
) : ViewModel() {

    private val _connectors = MutableStateFlow<List<ConnectorEntity>>(emptyList())
    val connectors = _connectors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadConnectors(userId)
    }

    private fun loadConnectors(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                connectorRepository.getAllConnectors(userId)
                    .collect { connectors ->
                        _connectors.value = connectors
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun connectConnector(connector: ConnectorEntity) {
        // TODO: Implement OAuth flow
    }

    fun disconnectConnector(connectorId: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                connectorRepository.getAllConnectors(userId)
                    .collect { connectors ->
                        connectors.find { it.id == connectorId }?.let { connector ->
                            connectorRepository.updateConnector(connector.copy(
                                status = "disconnected",
                                accessTokenEncrypted = null,
                                refreshTokenEncrypted = null,
                                updatedAt = System.currentTimeMillis()
                            ))
                        }
                    }
            }
        }
    }
}