import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.nomsy.models.Recipe
import com.example.nomsy.ui.theme.NomsySubtitle
import com.example.nomsy.ui.theme.NomsyTexts
import com.example.nomsy.ui.theme.NomsyTitle
import com.example.nomsy.ui.theme.NomsyBackground

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun recipeImage(imageUrl: String, description: String?) {
    val isInPreview = LocalInspectionMode.current

    if (isInPreview) {
        // Use gray placeholder in preview
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(NomsyBackground)
        )
    } else {
        // Use Glide at runtime
        GlideImage(
            model = imageUrl,
            contentDescription = description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(50.dp))
        )
    }
}

@Composable
fun recipesCard(recipe: Recipe) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = 6.dp,
        backgroundColor = Color(0xFFE3F2FD)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            recipeImage(recipe.strMealThumb, recipe.strMeal)

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recipe.strMeal,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = NomsyTitle
                )
                Text(
                    text = recipe.strCategory ?: "",
                    style = MaterialTheme.typography.body1,
                    color = NomsySubtitle
                )
                Text(
                    text = recipe.strArea ?: "",
                    style = MaterialTheme.typography.body2,
                    color = NomsyTexts
                )
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
