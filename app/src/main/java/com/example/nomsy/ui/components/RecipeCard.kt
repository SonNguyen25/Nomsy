package com.example.nomsy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.nomsy.data.local.entities.Recipe
import com.example.nomsy.ui.theme.NomsyColors

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun recipeImage(imageUrl: String?, content: String?, modifier: Modifier = Modifier) {
    if (imageUrl.isNullOrBlank() || imageUrl == "null") {
        Box(
            modifier = modifier
                .background(Color.Gray)
        )
    } else {
        GlideImage(
            model = imageUrl,
            contentDescription = content,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(RoundedCornerShape(20.dp))
        )
    }
}

@Composable
fun recipesCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("RecipeCard"),
        shape = RoundedCornerShape(20.dp),
        elevation = 6.dp
    ) {
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
        ) {
            recipeImage(
                imageUrl = recipe.strMealThumb,
                content = recipe.strMeal,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("RecipeImage")
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("RecipeInfoBox")
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = recipe.strMeal ?: "",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = NomsyColors.Title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("RecipeTitle")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recipe.strTags?.replace(",", " • ") ?: "",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold,
                        color = NomsyColors.Subtitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("RecipeTags")
                    )
                }
            }

        }
    }
}
