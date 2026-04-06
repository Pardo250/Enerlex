package com.example.enerlex.ui.devices

import androidx.lifecycle.ViewModel
import com.example.enerlex.data.repository.DeviceRepository
import com.example.enerlex.data.repository.DeviceRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DevicesViewModel(
    private val repository: DeviceRepository = DeviceRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevicesUiState())
    val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

    init {
        loadDevices()
    }

    private fun loadDevices() {
        _uiState.update { it.copy(devices = repository.getDevices()) }
    }

    fun onToggleDevice(deviceId: String) {
        val updated = repository.toggleDevice(deviceId)
        _uiState.update { it.copy(devices = updated) }
    }
}
