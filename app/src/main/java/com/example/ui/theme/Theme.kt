package com.example.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "textbook_theme_prefs")

object ThemePreferences {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")

    fun observeDarkMode(context: Context): Flow<Boolean?> =
        context.themeDataStore.data.map { prefs -> prefs[DARK_MODE_KEY] }

    suspend fun setDarkMode(context: Context, enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = NavyContainer,
    onPrimaryContainer = OnNavyContainer,
    secondary = EmeraldSecondary,
    onSecondary = SurfaceWhite,
    secondaryContainer = EmeraldContainer,
    onSecondaryContainer = OnEmeraldContainer,
    tertiary = AmberAccent,
    onTertiary = SurfaceWhite,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = OnAmberContainer,
    background = WarmWhiteBg,
    onBackground = DarkSlateText,
    surface = SurfaceWhite,
    onSurface = DarkSlateText,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = MutedSlateText,
    outline = OutlineLight,
    error = ErrorRed,
    errorContainer = ErrorContainerLight,
    onError = SurfaceWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = NavyPrimary,
    onPrimaryContainer = DarkTextPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = EmeraldDark,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = DarkTertiary,
    onTertiary = OnAmberContainer,
    tertiaryContainer = OnAmberContainer,
    onTertiaryContainer = AmberContainer,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextMuted,
    outline = DarkOutline,
    error = ErrorRed,
    onError = SurfaceWhite
)

val TextbookShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun TextbookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = TextbookTypography,
        shapes = TextbookShapes,
        content = content
    )
}
