package com.example.enerlex.ui.devicedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.enerlex.data.repository.DeviceRepository
import com.example.enerlex.data.repository.DeviceRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeviceDetailViewModel(
    private val deviceId: String,
    private val repository: DeviceRepository = DeviceRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceDetailUiState())
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()

    init {
        loadDevice()
    }

    private fun loadDevice() {
        val device = repository.getDeviceById(deviceId)
        val readings = repository.getLast24HoursReadings()
        // Costo estimado: kwh * $650 (tarifa ejemplo Colombia)
        val cost = (device?.todayKwh ?: 0.0) * 650

        _uiState.update {
            it.copy(
                device = device,
                readings = readings,
                estimatedCostToday = cost
            )
        }
    }

    fun onToggleDevice() {
        val deviceId = _uiState.value.device?.id ?: return
        val updated = repository.toggleDevice(deviceId)
        _uiState.update { it.copy(device = updated.find { d -> d.id == deviceId }) }
    }

    fun onPeriodSelected(period: Period) {
        val readings = when (period) {
            Period.DAY   -> repository.getLast24HoursReadings()
            Period.WEEK  -> repository.getWeekReadings()
            Period.MONTH -> repository.getWeekReadings() // Placeholder
        }
        _uiState.update { it.copy(selectedPeriod = period, readings = readings) }
    }

    /** Factory para pasar deviceId al ViewModel */
    class Factory(private val deviceId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DeviceDetailViewModel(deviceId) as T
        }
    }
}
