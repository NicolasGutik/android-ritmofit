# Endpoints API - RitmoFit

## Base URL
- **Desarrollo**: `http://10.0.2.2:8080/` (emulador Android)
- **Dispositivo físico**: `http://[IP_LOCAL]:8080/`
- **Producción**: `https://api.ritmofit.com/` (TODO)

## Autenticación

### 1. Enviar OTP
```http
POST /auth/login
Content-Type: application/json

{
  "email": "usuario@email.com"
}
```

**Respuesta Exitosa (200)**:
```json
"OTP enviado al correo."
```

**Implementación Android**:
```java
// En RitmoFitApiService.java
@POST("auth/login")
@Headers("Content-Type: application/json")
Call<ResponseBody> enviarOTP(@Body Map<String, String> body);

// En AuthRepositoryImpl.java
public LiveData<ApiResult<String>> enviarOTP(String email) {
    // Maneja ResponseBody y extrae string content
    // Configurado con Gson lenient para manejar respuestas de texto plano
}
```

**Respuesta de Error (400)**:
```json
"Email es requerido"
```

**Respuesta de Error (500)**:
```json
"Error al enviar OTP: [detalle del error]"
```

### 2. Validar OTP
```http
POST /auth/validate
Content-Type: application/json

{
  "email": "usuario@email.com",
  "code": "123456"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "user": {
    "id": 1,
    "email": "usuario@email.com",
    "nombre": "Juan",
    "apellido": "Pérez"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Implementación Android**:
```java
// En RitmoFitApiService.java
@POST("auth/validate")
@Headers("Content-Type: application/json")
Call<Map<String, Object>> validarOTP(@Body Map<String, String> body);

// En AuthRepositoryImpl.java
public LiveData<ApiResult<UserDTO>> validarOTP(String email, String otp) {
    // Extrae user y token de la respuesta
    // Guarda JWT en EncryptedSharedPreferences
    // Retorna UserDTO para la UI
}
```

**Respuesta de Error (400)**:
```json
"Email es requerido"
```
```json
"Código OTP es requerido"
```
```json
"Código OTP inválido"
```

### 3. Registrar Usuario
```http
POST /auth/register
Content-Type: application/json

{
  "email": "nuevo@email.com",
  "nombre": "María",
  "apellido": "González",
  "telefono": "+54911234567"
}
```

**Respuesta Exitosa (200)**:
```json
"Usuario registrado exitosamente"
```

### 4. Actualizar Usuario
```http
PUT /auth/update/{id}
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "email": "usuario@email.com",
  "nombre": "Juan Carlos",
  "apellido": "Pérez",
  "telefono": "+54911234567"
}
```

**Respuesta Exitosa (200)**:
```json
"Usuario actualizado exitosamente"
```

## Catálogo de Clases

### 1. Listar Clases con Filtros
```http
POST /catalogo/clases
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "sede": "Sede Norte",
  "disciplina": "Yoga",
  "fechaDesde": "2025-01-01T00:00:00",
  "fechaHasta": "2025-12-31T23:59:59",
  "pagina": 0,
  "tamanio": 10
}
```

**Respuesta Exitosa (200)**:
```json
{
  "contenido": [
    {
      "id": 1,
      "sede": "Sede Norte",
      "lugar": "Sala A",
      "fecha": "2025-01-15T10:00:00",
      "duracion": "60 minutos",
      "disciplina": "Yoga",
      "cupos": 20,
      "cuposDisponibles": 15,
      "nombreProfesor": "María González",
      "profesorId": 1
    }
  ],
  "pagina": 0,
  "tamanio": 10,
  "totalElementos": 25,
  "totalPaginas": 3,
  "primera": true,
  "ultima": false,
  "tieneSiguiente": true,
  "tieneAnterior": false
}
```

**Filtros Opcionales**:
- `sede`: Filtrar por sede específica
- `disciplina`: Filtrar por disciplina específica
- `fechaDesde`: Fecha de inicio para el rango
- `fechaHasta`: Fecha de fin para el rango
- `pagina`: Número de página (comienza en 0)
- `tamanio`: Tamaño de la página

### 2. Obtener Detalle de Clase
```http
GET /catalogo/clases/{id}
Authorization: Bearer {jwt_token}
```

**Respuesta Exitosa (200)**:
```json
{
  "id": 1,
  "sede": "Sede Norte",
  "lugar": "Sala A",
  "fecha": "2025-01-15T10:00:00",
  "duracion": "60 minutos",
  "disciplina": "Yoga",
  "cupos": 20,
  "cuposDisponibles": 15,
  "nombreProfesor": "María González",
  "profesorId": 1
}
```

### 3. Listar Sedes Disponibles
```http
GET /catalogo/sedes
Authorization: Bearer {jwt_token}
```

**Respuesta Exitosa (200)**:
```json
[
  "Sede Norte",
  "Sede Sur",
  "Sede Centro"
]
```

### 4. Listar Disciplinas Disponibles
```http
GET /catalogo/disciplinas
Authorization: Bearer {jwt_token}
```

**Respuesta Exitosa (200)**:
```json
[
  "Yoga",
  "Pilates",
  "CrossFit",
  "Spinning",
  "Funcional"
]
```

## Gestión de Reservas

### 1. Crear Reserva
```http
POST /api/reservas
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "claseId": 1
}
```

**Respuesta Exitosa (200)**:
```json
{
  "id": 1,
  "claseId": 1,
  "userId": 1,
  "estado": "CONFIRMADO",
  "fechaReserva": "2025-01-10T15:30:00",
  "fechaClase": "2025-01-15T10:00:00",
  "sede": "Sede Norte",
  "disciplina": "Yoga",
  "lugar": "Sala A",
  "profesor": "María González"
}
```

**Respuesta de Error (400)**:
```json
"No hay cupos disponibles"
```

**Respuesta de Error (409)**:
```json
"Ya tienes una reserva para esta clase"
```

### 2. Cancelar Reserva
```http
DELETE /api/reservas/{turnoId}
Authorization: Bearer {jwt_token}
```

**Respuesta Exitosa (200)**:
```json
{
  "id": 1,
  "claseId": 1,
  "userId": 1,
  "estado": "CANCELADO",
  "fechaReserva": "2025-01-10T15:30:00",
  "fechaClase": "2025-01-15T10:00:00",
  "sede": "Sede Norte",
  "disciplina": "Yoga",
  "lugar": "Sala A",
  "profesor": "María González"
}
```

### 3. Obtener Mis Reservas
```http
GET /api/reservas/mias
Authorization: Bearer {jwt_token}
```

**Respuesta Exitosa (200)**:
```json
[
  {
    "id": 1,
    "claseId": 1,
    "userId": 1,
    "estado": "CONFIRMADO",
    "fechaReserva": "2025-01-10T15:30:00",
    "fechaClase": "2025-01-15T10:00:00",
    "sede": "Sede Norte",
    "disciplina": "Yoga",
    "lugar": "Sala A",
    "profesor": "María González"
  },
  {
    "id": 2,
    "claseId": 3,
    "userId": 1,
    "estado": "CONFIRMADO",
    "fechaReserva": "2025-01-11T09:15:00",
    "fechaClase": "2025-01-16T14:00:00",
    "sede": "Sede Sur",
    "disciplina": "Pilates",
    "lugar": "Sala B",
    "profesor": "Carlos López"
  }
]
```

## Gestión de Turnos

### 1. Obtener Turnos por Clase
```http
GET /turnos/clase/{claseId}
Authorization: Bearer {jwt_token}
```

**Respuesta Exitosa (200)**:
```json
[
  {
    "id": 1,
    "claseId": 1,
    "userId": 1,
    "estado": "CONFIRMADO",
    "fechaReserva": "2025-01-10T15:30:00",
    "fechaClase": "2025-01-15T10:00:00"
  }
]
```

### 2. Contar Turnos Confirmados
```http
GET /turnos/clase/{claseId}/confirmados/count
Authorization: Bearer {jwt_token}
```

**Respuesta Exitosa (200)**:
```json
15
```

## Sistema de Clasificaciones

### 1. Crear Clasificación
```http
POST /clasificacion
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "id": null,
  "user": {
    "id": 1,
    "email": "usuario@email.com"
  },
  "clase": {
    "id": 1
  },
  "estrellas": 5,
  "comentario": "Excelente clase de yoga"
}
```

**Respuesta Exitosa (200)**:
```json
{
  "id": 1,
  "user": {
    "id": 1,
    "email": "usuario@email.com"
  },
  "clase": {
    "id": 1
  },
  "estrellas": 5,
  "comentario": "Excelente clase de yoga",
  "fechaCreacion": "2025-01-16T12:00:00"
}
```

### 2. Obtener Clasificaciones por Usuario
```http
GET /clasificacion/{email}
Authorization: Bearer {jwt_token}
```

**Respuesta Exitosa (200)**:
```json
[
  {
    "id": 1,
    "user": {
      "id": 1,
      "email": "usuario@email.com"
    },
    "clase": {
      "id": 1,
      "disciplina": "Yoga",
      "fecha": "2025-01-15T10:00:00"
    },
    "estrellas": 5,
    "comentario": "Excelente clase de yoga",
    "fechaCreacion": "2025-01-16T12:00:00"
  }
]
```

## Códigos de Error HTTP

### 200 - OK
Request exitoso

### 400 - Bad Request
Datos incorrectos o faltantes
```json
{
  "error": "Email es requerido",
  "timestamp": "2025-01-16T12:00:00",
  "path": "/auth/login"
}
```

### 401 - Unauthorized
JWT inválido o faltante
```json
{
  "error": "Token JWT inválido",
  "timestamp": "2025-01-16T12:00:00",
  "path": "/catalogo/clases"
}
```

### 403 - Forbidden
Sin permisos para el recurso
```json
{
  "error": "No tienes permisos para acceder a este recurso",
  "timestamp": "2025-01-16T12:00:00",
  "path": "/api/reservas"
}
```

### 404 - Not Found
Recurso no encontrado
```json
{
  "error": "Clase no encontrada",
  "timestamp": "2025-01-16T12:00:00",
  "path": "/catalogo/clases/999"
}
```

### 409 - Conflict
Conflicto de datos (ej: reserva duplicada)
```json
{
  "error": "Ya tienes una reserva para esta clase",
  "timestamp": "2025-01-16T12:00:00",
  "path": "/api/reservas"
}
```

### 500 - Internal Server Error
Error del servidor
```json
{
  "error": "Error interno del servidor",
  "timestamp": "2025-01-16T12:00:00",
  "path": "/auth/login"
}
```

## Headers Requeridos

### Autenticación
```
Authorization: Bearer {jwt_token}
```

### Content-Type
```
Content-Type: application/json
```

### Accept
```
Accept: application/json
```

## Rate Limiting

La API implementa rate limiting para prevenir abuso:

- **Autenticación**: 5 intentos por minuto por IP
- **General**: 100 requests por minuto por usuario autenticado
- **Headers de respuesta**:
  ```
  X-RateLimit-Limit: 100
  X-RateLimit-Remaining: 95
  X-RateLimit-Reset: 1642345678
  ```

## Paginación

Para endpoints que devuelven listas:

- **Parámetros**:
  - `pagina`: Número de página (comienza en 0)
  - `tamanio`: Tamaño de la página (máximo 50)
- **Respuesta**:
  - `contenido`: Array de elementos
  - `pagina`: Página actual
  - `tamanio`: Tamaño de la página
  - `totalElementos`: Total de elementos
  - `totalPaginas`: Total de páginas
  - `primera`: Es la primera página
  - `ultima`: Es la última página
  - `tieneSiguiente`: Tiene página siguiente
  - `tieneAnterior`: Tiene página anterior

## Formato de Fechas

Todas las fechas están en formato ISO 8601:
```
YYYY-MM-DDTHH:mm:ss
```

Ejemplo: `2025-01-15T10:00:00`

## Ejemplos de Uso Completo

### Flujo de Autenticación
```bash
# 1. Enviar OTP
curl -X POST http://10.0.2.2:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@email.com"}'

# 2. Validar OTP
curl -X POST http://10.0.2.2:8080/auth/validate \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@email.com", "code": "123456"}'

# 3. Usar JWT para acceder al catálogo
curl -X POST http://10.0.2.2:8080/catalogo/clases \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"pagina": 0, "tamanio": 10}'
```

### Flujo de Autenticación Android (IMPLEMENTADO)
```java
// 1. LoginActivity - Enviar OTP
authRepository.enviarOTP(email).observe(this, result -> {
    if (result instanceof ApiResult.Success) {
        // Mostrar campos de OTP
        showOtpFields();
    }
});

// 2. LoginActivity - Validar OTP
authRepository.validarOTP(email, otp).observe(this, result -> {
    if (result instanceof ApiResult.Success) {
        // JWT guardado automáticamente en EncryptedSharedPreferences
        // Navegar a MainActivity
        navigateToMain();
    }
});

// 3. MainActivity - Verificar autenticación
if (!authRepository.isUsuarioAutenticado()) {
    // Redirigir a LoginActivity
    startActivity(new Intent(this, LoginActivity.class));
}
```

### Flujo de Reserva
```bash
# 1. Obtener detalle de clase
curl -X GET http://10.0.2.2:8080/catalogo/clases/1 \
  -H "Authorization: Bearer {jwt_token}"

# 2. Crear reserva
curl -X POST http://10.0.2.2:8080/api/reservas \
  -H "Authorization: Bearer {jwt_token}" \
  -H "Content-Type: application/json" \
  -d '{"claseId": 1}'

# 3. Ver mis reservas
curl -X GET http://10.0.2.2:8080/api/reservas/mias \
  -H "Authorization: Bearer {jwt_token}"
```

## Notas Importantes

1. **JWT Obligatorio**: Todos los endpoints (excepto `/auth/**`) requieren JWT válido
2. **Paginación**: Comienza en página 0
3. **Filtros Opcionales**: Si no se especifica un filtro, se aplica a todos los registros
4. **Cupos Disponibles**: Se calculan automáticamente
5. **Seguridad**: Los usuarios solo pueden ver/modificar sus propios datos
6. **Timeouts**: Requests timeout después de 30 segundos
7. **CORS**: Configurado para desarrollo local

