import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.nomsy.models.Recipe
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun recipesCard(recipe: Recipe) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(25.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            GlideImage(
                model = recipe.strMealThumb,
                contentDescription = recipe.strMeal,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary)
                    .padding(12.dp)
            ) {
                Text(text = recipe.strMeal, style = MaterialTheme.typography.h6, color = Color.White)
                recipe.strCategory?.let {
                    Text(text = "Category: $it", style = MaterialTheme.typography.body2, color = Color.White)
                }
                recipe.strArea?.let {
                    Text(text = "Area: $it", style = MaterialTheme.typography.body2, color = Color.White)
                }
            }
        }
    }
}
