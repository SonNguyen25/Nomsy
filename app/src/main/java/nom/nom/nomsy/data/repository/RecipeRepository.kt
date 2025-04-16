package nom.nom.nomsy.data.repository

import nom.nom.nomsy.data.local.dao.RecipeDAO
import nom.nom.nomsy.data.local.entities.Recipe
import nom.nom.nomsy.data.local.models.toRecipe
import nom.nom.nomsy.data.remote.RecipeAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class RecipeRepository(
    private val api: RecipeAPIService,
    private val dao: RecipeDAO
) : IRecipeRepository {
    override suspend fun searchRecipes(query: String): List<Recipe> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = api.searchMeals(query).meals?.map { it.toRecipe() }.orEmpty()
            dao.insertRecipes(result)
            result
        } catch (e: IOException) {
            e.printStackTrace()
            dao.getAllRecipes() // fallback to cache if no network
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getAllRecipes(): List<Recipe> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = api.getAllRecipes().meals?.map { it.toRecipe() }.orEmpty()
            dao.insertRecipes(result)
            result
        } catch (e: IOException) {
            e.printStackTrace()
            dao.getAllRecipes() // fallback to cached data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}