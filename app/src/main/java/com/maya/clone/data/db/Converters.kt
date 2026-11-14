package com.maya.clone.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    private val gson = Gson()
    private val stringListType: Type = TypeToken.getParameterized(List::class.java, String::class.java).type
    private val mapStringAnyType: Type = TypeToken.getParameterized(Map::class.java, String::class.java, Any::class.java).type

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return value?.let { gson.fromJson<List<String>>(it, stringListType) } ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return gson.toJson(list ?: emptyList())
    }

    @TypeConverter
    fun fromMap(value: String?): Map<String, Any> {
        return value?.let { gson.fromJson<Map<String, Any>>(it, mapStringAnyType) } ?: emptyMap()
    }

    @TypeConverter
    fun toMap(map: Map<String, Any>?): String {
        return gson.toJson(map ?: emptyMap())
    }

    @TypeConverter
    fun fromByteArray(value: String?): ByteArray? {
        return value?.let { android.util.Base64.decode(it, android.util.Base64.DEFAULT) }
    }

    @TypeConverter
    fun toByteArray(bytes: ByteArray?): String? {
        return bytes?.let { android.util.Base64.encodeToString(it, android.util.Base64.DEFAULT) }
    }
}