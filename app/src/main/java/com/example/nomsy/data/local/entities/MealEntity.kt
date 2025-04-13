package com.example.nomsy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mealId: String,
    val date: String,
    val mealType: String,
    val foodName: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val timestamp: String
)