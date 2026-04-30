package com.example.enerlex.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enerlex.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onAccountDeleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = ThemeState.isDarkMode

    val bgColor      = if (isDark) EnerBackground     else EnerLightBackground
    val surfColor    = if (isDark) EnerSurfaceVariant  else EnerLightSurfaceVariant
    val textPrimary  = if (isDark) EnerTextPrimary     else EnerLightTextPrimary
    val textSecondary = if (isDark) EnerTextSecondary  else EnerLightTextSecondary
    val borderColor  = if (isDark) EnerBorder          else EnerLightBorder

    // Diálogo de confirmación de eliminación
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onShowDeleteDialog(false) },
            containerColor = if (isDark) EnerSurface else EnerLightSurface,
            icon = {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = EnerRed)
            },
            title = {
                Text("Eliminar cuenta", color = textPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "¿Estás seguro? Esta acción es irreversible. Se eliminarán todos tus datos.",
                    color = textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onDeleteAccount(onAccountDeleted) },
                    colors = ButtonDefaults.buttonColors(containerColor = EnerRed)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onShowDeleteDialog(false) }) {
                    Text("Cancelar", color = EnerGreen)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = textSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mi perfil",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Avatar ────────────────────────────────────────────────────
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(EnerGreen),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = uiState.name.firstOrNull()?.toString() ?: "?"
                    Text(
                        text = initial,
                        color = Color(0xFF003D2E),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.email,
                fontSize = 14.sp,
                color = textSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Resumen de consumo mensual ─────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = surfColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "TU CONSUMO HISTÓRICO",
                        color = textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${uiState.averageKwh}",
                            color = EnerGreen,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "kWh / mes prom.",
                            color = textSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    com.example.enerlex.ui.components.EnergyBarChart(
                        readings = uiState.monthlyReadings,
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Etiquetas de los meses
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        uiState.monthlyReadings.forEach { reading ->
                            Text(
                                text = reading.label,
                                color = textSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Editar nombre ─────────────────────────────────────────────
            Text("Editar perfil", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    focusedContainerColor = surfColor,
                    unfocusedContainerColor = surfColor,
                    focusedBorderColor = EnerGreen,
                    unfocusedBorderColor = borderColor,
                    focusedLabelColor = EnerGreen,
                    unfocusedLabelColor = textSecondary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mensajes de éxito/error del perfil
            uiState.successMessage?.let {
                Text(it, color = EnerGreen, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
            }
            uiState.errorMessage?.let {
                Text(it, color = EnerRed, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
            }

            Button(
                onClick = viewModel::onSaveChanges,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EnerGreen)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color(0xFF003D2E), modifier = Modifier.size(20.dp))
                } else {
                    Text("Guardar cambios", color = Color(0xFF003D2E), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = borderColor)
            Spacer(modifier = Modifier.height(32.dp))

            // ── Cambiar contraseña ────────────────────────────────────────
            Text("Cambiar contraseña", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.currentPassword,
                onValueChange = viewModel::onCurrentPasswordChange,
                label = { Text("Contraseña actual") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    focusedContainerColor = surfColor,
                    unfocusedContainerColor = surfColor,
                    focusedBorderColor = EnerGreen,
                    unfocusedBorderColor = borderColor,
                    focusedLabelColor = EnerGreen,
                    unfocusedLabelColor = textSecondary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.newPassword,
                onValueChange = viewModel::onNewPasswordChange,
                label = { Text("Nueva contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    focusedContainerColor = surfColor,
                    unfocusedContainerColor = surfColor,
                    focusedBorderColor = EnerGreen,
                    unfocusedBorderColor = borderColor,
                    focusedLabelColor = EnerGreen,
                    unfocusedLabelColor = textSecondary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.onChangePassword {} },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EnerGreen)
            ) {
                Text("Actualizar contraseña", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(40.dp))
            HorizontalDivider(color = borderColor)
            Spacer(modifier = Modifier.height(24.dp))

            // ── Zona de peligro ───────────────────────────────────────────
            Text("Zona de peligro", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = EnerRed)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { viewModel.onShowDeleteDialog(true) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EnerRed)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar mi cuenta", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
