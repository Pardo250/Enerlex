package com.example.enerlex.ui.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.enerlex.data.repository.ProfilePhotoRepository
import com.example.enerlex.data.repository.UserDataRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = FirebaseAuth.getInstance().currentUser?.email ?: "",
    val userPlan: String = "Plan Hogar Premium",
    val consumptionLimit: Double = 15.0,
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = true,
    val devicesLinked: Int = 8,
    val appVersion: String = "1.0.2",
    // ── Foto de perfil ────────────────────────────────────────────────────
    val profilePhotoUrl: String? = null,    // URL de Firebase Storage
    val localPhotoUri: Uri? = null,         // URI local para mostrar cambio inmediato
    val isUploadingPhoto: Boolean = false,  // Spinner en el avatar
    val uploadError: String? = null         // Mensaje de error si falla la subida
)

/**
 * Extiende AndroidViewModel para acceder al contexto de la aplicación
 * y poder abrir el InputStream de la URI seleccionada por el usuario.
 */
class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val userDataRepository = UserDataRepository()
    private val profilePhotoRepo   = ProfilePhotoRepository()

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        // Nombre desde Firestore
        userDataRepository.getUserName { name ->
            _uiState.update { it.copy(userName = name) }
        }
        // Foto de perfil guardada en Firestore (URL de Storage)
        profilePhotoRepo.getProfilePhotoUrl { url ->
            _uiState.update { it.copy(profilePhotoUrl = url) }
        }
    }

    // ── Cambiar foto de perfil ────────────────────────────────────────────────

    /**
     * Llamado cuando el usuario elige una imagen del Photo Picker.
     *
     * Estrategia de "cambio inmediato":
     *  1. Guardamos la URI local en el estado → la UI la muestra YA con Coil.
     *  2. En background subimos a Firebase Storage.
     *  3. Al terminar, reemplazamos la URI local por la URL permanente de Storage.
     */
    fun onPhotoSelected(imageUri: Uri) {
        // ① Mostrar cambio inmediato con la imagen local
        _uiState.update {
            it.copy(
                localPhotoUri    = imageUri,
                isUploadingPhoto = true,
                uploadError      = null
            )
        }

        // ② Subir en background
        viewModelScope.launch(Dispatchers.IO) {
            val downloadUrl = profilePhotoRepo.uploadProfilePhoto(
                context  = getApplication(),
                imageUri = imageUri
            )

            if (downloadUrl != null) {
                // ③ Éxito: usar la URL permanente de Storage
                _uiState.update {
                    it.copy(
                        profilePhotoUrl  = downloadUrl,
                        localPhotoUri    = null,         // ya no necesitamos la URI local
                        isUploadingPhoto = false
                    )
                }
            } else {
                // ③ Error: revertir y mostrar mensaje
                _uiState.update {
                    it.copy(
                        localPhotoUri    = null,
                        isUploadingPhoto = false,
                        uploadError      = "No se pudo guardar la foto. Inténtalo de nuevo."
                    )
                }
            }
        }
    }

    fun onDismissUploadError() {
        _uiState.update { it.copy(uploadError = null) }
    }

    // ── Otras acciones ────────────────────────────────────────────────────────

    fun onToggleNotifications() {
        _uiState.update { it.copy(notificationsEnabled = !it.notificationsEnabled) }
    }

    fun onToggleDarkMode() {
        _uiState.update { it.copy(darkModeEnabled = !it.darkModeEnabled) }
    }

    fun onSignOut() {
        FirebaseAuth.getInstance().signOut()
    }
}
