package com.example.nomsy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.nomsy.ui.theme.NomsyColors


@Composable
fun LabeledInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String? = null,
    isNumeric: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("LabeledInputRow")
    ) {
        Text(
            text = label,
            modifier = Modifier
                .width(80.dp)
                .testTag("InputLabel"),
            color = NomsyColors.Texts
        )

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (!isNumeric || newValue.all { it.isDigit() }) {
                    onValueChange(newValue)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (isNumeric) KeyboardType.Number else KeyboardType.Text
            ),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .padding(end = if (unit != null) 8.dp else 0.dp)
                .testTag("InputField"),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = NomsyColors.Texts,
                cursorColor = NomsyColors.Title,
                focusedBorderColor = NomsyColors.Title,
                unfocusedBorderColor = NomsyColors.Subtitle,
                backgroundColor = NomsyColors.PictureBackground
            )
        )

        if (unit != null) {
            Text(
                text = unit,
                color = NomsyColors.Texts,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .width(32.dp)
                    .testTag("InputUnit"),
            )
        }
    }
}
