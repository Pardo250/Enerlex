package com.example.enerlex.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Callback para operaciones asíncronas de Firestore.
     */
    public interface FirestoreCallback {
        void onResult(boolean success, String errorMessage);
    }

    /**
     * Guarda el perfil extra del usuario en Firestore.
     *
     * @param uid      UID del usuario autenticado
     * @param nombre   Nombre completo del usuario
     * @param empresa  Empresa del usuario (puede estar vacío)
     * @param callback Resultado de la operación
     */
    public void saveUserExtraData(String uid, String nombre, String empresa, FirestoreCallback callback) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", nombre);
        userMap.put("company", empresa);
        userMap.put("createdAt", System.currentTimeMillis());

        db.collection("users").document(uid).set(userMap)
                .addOnSuccessListener(unused -> {
                    if (callback != null) callback.onResult(true, null);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onResult(false, e.getMessage());
                });
    }
}
