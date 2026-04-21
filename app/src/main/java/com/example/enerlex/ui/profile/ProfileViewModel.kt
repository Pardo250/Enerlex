package com.example.enerlex.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val name: String = FirebaseAuth.getInstance().currentUser?.displayName ?: "",
    val email: String = FirebaseAuth.getInstance().currentUser?.email ?: "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val showDeleteDialog: Boolean = false,
    val monthlyReadings: List<com.example.enerlex.data.model.EnergyReading> = emptyList(),
    val averageKwh: Double = 0.0
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadMonthlyData()
    }

    private fun loadMonthlyData() {
        // Datos simulados de consumo de los últimos 6 meses
        val readings = listOf(
            com.example.enerlex.data.model.EnergyReading(0, 145f, "Ene"),
            com.example.enerlex.data.model.EnergyReading(1, 130f, "Feb"),
            com.example.enerlex.data.model.EnergyReading(2, 155f, "Mar"),
            com.example.enerlex.data.model.EnergyReading(3, 120f, "Abr"),
            com.example.enerlex.data.model.EnergyReading(4, 110f, "May"),
            com.example.enerlex.data.model.EnergyReading(5, 125f, "Jun")
        )
        val avg = readings.map { it.watts }.average()
        _uiState.update { it.copy(monthlyReadings = readings, averageKwh = Math.round(avg * 10.0) / 10.0) }
    }

    fun onNameChange(value: String) =
        _uiState.update { it.copy(name = value, errorMessage = null, successMessage = null) }

    fun onCurrentPasswordChange(value: String) =
        _uiState.update { it.copy(currentPassword = value, errorMessage = null) }

    fun onNewPasswordChange(value: String) =
        _uiState.update { it.copy(newPassword = value, errorMessage = null) }

    fun onShowDeleteDialog(show: Boolean) =
        _uiState.update { it.copy(showDeleteDialog = show) }

    /** Actualiza el nombre en Firestore y el displayName de Auth */
    fun onSaveChanges() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre no puede estar vacío") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        val user = auth.currentUser ?: return
        val uid = user.uid

        // Actualizar nombre en Firestore
        db.collection("users").document(uid)
            .update("name", state.name.trim())
            .addOnSuccessListener {
                // Actualizar nombre en Auth profile
                val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                    displayName = state.name.trim()
                }
                user.updateProfile(profileUpdates)
                    .addOnCompleteListener {
                        _uiState.update {
                            it.copy(isLoading = false, successMessage = "Perfil actualizado correctamente")
                        }
                    }
            }
            .addOnFailureListener { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error al guardar: ${e.message}")
                }
            }
    }

    /** Cambia la contraseña reautenticando primero */
    fun onChangePassword(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.currentPassword.isBlank() || state.newPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Completa ambos campos de contraseña") }
            return
        }
        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "La nueva contraseña debe tener al menos 6 caracteres") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val user = auth.currentUser ?: return
        val credential = EmailAuthProvider.getCredential(user.email ?: "", state.currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(state.newPassword)
                    .addOnSuccessListener {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                currentPassword = "",
                                newPassword = "",
                                successMessage = "Contraseña actualizada correctamente"
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
                    }
            }
            .addOnFailureListener {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Contraseña actual incorrecta")
                }
            }
    }

    /** Elimina la cuenta del usuario de Auth y Firestore */
    fun onDeleteAccount(onDeleted: () -> Unit) {
        _uiState.update { it.copy(isLoading = true, showDeleteDialog = false) }

        val user = auth.currentUser ?: return
        val uid = user.uid

        // 1. Borrar documento de Firestore
        db.collection("users").document(uid).delete()
            .addOnCompleteListener {
                // 2. Eliminar usuario de Firebase Auth
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onDeleted()
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "No se pudo eliminar la cuenta. Vuelve a iniciar sesión e intenta de nuevo."
                            )
                        }
                    }
                }
            }
    }
}
