package com.example.nomsy.data.local.models

data class Food(
    val id: String,
    val name: String,
    val calories: Double,
    val protein: Double,
    val fats: Double,
    val carbs: Double,
    val userId: String
)

