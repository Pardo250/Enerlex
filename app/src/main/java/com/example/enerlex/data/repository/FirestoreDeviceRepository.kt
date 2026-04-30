package com.example.enerlex.data.repository

import com.example.enerlex.data.model.Alert
import com.example.enerlex.data.model.AlertSeverity
import com.example.enerlex.data.model.Device
import com.example.enerlex.data.model.DeviceIcon
import com.example.enerlex.data.model.EnergyReading
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Repositorio de dispositivos respaldado por Firestore.
 * - Al primer inicio, crea los dispositivos del usuario con consumos reales.
 * - Al hacer toggle, actualiza el estado en Firestore y recalcula el kWh del día.
 * - Las lecturas del gráfico varían según si el dispositivo está encendido o apagado.
 */
class FirestoreDeviceRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    // ── Catálogo con consumos reales de electrodomésticos ────────────────────
    private val defaultDevices = listOf(
        mapOf(
            "id" to "1", "name" to "TV Sala",        "room" to "Sala",
            "isOn" to true,  "nominalWatts" to 150,  "iconType" to "TV",
            "schedule" to "06:00 - 23:00 · L-V"
        ),
        mapOf(
            "id" to "2", "name" to "Nevera",          "room" to "Cocina",
            "isOn" to true,  "nominalWatts" to 150,   "iconType" to "REFRIGERATOR"
        ),
        mapOf(
            "id" to "3", "name" to "PC Oficina",      "room" to "Oficina",
            "isOn" to true,  "nominalWatts" to 200,   "iconType" to "COMPUTER",
            "schedule" to "08:00 - 22:00 · L-V"
        ),
        mapOf(
            "id" to "4", "name" to "Lámpara Cuarto",  "room" to "Habitación",
            "isOn" to true,  "nominalWatts" to 12,    "iconType" to "LAMP"
        ),
        mapOf(
            "id" to "5", "name" to "Microondas",      "room" to "Cocina",
            "isOn" to false, "nominalWatts" to 1200,  "iconType" to "MICROWAVE"
        ),
        mapOf(
            "id" to "6", "name" to "Ventilador",      "room" to "Sala",
            "isOn" to false, "nominalWatts" to 70,    "iconType" to "FAN"
        ),
        mapOf(
            "id" to "7", "name" to "Lavadora",        "room" to "Lavandería",
            "isOn" to false, "nominalWatts" to 500,   "iconType" to "WASHER"
        ),
        mapOf(
            "id" to "8", "name" to "Aire Acondicionado", "room" to "Habitación",
            "isOn" to false, "nominalWatts" to 1500,  "iconType" to "AC"
        ),
        mapOf(
            "id" to "9", "name" to "Cafetera",        "room" to "Cocina",
            "isOn" to false, "nominalWatts" to 800,   "iconType" to "OTHER"
        ),
        mapOf(
            "id" to "10", "name" to "Consola Juegos", "room" to "Sala",
            "isOn" to true,  "nominalWatts" to 250,   "iconType" to "OTHER",
            "schedule" to "18:00 - 22:00 · L-D"
        ),
        mapOf(
            "id" to "11", "name" to "Secadora",       "room" to "Lavandería",
            "isOn" to false, "nominalWatts" to 2500,  "iconType" to "OTHER"
        ),
        mapOf(
            "id" to "12", "name" to "Freidora de Aire", "room" to "Cocina",
            "isOn" to false, "nominalWatts" to 1500,  "iconType" to "OTHER"
        )
    )

    // ── Cargar / Inicializar dispositivos ────────────────────────────────────
    fun loadDevices(onResult: (List<Device>) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { onResult(emptyList()); return }
        val colRef = db.collection("users").document(uid).collection("devices")

        colRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                // Primera vez → seed con datos reales
                initDevicesForUser(uid) { devices -> onResult(devices) }
            } else {
                val devices = snapshot.documents.mapNotNull { doc -> docToDevice(doc.data) }
                    .sortedBy { it.id }
                onResult(devices)
            }
        }.addOnFailureListener { onResult(emptyList()) }
    }

    // ── Observar dispositivos en tiempo real ─────────────────────────────────
    fun observeDevices(onResult: (List<Device>) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { onResult(emptyList()); return }
        val colRef = db.collection("users").document(uid).collection("devices")

        colRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                onResult(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val devices = snapshot.documents.mapNotNull { doc -> docToDevice(doc.data) }
                    .sortedBy { it.id.toIntOrNull() ?: 0 }
                
                // Add any newly pushed catalogue devices to existing users
                val missing = defaultDevices.filter { def -> devices.none { it.id == def["id"] } }
                if (missing.isNotEmpty()) {
                    addMissingDevices(uid, missing)
                }

                onResult(devices)
            } else if (snapshot != null && snapshot.isEmpty) {
                initDevicesForUser(uid) { devices -> onResult(devices) }
            }
        }
    }

    // ── Toggle ON/OFF y persistir ─────────────────────────────────────────────
    fun toggleDevice(deviceId: String, currentDevices: List<Device>, onResult: (List<Device>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val device = currentDevices.find { it.id == deviceId } ?: return
        val newIsOn = !device.isOn
        val newWatts = if (newIsOn) device.currentWatts.coerceAtLeast(nominalWatts(device)) else 0
        val newKwh   = calculateTodayKwh(newWatts)

        db.collection("users").document(uid).collection("devices").document(deviceId)
            .update(mapOf(
                "isOn"         to newIsOn,
                "currentWatts" to newWatts,
                "todayKwh"     to newKwh
            ))
            .addOnCompleteListener {
                val updated = currentDevices.map {
                    if (it.id == deviceId) it.copy(isOn = newIsOn, currentWatts = newWatts, todayKwh = newKwh)
                    else it
                }
                onResult(updated)
            }
    }

    // ── Lecturas 24h que varían según estado ─────────────────────────────────
    fun getReadingsForDevice(device: Device): List<EnergyReading> {
        val watts = if (device.isOn) device.currentWatts.toFloat() else 0f
        return (0..23).map { h ->
            val value = if (device.isOn) {
                when (h) {
                    in 0..5   -> watts * 0.3f
                    in 6..8   -> watts * 0.7f
                    in 9..12  -> watts * 0.9f
                    in 13..17 -> watts * 1.0f
                    in 18..21 -> watts * 0.85f
                    else      -> watts * 0.5f
                }
            } else 0f
            EnergyReading(h, value, "${h}h")
        }
    }

    /** Lecturas de semana basadas en el estado actual */
    fun getWeekReadingsForDevice(device: Device): List<EnergyReading> {
        val watts = if (device.isOn) device.currentWatts.toFloat() else 0f
        val days = listOf("L", "M", "Mi", "J", "V", "S", "D")
        return days.mapIndexed { i, label ->
            val factor = when (i) {
                0 -> 0.8f; 1 -> 0.9f; 2 -> 1.0f; 3 -> 0.85f; 4 -> 1.1f; 5 -> 0.5f; else -> 0.4f
            }
            EnergyReading(i, if (device.isOn) watts * factor else 0f, label)
        }
    }

    // ── Helpers privados ──────────────────────────────────────────────────────
    private fun initDevicesForUser(uid: String, onResult: (List<Device>) -> Unit) {
        val batch = db.batch()
        val colRef = db.collection("users").document(uid).collection("devices")
        val devices = defaultDevices.map { d ->
            val isOn     = d["isOn"] as Boolean
            val nominal  = (d["nominalWatts"] as Int)
            val watts    = if (isOn) nominal else 0
            val kwh      = calculateTodayKwh(watts)
            val doc = mapOf(
                "id"           to d["id"],
                "name"         to d["name"],
                "room"         to d["room"],
                "isOn"         to isOn,
                "nominalWatts" to nominal,
                "currentWatts" to watts,
                "todayKwh"     to kwh,
                "iconType"     to d["iconType"],
                "schedule"     to (d["schedule"] ?: null)
            )
            batch.set(colRef.document(d["id"] as String), doc)
            docToDevice(doc)!!
        }
        batch.commit().addOnCompleteListener { onResult(devices) }
    }

    private fun addMissingDevices(uid: String, missing: List<Map<String, Any?>>) {
        val batch = db.batch()
        val colRef = db.collection("users").document(uid).collection("devices")
        missing.forEach { d ->
            val isOn     = d["isOn"] as Boolean
            val nominal  = (d["nominalWatts"] as Int)
            val watts    = if (isOn) nominal else 0
            val kwh      = calculateTodayKwh(watts)
            val doc = mapOf(
                "id"           to d["id"],
                "name"         to d["name"],
                "room"         to d["room"],
                "isOn"         to isOn,
                "nominalWatts" to nominal,
                "currentWatts" to watts,
                "todayKwh"     to kwh,
                "iconType"     to d["iconType"],
                "schedule"     to (d["schedule"] ?: null)
            )
            batch.set(colRef.document(d["id"] as String), doc)
        }
        batch.commit()
    }

    private fun docToDevice(data: Map<String, Any?>?): Device? {
        if (data == null) return null
        return try {
            Device(
                id           = data["id"] as? String ?: return null,
                name         = data["name"] as? String ?: return null,
                room         = data["room"] as? String ?: "",
                isOn         = data["isOn"] as? Boolean ?: false,
                currentWatts = (data["currentWatts"] as? Long)?.toInt()
                               ?: (data["currentWatts"] as? Int) ?: 0,
                todayKwh     = (data["todayKwh"] as? Double) ?: 0.0,
                iconType     = parseIcon(data["iconType"] as? String),
                schedule     = data["schedule"] as? String
            )
        } catch (e: Exception) { null }
    }

    private fun parseIcon(raw: String?) = when (raw) {
        "TV"           -> DeviceIcon.TV
        "REFRIGERATOR" -> DeviceIcon.REFRIGERATOR
        "COMPUTER"     -> DeviceIcon.COMPUTER
        "LAMP"         -> DeviceIcon.LAMP
        "MICROWAVE"    -> DeviceIcon.MICROWAVE
        "FAN"          -> DeviceIcon.FAN
        "WASHER"       -> DeviceIcon.WASHER
        "AC"           -> DeviceIcon.AC
        else           -> DeviceIcon.OTHER
    }

    /** Nomina vatios por tipo, para restaurar al encender */
    private fun nominalWatts(device: Device): Int = when (device.name) {
        "Cafetera" -> 800
        "Consola Juegos" -> 250
        "Secadora" -> 2500
        "Freidora de Aire" -> 1500
        else -> when (device.iconType) {
            DeviceIcon.TV           -> 150
            DeviceIcon.REFRIGERATOR -> 150
            DeviceIcon.COMPUTER     -> 200
            DeviceIcon.LAMP         -> 12
            DeviceIcon.MICROWAVE    -> 1200
            DeviceIcon.FAN          -> 70
            DeviceIcon.WASHER       -> 500
            DeviceIcon.AC           -> 1500
            DeviceIcon.OTHER        -> 100
        }
    }

    /** kWh hoy = (watts × horas encendido) / 1000 — asumimos 8h de uso promedio */
    private fun calculateTodayKwh(watts: Int): Double =
        Math.round((watts * 8.0 / 1000.0) * 100.0) / 100.0
}
