package nom.nom.nomsy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nom.nom.nomsy.ui.theme.NomsyColors

@Composable
fun OnboardingBaseScreen(
    content: @Composable () -> Unit,
    buttonText: String,
    onNextClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NomsyColors.Background)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            content()

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = NomsyColors.Title,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}