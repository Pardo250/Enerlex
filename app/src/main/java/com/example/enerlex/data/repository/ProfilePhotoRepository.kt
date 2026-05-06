package com.example.enerlex.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/**
 * Maneja la subida de foto de perfil a Firebase Storage y la persistencia
 * de la URL de descarga en Firestore.
 *
 * Ruta en Storage: profile_photos/{uid}/avatar.jpg
 * Campo en Firestore: users/{uid} → photoUrl (String)
 */
class ProfilePhotoRepository {

    private val auth    = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val db      = FirebaseFirestore.getInstance()

    /**
     * 1. Sube la imagen al Storage sobreescribiendo la anterior (mismo path).
     * 2. Obtiene la URL pública de descarga.
     * 3. Persiste la URL en Firestore para leerla en cualquier sesión futura.
     *
     * @param context  Necesario para abrir el InputStream de la URI local.
     * @param imageUri URI local devuelta por el Photo Picker del sistema.
     * @return         URL de descarga pública, o null si ocurrió un error.
     */
    suspend fun uploadProfilePhoto(context: Context, imageUri: Uri): String? {
        val uid = auth.currentUser?.uid ?: return null

        return try {
            // ── 1. Referencia en Storage ───────────────────────────────────
            val storageRef = storage.reference
                .child("profile_photos/$uid/avatar.jpg")

            // ── 2. Subir el archivo ────────────────────────────────────────
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return null

            storageRef.putStream(inputStream).await()
            inputStream.close()

            // ── 3. Obtener URL de descarga ─────────────────────────────────
            val downloadUrl = storageRef.downloadUrl.await().toString()

            // ── 4. Guardar URL en Firestore ────────────────────────────────
            db.collection("users")
                .document(uid)
                .update("photoUrl", downloadUrl)
                .await()

            downloadUrl

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Lee la URL de la foto de perfil guardada en Firestore.
     * Llama [onResult] con la URL, o null si el usuario no tiene foto.
     */
    fun getProfilePhotoUrl(onResult: (String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { onResult(null); return }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc -> onResult(doc.getString("photoUrl")) }
            .addOnFailureListener { onResult(null) }
    }
}
