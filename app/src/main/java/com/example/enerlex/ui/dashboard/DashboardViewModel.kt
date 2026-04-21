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

            // ── Alertas Inteligentes basadas en consumo global y dispositivos conectados ──
            val totalCurrentWatts = devices.filter { it.isOn }.sumOf { it.currentWatts }
            val activeDevicesCount = devices.count { it.isOn }
            val worstDevice = devices.filter { it.isOn }.maxByOrNull { it.currentWatts }

            val alert = when {
                totalCurrentWatts >= 3500 -> {
                    Alert(
                        id = "dash_alert_global_crit",
                        deviceName = "Sistema Global",
                        message = "¡Sobrecarga térmica! $activeDevicesCount dispositivos encendidos ($totalCurrentWatts W)",
                        timeAgo = "Ahora mismo",
                        severity = AlertSeverity.CRITICAL
                    )
                }
                totalCurrentWatts >= 2000 -> {
                    Alert(
                        id = "dash_alert_global_warn",
                        deviceName = "Sistema Global",
                        message = "Consumo total alto ($totalCurrentWatts W)",
                        timeAgo = "Hace un momento",
                        severity = AlertSeverity.WARNING
                    )
                }
                worstDevice != null && worstDevice.currentWatts >= 1000 -> {
                    Alert(
                        id = "dash_alert_device_warn",
                        deviceName = worstDevice.name,
                        message = "Consumo individual elevado: ${worstDevice.currentWatts}W",
                        timeAgo = "Hace 10 min",
                        severity = AlertSeverity.WARNING
                    )
                }
                worstDevice != null && worstDevice.currentWatts >= 300 -> {
                    Alert(
                        id = "dash_alert_device_info",
                        deviceName = worstDevice.name,
                        message = "Uso continuo de energía (${worstDevice.currentWatts}W)",
                        timeAgo = "Hace 15 min",
                        severity = AlertSeverity.INFO
                    )
                }
                else -> null
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
