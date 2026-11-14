package com.maya.clone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "face_profiles",
    indices = [Index(value = ["user_id"])]
)
data class FaceProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "embedding")
    val embedding: ByteArray,

    @ColumnInfo(name = "photo_urls_json")
    val photoUrlsJson: String = "[]",

    @ColumnInfo(name = "quality_score")
    val qualityScore: Float? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)