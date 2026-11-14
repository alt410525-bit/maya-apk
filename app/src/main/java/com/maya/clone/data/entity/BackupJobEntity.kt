package com.maya.clone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "backup_jobs",
    indices = [Index(value = ["user_id", "created_at"])]
)
data class BackupJobEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "type")
    val type: String, // "full", "incremental", "settings_only", "faces_only"

    @ColumnInfo(name = "status")
    val status: String = "pending", // "pending", "running", "completed", "failed", "cancelled"

    @ColumnInfo(name = "storage_path")
    val storagePath: String? = null,

    @ColumnInfo(name = "size_bytes")
    val sizeBytes: Long? = null,

    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null,

    @ColumnInfo(name = "started_at")
    val startedAt: Long? = null,

    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)