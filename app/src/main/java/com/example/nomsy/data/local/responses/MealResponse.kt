package com.example.nomsy.responses

import com.example.nomsy.data.local.models.RecipeDto

data class MealResponse(
    val meals: List<RecipeDto>?
)
