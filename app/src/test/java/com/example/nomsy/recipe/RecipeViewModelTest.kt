package com.example.nomsy.recipe

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.nomsy.data.local.entities.Recipe
import com.example.nomsy.data.repository.IRecipeRepository
import com.example.nomsy.viewModels.RecipeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RecipeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mainDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeRepo: FakeRecipeRepo
    private lateinit var vm: RecipeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(mainDispatcher)
        fakeRepo = FakeRecipeRepo()
        vm = RecipeViewModel(repository = fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchUpdatesRecipesAndGrouping() = runTest {
        val r1 = Recipe(
            idMeal = "1",
            strMeal = "Apple Pie",
            strInstructions = "",
            strMealThumb = "",
            strYoutube = null,
            strCategory = "Dessert",
            strArea = null,
            strTags = null,
            ingredients = listOf("Apple")
        )
        val r2 = Recipe(
            idMeal = "2",
            strMeal = "Beef Stew",
            strInstructions = "",
            strMealThumb = "",
            strYoutube = null,
            strCategory = "Main",
            strArea = null,
            strTags = null,
            ingredients = listOf("Beef")
        )
        fakeRepo.searchResult = listOf(r1, r2)

        vm.search("anything")

        assertEquals(listOf(r1, r2), vm.recipes.value)

        val grouped = vm.recipesByCategory.value
        assertEquals(2, grouped.size)
        assertEquals(listOf(r1), grouped["Dessert"])
        assertEquals(listOf(r2), grouped["Main"])
    }

    @Test
    fun loadAllRecipesUpdatesRecipesAndGrouping() = runTest {
        val r1 = Recipe(
            idMeal = "A",
            strMeal = "Alpha",
            strInstructions = "",
            strMealThumb = "",
            strYoutube = null,
            strCategory = null,
            strArea = null,
            strTags = null,
            ingredients = emptyList()
        )
        val r2 = Recipe(
            idMeal = "B",
            strMeal = "Beta",
            strInstructions = "",
            strMealThumb = "",
            strYoutube = null,
            strCategory = "X",
            strArea = null,
            strTags = null,
            ingredients = emptyList()
        )
        fakeRepo.getAllResult = listOf(r1, r2)

        vm.loadAllRecipes()

        assertEquals(listOf(r1, r2), vm.recipes.value)

        val grouped = vm.recipesByCategory.value
        assertEquals(2, grouped.size)
        assertEquals(listOf(r1), grouped["Uncategorized"])
        assertEquals(listOf(r2), grouped["X"])
    }
}


private class FakeRecipeRepo : IRecipeRepository {
    var searchResult: List<Recipe> = emptyList()
    var getAllResult: List<Recipe> = emptyList()

    override suspend fun searchRecipes(query: String): List<Recipe> {
        return searchResult
    }

    override suspend fun getAllRecipes(): List<Recipe> {
        return getAllResult
    }
}
