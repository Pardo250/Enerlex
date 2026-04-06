package com.example.enerlex.ui.devicedetail

import com.example.enerlex.data.model.Device
import com.example.enerlex.data.model.EnergyReading

data class DeviceDetailUiState(
    val device: Device? = null,
    val readings: List<EnergyReading> = emptyList(),
    val selectedPeriod: Period = Period.DAY,
    val estimatedCostToday: Double = 0.0,
    val isLoading: Boolean = false
)

enum class Period { DAY, WEEK, MONTH }
