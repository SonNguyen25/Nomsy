package com.example.nomsy.data.local.dao

import androidx.room.*
import com.example.nomsy.data.local.models.Recipe

@Dao
interface RecipeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<Recipe>)

    @Query("SELECT * FROM recipe")
    suspend fun getAllRecipes(): List<Recipe>

    @Query("SELECT * FROM recipe WHERE idMeal = :id LIMIT 1")
    suspend fun getRecipeById(id: String): Recipe?
}
