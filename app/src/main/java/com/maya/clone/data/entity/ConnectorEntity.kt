package com.maya.clone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "connectors",
    indices = [Index(value = ["user_id", "status"])]
)
data class ConnectorEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "type")
    val type: String, // "google", "whatsapp", "gmail", "calendar", "notion", etc.

    @ColumnInfo(name = "config_json")
    val configJson: String,

    @ColumnInfo(name = "access_token_encrypted")
    val accessTokenEncrypted: String? = null,

    @ColumnInfo(name = "refresh_token_encrypted")
    val refreshTokenEncrypted: String? = null,

    @ColumnInfo(name = "token_expires_at")
    val tokenExpiresAt: Long? = null,

    @ColumnInfo(name = "status")
    val status: String = "disconnected", // "connected", "disconnected", "error", "expired"

    @ColumnInfo(name = "last_sync_at")
    val lastSyncAt: Long? = null,

    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)