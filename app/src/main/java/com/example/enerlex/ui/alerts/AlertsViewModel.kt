package com.example.enerlex.ui.alerts

import androidx.lifecycle.ViewModel
import com.example.enerlex.data.repository.DeviceRepository
import com.example.enerlex.data.repository.DeviceRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AlertsViewModel(
    private val repository: DeviceRepository = DeviceRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        loadAlerts()
    }

    private fun loadAlerts() {
        val all = repository.getAlerts()
        val withRecommendation = all.filter { it.recommendation != null }
        _uiState.update {
            it.copy(alerts = all, recommendations = withRecommendation)
        }
    }
}
