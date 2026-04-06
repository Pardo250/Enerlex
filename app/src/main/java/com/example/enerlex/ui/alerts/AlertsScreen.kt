package com.example.enerlex.ui.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enerlex.data.model.Alert
import com.example.enerlex.data.model.AlertSeverity
import com.example.enerlex.ui.theme.*

/**
 * Pantalla Alertas – pantalla 05 del mockup.
 * Lista de alertas con nivel de severidad y sección de recomendaciones.
 */
@Composable
fun AlertsScreen(viewModel: AlertsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EnerBackground)
    ) {
        // ── Header ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Alertas",
                color = EnerTextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${uiState.alerts.size} alertas activas",
                color = EnerTextSecondary,
                fontSize = 13.sp
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Alertas
            items(uiState.alerts, key = { it.id }) { alert ->
                AlertCard(alert = alert)
            }

            // Recomendaciones
            if (uiState.recommendations.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "RECOMENDACIONES",
                        color = EnerTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(uiState.recommendations, key = { "rec_${it.id}" }) { alert ->
                    RecommendationCard(alert = alert)
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun AlertCard(alert: Alert) {
    val accentColor = when (alert.severity) {
        AlertSeverity.CRITICAL -> EnerRed
        AlertSeverity.WARNING  -> EnerYellow
        AlertSeverity.INFO     -> EnerGreen
        AlertSeverity.SYSTEM   -> EnerTextSecondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EnerSurfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barra lateral de color
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.deviceName,
                    color = EnerTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = alert.message,
                    color = EnerTextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alert.timeAgo,
                    color = EnerTextHint,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(alert: Alert) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(EnerGreenDim)
            .border(1.dp, EnerGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "💡", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = alert.recommendation ?: "",
                color = EnerGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
