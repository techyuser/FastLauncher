package com.fastlauncher.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fastlauncher.app.ui.screens.MainLauncherScreen
import com.fastlauncher.app.ui.theme.FastLauncherTheme
import com.fastlauncher.app.ui.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            FastLauncherTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: LauncherViewModel = viewModel()
                    MainLauncherScreen(
                        viewModel = viewModel,
                        onOpenApp = { intent ->
                            try {
                                startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        // Custom home screen behavior: do not exit launcher on back press
    }
}
