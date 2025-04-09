import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.nomsy.models.Recipe
import com.bumptech.glide.integration.compose.GlideImage
import com.example.nomsy.ui.theme.NomsySubtitle
import com.example.nomsy.ui.theme.NomsyTexts
import com.example.nomsy.ui.theme.NomsyTitle
import androidx.compose.ui.tooling.preview.Preview

val placeholderRecipe = Recipe(
    strMeal = "Loading...",
    strCategory = "Unknown",
    strArea = "Unknown",
    strTags = "None",
    strMealThumb = "",
    idMeal = "",
    strInstructions = "",
    strYoutube = "",
    ingredients = emptyList()
)

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun recipesCard(recipe: Recipe) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Meal Image
            GlideImage(
                model = recipe.strMealThumb,
                contentDescription = recipe.strMeal,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(50.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // General Information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title
                Text(
                    text = recipe.strMeal,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = NomsyTitle
                )
                // Category
                Text(
                    text = recipe.strCategory ?: "",
                    style = MaterialTheme.typography.body1,
                    color = NomsySubtitle
                )
                // Area
                Text(
                    text = recipe.strArea ?: "",
                    style = MaterialTheme.typography.body2,
                    color = NomsyTexts
                )
                // Tags
                Text(
                    text = recipe.strTags ?: "",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    color = NomsyTexts
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun recipeCardPreview() {
    val sampleRecipe = Recipe(
        idMeal = "12345",
        strMeal = "Chicken Burger",
        strInstructions = "Just cook it well.",
        strMealThumb = "https://www.themealdb.com/images/media/meals/qptpvt1487339892.jpg",
        strYoutube = null,
        strCategory = "Fast Food",
        strArea = "American",
        strTags = "Burger,Grill",
        ingredients = listOf("Chicken", "Bun", "Lettuce", "Tomato", "Cheese")
    )

    recipesCard(recipe = sampleRecipe)
}
