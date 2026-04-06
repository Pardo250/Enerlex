package com.example.enerlex.ui.alerts

import com.example.enerlex.data.model.Alert

data class AlertsUiState(
    val alerts: List<Alert> = emptyList(),
    val recommendations: List<Alert> = emptyList()
)
