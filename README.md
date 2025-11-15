# RitmoFit Android App

Aplicación móvil Android para el sistema de gestión de clases de fitness RitmoFit.

## 🚀 Características

- **Autenticación segura** con OTP por email
- **Catálogo de clases** con filtros por sede, disciplina y fecha
- **Gestión de reservas** - crear, ver y cancelar reservas
- **Perfil de usuario** - ver y editar datos personales
- **Historial de asistencias** - ver clases asistidas
- **Arquitectura moderna** con Single Activity + Fragments
- **Inyección de dependencias** con Hilt
- **Comunicación segura** con JWT y HTTPS
- **Almacenamiento seguro** con EncryptedSharedPreferences

## 🛠️ Tecnologías

- **Lenguaje**: Java
- **UI**: XML (no Compose)
- **Arquitectura**: MVVM con Repository Pattern
- **DI**: Hilt (Dagger)
- **Networking**: Retrofit + OkHttp
- **Navigation**: Navigation Component
- **Storage**: EncryptedSharedPreferences
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 36 (Android 14)

## 📱 Funcionalidades Implementadas

### ✅ Primera Entrega
1. **Autenticación por OTP** - Email → OTP → JWT
2. **Perfil de usuario** - Ver/editar datos básicos
4. **Catálogo de clases** - Listado con filtros y detalle
5. **Gestión de reservas** - Crear, ver y cancelar reservas
8. **Historial** - Base implementada para futuras funcionalidades

### ⏳ Futuras Entregas
3. **Check-in por QR** - Escaneo de códigos QR
6. **Notificaciones push** - Recordatorios de clases
7. **Sistema de clasificaciones** - Rating y comentarios

## 🏗️ Arquitectura

```
UI Layer (Presentation)
├── Activities/Fragments
├── ViewModels
└── Adapters

Domain Layer (Business Logic)
├── Models/DTOs
└── Use Cases

Data Layer (Data Access)
├── Repositories
├── API Services
└── Local Storage
```

## 🚀 Quick Start

### Prerrequisitos
- Android Studio Arctic Fox+
- JDK 17
- Android SDK API 29+
- Backend RitmoFit corriendo en `http://localhost:8080`

### Instalación

1. **Clonar el proyecto**
   ```bash
   git clone [repository-url]
   cd ritmofit-android
   ```

2. **Levantar el backend**
   ```bash
   cd ../tpoAplicaciones1
   mvn spring-boot:run
   ```

3. **Configurar Base URL**
   ```java
   // En NetworkModule.java
   private static final String BASE_URL = "http://10.0.2.2:8080/"; // Emulador
   // private static final String BASE_URL = "http://[IP_LOCAL]:8080/"; // Dispositivo
   ```

4. **Build y ejecutar**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

### Configuración de Prueba

**Usuario de prueba:**
- Email: `test@ritmofit.com`
- OTP: `123456`

## 📖 Documentación

- [📋 Plan de Implementación](docs/00-plan.md)
- [🏗️ Arquitectura](docs/arquitectura.md)
- [🔒 Seguridad](docs/seguridad.md)
- [🌐 Endpoints API](docs/endpoints.md)
- [🔧 Build y Run](docs/build-run.md)

## 🧪 Testing

### Pruebas Manuales
1. **Autenticación**: Login → OTP → Navegación a Main
2. **Catálogo**: Cargar clases → Ver detalle → Filtros
3. **Reservas**: Crear reserva → Ver en "Mis Reservas" → Cancelar
4. **Navegación**: Entre fragments con Navigation Component
5. **Servicios**: SyncService y BroadcastReceiver funcionando

### Pruebas Automatizadas (Pendientes)
- Unit tests para ViewModels
- Integration tests para Repositories
- UI tests para Fragments

## 🔧 Configuración de Desarrollo

### Estructura del Proyecto
```
app/src/main/java/com/ritmofit/app/
├── data/
│   ├── api/           # RitmoFitApiService
│   ├── dto/           # Modelos de datos
│   └── repository/    # Repositories
├── di/                # Módulos Hilt
├── ui/
│   ├── auth/          # Autenticación
│   ├── home/          # Catálogo de clases
│   ├── reservas/      # Gestión de reservas
│   ├── historial/     # Historial
│   ├── perfil/        # Perfil de usuario
│   └── adapters/      # Adapters para RecyclerView
├── service/           # SyncService
├── receiver/          # AppEventsReceiver
└── MainActivity.java  # Single Activity
```

### Dependencias Principales
```kotlin
// Hilt
implementation("com.google.dagger:hilt-android:2.44")
kapt("com.google.dagger:hilt-compiler:2.44")

// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")

// Navigation
implementation("androidx.navigation:navigation-fragment:2.7.0")
implementation("androidx.navigation:navigation-ui:2.7.0")

// Security
implementation("androidx.security:security-crypto:1.1.0-alpha06")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
```

## 🔒 Seguridad

- **JWT**: Almacenado en EncryptedSharedPreferences
- **HTTPS**: Configurado para producción
- **Validación**: Sanitización de datos de entrada
- **Permisos**: Mínimos necesarios (solo INTERNET)
- **Logging**: Seguro, sin datos sensibles

## 🚀 Despliegue

### Desarrollo
```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/
```

### Producción
```bash
./gradlew assembleRelease
# APK firmado: app/build/outputs/apk/release/
```

### Configuración de Producción
1. Cambiar `BASE_URL` a `https://api.ritmofit.com/`
2. Habilitar ProGuard/R8
3. Configurar Certificate Pinning
4. Firmar APK con keystore de producción

## 🤝 Contribución

1. Fork el proyecto
2. Crear feature branch (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Abrir Pull Request

## 📄 Licencia

Este proyecto es parte del trabajo práctico de Desarrollo de Aplicaciones 1 - UADE.

## 📞 Soporte

Para soporte técnico o preguntas sobre la implementación, consultar la documentación en `/docs` o contactar al equipo de desarrollo.

---

**Desarrollado para RitmoFit** 🏋️‍♀️💪