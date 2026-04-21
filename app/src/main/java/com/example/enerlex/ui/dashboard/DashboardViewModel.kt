package com.example.enerlex.ui.dashboard

import androidx.lifecycle.ViewModel
import com.example.enerlex.data.model.Alert
import com.example.enerlex.data.model.AlertSeverity
import com.example.enerlex.data.model.Device
import com.example.enerlex.data.model.DeviceIcon
import com.example.enerlex.data.repository.UserDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel : ViewModel() {

    private val userDataRepository = UserDataRepository()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadUserName()
        loadDashboardData()
    }

    /** Carga el nombre real del usuario desde Firestore / Firebase Auth */
    private fun loadUserName() {
        userDataRepository.getUserName { name ->
            _uiState.update { it.copy(userName = name) }
        }
    }

    /** Carga o crea estadísticas del dashboard para este usuario en Firestore */
    private fun loadDashboardData() {
        _uiState.update { it.copy(isLoading = true) }

        userDataRepository.loadOrCreateDashboardData { data ->
            // Construir lista de "top devices" a partir de los watts por usuario
            val topDevices = data.deviceWatts.entries
                .sortedByDescending { it.value ?: 0 }
                .take(2)
                .mapIndexed { idx, entry ->
                    Device(
                        id = "dash_${idx}",
                        name = entry.key,
                        room = "",
                        isOn = true,
                        currentWatts = entry.value ?: 0,
                        todayKwh = (entry.value ?: 0) / 1000.0 * 8,
                        iconType = when (entry.key) {
                            "Nevera"  -> DeviceIcon.REFRIGERATOR
                            "TV Sala" -> DeviceIcon.TV
                            "PC"      -> DeviceIcon.COMPUTER
                            "Lámpara" -> DeviceIcon.LAMP
                            else      -> DeviceIcon.OTHER
                        }
                    )
                }

            val alert = Alert(
                id = "dash_alert",
                deviceName = data.alertDevice,
                message = data.alertMessage,
                timeAgo = "Hace 15 min",
                severity = AlertSeverity.CRITICAL
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    totalKwhToday = data.totalKwhToday,
                    readings = data.readings,
                    topDevices = topDevices,
                    latestAlert = alert
                )
            }
        }
    }
}
