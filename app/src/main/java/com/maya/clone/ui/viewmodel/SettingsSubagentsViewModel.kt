package com.maya.clone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.AgentEntity
import com.maya.clone.data.repository.AgentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsSubagentsViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {

    private val _subagents = MutableStateFlow<List<AgentEntity>>(emptyList())
    val subagents = _subagents.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadSubagents(userId)
    }

    private fun loadSubagents(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                agentRepository.getAllAgents(userId)
                    .collect { agents ->
                        _subagents.value = agents.filter { !it.name.contains("Assistant", ignoreCase = true) && !it.name.contains("Coder", ignoreCase = true) && !it.name.contains("Creative", ignoreCase = true) }
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun createSubagent(name: String, systemPrompt: String = "") {
        currentUserId?.let { userId ->
            val agent = AgentEntity(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                name = name,
                systemPrompt = systemPrompt,
                model = "gpt-4o-mini",
                temperature = 0.7f,
                toolsJson = "[]",
                permissionsJson = "{}",
                isActive = true
            )
            viewModelScope.launch {
                agentRepository.createAgent(agent)
            }
        }
    }

    fun deleteSubagent(agentId: String) {
        viewModelScope.launch {
            agentRepository.deleteAgent(agentId)
        }
    }

    fun toggleSubagentActive(agent: AgentEntity) {
        viewModelScope.launch {
            agentRepository.updateAgent(agent.copy(isActive = !agent.isActive, updatedAt = System.currentTimeMillis()))
        }
    }
}