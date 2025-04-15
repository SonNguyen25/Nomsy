package com.example.nomsy.data.repository

import com.example.nomsy.data.local.dao.RecipeDAO
import com.example.nomsy.data.local.entities.Recipe
import com.example.nomsy.data.local.models.RecipeDto
import com.example.nomsy.data.local.models.toRecipe
import com.example.nomsy.data.remote.RecipeAPIService
import com.example.nomsy.responses.MealResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class RecipeRepositoryTest {

    private lateinit var fakeApi: FakeRecipeAPIService
    private lateinit var fakeDao: FakeRecipeDAO
    private lateinit var repo: RecipeRepository

    @Before
    fun setup() {
        fakeApi = FakeRecipeAPIService()
        fakeDao = FakeRecipeDAO()
        repo = RecipeRepository(api = fakeApi, dao = fakeDao)
    }

    private fun dto(
        id: String,
        name: String,
        instr: String = "",
        thumb: String = "",
        ing1: String? = null,
        meas1: String? = null
    ): RecipeDto {
        return RecipeDto(
            idMeal           = id,
            strMeal          = name,
            strInstructions  = instr,
            strMealThumb     = thumb,
            strYoutube       = null,
            strCategory      = null,
            strArea          = null,
            strTags          = null,
            strIngredient1   = ing1,
            strIngredient2   = null,
            strIngredient3   = null,
            strIngredient4   = null,
            strIngredient5   = null,
            strIngredient6   = null,
            strIngredient7   = null,
            strIngredient8   = null,
            strIngredient9   = null,
            strIngredient10  = null,
            strIngredient11  = null,
            strIngredient12  = null,
            strIngredient13  = null,
            strIngredient14  = null,
            strIngredient15  = null,
            strIngredient16  = null,
            strIngredient17  = null,
            strIngredient18  = null,
            strIngredient19  = null,
            strIngredient20  = null,
            strMeasure1      = meas1,
            strMeasure2      = null,
            strMeasure3      = null,
            strMeasure4      = null,
            strMeasure5      = null,
            strMeasure6      = null,
            strMeasure7      = null,
            strMeasure8      = null,
            strMeasure9      = null,
            strMeasure10     = null,
            strMeasure11     = null,
            strMeasure12     = null,
            strMeasure13     = null,
            strMeasure14     = null,
            strMeasure15     = null,
            strMeasure16     = null,
            strMeasure17     = null,
            strMeasure18     = null,
            strMeasure19     = null,
            strMeasure20     = null
        )
    }

    @Test
    fun searchRecipesReturnsMappedRecipesAndCachesThem() = runTest {
        val dtos = listOf(
            dto("1","Taco","inst","", "Ing1","M1"),
            dto("2","Burrito","inst","", "Ing2","M2")
        )
        fakeApi.searchMealsResponse = MealResponse(meals = dtos)

        val result = repo.searchRecipes("anything")
        val expected = dtos.map { it.toRecipe() }

        assertEquals(expected, result)
        assertEquals(expected, fakeDao.inserted)
    }

    @Test
    fun searchRecipesOnIOExceptionReturnsCached() = runTest {
        fakeApi.shouldThrowSearchIOException = true
        val cached = listOf(Recipe("c1","Cached","","",null,null,null,null, emptyList()))
        fakeDao.cached = cached

        val result = repo.searchRecipes("q")
        assertEquals(cached, result)
        assertTrue(fakeDao.inserted.isEmpty())
    }

    @Test
    fun searchRecipesOnExceptionReturnsEmpty() = runTest {
        fakeApi.shouldThrowSearchGeneric = true
        fakeDao.cached = listOf(Recipe("c2","X","","",null,null,null,null, emptyList()))

        val result = repo.searchRecipes("q")
        assertTrue(result.isEmpty())
        assertTrue(fakeDao.inserted.isEmpty())
    }

    @Test
    fun getAllRecipesReturnsMappedRecipesAndCachesThem() = runTest {
        val dtos = listOf(
            dto("10","Soup","inst","", "I1","M1"),
            dto("20","Salad","inst","", "I2","M2")
        )
        fakeApi.getAllRecipesResponse = MealResponse(meals = dtos)

        val result = repo.getAllRecipes()
        val expected = dtos.map { it.toRecipe() }

        assertEquals(expected, result)
        assertEquals(expected, fakeDao.inserted)
    }

    @Test
    fun getAllRecipesOnIOExceptionReturnsCached() = runTest {
        fakeApi.shouldThrowGetAllIOException = true
        val cached = listOf(Recipe("d1","CachedAll","","",null,null,null,null, emptyList()))
        fakeDao.cached = cached

        val result = repo.getAllRecipes()
        assertEquals(cached, result)
        assertTrue(fakeDao.inserted.isEmpty())
    }

    @Test
    fun getAllRecipesOnExceptionReturnsEmpty() = runTest {
        fakeApi.shouldThrowGetAllGeneric = true
        fakeDao.cached = listOf(Recipe("d2","Y","","",null,null,null,null, emptyList()))

        val result = repo.getAllRecipes()
        assertTrue(result.isEmpty())
        assertTrue(fakeDao.inserted.isEmpty())
    }
}


class FakeRecipeAPIService : RecipeAPIService {
    var searchMealsResponse: MealResponse? = null
    var shouldThrowSearchIOException = false
    var shouldThrowSearchGeneric = false

    var getAllRecipesResponse: MealResponse? = null
    var shouldThrowGetAllIOException = false
    var shouldThrowGetAllGeneric = false

    override suspend fun searchMeals(query: String): MealResponse {
        if (shouldThrowSearchIOException) throw IOException("network error")
        if (shouldThrowSearchGeneric) throw RuntimeException("boom")
        return searchMealsResponse ?: MealResponse(meals = emptyList())
    }

    override suspend fun getAllRecipes(search: String): MealResponse {
        if (shouldThrowGetAllIOException) throw IOException("network error")
        if (shouldThrowGetAllGeneric) throw RuntimeException("boom")
        return getAllRecipesResponse ?: MealResponse(meals = emptyList())
    }
}

class FakeRecipeDAO : RecipeDAO {
    var inserted: List<Recipe> = emptyList()
    var cached: List<Recipe> = emptyList()

    override suspend fun insertRecipes(recipes: List<Recipe>) {
        inserted = recipes
    }

    override suspend fun getAllRecipes(): List<Recipe> {
        return cached
    }

    override suspend fun getRecipeById(id: String) = null
}
