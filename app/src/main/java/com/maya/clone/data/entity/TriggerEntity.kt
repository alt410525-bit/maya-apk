package com.maya.clone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "triggers",
    indices = [Index(value = ["user_id", "enabled"])]
)
data class TriggerEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "event_type")
    val eventType: String,

    @ColumnInfo(name = "conditions_json")
    val conditionsJson: String,

    @ColumnInfo(name = "actions_json")
    val actionsJson: String,

    @ColumnInfo(name = "enabled")
    val enabled: Boolean = true,

    @ColumnInfo(name = "cooldown_ms")
    val cooldownMs: Long = 0,

    @ColumnInfo(name = "last_fired_at")
    val lastFiredAt: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)