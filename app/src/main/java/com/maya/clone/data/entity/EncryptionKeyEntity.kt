package com.maya.clone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "encryption_keys",
    indices = [
        Index(value = ["user_id", "purpose", "key_version"], unique = true)
    ]
)
data class EncryptionKeyEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "purpose")
    val purpose: String, // "backup", "face_embeddings", "api_keys", "messages", "export"

    @ColumnInfo(name = "algorithm")
    val algorithm: String = "AES-256-GCM",

    @ColumnInfo(name = "key_material_encrypted")
    val keyMaterialEncrypted: String,

    @ColumnInfo(name = "key_version")
    val keyVersion: Int = 1,

    @ColumnInfo(name = "rotated_at")
    val rotatedAt: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)