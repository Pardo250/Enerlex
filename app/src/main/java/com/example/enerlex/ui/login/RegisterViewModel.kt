package com.example.enerlex.ui.login

import androidx.lifecycle.ViewModel
import com.example.enerlex.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) =
        _uiState.update { it.copy(name = value, errorMessage = null) }

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, errorMessage = null) }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, errorMessage = null) }

    fun onConfirmPasswordChange(value: String) =
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }

    fun onTogglePasswordVisibility() =
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    fun onRegister(onSuccess: () -> Unit) {
        val state = _uiState.value

        // Validaciones básicas
        if (state.name.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor, completa todos los campos") }
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // 1. Crear usuario en Firebase Auth
        auth.createUserWithEmailAndPassword(state.email.trim(), state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    // 2. Guardar perfil en Firestore en segundo plano
                    userRepository.saveUserExtraData(uid, state.name.trim(), "") { success, error ->
                        if (!success) {
                            android.util.Log.w("RegisterVM", "Firestore write failed: $error")
                        }
                    }
                    // 3. Navegar de inmediato, sin esperar a Firestore
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                } else {
                    val error = when {
                        task.exception?.message?.contains("email address is already in use") == true ->
                            "Ya existe una cuenta con ese correo electrónico"
                        task.exception?.message?.contains("badly formatted") == true ->
                            "El formato del correo electrónico no es válido"
                        task.exception?.message?.contains("CONFIGURATION_NOT_FOUND") == true ->
                            "Servicio no disponible. Intenta más tarde"
                        else ->
                            "No se pudo crear la cuenta. Intenta de nuevo"
                    }
                    _uiState.update { it.copy(isLoading = false, errorMessage = error) }
                }
            }
    }
}
