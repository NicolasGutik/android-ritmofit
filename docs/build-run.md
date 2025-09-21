# Build y Run - RitmoFit Android

## Requisitos del Sistema

### Software Necesario
- **Android Studio**: Arctic Fox o superior
- **JDK**: 17 (Java 17)
- **Android SDK**: API 29+ (Android 10+)
- **Gradle**: 7.4+
- **Kotlin**: 1.7.0+ (para el build system)

### Hardware Recomendado
- **RAM**: 8GB mínimo, 16GB recomendado
- **Disco**: 10GB libres
- **CPU**: Intel i5/AMD Ryzen 5 o superior

## Configuración del Entorno

### 1. Clonar/Descargar el Proyecto
```bash
# El proyecto ya está en: /ritmofit-android/
cd ritmofit-android
```

### 2. Configurar Android Studio
1. Abrir Android Studio
2. Seleccionar "Open an existing Android Studio project"
3. Navegar a la carpeta `ritmofit-android`
4. Seleccionar el archivo `build.gradle.kts` en la raíz
5. Esperar a que Gradle sincronice (puede tomar varios minutos)

### 3. Configurar Emulador Android
```bash
# Opción 1: Desde Android Studio
# Tools → AVD Manager → Create Virtual Device
# Seleccionar Pixel 6 con API 34 (Android 14)

# Opción 2: Desde línea de comandos
avdmanager create avd -n "Pixel_6_API_34" -k "system-images;android-34;google_apis;x86_64"
```

## Configuración de la API

### 1. Levantar el Backend
```bash
# Navegar al directorio del backend
cd ../tpoAplicaciones1

# Verificar que Java y Maven estén instalados
java -version
mvn -version

# Compilar y ejecutar
mvn clean install
mvn spring-boot:run

# El backend estará disponible en:
# http://localhost:8080
```

### 2. Configurar Base URL en la App

#### Para Emulador Android
```java
// En NetworkModule.java
private static final String BASE_URL = "http://10.0.2.2:8080/";
```

#### Para Dispositivo Físico
```java
// En NetworkModule.java
private static final String BASE_URL = "http://[TU_IP_LOCAL]:8080/";

// Ejemplo:
// private static final String BASE_URL = "http://192.168.1.100:8080/";
```

#### Encontrar IP Local
```bash
# Windows
ipconfig

# macOS/Linux
ifconfig

# Buscar la IP en la red local (ej: 192.168.1.100)
```

## Build del Proyecto

### 1. Build de Desarrollo
```bash
# Desde Android Studio
# Build → Make Project (Ctrl+F9)

# Desde línea de comandos
./gradlew assembleDebug
```

### 2. Build de Release
```bash
# Desde línea de comandos
./gradlew assembleRelease

# El APK estará en: app/build/outputs/apk/release/
```

### 3. Verificar Dependencias
```bash
# Verificar que todas las dependencias se descargaron correctamente
./gradlew dependencies

# Limpiar cache si hay problemas
./gradlew clean
```

## Ejecutar la Aplicación

### 1. Desde Android Studio
1. Conectar dispositivo o iniciar emulador
2. Seleccionar dispositivo en la barra superior
3. Hacer clic en "Run" (▶️) o presionar Shift+F10
4. Esperar a que la app se instale y ejecute

### 2. Desde Línea de Comandos
```bash
# Instalar APK en dispositivo conectado
./gradlew installDebug

# Ejecutar app (requiere adb)
adb shell am start -n com.ritmofit.app/.MainActivity
```

### 3. Verificar Instalación
```bash
# Verificar que la app está instalada
adb shell pm list packages | grep ritmofit

# Ver logs de la aplicación
adb logcat | grep RitmoFit
```

## Configuración de Pruebas

### 1. Datos de Prueba
```json
// Usuario de prueba (crear en el backend)
{
  "email": "test@ritmofit.com",
  "nombre": "Usuario",
  "apellido": "Prueba",
  "telefono": "+54911234567"
}

// OTP de prueba (configurar en backend)
// Código: "123456"
```

### 2. Flujo de Prueba
1. **Iniciar app** → Debe mostrar LoginActivity
2. **Login** → Ingresar email de prueba
3. **OTP** → Ingresar código "123456"
4. **Navegación** → Debe ir a MainActivity con HomeFragment
5. **Cargar clases** → Debe mostrar lista de clases
6. **Detalle** → Tocar una clase para ver detalles
7. **Reservas** → Navegar a "Mis Reservas"

### 3. Verificar Funcionalidades
```bash
# Verificar logs de autenticación
adb logcat | grep "AuthRepository"

# Verificar logs de red
adb logcat | grep "NetworkModule"

# Verificar logs de navegación
adb logcat | grep "Navigation"
```

## Solución de Problemas

### Error: "Could not find method compile()"
```bash
# Actualizar Gradle Wrapper
./gradlew wrapper --gradle-version=7.6

# O cambiar en gradle/wrapper/gradle-wrapper.properties
distributionUrl=https\://services.gradle.org/distributions/gradle-7.6-all.zip
```

### Error: "SDK location not found"
```bash
# Crear archivo local.properties en la raíz del proyecto
echo "sdk.dir=C\:\\Users\\[USER]\\AppData\\Local\\Android\\Sdk" > local.properties

# O configurar en Android Studio:
# File → Project Structure → SDK Location
```

### Error: "Connection refused" al conectar con API
```bash
# Verificar que el backend esté corriendo
curl http://localhost:8080/auth/login

# Verificar IP en NetworkModule.java
# Para emulador: 10.0.2.2
# Para dispositivo: IP local de tu máquina

# Verificar firewall
# Windows: Desactivar firewall temporalmente
# macOS: System Preferences → Security & Privacy → Firewall
```

### Error: "JWT token invalid"
```bash
# Verificar que el backend esté configurado correctamente
# Verificar que el usuario esté registrado
# Verificar que el OTP sea válido

# Limpiar datos de la app
adb shell pm clear com.ritmofit.app
```

### Error: "Build failed" con dependencias
```bash
# Limpiar proyecto
./gradlew clean

# Invalidar cache de Android Studio
# File → Invalidate Caches and Restart

# Verificar versión de Gradle
./gradlew --version
```

### Error: "Device not found"
```bash
# Verificar dispositivos conectados
adb devices

# Reiniciar ADB
adb kill-server
adb start-server

# Verificar que USB Debugging esté habilitado
# Settings → Developer Options → USB Debugging
```

## Configuración de Producción

### 1. Configurar ProGuard
```gradle
// En app/build.gradle.kts
android {
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### 2. Firmar APK
```bash
# Generar keystore
keytool -genkey -v -keystore ritmofit-release-key.keystore -alias ritmofit -keyalg RSA -keysize 2048 -validity 10000

# Configurar signing en build.gradle.kts
android {
    signingConfigs {
        release {
            storeFile file('ritmofit-release-key.keystore')
            storePassword 'password'
            keyAlias 'ritmofit'
            keyPassword 'password'
        }
    }
}
```

### 3. Configurar Base URL de Producción
```java
// En NetworkModule.java
private static final String BASE_URL = "https://api.ritmofit.com/";
```

## Comandos Útiles

### Desarrollo
```bash
# Build y run en un comando
./gradlew installDebug && adb shell am start -n com.ritmofit.app/.MainActivity

# Ver logs en tiempo real
adb logcat | grep -E "(RitmoFit|AndroidRuntime)"

# Limpiar y rebuild
./gradlew clean assembleDebug

# Verificar dependencias
./gradlew app:dependencies
```

### Testing
```bash
# Ejecutar tests unitarios
./gradlew test

# Ejecutar tests de UI
./gradlew connectedAndroidTest

# Generar reporte de coverage
./gradlew jacocoTestReport
```

### Debugging
```bash
# Ver logs específicos de la app
adb logcat -s RitmoFit

# Ver logs de red
adb logcat -s OkHttp

# Ver logs de Hilt
adb logcat -s Hilt
```

## Estructura del Proyecto

```
ritmofit-android/
├── app/
│   ├── build.gradle.kts          # Configuración de la app
│   ├── proguard-rules.pro        # Reglas de ProGuard
│   └── src/main/
│       ├── AndroidManifest.xml   # Manifest de la app
│       ├── java/com/ritmofit/app/
│       │   ├── data/             # Capa de datos
│       │   ├── di/               # Dependency Injection
│       │   ├── ui/               # Capa de UI
│       │   ├── service/          # Servicios
│       │   └── receiver/         # Broadcast Receivers
│       └── res/                  # Recursos (layouts, strings, etc.)
├── build.gradle.kts              # Configuración del proyecto
├── gradle.properties             # Propiedades de Gradle
├── settings.gradle.kts           # Configuración de settings
└── docs/                         # Documentación
```

## Conclusión

El proyecto está configurado para desarrollo local con conexión al backend en `/tpoAplicaciones1`. Para ejecutar:

1. **Levantar backend**: `mvn spring-boot:run` en `/tpoAplicaciones1`
2. **Configurar IP**: Ajustar `BASE_URL` en `NetworkModule.java`
3. **Build y run**: Desde Android Studio o línea de comandos
4. **Probar**: Usar datos de prueba para verificar funcionalidades

La app está lista para desarrollo y testing local, con configuración preparada para producción futura.

