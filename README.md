<div align="center">

# ⚡ Enerlex

### Smart Home Energy Management — Android App

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Material 3](https://img.shields.io/badge/Material%203-757575?style=for-the-badge&logo=material-design&logoColor=white)](https://m3.material.io/)

*Monitorea, controla y optimiza el consumo energético de tu hogar — desde la palma de tu mano.*

</div>

---

## 📖 Descripción

**Enerlex** es una aplicación Android diseñada para la gestión inteligente del consumo eléctrico del hogar. Conectada a enchufes inteligentes y sensores de energía, permite al usuario visualizar en tiempo real cuánta energía consume cada dispositivo, recibir alertas cuando el consumo supera los límites establecidos y tomar decisiones informadas para reducir la factura eléctrica.

La app sigue los principios de **Clean Architecture** con una interfaz oscura, moderna y fluida construida íntegramente con **Jetpack Compose**.

---

## ✨ Funcionalidades

| Pantalla | Descripción |
|---|---|
| 🏠 **Dashboard** | Resumen del consumo total del día, gráfico de las últimas 24 horas, top de dispositivos activos y alerta crítica más reciente |
| 📱 **Dispositivos** | Lista completa de dispositivos vinculados con estado on/off, consumo en Watts y navegación al detalle |
| 🔍 **Detalle de Dispositivo** | Consumo en tiempo real (W), costo estimado del día, gráfico histórico por período (Día / Semana / Mes) y programación horaria |
| 🔔 **Alertas** | Historial de alertas clasificadas por severidad (crítica, advertencia, informativa) junto con recomendaciones de ahorro |
| ⚙️ **Configuración** | Perfil de usuario, límites de consumo personalizados, notificaciones, modo oscuro y seguridad 2FA |

---

## 🗂️ Arquitectura

```
com.example.enerlex
│
├── data/
│   ├── model/          # Entidades de dominio (Device, Alert, EnergyReading)
│   ├── local/          # Fuentes de datos locales (repositorios en memoria/Room)
│   └── repository/     # Contratos e implementaciones de repositorios
│
└── ui/
    ├── theme/          # Sistema de diseño: colores, tipografía, formas
    ├── components/     # Componentes reutilizables (DeviceCard, EnergyBarChart, EnergyLineChart)
    ├── navigation/     # Grafo de navegación (NavController + rutas)
    ├── dashboard/      # Pantalla 01 – Resumen del hogar
    ├── devices/        # Pantalla 02 – Lista de dispositivos
    ├── devicedetail/   # Pantalla 03 – Detalle y control de un dispositivo
    ├── alerts/         # Pantalla 04 – Centro de alertas
    ├── settings/       # Pantalla 05 – Configuración y perfil
    └── login/          # Pantalla 06 – Autenticación
```

El patrón por pantalla es **MVVM** (ViewModel + UiState + Screen Composable), con flujos reactivos mediante `StateFlow` y `collectAsState`.

---

## 🎨 Sistema de Diseño

EnerFlow usa un tema oscuro personalizado construido sobre **Material 3**:

| Token | Color | Uso |
|---|---|---|
| `EnerBackground` | `#0A0F0D` | Fondo principal |
| `EnerSurfaceVariant` | `#141A17` | Tarjetas y superficies |
| `EnerGreen` | `#00E676` | Acento primario, valor en Watts |
| `EnerYellow` | `#FFD740` | Costo estimado, advertencias |
| `EnerRed` | `#FF5252` | Alertas críticas |
| `EnerTextPrimary` | `#ECFDF5` | Texto principal |
| `EnerTextSecondary` | `#6B9E84` | Texto secundario |

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose + Material 3
- **Navegación:** Navigation Compose
- **Estado:** ViewModel + StateFlow + collectAsState
- **Gráficos:** Canvas API (barras y líneas custom)
- **Arquitectura:** Clean Architecture · MVVM
- **Build System:** Gradle (Kotlin DSL)
- **Min SDK:** 24 · **Target SDK:** 34

---

## 🚀 Cómo ejecutar

### Prerrequisitos

- Android Studio **Hedgehog** (2023.1.1) o superior
- JDK 17+
- SDK Android 34

### Pasos

```bash
# 1. Clona el repositorio
git clone https://github.com/tu-usuario/enerlex.git
cd enerlex

# 2. Abre el proyecto en Android Studio
# File → Open → selecciona la carpeta raíz

# 3. Sincroniza dependencias
./gradlew build

# 4. Ejecuta en emulador o dispositivo físico
# Menú Run → Run 'app'
```

---

## 📂 Modelos de Datos

```kotlin
// Dispositivo conectado a un enchufe inteligente
data class Device(
    val id: String,
    val name: String,
    val room: String,
    val isOn: Boolean,
    val currentWatts: Int,       // Consumo actual
    val todayKwh: Double,        // kWh consumidos hoy
    val iconType: DeviceIcon,
    val schedule: String? = null // Ej: "06:00 - 23:00 · L-V"
)

// Alerta de consumo energético
data class Alert(
    val id: String,
    val deviceName: String,
    val message: String,
    val timeAgo: String,
    val severity: AlertSeverity, // CRITICAL | WARNING | INFO | SYSTEM
    val recommendation: String? = null
)
```

---

## 🗺️ Roadmap

- [x] Dashboard con gráfico de 24 horas
- [x] Lista y control de dispositivos (on/off)
- [x] Detalle de dispositivo con gráfico histórico
- [x] Centro de alertas con severidades
- [x] Pantalla de configuración y perfil
- [ ] Integración con API REST (backend IoT)
- [ ] Autenticación real (Firebase / JWT)
- [ ] Notificaciones push en tiempo real
- [ ] Soporte multi-hogar
- [ ] Widget para pantalla de inicio

---

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor abre un issue o envía un pull request siguiendo las guías de estilo del proyecto.

---

<div align="center">

Hecho con ⚡ y Kotlin · © 2026 EnerFlow

</div>
