package com.example.enerlex.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.enerlex.data.model.EnergyReading
import com.example.enerlex.ui.theme.EnerGreen

/**
 * Gráfico de línea suavizado para el detalle de dispositivo.
 * Incluye relleno degradado debajo de la curva.
 */
@Composable
fun EnergyLineChart(
    readings: List<EnergyReading>,
    lineColor: Color = EnerGreen,
    modifier: Modifier = Modifier
) {
    if (readings.size < 2) return

    val maxWatts = readings.maxOf { it.watts }.coerceAtLeast(1f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val stepX = width / (readings.size - 1)

        // Puntos de la curva
        val points = readings.mapIndexed { i, r ->
            Offset(
                x = i * stepX,
                y = height - (r.watts / maxWatts) * height
            )
        }

        // Path de la línea
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val curr = points[i]
                val cpX = (prev.x + curr.x) / 2f
                cubicTo(cpX, prev.y, cpX, curr.y, curr.x, curr.y)
            }
        }

        // Path del relleno (cierra hacia la base)
        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(points.last().x, height)
            lineTo(points.first().x, height)
            close()
        }

        // Dibujar relleno con degradado
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    lineColor.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = height
            )
        )

        // Dibujar línea
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(
                width = 3f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Puntos en cada vértice
        points.forEach { point ->
            drawCircle(
                color = lineColor,
                radius = 4f,
                center = point
            )
        }
    }
}
