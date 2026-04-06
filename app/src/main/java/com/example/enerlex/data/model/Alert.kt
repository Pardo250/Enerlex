package com.example.enerlex.data.model

/**
 * Representa una alerta de consumo energético.
 */
data class Alert(
    val id: String,
    val deviceName: String,
    val message: String,
    val timeAgo: String,
    val severity: AlertSeverity,
    val recommendation: String? = null
)

enum class AlertSeverity {
    CRITICAL,   // Rojo – consumo sobre el límite
    WARNING,    // Amarillo – encendido muchas horas
    INFO,       // Gris – informativo
    SYSTEM      // Sin conexión / sistema
}
