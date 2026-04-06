package com.example.enerlex.ui.theme

import androidx.compose.ui.graphics.Color

// ─── PALETA ENERFLOW ────────────────────────────────────────────────────────

// Fondos
val EnerBackground       = Color(0xFF0D1117)  // Fondo principal (azul marino muy oscuro)
val EnerSurface          = Color(0xFF161B22)  // Cards y superficies
val EnerSurfaceVariant   = Color(0xFF1C2333)  // Cards secundarias / fields de entrada
val EnerSurfaceElevated  = Color(0xFF21293D)  // Cards con elevación

// Acento principal – verde néon
val EnerGreen            = Color(0xFF00D4AA)  // Toggles ON, botones primarios, valores
val EnerGreenDim         = Color(0xFF00D4AA).copy(alpha = 0.15f) // Fondo chip "eficiente"
val EnerGreenDark        = Color(0xFF00A882)  // Estado pressed

// Alertas y estados
val EnerYellow           = Color(0xFFF5A623)  // Alertas medias / wattios altos
val EnerOrange           = Color(0xFFFF6B35)  // Alertas naranjas (consumo alto)
val EnerRed              = Color(0xFFFF4757)  // Alertas críticas / "Cortar energía"
val EnerRedDim           = Color(0xFFFF4757).copy(alpha = 0.12f) // Fondo alerta crítica

// Indicadores de dispositivos (puntos de color en lista)
val EnerDeviceGreen      = Color(0xFF00D4AA)  // Dispositivo encendido – normal
val EnerDeviceYellow     = Color(0xFFF5A623)  // Dispositivo encendido – alto consumo
val EnerDeviceGray       = Color(0xFF4A5568)  // Dispositivo apagado

// Textos
val EnerTextPrimary      = Color(0xFFE8F4F8)  // Texto principal
val EnerTextSecondary    = Color(0xFF8B9DC3)  // Texto secundario / subtítulos
val EnerTextHint         = Color(0xFF4A5568)  // Placeholders y pistas

// Divisores / bordes
val EnerDivider          = Color(0xFF2D3748)  // Líneas divisorias
val EnerBorder           = Color(0xFF2D3748)  // Bordes de campos

// Botón Google
val EnerGoogleButton     = Color(0xFF1C2333)

// Colores de gráfico de barras
val EnerBarNormal        = Color(0xFF00D4AA)
val EnerBarHigh          = Color(0xFFF5A623)
val EnerBarPeak          = Color(0xFFFF4757)