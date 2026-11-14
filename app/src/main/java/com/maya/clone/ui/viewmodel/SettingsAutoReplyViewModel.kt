package com.maya.clone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.AutoReplyRuleEntity
import com.maya.clone.data.repository.AutoReplyRuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsAutoReplyViewModel @Inject constructor(
    private val autoReplyRuleRepository: AutoReplyRuleRepository
) : ViewModel() {

    private val _enabledRules = MutableStateFlow<List<AutoReplyRuleEntity>>(emptyList())
    val enabledRules = _enabledRules.asStateFlow()

    private val _allRules = MutableStateFlow<List<AutoReplyRuleEntity>>(emptyList())
    val allRules = _allRules.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadRules(userId)
    }

    private fun loadRules(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                autoReplyRuleRepository.getEnabledRules(userId)
                    .collect { rules -> _enabledRules.value = rules }
                autoReplyRuleRepository.getAllRules(userId)
                    .collect { rules -> _allRules.value = rules }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveRule(rule: AutoReplyRuleEntity) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                autoReplyRuleRepository.saveRule(rule)
            }
        }
    }

    fun updateRule(rule: AutoReplyRuleEntity) {
        viewModelScope.launch {
            autoReplyRuleRepository.updateRule(rule)
        }
    }

    fun deleteRule(ruleId: String) {
        viewModelScope.launch {
            autoReplyRuleRepository.deleteRule(ruleId)
        }
    }
}