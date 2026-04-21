package com.example.enerlex.ui.dashboard

import com.example.enerlex.data.model.Alert
import com.example.enerlex.data.model.Device
import com.example.enerlex.data.model.EnergyReading

data class DashboardUiState(
    val userName: String = "",
    val totalKwhToday: Double = 0.0,
    val topDevices: List<Device> = emptyList(),
    val latestAlert: Alert? = null,
    val readings: List<EnergyReading> = emptyList(),
    val isLoading: Boolean = true
)
