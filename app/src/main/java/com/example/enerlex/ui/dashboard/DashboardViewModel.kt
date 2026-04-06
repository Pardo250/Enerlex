package com.example.enerlex.ui.dashboard

import androidx.lifecycle.ViewModel
import com.example.enerlex.data.repository.DeviceRepository
import com.example.enerlex.data.repository.DeviceRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel(
    private val repository: DeviceRepository = DeviceRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val devices = repository.getDevices()
        val topDevices = devices.filter { it.isOn }.sortedByDescending { it.currentWatts }.take(2)
        val latestAlert = repository.getAlerts().firstOrNull()
        val readings = repository.getLast24HoursReadings()
        val totalKwh = repository.getTotalKwhToday()

        _uiState.update {
            it.copy(
                totalKwhToday = totalKwh,
                topDevices = topDevices,
                latestAlert = latestAlert,
                readings = readings
            )
        }
    }
}
