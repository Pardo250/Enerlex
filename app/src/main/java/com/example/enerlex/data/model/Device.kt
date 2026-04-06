package com.example.enerlex.data.model

/**
 * Representa un dispositivo conectado a una toma corriente inteligente.
 */
data class Device(
    val id: String,
    val name: String,
    val room: String,
    val isOn: Boolean,
    val currentWatts: Int,        // Consumo actual en Watts
    val todayKwh: Double,         // kWh consumidos hoy
    val iconType: DeviceIcon,     // Tipo de icono a mostrar
    val schedule: String? = null  // Ej: "06:00 - 23:00 · L-V"
)

enum class DeviceIcon {
    TV, REFRIGERATOR, COMPUTER, LAMP, MICROWAVE, FAN, WASHER, AC, OTHER
}
