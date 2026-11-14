package com.maya.clone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.TriggerEntity
import com.maya.clone.data.repository.TriggerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventTriggersViewModel @Inject constructor(
    private val triggerRepository: TriggerRepository
) : ViewModel() {

    private val _enabledTriggers = MutableStateFlow<List<TriggerEntity>>(emptyList())
    val enabledTriggers = _enabledTriggers.asStateFlow()

    private val _allTriggers = MutableStateFlow<List<TriggerEntity>>(emptyList())
    val allTriggers = _allTriggers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadTriggers(userId)
    }

    private fun loadTriggers(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                triggerRepository.getEnabledTriggers(userId)
                    .collect { _enabledTriggers.value = it }
                triggerRepository.getAllTriggers(userId)
                    .collect { _allTriggers.value = it }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveTrigger(trigger: TriggerEntity) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                triggerRepository.saveTrigger(trigger)
            }
        }
    }

    fun updateTrigger(trigger: TriggerEntity) {
        viewModelScope.launch {
            triggerRepository.updateTrigger(trigger)
        }
    }

    fun deleteTrigger(triggerId: String) {
        viewModelScope.launch {
            triggerRepository.deleteTrigger(triggerId)
        }
    }
}