# Seguridad - RitmoFit Android

## Implementación de Seguridad

### 1. Almacenamiento Seguro de Datos Sensibles

#### EncryptedSharedPreferences
```java
@Module
@InstallIn(SingletonComponent.class)
public class StorageModule {
    
    @Provides
    @Singleton
    public MasterKey provideMasterKey(@ApplicationContext Context context) {
        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        )
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build();
        
        return new MasterKey.Builder(context)
                .setKeyGenParameterSpec(keyGenParameterSpec)
                .build();
    }
    
    @Provides
    @Singleton
    public SharedPreferences provideEncryptedSharedPreferences(
            @ApplicationContext Context context,
            MasterKey masterKey) {
        return EncryptedSharedPreferences.create(
                context,
                "ritmo_fit_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }
}
```

#### Almacenamiento de JWT
```java
public class AuthRepositoryImpl implements AuthRepository {
    
    private static final String KEY_JWT_TOKEN = "jwt_token";
    private static final String KEY_USER_DATA = "user_data";
    
    @Override
    public void guardarToken(String token) {
        encryptedPrefs.edit()
                .putString(KEY_JWT_TOKEN, token)
                .apply();
    }
    
    @Override
    public String obtenerToken() {
        return encryptedPrefs.getString(KEY_JWT_TOKEN, null);
    }
    
    @Override
    public void cerrarSesion() {
        encryptedPrefs.edit()
                .remove(KEY_JWT_TOKEN)
                .remove(KEY_USER_DATA)
                .apply();
    }
}
```

### 2. Interceptor JWT Automático

#### Configuración del Interceptor
```java
@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    
    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(@ApplicationContext Context context) {
        
        // Interceptor para agregar JWT automáticamente
        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();
            
            // Obtener token desde storage seguro
            String token = obtenerTokenDesdeStorage();
            
            Request newRequest = originalRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token) // JWT automático
                    .build();
            
            return chain.proceed(newRequest);
        };
        
        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}
```

### 3. Comunicación HTTPS

#### Configuración de Red Segura
```java
// En NetworkModule
private static final String BASE_URL = "http://10.0.2.2:8080/"; // Desarrollo
// Para producción: "https://api.ritmofit.com/"

@Provides
@Singleton
public OkHttpClient provideOkHttpClient() {
    
    // Interceptor de logging (solo en debug)
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    if (BuildConfig.DEBUG) {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    } else {
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
    }
    
    return new OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            // TODO: Agregar Certificate Pinning para producción
            // .certificatePinner(certificatePinner)
            .build();
}
```

### 4. Validación de Datos de Entrada

#### Validación en Formularios
```java
public class LoginActivity extends AppCompatActivity {
    
    private boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            mostrarError("Email es requerido");
            return false;
        }
        
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        if (!email.matches(emailPattern)) {
            mostrarError("Email inválido");
            return false;
        }
        
        return true;
    }
    
    private boolean validarOTP(String otp) {
        if (otp == null || otp.trim().isEmpty()) {
            mostrarError("Código OTP es requerido");
            return false;
        }
        
        if (otp.length() != 6) {
            mostrarError("El código OTP debe tener 6 dígitos");
            return false;
        }
        
        return true;
    }
}
```

#### Sanitización de Datos
```java
public class DataSanitizer {
    
    public static String sanitizarInput(String input) {
        if (input == null) return "";
        
        // Remover caracteres peligrosos
        return input.trim()
                .replaceAll("[<>\"'&]", "")
                .replaceAll("\\s+", " ");
    }
    
    public static boolean esInputSeguro(String input) {
        // Verificar que no contenga scripts o caracteres peligrosos
        String dangerousPatterns = "(?i).*(script|javascript|vbscript|onload|onerror).*";
        return !input.matches(dangerousPatterns);
    }
}
```

### 5. Manejo Seguro de Errores

#### No Exponer Información Sensible
```java
public class ErrorHandler {
    
    public static String getErrorMessage(Throwable throwable) {
        // No exponer stack traces en producción
        if (BuildConfig.DEBUG) {
            return throwable.getMessage();
        } else {
            // Mensajes genéricos para producción
            if (throwable instanceof UnknownHostException) {
                return "Error de conexión. Verifique su internet.";
            } else if (throwable instanceof SocketTimeoutException) {
                return "Tiempo de espera agotado. Intente nuevamente.";
            } else {
                return "Ocurrió un error inesperado.";
            }
        }
    }
}
```

#### Logging Seguro
```java
public class SecureLogger {
    
    private static final String TAG = "RitmoFit";
    
    public static void logDebug(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }
    
    public static void logError(String message, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, throwable);
        }
        // En producción, enviar a servicio de crash reporting
        // FirebaseCrashlytics.getInstance().recordException(throwable);
    }
    
    // Nunca loggear datos sensibles
    public static void logAuth(String message) {
        // Solo loggear que ocurrió autenticación, no el token
        logDebug("Auth event: " + message);
    }
}
```

### 6. Permisos de Android

#### AndroidManifest.xml
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Solo permisos necesarios -->
    <uses-permission android:name="android.permission.INTERNET" />
    
    <!-- TODO: CAMERA se agregará en la 3ra entrega para QR -->
    <!-- <uses-permission android:name="android.permission.CAMERA" /> -->
    
    <application
        android:name=".RitmoFitApplication"
        android:allowBackup="false"  <!-- Deshabilitado por seguridad -->
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:theme="@style/Theme.RitmoFit">
        
        <!-- Activities con exported controlado -->
        <activity
            android:name=".MainActivity"
            android:exported="true">  <!-- Solo launcher -->
            
        <activity
            android:name=".auth.ui.LoginActivity"
            android:exported="false"> <!-- No accesible desde fuera -->
            
    </application>
</manifest>
```

#### Verificación de Permisos en Runtime
```java
public class PermissionHelper {
    
    public static boolean verificarPermisoInternet(Context context) {
        return ContextCompat.checkSelfPermission(context, 
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    }
    
    @TargetApi(Build.VERSION_CODES.M)
    public static void solicitarPermisoInternet(Activity activity) {
        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)) {
            // Mostrar explicación
            new AlertDialog.Builder(activity)
                    .setTitle("Permiso requerido")
                    .setMessage("Necesitamos acceso a internet para conectarnos al servidor.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        activity.requestPermissions(
                                new String[]{Manifest.permission.INTERNET},
                                REQUEST_INTERNET_PERMISSION
                        );
                    })
                    .show();
        } else {
            activity.requestPermissions(
                    new String[]{Manifest.permission.INTERNET},
                    REQUEST_INTERNET_PERMISSION
            );
        }
    }
}
```

### 7. Protección contra Ataques Comunes

#### SQL Injection Prevention
```java
// No usamos SQL directo, pero si lo hiciéramos:
public class DatabaseHelper {
    
    // MALO: Vulnerable a SQL injection
    // String query = "SELECT * FROM users WHERE email = '" + email + "'";
    
    // BUENO: Usar parámetros preparados
    public User buscarUsuarioPorEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        // Usar parámetros preparados
        return database.query(query, new String[]{email});
    }
}
```

#### XSS Prevention
```java
public class XSSProtection {
    
    public static String escaparHtml(String input) {
        if (input == null) return "";
        
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
    
    public static String limpiarInput(String input) {
        // Remover tags HTML peligrosos
        return input.replaceAll("<[^>]*>", "");
    }
}
```

#### CSRF Protection
```java
public class CSRFProtection {
    
    private static final String CSRF_TOKEN_KEY = "csrf_token";
    
    public static String generarCSRFToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    
    public static boolean validarCSRFToken(String token) {
        String storedToken = getStoredCSRFToken();
        return token != null && token.equals(storedToken);
    }
}
```

### 8. Configuración de Producción

#### ProGuard Rules
```proguard
# Mantener clases de seguridad
-keep class androidx.security.crypto.** { *; }
-keep class javax.crypto.** { *; }

# Obfuscar código pero mantener funcionalidad
-keep class com.ritmofit.app.data.dto.** { *; }
-keep class com.ritmofit.app.data.api.** { *; }

# No loggear en release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
}
```

#### Build Variants
```gradle
android {
    buildTypes {
        debug {
            buildConfigField "boolean", "DEBUG_MODE", "true"
            buildConfigField "String", "BASE_URL", '"http://10.0.2.2:8080/"'
        }
        
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            buildConfigField "boolean", "DEBUG_MODE", "false"
            buildConfigField "String", "BASE_URL", '"https://api.ritmofit.com/"'
        }
    }
}
```

### 9. Auditoría de Seguridad

#### Checklist de Seguridad
- ✅ **Datos sensibles encriptados**: JWT en EncryptedSharedPreferences
- ✅ **Comunicación segura**: HTTPS en producción
- ✅ **Validación de entrada**: Sanitización de datos de usuario
- ✅ **Manejo seguro de errores**: No exponer información sensible
- ✅ **Permisos mínimos**: Solo INTERNET necesario
- ✅ **Logging seguro**: No loggear datos sensibles
- ⏳ **Certificate Pinning**: Pendiente para producción
- ⏳ **Root Detection**: Pendiente para producción
- ⏳ **Obfuscation**: Configurado en ProGuard

#### Monitoreo de Seguridad
```java
public class SecurityMonitor {
    
    public static void reportarIntentoAccesoNoAutorizado(String endpoint) {
        // Enviar a servicio de monitoreo
        Log.w("SECURITY", "Intento de acceso no autorizado: " + endpoint);
        
        // En producción: enviar a Firebase Analytics o similar
        // Bundle bundle = new Bundle();
        // bundle.putString("endpoint", endpoint);
        // FirebaseAnalytics.getInstance().logEvent("unauthorized_access", bundle);
    }
    
    public static void reportarErrorDeAutenticacion(String motivo) {
        Log.w("SECURITY", "Error de autenticación: " + motivo);
        
        // Rate limiting o bloqueo temporal si hay muchos intentos
    }
}
```

## Mejoras Futuras de Seguridad

### 1. Biometric Authentication
```java
// TODO: Implementar en futuras versiones
public class BiometricAuth {
    public void autenticarConBiometria() {
        // Huella dactilar o Face ID
        // Para operaciones sensibles
    }
}
```

### 2. Certificate Pinning
```java
// TODO: Para producción
public class CertificatePinning {
    public static CertificatePinner createCertificatePinner() {
        return new CertificatePinner.Builder()
                .add("api.ritmofit.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
                .build();
    }
}
```

### 3. Root Detection
```java
// TODO: Para producción
public class RootDetection {
    public static boolean isDeviceRooted() {
        // Detectar si el dispositivo está rooteado
        // Bloquear app si está rooteado
        return false;
    }
}
```

### 4. App Integrity Verification
```java
// TODO: Para producción
public class AppIntegrity {
    public static boolean verificarIntegridad() {
        // Verificar que la app no ha sido modificada
        // Play Integrity API
        return true;
    }
}
```

## Conclusión

La implementación de seguridad sigue las mejores prácticas de Android:

1. **Almacenamiento seguro** con EncryptedSharedPreferences
2. **Comunicación encriptada** con HTTPS y JWT
3. **Validación de entrada** para prevenir inyecciones
4. **Manejo seguro de errores** sin exponer información sensible
5. **Permisos mínimos** necesarios
6. **Logging seguro** sin datos sensibles

La aplicación está preparada para producción con configuraciones adicionales de Certificate Pinning, Root Detection y App Integrity Verification.
