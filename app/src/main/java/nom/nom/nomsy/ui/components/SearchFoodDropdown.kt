package nom.nom.nomsy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import nom.nom.nomsy.data.local.entities.Food
import nom.nom.nomsy.ui.theme.NomsyColors
import nom.nom.nomsy.viewModels.IFoodViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SearchFoodDropdown(
    viewModel: IFoodViewModel,
    foodName: String,
    onSelectFood: (Food) -> Unit,
    onQueryChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = viewModel.searchResults
    val focusManager = LocalFocusManager.current

    var searchJob by remember { mutableStateOf<Job?>(null) }

    val coroutineScope = rememberCoroutineScope()


    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                onQueryChange(it)
                expanded = true

                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    delay(300)
                    viewModel.searchFoodsFromApi(it)
                }
            },
            placeholder = {
                Text("Search Food", color = NomsyColors.Texts.copy(alpha = 0.6f))
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .testTag("SearchTextField"),
            singleLine = true,
            shape = RoundedCornerShape(15.dp),
            textStyle = TextStyle(color = NomsyColors.Texts),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = NomsyColors.Texts,
                unfocusedBorderColor = NomsyColors.Texts,
                textColor = NomsyColors.Texts,
                cursorColor = NomsyColors.Texts,
                placeholderColor = NomsyColors.Texts.copy(alpha = 0.6f)
            ),
            trailingIcon = {
                Row {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            onQueryChange("")
                            expanded = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = NomsyColors.Subtitle)
                        }
                    }
                    IconButton(onClick = { focusManager.clearFocus() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = NomsyColors.Subtitle)
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    expanded = true
                }
            )
        )

        DropdownMenu(
            expanded = expanded && searchResults.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(300.dp)
                .background(NomsyColors.Background)
                .testTag("SearchDropdown")
        ) {
            searchResults.take(5).forEach { food ->
                Box(
                    modifier = Modifier
                        .testTag("SearchResult_${food.food_name}")
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .border(1.dp, NomsyColors.Title, RoundedCornerShape(8.dp))
                        .clickable {
                            onSelectFood(food)
                            searchQuery = food.food_name
                            expanded = false
                            focusManager.clearFocus()
                        }
                        .padding(12.dp)
                ) {
                    Text(food.food_name, color = NomsyColors.Texts)
                }
            }
        }
    }
}

