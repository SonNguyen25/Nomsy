package com.example.nomsy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nomsy.data.local.entities.Food
import com.example.nomsy.ui.theme.NomsyColors
import com.example.nomsy.viewModels.IFoodViewModel


@Composable
fun ManualInputForm(
    foodName: String, onFoodNameChange: (String) -> Unit,
    calories: String, onCaloriesChange: (String) -> Unit,
    protein: String, onProteinChange: (String) -> Unit,
    carbs: String, onCarbsChange: (String) -> Unit,
    fat: String, onFatChange: (String) -> Unit,
    mealType: String, onMealTypeChange: (String) -> Unit,
    calPercent: Float,
    proteinPercent: Float,
    carbsPercent: Float,
    fatPercent: Float,
    onSelectFood: (Food) -> Unit,
    viewModel: IFoodViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        LaunchedEffect(Unit) {
            if (viewModel.allFoods.isEmpty()) {
                viewModel.fetchAllFoods()
            }
        }

        SearchFoodDropdown(
            viewModel = viewModel,
            foodName = foodName,
            onSelectFood = onSelectFood,
            onQueryChange = onFoodNameChange
        )

        LabeledInputRow("Name:", foodName, onFoodNameChange)
        LabeledInputRow("Calories:", calories, onCaloriesChange, unit = "kcal", isNumeric = true)
        LabeledInputRow("Protein:", protein, onProteinChange, unit = "g", isNumeric = true)
        LabeledInputRow("Carbs:", carbs, onCarbsChange, unit = "g", isNumeric = true)
        LabeledInputRow("Fats:", fat, onFatChange, unit = "g", isNumeric = true)

        MealTypeSelector(
            selectedMealType = mealType,
            onMealTypeChange = onMealTypeChange
        )

        Text(
            "Daily Goals Completion",
            color = NomsyColors.Title,
            fontWeight = FontWeight.Bold,
            style = androidx.compose.material.MaterialTheme.typography.h6,
        )

        // Wrapped 2 per row
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutrientCircle("Calories", calPercent, NomsyColors.Title, modifier = Modifier.weight(1f))
                NutrientCircle("Protein", proteinPercent, NomsyColors.Title, modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutrientCircle("Carbs", carbsPercent, NomsyColors.Title, modifier = Modifier.weight(1f))
                NutrientCircle("Fat", fatPercent, NomsyColors.Title, modifier = Modifier.weight(1f))
            }
        }
    }
}