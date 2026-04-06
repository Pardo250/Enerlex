package com.example.enerlex.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enerlex.data.model.Alert
import com.example.enerlex.data.model.AlertSeverity
import com.example.enerlex.data.model.Device
import com.example.enerlex.ui.components.EnergyBarChart
import com.example.enerlex.ui.components.toIcon
import com.example.enerlex.ui.theme.*

/**
 * Pantalla Dashboard – pantalla 02 del mockup.
 * Muestra: consumo total, gráfico 24h, top dispositivos y alerta activa.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onDeviceClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EnerBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ── Header ────────────────────────────────────────────────────────
        Text(
            text = "Bienvenido, Carlos",
            color = EnerTextSecondary,
            fontSize = 13.sp
        )
        Text(
            text = "Tu hogar",
            color = EnerTextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Card consumo total ────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EnerSurnaceVariantCard)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "CONSUMO TOTAL HOY",
                    color = EnerTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format("%.1f", uiState.totalKwhToday),
                        color = EnerGreen,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 52.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "kWh",
                        color = EnerTextSecondary,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Chip de eficiencia
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EnerGreenDim)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "12% eficiente",
                            color = EnerGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Card gráfico 24h ──────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EnerSurnaceVariantCard)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ÚLTIMAS 24 HORAS",
                    color = EnerTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                EnergyBarChart(
                    readings = uiState.readings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Top dispositivos encendidos ───────────────────────────────────
        if (uiState.topDevices.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.topDevices.forEach { device ->
                    TopDeviceChip(
                        device = device,
                        modifier = Modifier.weight(1f),
                        onClick = { onDeviceClick(device.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Alerta activa ─────────────────────────────────────────────────
        uiState.latestAlert?.let { alert ->
            ActiveAlertBanner(alert = alert)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TopDeviceChip(
    device: Device,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val dotColor = if (device.currentWatts > 300) EnerYellow else EnerGreen

    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EnerSurnaceVariantCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = device.iconType.toIcon(),
                    contentDescription = device.name,
                    tint = EnerTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = device.name, color = EnerTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = "${device.currentWatts}W",
                color = dotColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ActiveAlertBanner(alert: Alert) {
    val bgColor = when (alert.severity) {
        AlertSeverity.CRITICAL -> EnerRedDim
        AlertSeverity.WARNING  -> EnerYellow.copy(alpha = 0.1f)
        else -> EnerSurfaceVariant
    }
    val accentColor = when (alert.severity) {
        AlertSeverity.CRITICAL -> EnerRed
        AlertSeverity.WARNING  -> EnerYellow
        else -> EnerTextSecondary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "⚠ ${alert.deviceName}: ${alert.message}",
                    color = accentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = alert.timeAgo,
                    color = EnerTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// Alias de color para cards dentro del dashboard
private val EnerSurnaceVariantCard = EnerSurfaceVariant
