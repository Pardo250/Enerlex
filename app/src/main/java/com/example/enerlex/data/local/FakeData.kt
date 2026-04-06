package com.example.enerlex.data.local

import com.example.enerlex.data.model.Alert
import com.example.enerlex.data.model.AlertSeverity
import com.example.enerlex.data.model.Device
import com.example.enerlex.data.model.DeviceIcon
import com.example.enerlex.data.model.EnergyReading

/**
 * Datos de muestra que simulan la respuesta de un backend / base de datos local.
 * En fases posteriores este objeto será reemplazado por llamadas a una API real o Room.
 */
object FakeData {

    val devices = mutableListOf(
        Device(
            id = "1",
            name = "TV Sala",
            room = "Sala",
            isOn = true,
            currentWatts = 120,
            todayKwh = 2.1,
            iconType = DeviceIcon.TV,
            schedule = "06:00 - 23:00 · L-V"
        ),
        Device(
            id = "2",
            name = "Nevera",
            room = "Cocina",
            isOn = true,
            currentWatts = 340,
            todayKwh = 8.3,
            iconType = DeviceIcon.REFRIGERATOR
        ),
        Device(
            id = "3",
            name = "PC Oficina",
            room = "Oficina",
            isOn = true,
            currentWatts = 85,
            todayKwh = 1.4,
            iconType = DeviceIcon.COMPUTER,
            schedule = "08:00 - 22:00 · L-V"
        ),
        Device(
            id = "4",
            name = "Lámpara Cuarto",
            room = "Habitación",
            isOn = true,
            currentWatts = 15,
            todayKwh = 0.3,
            iconType = DeviceIcon.LAMP
        ),
        Device(
            id = "5",
            name = "Microondas",
            room = "Cocina",
            isOn = false,
            currentWatts = 0,
            todayKwh = 0.5,
            iconType = DeviceIcon.MICROWAVE
        ),
        Device(
            id = "6",
            name = "Ventilador",
            room = "Sala",
            isOn = false,
            currentWatts = 0,
            todayKwh = 0.0,
            iconType = DeviceIcon.FAN
        ),
        Device(
            id = "7",
            name = "Lavadora",
            room = "Lavandería",
            isOn = false,
            currentWatts = 0,
            todayKwh = 1.2,
            iconType = DeviceIcon.WASHER
        ),
        Device(
            id = "8",
            name = "Aire Acondicionado",
            room = "Habitación",
            isOn = false,
            currentWatts = 0,
            todayKwh = 0.0,
            iconType = DeviceIcon.AC
        )
    )

    val alerts = listOf(
        Alert(
            id = "1",
            deviceName = "Nevera",
            message = "Consumo 40% sobre el límite",
            timeAgo = "Hace 15 min",
            severity = AlertSeverity.CRITICAL,
            recommendation = "Apaga la Nevera cuando no la uses. Ahorro: \$2.400/mes"
        ),
        Alert(
            id = "2",
            deviceName = "TV Sala",
            message = "Encendida más de 8 horas",
            timeAgo = "Hace 1 hora",
            severity = AlertSeverity.WARNING,
            recommendation = "Programa el apagado automático de la TV Sala a las 22:00."
        ),
        Alert(
            id = "3",
            deviceName = "PC Oficina",
            message = "Consumo nocturno detectado",
            timeAgo = "Hace 2 horas",
            severity = AlertSeverity.WARNING
        ),
        Alert(
            id = "4",
            deviceName = "Sistema",
            message = "Ventilador sin conexión",
            timeAgo = "Hace 5 horas",
            severity = AlertSeverity.SYSTEM
        )
    )

    /** Lecturas de las últimas 24 horas para el gráfico del dashboard */
    val last24HoursReadings = listOf(
        EnergyReading(0, 180f, "0h"),
        EnergyReading(1, 120f, "1h"),
        EnergyReading(2, 90f, "2h"),
        EnergyReading(3, 80f, "3h"),
        EnergyReading(4, 85f, "4h"),
        EnergyReading(5, 110f, "5h"),
        EnergyReading(6, 220f, "6h"),
        EnergyReading(7, 380f, "7h"),
        EnergyReading(8, 420f, "8h"),
        EnergyReading(9, 450f, "9h"),
        EnergyReading(10, 390f, "10h"),
        EnergyReading(11, 410f, "11h"),
        EnergyReading(12, 500f, "12h"),
        EnergyReading(13, 480f, "13h"),
        EnergyReading(14, 350f, "14h"),
        EnergyReading(15, 320f, "15h"),
        EnergyReading(16, 400f, "16h"),
        EnergyReading(17, 560f, "17h"),
        EnergyReading(18, 620f, "18h"),
        EnergyReading(19, 590f, "19h"),
        EnergyReading(20, 510f, "20h"),
        EnergyReading(21, 430f, "21h"),
        EnergyReading(22, 290f, "22h"),
        EnergyReading(23, 210f, "23h")
    )

    /** Lecturas de la semana para el detalle de dispositivo */
    val weekReadings = listOf(
        EnergyReading(0, 100f, "L"),
        EnergyReading(1, 130f, "M"),
        EnergyReading(2, 115f, "Mi"),
        EnergyReading(3, 140f, "J"),
        EnergyReading(4, 160f, "V"),
        EnergyReading(5, 80f, "S"),
        EnergyReading(6, 70f, "D")
    )
}
