package com.maya.clone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.AwarenessEventEntity
import com.maya.clone.data.repository.AwarenessEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AwarenessLogViewModel @Inject constructor(
    private val awarenessEventRepository: AwarenessEventRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<AwarenessEventEntity>>(emptyList())
    val events = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadEvents(userId)
    }

    private fun loadEvents(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                awarenessEventRepository.getRecentEvents(userId, 100)
                    .also { _events.value = it }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearLog() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                awarenessEventRepository.deleteOldEvents(userId, 0)
                loadEvents(userId)
            }
        }
    }
}