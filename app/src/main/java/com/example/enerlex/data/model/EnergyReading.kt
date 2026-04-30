package com.example.enerlex.data.model

/**
 * Lectura de energía para un punto de tiempo en el gráfico de consumo.
 */
data class EnergyReading(
    val hour: Int,         // 0-23
    val watts: Float,      // Consumo en ese momento
    val label: String,     // Etiqueta visible (ej. "14h")
    val topDevice: String? = null // Dispositivo con mayor consumo en este periodo (opcional)
)
