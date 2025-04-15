package com.example.nomsy.ui.screens.recipe

import com.example.nomsy.data.local.entities.Recipe
import com.example.nomsy.data.repository.IRecipeRepository

class FakeRecipeRepository : IRecipeRepository {

    private val fakeRecipes = listOf(
        Recipe(
            idMeal = "1",
            strMeal = "Sashimi Combo",
            strInstructions = "Slice and serve chilled.",
            strMealThumb = "https://example.com/sashimi.jpg",
            strYoutube = null,
            strCategory = "Seafood",
            strArea = "Japanese",
            strTags = "Fresh",
            ingredients = listOf("Tuna", "Rice", "Seaweed")
        ),
        Recipe(
            idMeal = "2",
            strMeal = "Katsu Curry",
            strInstructions = "Fry katsu, serve with curry and rice.",
            strMealThumb = "https://example.com/katsu.jpg",
            strYoutube = null,
            strCategory = "Japanese",
            strArea = "Japanese",
            strTags = "Spicy",
            ingredients = listOf("Pork", "Panko", "Curry Sauce")
        )
    )

    override suspend fun getAllRecipes(): List<Recipe> = fakeRecipes

    override suspend fun searchRecipes(query: String): List<Recipe> {
        return fakeRecipes.filter { it.strMeal.contains(query, ignoreCase = true) }
    }
}