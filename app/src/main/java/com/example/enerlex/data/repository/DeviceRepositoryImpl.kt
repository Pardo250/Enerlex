package com.example.enerlex.data.repository

import com.example.enerlex.data.local.FakeData
import com.example.enerlex.data.model.Alert
import com.example.enerlex.data.model.Device
import com.example.enerlex.data.model.EnergyReading

/**
 * Implementación del repositorio usando datos fake locales.
 * En el futuro esta clase puede ser reemplazada por una implementación
 * que consuma una API REST o una base de datos Room.
 */
class DeviceRepositoryImpl : DeviceRepository {

    override fun getDevices(): List<Device> = FakeData.devices.toList()

    override fun getDeviceById(id: String): Device? =
        FakeData.devices.find { it.id == id }

    override fun toggleDevice(id: String): List<Device> {
        val index = FakeData.devices.indexOfFirst { it.id == id }
        if (index != -1) {
            val device = FakeData.devices[index]
            FakeData.devices[index] = device.copy(
                isOn = !device.isOn,
                currentWatts = if (device.isOn) 0 else device.currentWatts.coerceAtLeast(1)
            )
        }
        return FakeData.devices.toList()
    }

    override fun getAlerts(): List<Alert> = FakeData.alerts

    override fun getLast24HoursReadings(): List<EnergyReading> = FakeData.last24HoursReadings

    override fun getWeekReadings(): List<EnergyReading> = FakeData.weekReadings

    override fun getTotalKwhToday(): Double =
        FakeData.devices.sumOf { it.todayKwh }
}
