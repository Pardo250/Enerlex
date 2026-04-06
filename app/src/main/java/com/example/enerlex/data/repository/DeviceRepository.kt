package com.example.enerlex.data.repository

import com.example.enerlex.data.model.Alert
import com.example.enerlex.data.model.Device
import com.example.enerlex.data.model.EnergyReading

/**
 * Contrato del repositorio de dispositivos.
 * Abstrae la fuente de datos (fake, Room, API) del resto de la app.
 */
interface DeviceRepository {
    fun getDevices(): List<Device>
    fun getDeviceById(id: String): Device?
    fun toggleDevice(id: String): List<Device>
    fun getAlerts(): List<Alert>
    fun getLast24HoursReadings(): List<EnergyReading>
    fun getWeekReadings(): List<EnergyReading>
    fun getTotalKwhToday(): Double
}
