# Funcionalidad de Mis Reservas

## Descripción
La funcionalidad "Mis Reservas" permite a los usuarios ver todas sus reservas de turnos de clases en la aplicación RitmoFit.

## Arquitectura

### Componentes Principales

1. **MisReservasFragment** - Fragmento principal que muestra la lista de reservas
2. **MisReservasViewModel** - ViewModel que maneja la lógica de negocio
3. **ReservaRepository** - Repositorio que gestiona las llamadas a la API
4. **TurnoAdapter** - Adaptador para mostrar las reservas en un RecyclerView
5. **RitmoFitApiService** - Servicio API para comunicación con el backend

### Flujo de Datos

```
Usuario → MisReservasFragment → MisReservasViewModel → ReservaRepository → RitmoFitApiService → Backend
```

### API Endpoint
- **GET** `/api/reservas/mias` - Obtiene todas las reservas del usuario autenticado

## Características Implementadas

### ✅ Funcionalidades Completadas

1. **Visualización de Reservas**
   - Lista todas las reservas del usuario
   - Muestra información detallada: disciplina, sede, profesor, fecha, estado
   - Estados visuales: CONFIRMADO (verde), CANCELADO (rojo), PENDIENTE (naranja)

2. **Estados de Carga**
   - ProgressBar durante la carga
   - Mensaje de estado vacío cuando no hay reservas
   - Manejo de errores con mensajes informativos

3. **Interfaz de Usuario**
   - Diseño moderno con Material Design
   - Cards para cada reserva
   - Navegación por tabs inferior
   - Formateo de fechas legible

4. **Gestión de Estados**
   - Loading state
   - Success state
   - Error state
   - Empty state

## Estructura de Datos

### TurnoDTO
```java
{
  "id": Long,
  "claseId": Long,
  "userId": Long,
  "estado": String, // CONFIRMADO, CANCELADO, PENDIENTE
  "fechaReserva": String, // ISO 8601
  "fechaClase": String, // ISO 8601
  "sede": String,
  "disciplina": String,
  "lugar": String,
  "profesor": String
}
```

## Navegación

La funcionalidad está integrada en la navegación principal:
- **Tab**: "Mis Reservas" en la navegación inferior
- **Ruta**: `misReservasFragment` en el nav_graph.xml

## Uso

1. El usuario navega a la pestaña "Mis Reservas"
2. La aplicación automáticamente carga las reservas del usuario
3. Se muestran todas las reservas con su información detallada
4. El usuario puede hacer clic en una reserva para ver más detalles

## Configuración del Backend

Para que funcione correctamente, el backend debe:
1. Implementar el endpoint `GET /api/reservas/mias`
2. Retornar un array de objetos TurnoDTO
3. Incluir el token de autenticación en las headers
4. Manejar la autenticación del usuario

## Próximas Mejoras

- [ ] Implementar cancelación de reservas
- [ ] Filtros por estado de reserva
- [ ] Búsqueda de reservas
- [ ] Notificaciones push para recordatorios
- [ ] Sincronización offline
