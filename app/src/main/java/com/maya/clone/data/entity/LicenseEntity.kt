package com.maya.clone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "licenses",
    indices = [Index(value = ["user_id"])]
)
data class LicenseEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "tier")
    val tier: String, // "free", "pro", "promax"

    @ColumnInfo(name = "purchase_token")
    val purchaseToken: String? = null,

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long? = null,

    @ColumnInfo(name = "features_json")
    val featuresJson: String = "[]",

    @ColumnInfo(name = "verified_at")
    val verifiedAt: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)