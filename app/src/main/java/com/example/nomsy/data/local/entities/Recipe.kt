package com.example.nomsy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe")
data class Recipe(
    @PrimaryKey val idMeal: String,
    val strMeal: String,
    val strInstructions: String,
    val strMealThumb: String,
    val strYoutube: String?,
    val strCategory: String?,
    val strArea: String?,
    val strTags: String?,
    val ingredients: List<String> = listOf()
)


