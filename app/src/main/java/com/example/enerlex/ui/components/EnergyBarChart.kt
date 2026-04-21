package com.example.enerlex.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.enerlex.data.model.EnergyReading
import com.example.enerlex.ui.theme.EnerBarHigh
import com.example.enerlex.ui.theme.EnerBarNormal
import com.example.enerlex.ui.theme.EnerBarPeak

/**
 * Gráfico de barras para el consumo de las últimas 24 horas.
 * Los colores cambian según la intensidad del consumo.
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

    Canvas(modifier = modifier) {
        val totalWidth = size.width
        val totalHeight = size.height
        val barCount = readings.size
        val gap = 3.dp.toPx()
        val barWidth = (totalWidth - gap * (barCount - 1)) / barCount

        readings.forEachIndexed { index, reading ->
            val normalizedHeight = (reading.watts / maxWatts) * totalHeight
            val x = index * (barWidth + gap)
            val y = totalHeight - normalizedHeight

            val barColor: Color = when {
                reading.watts / maxWatts > 0.75f -> EnerBarPeak
                reading.watts / maxWatts > 0.45f -> EnerBarHigh
                else -> EnerBarNormal
            }

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, normalizedHeight),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
        }
    }
}
