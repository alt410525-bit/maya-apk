package com.maya.clone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "api_keys",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "provider"])
    ]
)
data class ApiKeyEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "provider")
    val provider: String,

    @ColumnInfo(name = "key_hash")
    val keyHash: String,

    @ColumnInfo(name = "key_encrypted")
    val keyEncrypted: String,

    @ColumnInfo(name = "label")
    val label: String? = null,

    @ColumnInfo(name = "scopes_json")
    val scopesJson: String = "[]",

    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: Long? = null,

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)