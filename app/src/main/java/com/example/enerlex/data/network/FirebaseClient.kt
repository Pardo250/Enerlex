package com.example.enerlex.data.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseClient {
// Instancia para Autenticación
val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

// Instancia para Base de Datos (Firestore)
val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
}