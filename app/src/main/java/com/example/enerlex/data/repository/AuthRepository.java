package com.example.enerlex.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    // Interfaz para el callback (equivalente a onResult en Kotlin)
    public interface AuthCallback {
        void onResult(boolean success, String errorMessage);
    }

    // Intentar iniciar sesión
    public void signIn(String email, String pass, AuthCallback callback) {
        if (email == null || email.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
            callback.onResult(false, "Campos vacíos");
            return;
        }

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onResult(true, null);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                        callback.onResult(false, error);
                    }
                });
    }

    // Obtener usuario actual
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // Cerrar sesión
    public void signOut() {
        auth.signOut();
    }
}