package nom.nom.nomsy.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import nom.nom.nomsy.data.repository.RecipeRepository

class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecipeViewModel(repository) as T
    }
}