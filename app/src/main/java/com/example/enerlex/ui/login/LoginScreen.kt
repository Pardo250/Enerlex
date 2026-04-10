package com.example.enerlex.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enerlex.ui.theme.*

/**
 * Pantalla de inicio de sesión – pantalla 01 del mockup.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EnerBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Logo ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(EnerSurfaceVariant)
                    .border(1.dp, EnerGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(EnerGreen, EnerGreenDark)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "EnerFlow",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = EnerTextPrimary
            )
            Text(
                text = "Monitoreo inteligente de energía",
                fontSize = 14.sp,
                color = EnerTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Campo Email ───────────────────────────────────────────────
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = {
                    Text("correo@ejemplo.com", color = EnerTextHint)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = EnerTextPrimary,
                    unfocusedTextColor = EnerTextPrimary,
                    focusedContainerColor = EnerSurfaceVariant,
                    unfocusedContainerColor = EnerSurfaceVariant,
                    focusedBorderColor = EnerGreen,
                    unfocusedBorderColor = EnerBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Campo Contraseña ──────────────────────────────────────────
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = {
                    Text("••••••••", color = EnerTextHint)
                },
                singleLine = true,
                visualTransformation = if (uiState.isPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                            contentDescription = "Ver contraseña",
                            tint = EnerTextSecondary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = EnerTextPrimary,
                    unfocusedTextColor = EnerTextPrimary,
                    focusedContainerColor = EnerSurfaceVariant,
                    unfocusedContainerColor = EnerSurfaceVariant,
                    focusedBorderColor = EnerGreen,
                    unfocusedBorderColor = EnerBorder
                )
            )

            // Error
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = error,
                    color = EnerRed,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Olvidé contraseña
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = {}) {
                    Text(
                        text = "¿Olvidé mi contraseña?",
                        color = EnerGreen,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Botón Iniciar Sesión ──────────────────────────────────────
            Button(
                onClick = { viewModel.onLogin(onSuccess = onLoginSuccess) },
                enabled = !uiState.isLoading, // Desactiva el botón mientras carga
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EnerGreen)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color(0xFF003D2E), modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003D2E)
                    )
                }
            }

            // ── Continuar con Google ──────────────────────────────────────
            OutlinedButton(
                onClick = { onLoginSuccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(listOf(EnerBorder, EnerBorder))
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = EnerGoogleButton,
                    contentColor = EnerTextPrimary
                )
            ) {
                Text(
                    text = "Continuar con Google",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Crear cuenta ──────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = EnerTextSecondary,
                    fontSize = 14.sp
                )
                TextButton(onClick = {}) {
                    Text(
                        text = "Crear cuenta",
                        color = EnerGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
