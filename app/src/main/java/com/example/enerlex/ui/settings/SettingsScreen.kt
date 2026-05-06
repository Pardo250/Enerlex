package com.example.enerlex.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.enerlex.ui.theme.*

/**
 * Pantalla de Configuración con cambio de foto de perfil.
 *
 * Flujo de foto:
 *  1. Usuario toca el avatar → se abre el Photo Picker del sistema (sin permiso).
 *  2. Se selecciona una imagen → se muestra INMEDIATAMENTE usando la URI local.
 *  3. En background se sube a Firebase Storage.
 *  4. Al terminar, la URI local se reemplaza por la URL permanente de Storage.
 *  5. Durante la subida se muestra un spinner en el avatar.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToProfile: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // ── Photo Picker (no requiere permisos en API 19+) ────────────────────────
    val photoPicker = rememberLauncherForActivityResult(
        contract = PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.onPhotoSelected(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        // ── Tarjeta de perfil ─────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ── Avatar clickeable ─────────────────────────────────────────
                ProfileAvatarWithPicker(
                    localUri    = uiState.localPhotoUri,
                    remoteUrl   = uiState.profilePhotoUrl,
                    userName    = uiState.userName,
                    isUploading = uiState.isUploadingPhoto,
                    onClick     = {
                        photoPicker.launch(
                            PickVisualMediaRequest(PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.userName.ifBlank { "Cargando..." },
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

                IconButton(onClick = onNavigateToProfile) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Ver perfil",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ── Banner de error de subida ─────────────────────────────────────────
        AnimatedVisibility(
            visible = uiState.uploadError != null,
            enter = fadeIn(),
            exit  = fadeOut()
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EnerRedDim)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.uploadError ?: "",
                        color = EnerRed,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.onDismissUploadError() }) {
                        Text("OK", color = EnerRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Sección de ajustes ────────────────────────────────────────────────
        SettingsSection {
            SettingsItemArrow(
                title    = "Límites de consumo",
                subtitle = "Personaliza umbrales"
            )
            HorizontalDivider(color = EnerDivider, thickness = 1.dp)

            SettingsItemToggle(
                title    = "Notificaciones",
                subtitle = "Push, Email, SMS",
                checked  = uiState.notificationsEnabled,
                onToggle = { viewModel.onToggleNotifications() }
            )
            HorizontalDivider(color = EnerDivider, thickness = 1.dp)

            SettingsItemArrow(
                title    = "Dispositivos",
                subtitle = "${uiState.devicesLinked} vinculados"
            )
            HorizontalDivider(color = EnerDivider, thickness = 1.dp)

            SettingsItemToggle(
                title    = "Modo oscuro",
                subtitle = if (uiState.darkModeEnabled) "Activado" else "Desactivado",
                checked  = uiState.darkModeEnabled,
                onToggle = { viewModel.onToggleDarkMode() }
            )
            HorizontalDivider(color = EnerDivider, thickness = 1.dp)

            SettingsItemArrow(title = "Seguridad",   subtitle = "Autenticación 2FA")
            HorizontalDivider(color = EnerDivider, thickness = 1.dp)
            SettingsItemArrow(title = "Acerca de",   subtitle = "Versión ${uiState.appVersion}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Cerrar sesión ─────────────────────────────────────────────────────
        OutlinedButton(
            onClick = { viewModel.onSignOut(); onSignOut() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape  = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = EnerRed)
        ) {
            Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = null,
                modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Cerrar sesión", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Avatar con foto + badge de cámara
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Prioridad para mostrar imagen:
 *   1. [localUri]  → imagen seleccionada antes de subir (instantáneo)
 *   2. [remoteUrl] → URL de Firebase Storage (carga con Coil)
 *   3. Inicial del nombre sobre fondo verde (fallback)
 *
 * Badge inferior derecho:
 *   - Ícono de cámara si no está subiendo
 *   - CircularProgressIndicator si [isUploading] = true
 */
@Composable
private fun ProfileAvatarWithPicker(
    localUri: android.net.Uri?,
    remoteUrl: String?,
    userName: String,
    isUploading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clickable(enabled = !isUploading) { onClick() }
    ) {
        // ── Foto o inicial ────────────────────────────────────────────────────
        val imageModel: Any? = localUri ?: remoteUrl?.takeIf { it.isNotBlank() }

        if (imageModel != null) {
            AsyncImage(
                model            = imageModel,
                contentDescription = "Foto de perfil",
                contentScale     = ContentScale.Crop,
                modifier         = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(2.dp, EnerGreen, CircleShape)
            )
        } else {
            // Fallback: inicial del usuario
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(EnerGreen)
                    .border(2.dp, EnerGreenDark, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = userName.firstOrNull()?.toString() ?: "?",
                    color      = Color(0xFF003D2E),
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── Badge cámara / spinner ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(26.dp)
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
                .border(2.dp, EnerGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(14.dp),
                    color       = EnerGreen,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector        = Icons.Filled.CameraAlt,
                    contentDescription = "Cambiar foto",
                    tint               = EnerGreen,
                    modifier           = Modifier.size(14.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Componentes internos
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SettingsSection(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
    }
}

@Composable
private fun SettingsItemArrow(title: String, subtitle: String) {
    Row(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        Icon(Icons.Filled.ChevronRight, null,
            tint     = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SettingsItemToggle(
    title: String, subtitle: String, checked: Boolean, onToggle: () -> Unit
) {
    Row(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        Switch(
            checked         = checked,
            onCheckedChange = { onToggle() },
            colors          = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = EnerGreen,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
