# Arquitectura - RitmoFit Android

## Patrones Arquitectónicos Implementados

### 1. Single Activity + Multiple Fragments
- **MainActivity**: Actúa como contenedor único
- **FragmentContainerView**: Host para Navigation Component
- **Navigation Component**: Gestiona navegación entre fragments
- **SafeArgs**: Paso seguro de datos entre fragments

### 2. Separation of Concerns (SoC)
Cada capa tiene una responsabilidad específica:

```
UI Layer (Presentation)
├── Activities/Fragments
├── ViewModels
├── Adapters
└── Layouts (XML)

Domain Layer (Business Logic)
├── Models/DTOs
├── Use Cases (implícitos en ViewModels)
└── Business Rules

Data Layer (Data Access)
├── Repositories
├── API Services
├── Local Storage
└── Network Layer
```

### 3. Single Source of Truth (SSOT)
- **ViewModels**: Única fuente de verdad para estado de UI
- **LiveData**: Flujo reactivo de datos
- **Repositories**: Única fuente de datos para cada dominio

### 4. Unidirectional Data Flow (UDF)
```
API → Repository → ViewModel → Fragment → UI
  ↑                                    ↓
  └────────── User Actions ←─────────────┘
```

## Capas de la Aplicación

### UI Layer (Presentación)

#### Activities
```java
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject AuthRepository authRepository;
    
    // Single Activity con Navigation Component
    // Verifica autenticación al iniciar
    // Host de todos los fragments
}

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    @Inject AuthRepository authRepository;
    
    // Autenticación OTP completa
    // UI: email input → OTP input → validación
    // Flujo: enviar OTP → validar OTP → navegar a MainActivity
    // Persistencia: JWT almacenado en EncryptedSharedPreferences
}
```

#### Fragments
```java
@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private HomeViewModel viewModel;
    
    // Observa cambios en ViewModel
    // Renderiza UI según estado
    // Maneja interacciones del usuario
}
```

#### ViewModels
```java
@HiltViewModel
public class HomeViewModel extends ViewModel {
    private final ClaseRepository repository;
    private final MutableLiveData<ApiResult<...>> data;
    
    // Expone datos para UI
    // Maneja lógica de negocio
    // Coordina con Repository
}
```

### Domain Layer (Lógica de Negocio)

#### DTOs/Models
```java
public class ClaseDTO {
    // Representación de datos de API
    // Serialización/Deserialización
    // Validaciones básicas
}
```

#### ApiResult Pattern
```java
public abstract class ApiResult<T> {
    public static class Loading<T> extends ApiResult<T>
    public static class Success<T> extends ApiResult<T>
    public static class Error<T> extends ApiResult<T>
}
```

### Data Layer (Acceso a Datos)

#### Repositories
```java
public interface ClaseRepository {
    LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> obtenerClases(FiltroClaseDTO filtro);
    LiveData<ApiResult<ClaseDTO>> obtenerDetalleClase(Long id);
}

@Singleton
public class ClaseRepositoryImpl implements ClaseRepository {
    private final RitmoFitApiService apiService;
    
    // Implementa lógica de acceso a datos
    // Maneja errores de red
    // Transforma respuestas de API
}
```

#### API Services
```java
public interface RitmoFitApiService {
    @POST("catalogo/clases")
    Call<RespuestaPaginadaDTO<ClaseDTO>> obtenerClases(@Body FiltroClaseDTO filtro);
    
    // Define endpoints de API
    // Retrofit maneja serialización
    // Type-safe networking
}
```

## Dependency Injection con Hilt

### Application
```java
@HiltAndroidApp
public class RitmoFitApplication extends Application {
    // Punto de entrada para Hilt
    // Configuración global de DI
}
```

### Modules

#### NetworkModule
```java
@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    
    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        // Configuración de timeouts
        // Interceptors (auth, logging)
        // Cache policies
    }
    
    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient client) {
        // Base URL
        // Converters (Gson)
        // Call adapters
    }
}
```

#### StorageModule
```java
@Module
@InstallIn(SingletonComponent.class)
public class StorageModule {
    
    @Provides
    @Singleton
    public SharedPreferences provideEncryptedSharedPreferences() {
        // EncryptedSharedPreferences
        // MasterKey con Keystore
        // Storage seguro para JWT
    }
}
```

#### RepositoryModule
```java
@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {
    
    @Binds
    @Singleton
    public abstract ClaseRepository bindClaseRepository(ClaseRepositoryImpl impl);
}
```

## Flujo de Datos

### 1. Carga Inicial de Datos
```
User abre app → MainActivity → Verifica auth → 
  ├─ Si NO autenticado: LoginActivity → Email → OTP → JWT → MainActivity
  └─ Si autenticado: HomeFragment → ViewModel → Repository → API → Response → UI
```

### 2. Interacción del Usuario
```
User click → Fragment → ViewModel → Repository → API → Success/Error → UI Update
```

### 3. Manejo de Estados
```
Loading State → ProgressBar visible
Success State → Mostrar datos en RecyclerView
Error State → Mensaje de error + botón retry
```

## Navegación

### Navigation Graph
```xml
<navigation app:startDestination="@id/homeFragment">
    <fragment android:id="@+id/homeFragment">
        <action app:destination="@id/detalleClaseFragment" />
    </fragment>
    
    <fragment android:id="@+id/detalleClaseFragment">
        <argument android:name="claseId" app:argType="long" />
    </fragment>
</navigation>
```

### SafeArgs
```java
// Enviar datos
Bundle bundle = new Bundle();
bundle.putLong("claseId", clase.getId());
Navigation.findNavController(view).navigate(R.id.action_home_to_detalle, bundle);

// Recibir datos
Long claseId = getArguments().getLong("claseId");
```

## Manejo de Errores

### Estrategia de Errores
1. **Repository Level**: Captura errores de API
2. **ViewModel Level**: Transforma a ApiResult
3. **Fragment Level**: Renderiza estado de error
4. **User Level**: Opción de retry

### Tipos de Errores Manejados
- **Network Errors**: Sin conexión, timeouts
- **HTTP Errors**: 400, 401, 500, etc.
- **Parse Errors**: JSON malformado
- **Business Logic Errors**: Validaciones fallidas

### Recuperación de Errores
- **Retry Automático**: Para errores temporales
- **Retry Manual**: Botón "Reintentar" en UI
- **Fallback**: Datos en cache si disponibles

## Servicios en Background

### SyncService
```java
@AndroidEntryPoint
public class SyncService extends Service {
    @Inject ClaseRepository claseRepository;
    
    // Foreground service
    // Sincronización de datos
    // Notificaciones de progreso
    // Auto-stop al completar
}
```

### AppEventsReceiver
```java
public class AppEventsReceiver extends BroadcastReceiver {
    // Escucha eventos del sistema
    // BATTERY_LOW, CONNECTIVITY_ACTION
    // Notificaciones informativas
}
```

## Seguridad

### Almacenamiento Seguro
- **EncryptedSharedPreferences**: Para datos sensibles
- **MasterKey**: Generado con Android Keystore
- **JWT Storage**: Token nunca en texto plano

### Comunicación de Red
- **HTTPS**: En producción
- **JWT Interceptor**: Agrega token automáticamente
- **Certificate Pinning**: Para producción (TODO)

### Validación de Datos
- **Input Validation**: En formularios
- **Response Validation**: Verificar estructura de API
- **Sanitización**: Limpiar datos de usuario

## Testing Strategy

### Unit Tests (Pendientes)
- ViewModels
- Repositories
- Use Cases

### Integration Tests (Pendientes)
- API Services
- Database operations
- Navigation flow

### UI Tests (Pendientes)
- Fragment interactions
- Navigation scenarios
- Error handling

## Escalabilidad

### Preparado para Crecimiento
- **Modular Architecture**: Fácil agregar nuevas features
- **Dependency Injection**: Desacoplamiento de componentes
- **Repository Pattern**: Fácil cambiar fuentes de datos
- **Clean Separation**: Cada capa independiente

### Futuras Mejoras
- **Offline Support**: Room database
- **Image Loading**: Glide/Coil
- **Caching**: HTTP cache + local cache
- **Analytics**: Firebase Analytics
- **Crash Reporting**: Firebase Crashlytics

