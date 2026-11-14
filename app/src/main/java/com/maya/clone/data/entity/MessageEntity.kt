package com.maya.clone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["conversation_id", "created_at"]),
        Index(value = ["status"])
    ]
)
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "conversation_id")
    val conversationId: String,

    @ColumnInfo(name = "role")
    val role: String, // "user", "assistant", "system", "tool"

    @ColumnInfo(name = "content")
    val content: String = "",

    @ColumnInfo(name = "attachments_json")
    val attachmentsJson: String = "[]",

    @ColumnInfo(name = "metadata_json")
    val metadataJson: String = "{}",

    @ColumnInfo(name = "status")
    val status: String = "sent", // "sending", "sent", "streaming", "failed", "cancelled"

    @ColumnInfo(name = "token_count")
    val tokenCount: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)