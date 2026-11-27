package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.firebase.configureAppCheck
import com.example.ui.TextbookApp
import com.example.ui.theme.TextbookTheme
import com.example.ui.theme.ThemePreferences
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1 (REQUIRED): Wire Firebase App Check with launch intent BEFORE UI, ViewModels, or Firebase calls
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            val scope = rememberCoroutineScope()
            val savedDarkMode by ThemePreferences.observeDarkMode(applicationContext)
                .collectAsStateWithLifecycle(initialValue = null)
            val darkTheme = savedDarkMode ?: isSystemInDarkTheme()

            TextbookTheme(darkTheme = darkTheme) {
                TextbookApp(
                    isDarkMode = darkTheme,
                    onToggleDarkMode = {
                        scope.launch {
                            ThemePreferences.setDarkMode(applicationContext, !darkTheme)
                        }
                    }
                )
            }
        }
    }
}
