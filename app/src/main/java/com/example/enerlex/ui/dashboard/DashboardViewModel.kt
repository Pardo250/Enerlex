package com.example.enerlex.ui.dashboard

import androidx.lifecycle.ViewModel
import com.example.enerlex.data.model.Alert
import com.example.enerlex.data.model.AlertSeverity
import com.example.enerlex.data.model.EnergyReading
import com.example.enerlex.data.repository.FirestoreDeviceRepository
import com.example.enerlex.data.repository.UserDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel : ViewModel() {

    private val userDataRepository    = UserDataRepository()
    private val deviceRepository      = FirestoreDeviceRepository()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadUserName()
        loadFromDevices()
    }

    /** Nombre real desde Firestore / Auth */
    private fun loadUserName() {
        userDataRepository.getUserName { name ->
            _uiState.update { it.copy(userName = name) }
        }
    }

    /**
     * Carga los dispositivos del usuario desde Firestore y deriva:
     *  - totalKwhToday: suma de kWh de todos los dispositivos encendidos
     *  - readings 24h:  suma de consumo por hora de los encendidos
     *  - topDevices:    los 2 con mayor consumo en este momento
     *  - latestAlert:   el dispositivo de mayor consumo como alerta
     */
    fun loadFromDevices() {
        _uiState.update { it.copy(isLoading = true) }

        deviceRepository.observeDevices { devices ->
            // ── Total kWh del día (solo dispositivos ON) ─────────────────
            val totalKwh = devices.filter { it.isOn }.sumOf { it.todayKwh }

            // ── Gráfica 24h: suma de watts por hora ───────────────────────
            val readings = (0..23).map { h ->
                val totalWatts = devices.filter { it.isOn }.sumOf { device ->
                    val factor = when (h) {
                        in 0..5   -> 0.3
                        in 6..8   -> 0.7
                        in 9..12  -> 0.9
                        in 13..17 -> 1.0
                        in 18..21 -> 0.85
                        else      -> 0.5
                    }
                    device.currentWatts * factor
                }
                EnergyReading(h, totalWatts.toFloat(), "${h}h")
            }

            // ── Top 2 dispositivos encendidos por mayor consumo ───────────
            val topDevices = devices
                .filter { it.isOn }
                .sortedByDescending { it.currentWatts }
                .take(2)

            // ── Alerta: dispositivo de mayor consumo ──────────────────────
            val worstDevice = devices.filter { it.isOn }
                .maxByOrNull { it.currentWatts }

            val alert = worstDevice?.let {
                val msg = when {
                    it.currentWatts >= 1000 -> "Consumo muy alto: ${it.currentWatts}W"
                    it.currentWatts >= 300  -> "Consumo 40% sobre el límite"
                    else                    -> "Encendido más de 8 horas"
                }
                val severity = when {
                    it.currentWatts >= 1000 -> AlertSeverity.CRITICAL
                    it.currentWatts >= 300  -> AlertSeverity.WARNING
                    else                    -> AlertSeverity.INFO
                }
                Alert(
                    id         = "dash_alert",
                    deviceName = it.name,
                    message    = msg,
                    timeAgo    = "Hace 15 min",
                    severity   = severity
                )
            }

            _uiState.update {
                it.copy(
                    isLoading     = false,
                    totalKwhToday = Math.round(totalKwh * 10.0) / 10.0,
                    readings      = readings,
                    topDevices    = topDevices,
                    latestAlert   = alert
                )
            }
        }
    }
}
