package com.example.enerlex.ui.devices

import androidx.lifecycle.ViewModel
import com.example.enerlex.data.model.Device
import com.example.enerlex.data.repository.FirestoreDeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DevicesViewModel : ViewModel() {

    private val repository = FirestoreDeviceRepository()

    private val _uiState = MutableStateFlow(DevicesUiState())
    val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

    init {
        loadDevices()
    }

    private fun loadDevices() {
        _uiState.update { it.copy(isLoading = true) }
        repository.observeDevices { devices ->
            _uiState.update { it.copy(devices = devices, isLoading = false) }
        }
    }

    fun onToggleDevice(deviceId: String) {
        val currentDevices = _uiState.value.devices
        repository.toggleDevice(deviceId, currentDevices) { updated ->
            _uiState.update { it.copy(devices = updated) }
        }
    }
}
