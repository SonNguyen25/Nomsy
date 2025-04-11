package com.example.nomsy.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.nomsy.data.local.Converters

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class User(
    @PrimaryKey val id: String,
    val username: String,
    val password: String,
    val name: String,
    val age: Int,
    val height: Double,
    val weight: Double,
    val fitness_goal: String,
    val nutrition_goals: Map<String, Int>
)
