package nom.nom.nomsy.responses

import nom.nom.nomsy.data.local.models.RecipeDto

data class MealResponse(
    val meals: List<RecipeDto>?
)
