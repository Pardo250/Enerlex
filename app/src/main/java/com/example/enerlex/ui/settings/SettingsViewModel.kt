package com.example.enerlex.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val userName: String = "Carlos Mendoza",
    val userEmail: String = "carlos@email.com",
    val userPlan: String = "Plan Hogar Premium",
    val consumptionLimit: Double = 15.0,    // kWh/día
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = true,
    val devicesLinked: Int = 8,
    val appVersion: String = "1.0.2"
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onToggleNotifications() {
        _uiState.update { it.copy(notificationsEnabled = !it.notificationsEnabled) }
    }

    fun onToggleDarkMode() {
        _uiState.update { it.copy(darkModeEnabled = !it.darkModeEnabled) }
    }
}
