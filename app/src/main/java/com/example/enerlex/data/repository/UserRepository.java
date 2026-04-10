package com.example.enerlex.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveUserExtraData(String uid, String nombre, String empresa) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", nombre);
        userMap.put("company", empresa);
        userMap.put("createdAt", System.currentTimeMillis());

        db.collection("users").document(uid).set(userMap);
    }
}
