package com.maya.clone.ui.viewmodel

import kotlinx.coroutines.delay
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.BackupJobEntity
import com.maya.clone.data.repository.BackupJobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsBackupViewModel @Inject constructor(
    private val backupJobRepository: BackupJobRepository
) : ViewModel() {

    private val _backupJobs = MutableStateFlow<List<BackupJobEntity>>(emptyList())
    val backupJobs = _backupJobs.asStateFlow()

    private val _pendingJobs = MutableStateFlow<List<BackupJobEntity>>(emptyList())
    val pendingJobs = _pendingJobs.asStateFlow()

    private val _isCreating = MutableStateFlow<Boolean>(false)
    val isCreating = _isCreating.asStateFlow()

    private val _createProgress = MutableStateFlow<Int>(0)
    val createProgress = _createProgress.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentUserId: String? = null

    fun init(userId: String) {
        currentUserId = userId
        loadBackupJobs(userId)
    }

    private fun loadBackupJobs(userId: String) {
        viewModelScope.launch {
            backupJobRepository.getJobsForUser(userId)
                .collect { jobs ->
                    _backupJobs.value = jobs
                }
            val pending = backupJobRepository.getPendingJobs(userId)
            _pendingJobs.value = pending
        }
    }

    fun createBackup(type: String = "full") {
        currentUserId?.let { userId ->
            _isCreating.value = true
            _createProgress.value = 0
            val job = BackupJobEntity(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                type = type,
                status = "running",
                createdAt = System.currentTimeMillis()
            )
            viewModelScope.launch {
                backupJobRepository.createJob(job)
                // Simulate backup progress
                for (i in 1..10) {
                    _createProgress.value = i * 10
                    delay(500)
                }
                // Update job as completed
                val completedJob = job.copy(
                    status = "completed",
                    completedAt = System.currentTimeMillis(),
                    sizeBytes = 1024 * 1024 * 50, // 50 MB placeholder
                    storagePath = "backups/$userId/${job.id}.enc"
                )
                backupJobRepository.updateJob(completedJob)
                _isCreating.value = false
                _createProgress.value = 100
                loadBackupJobs(userId)
            }
        }
    }

    fun restoreBackup(jobId: String) {
        // TODO: Implement restore from Supabase Storage
        _error.value = "Restore not yet implemented"
    }

    fun deleteBackup(jobId: String) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                backupJobRepository.getJobsForUser(userId)
                    .collect { jobs ->
                        // Note: BackupJobRepository doesn't have deleteById, would need to add
                    }
            }
        }
    }
}