package com.maya.clone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "agents",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "is_active"])
    ]
)
data class AgentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String? = null,

    @ColumnInfo(name = "system_prompt")
    val systemPrompt: String = "",

    @ColumnInfo(name = "model")
    val model: String = "gpt-4o-mini",

    @ColumnInfo(name = "temperature")
    val temperature: Float = 0.7f,

    @ColumnInfo(name = "tools_json")
    val toolsJson: String = "[]",

    @ColumnInfo(name = "permissions_json")
    val permissionsJson: String = "{}",

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)