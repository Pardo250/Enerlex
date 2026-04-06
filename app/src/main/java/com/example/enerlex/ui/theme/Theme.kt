package com.example.enerlex.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// EnerFlow usa siempre dark theme (según los mockups)
private val EnerDarkColorScheme = darkColorScheme(
    primary          = EnerGreen,
    onPrimary        = Color(0xFF004D3A),
    primaryContainer = Color(0xFF00372A),
    onPrimaryContainer = EnerGreen,

    secondary        = EnerYellow,
    onSecondary      = Color(0xFF3D2700),

    tertiary         = EnerRed,
    onTertiary       = Color(0xFF5C0010),

    background       = EnerBackground,
    onBackground     = EnerTextPrimary,

    surface          = EnerSurface,
    onSurface        = EnerTextPrimary,
    surfaceVariant   = EnerSurfaceVariant,
    onSurfaceVariant = EnerTextSecondary,

    outline          = EnerBorder,
    outlineVariant   = EnerDivider,

    error            = EnerRed,
    onError          = Color.White,
    errorContainer   = EnerRedDim,
    onErrorContainer = EnerRed
)

@Composable
fun EnerlexTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EnerDarkColorScheme,
        typography  = Typography,
        content     = content
    )
}