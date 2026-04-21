package com.example.enerlex.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
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
import com.example.enerlex.ui.theme.*

/**
 * Pantalla Configuración – pantalla 06 del mockup.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EnerBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ── Perfil de usuario (clickeable → navega al perfil) ─────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToProfile() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(EnerGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.userName.firstOrNull()?.toString() ?: "?",
                        color = Color(0xFF003D2E),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.userName,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.userEmail,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                    Text(
                        text = uiState.userPlan,
                        color = EnerGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Ver perfil",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Opciones de configuración ─────────────────────────────────────
        SettingsSection {
            // Límites de consumo
            SettingsItemArrow(
                title = "Límites de consumo",
                subtitle = "Personaliza umbrales"
            )
            HorizontalDivider(color = EnerDivider, thickness = 1.dp)

            // Notificaciones con toggle
            SettingsItemToggle(
                title = "Notificaciones",
                subtitle = "Push, Email, SMS",
                checked = uiState.notificationsEnabled,
                onToggle = { viewModel.onToggleNotifications() }
            )
            HorizontalDivider(color = EnerDivider, thickness = 1.dp)

            // Dispositivos
            SettingsItemArrow(
                title = "Dispositivos",
                subtitle = "${uiState.devicesLinked} vinculados"
            )
            HorizontalDivider(color = EnerDivider, thickness = 1.dp)

            // Modo oscuro con toggle
            SettingsItemToggle(
                title = "Modo oscuro",
                subtitle = if (uiState.darkModeEnabled) "Activado" else "Desactivado",
                checked = uiState.darkModeEnabled,
                onToggle = { viewModel.onToggleDarkMode() }
            )
            HorizontalDivider(color = EnerDivider, thickness = 1.dp)

            // Seguridad
            SettingsItemArrow(
                title = "Seguridad",
                subtitle = "Autenticación 2FA"
            )
            HorizontalDivider(color = EnerDivider, thickness = 1.dp)

            // Acerca de
            SettingsItemArrow(
                title = "Acerca de",
                subtitle = "Versión ${uiState.appVersion}"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingsSection(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EnerSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
    }
}

@Composable
private fun SettingsItemArrow(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = EnerTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = EnerTextSecondary, fontSize = 12.sp)
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = EnerTextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsItemToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = EnerTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = EnerTextSecondary, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EnerGreen,
                uncheckedThumbColor = EnerTextSecondary,
                uncheckedTrackColor = EnerSurfaceElevated
            )
        )
    }
}
