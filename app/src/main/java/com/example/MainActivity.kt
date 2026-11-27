package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.firebase.configureAppCheck
import com.example.ui.TextbookApp
import com.example.ui.i18n.AppLanguages
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.appStringsFor
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
            val savedLanguageCode by ThemePreferences.observeLanguageCode(applicationContext)
                .collectAsStateWithLifecycle(initialValue = "en")
            val darkTheme = savedDarkMode ?: isSystemInDarkTheme()
            val currentLanguage = remember(savedLanguageCode) { AppLanguages.byCode(savedLanguageCode) }
            val appStrings = remember(currentLanguage.code) { appStringsFor(currentLanguage.code) }
            val layoutDirection = if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            TextbookTheme(darkTheme = darkTheme) {
                CompositionLocalProvider(
                    LocalAppLanguage provides currentLanguage,
                    LocalAppStrings provides appStrings,
                    LocalLayoutDirection provides layoutDirection
                ) {
                    TextbookApp(
                        isDarkMode = darkTheme,
                        currentLanguage = currentLanguage,
                        onSelectLanguage = { lang ->
                            scope.launch {
                                ThemePreferences.setLanguageCode(applicationContext, lang.code)
                            }
                        },
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
}
