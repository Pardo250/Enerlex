package com.example.enerlex.ui.devices

import com.example.enerlex.data.model.Device

data class DevicesUiState(
    val devices: List<Device> = emptyList(),
    val isLoading: Boolean = false
)
