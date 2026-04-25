package nz.keeleysgreenhouse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import nz.keeleysgreenhouse.app.ui.GreenhouseApp
import nz.keeleysgreenhouse.app.ui.theme.GreenhouseTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreenhouseTheme {
                GreenhouseApp()
            }
        }
    }
}
