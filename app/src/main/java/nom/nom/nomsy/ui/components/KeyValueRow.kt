package nom.nom.nomsy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nom.nom.nomsy.ui.theme.NomsyColors

@Composable
fun KeyValueRow(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color = NomsyColors.Texts
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, color = color, modifier = Modifier.testTag("key-value"))
        Text(text = value, fontSize = 16.sp, color = color, modifier = Modifier.testTag("key-value"))
    }
}