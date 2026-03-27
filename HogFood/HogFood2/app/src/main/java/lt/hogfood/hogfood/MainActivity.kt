package lt.hogfood.hogfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import lt.hogfood.hogfood.navigation.AppNavGraph
import lt.hogfood.hogfood.ui.theme.HogFoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HogFoodTheme {
                AppNavGraph()
            }
        }
    }
}