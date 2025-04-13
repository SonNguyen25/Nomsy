package com.example.nomsy.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: String,
    val date: String,
    val meal_type: String,
    val food_name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int
)

