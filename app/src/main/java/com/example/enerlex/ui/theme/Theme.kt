package com.example.enerlex.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// ─── Estado global del tema (observable desde cualquier parte) ────────────────
object ThemeState {
    var isDarkMode by mutableStateOf(true)
}

// ─── Esquema OSCURO ───────────────────────────────────────────────────────────
private val EnerDarkColorScheme = darkColorScheme(
    primary            = EnerGreen,
    onPrimary          = Color(0xFF004D3A),
    primaryContainer   = Color(0xFF00372A),
    onPrimaryContainer = EnerGreen,

    secondary          = EnerYellow,
    onSecondary        = Color(0xFF3D2700),

    tertiary           = EnerRed,
    onTertiary         = Color(0xFF5C0010),

    background         = EnerBackground,
    onBackground       = EnerTextPrimary,

    surface            = EnerSurface,
    onSurface          = EnerTextPrimary,
    surfaceVariant     = EnerSurfaceVariant,
    onSurfaceVariant   = EnerTextSecondary,

    outline            = EnerBorder,
    outlineVariant     = EnerDivider,

    error              = EnerRed,
    onError            = Color.White,
    errorContainer     = EnerRedDim,
    onErrorContainer   = EnerRed
)

// ─── Esquema CLARO ────────────────────────────────────────────────────────────
private val EnerLightColorScheme = lightColorScheme(
    primary            = EnerGreen,
    onPrimary          = Color(0xFF003D2E),
    primaryContainer   = Color(0xFFB2F5E4),
    onPrimaryContainer = Color(0xFF003D2E),

    secondary          = EnerYellow,
    onSecondary        = Color(0xFF3D2700),

    tertiary           = EnerRed,
    onTertiary         = Color.White,

    background         = EnerLightBackground,
    onBackground       = EnerLightTextPrimary,

    surface            = EnerLightSurface,
    onSurface          = EnerLightTextPrimary,
    surfaceVariant     = EnerLightSurfaceVariant,
    onSurfaceVariant   = EnerLightTextSecondary,

    outline            = EnerLightBorder,
    outlineVariant     = EnerLightDivider,

    error              = EnerRed,
    onError            = Color.White,
    errorContainer     = EnerRedDim,
    onErrorContainer   = EnerRed
)

@Composable
fun EnerlexTheme(
    darkMode: Boolean = ThemeState.isDarkMode,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkMode) EnerDarkColorScheme else EnerLightColorScheme,
        typography  = Typography,
        content     = content
    )
}