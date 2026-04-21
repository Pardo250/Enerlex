package com.example.enerlex.ui.devicedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.enerlex.data.model.Device
import com.example.enerlex.data.repository.FirestoreDeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeviceDetailViewModel(
    private val deviceId: String,
    private val repository: FirestoreDeviceRepository = FirestoreDeviceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceDetailUiState())
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()

    init {
        loadDevice()
    }

    private fun loadDevice() {
        // Cargar todos los dispositivos del usuario y quedarnos con el que nos interesa
        repository.loadDevices { devices ->
            val device = devices.find { it.id == deviceId }
            updateStateForDevice(device, Period.DAY)
        }
    }

    private fun updateStateForDevice(device: Device?, period: Period) {
        if (device == null) return
        val readings = when (period) {
            Period.DAY   -> repository.getReadingsForDevice(device)
            Period.WEEK  -> repository.getWeekReadingsForDevice(device)
            Period.MONTH -> repository.getWeekReadingsForDevice(device) // placeholder mes
        }
        // Costo estimado: kWh × $650 (tarifa Colombia aproximada)
        val cost = device.todayKwh * 650.0

        _uiState.update {
            it.copy(
                device = device,
                readings = readings,
                estimatedCostToday = cost,
                selectedPeriod = period
            )
        }
    }

    fun onToggleDevice() {
        val current = _uiState.value.device ?: return
        val deviceList = listOfNotNull(current) + emptyList<Device>()

        // Primero cargar todos para tener la lista completa (necesaria para el repositorio)
        repository.loadDevices { allDevices ->
            repository.toggleDevice(current.id, allDevices) { updatedList ->
                val updated = updatedList.find { it.id == current.id }
                updateStateForDevice(updated, _uiState.value.selectedPeriod)
            }
        }
    }

    fun onPeriodSelected(period: Period) {
        val device = _uiState.value.device ?: return
        updateStateForDevice(device, period)
    }

    class Factory(private val deviceId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DeviceDetailViewModel(deviceId) as T
        }
    }
}
