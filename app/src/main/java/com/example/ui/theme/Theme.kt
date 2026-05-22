package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = HerbalSage,
    onPrimary = Color.Black,
    primaryContainer = ForestGreen,
    onPrimaryContainer = Color.White,
    secondary = AccentTerraCotta,
    onSecondary = Color.White,
    tertiary = SunwarmGold,
    background = DeepOliveBg,
    onBackground = Color(0xFFE2E3DF),
    surface = DarkSageSurface,
    onSurface = Color(0xFFE2E3DF),
    surfaceVariant = Color(0xFF263028),
    onSurfaceVariant = Color(0xFFC2C8C2)
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0F0D8),
    onPrimaryContainer = ForestGreen,
    secondary = AccentTerraCotta,
    onSecondary = Color.White,
    tertiary = SunwarmGold,
    background = LightEarthy,
    onBackground = Color(0xFF1B1D1B),
    surface = CreamSurface,
    onSurface = Color(0xFF1B1D1B),
    surfaceVariant = Color(0xFFE1E5E0),
    onSurfaceVariant = Color(0xFF434943)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to respect our custom botanical branding by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
