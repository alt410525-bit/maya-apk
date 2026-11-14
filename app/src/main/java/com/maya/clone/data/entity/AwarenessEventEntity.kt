package com.maya.clone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "awareness_events",
    indices = [
        Index(value = ["user_id", "created_at"]),
        Index(value = ["user_id", "type"])
    ]
)
data class AwarenessEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "type")
    val type: String, // "location", "sensor", "app", "notification", "call"

    @ColumnInfo(name = "payload_json")
    val payloadJson: String,

    @ColumnInfo(name = "processed")
    val processed: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)