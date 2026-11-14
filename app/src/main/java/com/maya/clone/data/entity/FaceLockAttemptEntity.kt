package com.maya.clone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "face_lock_attempts",
    indices = [Index(value = ["user_id", "created_at"])]
)
data class FaceLockAttemptEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "success")
    val success: Boolean,

    @ColumnInfo(name = "confidence")
    val confidence: Float? = null,

    @ColumnInfo(name = "fallback_used")
    val fallbackUsed: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)