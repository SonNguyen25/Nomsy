package nom.nom.nomsy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import nom.nom.nomsy.ui.navigation.NomsyAppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NomsyAppNavHost(rememberNavController())
        }
    }
}
