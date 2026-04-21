package com.example.enerlex.ui.alerts

import androidx.lifecycle.ViewModel
import com.example.enerlex.data.model.Alert
import com.example.enerlex.data.model.AlertSeverity
import com.example.enerlex.data.repository.FirestoreDeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AlertsViewModel : ViewModel() {

    private val repository = FirestoreDeviceRepository()

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        observeAlerts()
    }

    private fun observeAlerts() {
        repository.observeDevices { devices ->
            val totalWatts = devices.filter { it.isOn }.sumOf { it.currentWatts }
            val activeCount = devices.count { it.isOn }
            
            val activeAlerts = mutableListOf<Alert>()

            // 1. Alertas globales (solo si aplican excesos)
            if (totalWatts >= 3500) {
                activeAlerts.add(
                    Alert(
                        id = "global_crit",
                        deviceName = "Sistema Global",
                        message = "¡Sobrecarga térmica! $activeCount disp. ($totalWatts W)",
                        timeAgo = "Ahora",
                        severity = AlertSeverity.CRITICAL,
                        recommendation = "Por favor, apaga o desconecta dispositivos de alto consumo para evitar una falla eléctrica."
                    )
                )
            } else if (totalWatts >= 2000) {
                activeAlerts.add(
                    Alert(
                        id = "global_warn",
                        deviceName = "Sistema Global",
                        message = "Consumo general alto: $totalWatts W",
                        timeAgo = "Ahora",
                        severity = AlertSeverity.WARNING,
                        recommendation = "Intenta apagar dispositivos grandes que no estés usando para reducir tu factura."
                    )
                )
            }

            // 2. Alertas especificas por dispositivo (solo rojos y amarillos reales, si está ON)
            devices.filter { it.isOn }.forEach { d ->
                if (d.currentWatts >= 1000) {
                    activeAlerts.add(
                        Alert(
                            id = "dev_crit_${d.id}",
                            deviceName = d.name,
                            message = "Consumo extremadamente alto (${d.currentWatts}W)",
                            timeAgo = "Ahora",
                            severity = AlertSeverity.CRITICAL,
                            recommendation = "Asegúrate de no dejar ${d.name} encendido si no es necesario."
                        )
                    )
                } else if (d.currentWatts >= 300) {
                    activeAlerts.add(
                        Alert(
                            id = "dev_warn_${d.id}",
                            deviceName = d.name,
                            message = "Consumo elevado y pesado detectado (${d.currentWatts}W)",
                            timeAgo = "Ahora",
                            severity = AlertSeverity.WARNING,
                            recommendation = "Revisa si puedes activar el modo ahorro en ${d.name}."
                        )
                    )
                }
            }

            val recommendations = activeAlerts.mapNotNull { it.recommendation }.distinct().mapIndexed { i, rec ->
                // Fakeamos unos Alerts tipo recomendacion
                Alert("rec_$i", "", "", "", AlertSeverity.INFO, rec)
            }

            _uiState.update {
                it.copy(
                    alerts = activeAlerts,
                    // Pasamos las recomendaciones a la ui list
                    recommendations = activeAlerts.filter { a -> a.recommendation != null }
                )
            }
        }
    }
}
