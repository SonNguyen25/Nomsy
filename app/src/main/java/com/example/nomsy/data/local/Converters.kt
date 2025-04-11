package com.example.nomsy.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromNutritionGoalsMap(value: Map<String, Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toNutritionGoalsMap(value: String): Map<String, Int> {
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return Gson().fromJson(value, mapType)
    }
}