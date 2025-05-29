package com.tommy.siliconflow.app.datasbase

import androidx.room.TypeConverter
import com.tommy.siliconflow.app.network.JsonSerializationHelper

class Converters {
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return JsonSerializationHelper.jsonX().encodeToString(list)
    }

    @TypeConverter
    fun toStringList(json: String): List<String> {
        return JsonSerializationHelper.jsonX().decodeFromString(json)
    }
}