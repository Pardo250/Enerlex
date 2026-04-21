package com.example.enerlex.ui.settings

import androidx.lifecycle.ViewModel
import com.example.enerlex.ui.theme.ThemeState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val userName: String = FirebaseAuth.getInstance().currentUser?.displayName ?: "Usuario",
    val userEmail: String = FirebaseAuth.getInstance().currentUser?.email ?: "",
    val userPlan: String = "Plan Hogar Premium",
    val consumptionLimit: Double = 15.0,
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = ThemeState.isDarkMode,
    val devicesLinked: Int = 8,
    val appVersion: String = "1.0.2"
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onToggleNotifications() {
        _uiState.update { it.copy(notificationsEnabled = !it.notificationsEnabled) }
    }

    /** Cambia el tema globalmente para toda la app */
    fun onToggleDarkMode() {
        val newValue = !ThemeState.isDarkMode
        ThemeState.isDarkMode = newValue
        _uiState.update { it.copy(darkModeEnabled = newValue) }
    }
}
