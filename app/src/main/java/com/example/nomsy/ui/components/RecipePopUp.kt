package com.example.nomsy.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.nomsy.data.local.entities.Recipe
import com.example.nomsy.ui.theme.NomsyColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.platform.testTag


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun recipePopUp(
    recipe: Recipe,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NomsyColors.Title, shape = RoundedCornerShape(8.dp))
                .background(NomsyColors.Background, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .testTag("RecipePopUp"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlideImage(
                model = recipe.strMealThumb,
                contentDescription = recipe.strMeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("RecipeImage")
            )

            Text(
                text = recipe.strMeal,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NomsyColors.Title,
                modifier = Modifier.testTag("RecipeTitle")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Category: ${recipe.strCategory}",
                color = NomsyColors.Subtitle,
                modifier = Modifier.testTag("RecipeCategory")
            )

            Text(
                text = "Area: ${recipe.strArea}",
                color = NomsyColors.Subtitle,
                modifier = Modifier.testTag("RecipeArea")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ingredients",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = NomsyColors.Title,
                modifier = Modifier
                    .align(Alignment.Start)
                    .testTag("IngredientsHeader")
            )

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("IngredientsList")
            ) {
                recipe.ingredients.forEachIndexed { index, ingredient ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NomsyColors.Title, RoundedCornerShape(8.dp))
                            .background(NomsyColors.Background)
                            .padding(12.dp)
                            .testTag("Ingredient_$index")
                    ) {
                        Text(
                            text = ingredient,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NomsyColors.Texts
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Instructions",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = NomsyColors.Title,
                modifier = Modifier
                    .align(Alignment.Start)
                    .testTag("InstructionsHeader")
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = recipe.strInstructions,
                color = NomsyColors.Texts,
                lineHeight = 20.sp,
                modifier = Modifier.testTag("RecipeInstructions")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                recipe.strYoutube?.let { url ->
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NomsyColors.Background),
                        border = BorderStroke(1.dp, NomsyColors.Title),
                        modifier = Modifier.testTag("WatchVideoButton")
                    ) {
                        Text(text = "Watch Video", color = NomsyColors.Title)
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("CloseButton")
                ) {
                    Text("Close", color = NomsyColors.Subtitle)
                }
            }
        }
    }
}
