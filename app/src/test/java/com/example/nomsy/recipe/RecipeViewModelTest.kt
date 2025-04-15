package com.example.nomsy.recipe

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Rule
import com.example.nomsy.data.local.models.Recipe
import com.example.nomsy.utils.Result
import com.example.nomsy.viewModels.RecipeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class RecipeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeRecipeRepository: FakeRecipeRepository
    private lateinit var recipeViewModel: RecipeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRecipeRepository = FakeRecipeRepository()
        recipeViewModel = RecipeViewModel(fakeRecipeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search() updates recipes and recipesByCategory correctly`() = runTest {
        val mockQuery = "Pasta"
        val mockResult = listOf(
            Recipe(idMeal = "1", strMeal = "Spaghetti", strCategory = "Italian"),
            Recipe(idMeal = "2", strMeal = "Fettuccine", strCategory = "Italian")
        )

        fakeRecipeRepository.setSearchResult(mockQuery, mockResult)

        recipeViewModel.search(mockQuery)
        advanceUntilIdle()

        val recipes = recipeViewModel.recipes.value
        val grouped = recipeViewModel.recipesByCategory.value

        assertEquals(2, recipes.size)
        assertEquals("Spaghetti", recipes[0].strMeal)
        assertEquals(1, grouped.size)
        assertEquals(2, grouped["Italian"]?.size)
    }

    @Test
    fun `loadAllRecipes() loads all recipes and groups them`() = runTest {
        val allRecipes = listOf(
            Recipe(idMeal = "1", strMeal = "Salad", strCategory = "Healthy"),
            Recipe(idMeal = "2", strMeal = "Pizza", strCategory = "Fast Food"),
            Recipe(idMeal = "3", strMeal = "Soup", strCategory = null)
        )

        fakeRecipeRepository.setAllRecipes(allRecipes)

        recipeViewModel.loadAllRecipes()
        advanceUntilIdle()

        val recipes = recipeViewModel.recipes.value
        val categories = recipeViewModel.recipesByCategory.value

        assertEquals(3, recipes.size)
        assertEquals(3, categories.values.sumOf { it.size })
        assertTrue(categories.containsKey("Uncategorized"))
        assertEquals("Soup", categories["Uncategorized"]?.first()?.strMeal)
    }
}
