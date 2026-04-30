package com.example.enerlex.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enerlex.data.model.EnergyReading
import com.example.enerlex.ui.theme.EnerBarHigh
import com.example.enerlex.ui.theme.EnerBarNormal
import com.example.enerlex.ui.theme.EnerBarPeak

/**
 * Gráfico de barras para el consumo de las últimas 24 horas.
 * Los colores cambian según la intensidad del consumo.
 * Muestra el porcentaje de consumo al pasar el dedo/cursor sobre cada barra.
 */
@Composable
fun EnergyBarChart(
    readings: List<EnergyReading>,
    modifier: Modifier = Modifier
) {
    if (readings.isEmpty()) return

    // Limitamos la escala máxima a 4000W para que la gráfica refleje cambios de altura y color reales
    // basándose en la capacidad total de la casa. Si se pasa de 4000, entonces se escala a su máximo.
    val maxWatts = 4000f.coerceAtLeast(readings.maxOf { it.watts }.coerceAtLeast(1f))
    val totalWatts = readings.sumOf { it.watts.toDouble() }.toFloat().coerceAtLeast(1f)
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    BoxWithConstraints(modifier = modifier) {
        val barCount = readings.size
        val gap = 3.dp

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(readings) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull()
                            if (change != null) {
                                if (change.pressed) {
                                    val x = change.position.x
                                    val gapPx = gap.toPx()
                                    val barWidthPx = (size.width - gapPx * (barCount - 1)) / barCount
                                    val index = (x / (barWidthPx + gapPx)).toInt()
                                    if (index in readings.indices) {
                                        selectedIndex = index
                                    }
                                } else {
                                    selectedIndex = null
                                }
                            }
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val gapPx = gap.toPx()
            val barWidthPx = (canvasWidth - gapPx * (barCount - 1)) / barCount

            readings.forEachIndexed { index, reading ->
                val normalizedHeight = (reading.watts / maxWatts) * canvasHeight
                val x = index * (barWidthPx + gapPx)
                val y = canvasHeight - normalizedHeight

                val isSelected = index == selectedIndex
                
                // Colores basados en el consumo real absoluto.
                // "Excesivo" se considera más de 3500W, y "Alto" más de 2000W.
                val baseColor = when {
                    reading.watts > 3500f -> EnerBarPeak
                    reading.watts > 2000f -> EnerBarHigh
                    else -> EnerBarNormal
                }
                
                val barColor = if (isSelected) baseColor.copy(alpha = 0.6f) else baseColor

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidthPx, normalizedHeight),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )
            }
        }

        selectedIndex?.let { index ->
            val reading = readings[index]
            val percentage = (reading.watts / totalWatts) * 100
            
            val barWidthDp = (maxWidth - gap * (barCount - 1)) / barCount
            val xOffset = (index * (barWidthDp.value + gap.value)).dp

            val normalizedHeight = (reading.watts / maxWatts) * maxHeight.value
            var yOffset = (maxHeight.value - normalizedHeight).dp - 28.dp
            if (yOffset < 0.dp) yOffset = 0.dp

            var finalXOffset = xOffset + barWidthDp / 2 - 20.dp
            if (finalXOffset < 0.dp) finalXOffset = 0.dp
            if (finalXOffset > maxWidth - 40.dp) finalXOffset = maxWidth - 40.dp

            Box(
                modifier = Modifier
                    .offset(x = finalXOffset, y = yOffset)
                    .background(Color(0xFF2C2F33), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = String.format("%.1f%%", percentage),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
