package nom.nom.nomsy.viewModels

import nom.nom.nomsy.data.local.entities.Recipe
import kotlinx.coroutines.flow.StateFlow

interface IRecipeViewModel {
    val recipes: StateFlow<List<Recipe>>
    val recipesByCategory: StateFlow<Map<String, List<Recipe>>>

    fun search(query: String)
    fun loadAllRecipes()
}