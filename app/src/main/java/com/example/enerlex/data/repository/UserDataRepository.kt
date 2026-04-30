package com.example.enerlex.data.repository

import com.example.enerlex.data.local.FakeData
import com.example.enerlex.data.model.Alert
import com.example.enerlex.data.model.AlertSeverity
import com.example.enerlex.data.model.Device
import com.example.enerlex.data.model.DeviceIcon
import com.example.enerlex.data.model.EnergyReading
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.random.Random

/**
 * Repositorio que combina datos locales (FakeData) con Firestore.
 * - El perfil del usuario (nombre, email) se lee de Firestore/Auth.
 * - Las estadísticas del dashboard se generan de forma semi-aleatoria por UID
 *   y se persisten en Firestore para que varíen por cuenta.
 */
class UserDataRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ── Obtener nombre del usuario ────────────────────────────────────────────
    fun getUserName(onResult: (String) -> Unit) {
        val user = auth.currentUser ?: run { onResult("Usuario"); return }

        // Primero intentamos Firestore (más actualizado que displayName)
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name")
                    ?: user.displayName
                    ?: user.email?.substringBefore("@")
                    ?: "Usuario"
                onResult(name)
            }
            .addOnFailureListener {
                // Fallback al displayName de Auth
                onResult(user.displayName ?: user.email?.substringBefore("@") ?: "Usuario")
            }
    }

    // ── Guardar/leer estadísticas del dashboard por usuario ───────────────────
    fun loadOrCreateDashboardData(onResult: (DashboardData) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            onResult(generateDashboardData("anonymous"))
            return
        }

        db.collection("users").document(uid)
            .collection("dashboard").document("stats")
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    // Ya existe → leer los datos guardados
                    val data = parseDashboardData(doc.data ?: emptyMap())
                    onResult(data)
                } else {
                    // Primera vez → generar datos únicos para este usuario y guardarlos
                    val data = generateDashboardData(uid)
                    saveDashboardData(uid, data)
                    onResult(data)
                }
            }
            .addOnFailureListener {
                onResult(generateDashboardData(uid))
            }
    }

    private fun saveDashboardData(uid: String, data: DashboardData) {
        val map = mapOf(
            "totalKwhToday" to data.totalKwhToday,
            "readings" to data.readings.map {
                mapOf("hour" to it.hour, "watts" to it.watts)
            },
            "deviceWatts" to data.deviceWatts,
            "alertMessage" to data.alertMessage,
            "alertDevice" to data.alertDevice
        )
        db.collection("users").document(uid)
            .collection("dashboard").document("stats")
            .set(map, SetOptions.merge())
    }

    private fun parseDashboardData(map: Map<String, Any?>): DashboardData {
        val totalKwh = (map["totalKwhToday"] as? Double) ?: 13.8
        val deviceWatts = (map["deviceWatts"] as? Map<*, *>)?.map {
            it.key.toString() to (it.value as? Long)?.toInt()
        }?.toMap() ?: emptyMap()
        val alertMessage = (map["alertMessage"] as? String) ?: "Consumo sobre el límite"
        val alertDevice = (map["alertDevice"] as? String) ?: "Nevera"

        @Suppress("UNCHECKED_CAST")
        val rawReadings = (map["readings"] as? List<Map<String, Any?>>) ?: emptyList()
        val readings = rawReadings.mapIndexed { idx, r ->
            EnergyReading(
                hour = (r["hour"] as? Long)?.toInt() ?: idx,
                watts = ((r["watts"] as? Double)?.toFloat()) ?: 200f,
                label = "${(r["hour"] as? Long)?.toInt() ?: idx}h"
            )
        }

        return DashboardData(totalKwh, readings, deviceWatts, alertMessage, alertDevice)
    }

    /** Genera estadísticas únicas seeded por el UID del usuario */
    private fun generateDashboardData(uid: String): DashboardData {
        val seed = uid.hashCode().toLong()
        val rng = Random(seed)

        val totalKwh = 8.0 + rng.nextDouble() * 12.0  // 8–20 kWh
        val readings = (0..23).map { h ->
            val base = when (h) {
                in 0..5  -> 80f + rng.nextFloat() * 100f
                in 6..8  -> 250f + rng.nextFloat() * 200f
                in 9..12 -> 300f + rng.nextFloat() * 250f
                in 13..17 -> 350f + rng.nextFloat() * 280f
                in 18..21 -> 450f + rng.nextFloat() * 200f
                else     -> 200f + rng.nextFloat() * 100f
            }
            EnergyReading(h, base, "${h}h")
        }

        val deviceWatts = mapOf(
            "Nevera"   to (280 + rng.nextInt(120)),
            "TV Sala"  to (80  + rng.nextInt(120)),
            "PC"       to (60  + rng.nextInt(80)),
            "Lámpara"  to (10  + rng.nextInt(20))
        )

        val alertMessages = listOf(
            "Consumo 40% sobre el límite",
            "Encendida más de 8 horas",
            "Pico de consumo detectado",
            "Consumo nocturno inusual"
        )
        val alertDevices = listOf("Nevera", "TV Sala", "PC Oficina", "Aire Acondicionado")

        return DashboardData(
            totalKwhToday = Math.round(totalKwh * 10.0) / 10.0,
            readings = readings,
            deviceWatts = deviceWatts.mapValues { it.value },
            alertMessage = alertMessages[rng.nextInt(alertMessages.size)],
            alertDevice = alertDevices[rng.nextInt(alertDevices.size)]
        )
    }
}

data class DashboardData(
    val totalKwhToday: Double,
    val readings: List<EnergyReading>,
    val deviceWatts: Map<String, Int?>,
    val alertMessage: String,
    val alertDevice: String
)
