package com.example.nomsy.responses

import com.example.nomsy.data.local.models.Recipe

data class MealResponse(
    val meals: List<Recipe>?
)
