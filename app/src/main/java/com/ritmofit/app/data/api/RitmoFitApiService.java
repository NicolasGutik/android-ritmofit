package com.ritmofit.app.data.api;

import com.ritmofit.app.data.dto.*;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface RitmoFitApiService {
    
    // ========== AUTENTICACIÓN ==========
    
    /**
     * Envía OTP al email del usuario
     * POST /auth/login
     */
    @POST("auth/login")
    @Headers("Content-Type: application/json")
    Call<Map<String, Object>> enviarOTP(@Body Map<String, String> body);
    
    /**
     * Valida OTP y obtiene JWT
     * POST /auth/validate
     */
    @POST("auth/validate")
    @Headers("Content-Type: application/json")
    Call<Map<String, Object>> validarOTP(@Body Map<String, String> body);
    
    /**
     * Registra nuevo usuario
     * POST /auth/register
     */
    @POST("auth/register")
    @Headers("Content-Type: application/json")
    Call<Map<String, Object>> registrarUsuario(@Body UserDTO userDTO);
    
    /**
     * Actualiza datos del usuario
     * PUT /auth/update/{id}
     */
    @PUT("auth/update/{id}")
    @Headers("Content-Type: application/json")
    Call<String> actualizarUsuario(@Path("id") Long id, @Body UserDTO userDTO);
    
    // ========== CATÁLOGO DE CLASES ==========
    
    /**
     * Obtiene listado paginado y filtrado de clases
     * POST /catalogo/clases
     */
    @POST("catalogo/clases")
    @Headers("Content-Type: application/json")
    Call<RespuestaPaginadaDTO<ClaseDTO>> obtenerClases(@Body FiltroClaseDTO filtro);
    
    /**
     * Obtiene detalle de una clase específica
     * GET /catalogo/clases/{id}
     */
    @GET("catalogo/clases/{id}")
    Call<ClaseDTO> obtenerDetalleClase(@Path("id") Long id);
    
    /**
     * Obtiene todas las sedes disponibles
     * GET /catalogo/sedes
     */
    @GET("catalogo/sedes")
    Call<List<String>> obtenerSedes();
    
    /**
     * Obtiene todas las disciplinas disponibles
     * GET /catalogo/disciplinas
     */
    @GET("catalogo/disciplinas")
    Call<List<String>> obtenerDisciplinas();
    
    // ========== GESTIÓN DE RESERVAS ==========
    
    /**
     * Crea una nueva reserva
     * POST /api/reservas
     */
    @POST("api/reservas")
    @Headers("Content-Type: application/json")
    Call<TurnoDTO> crearReserva(@Body ReservaDTO reservaDTO);
    
    /**
     * Cancela una reserva
     * DELETE /api/reservas/{turnoId}
     */
    @DELETE("api/reservas/{turnoId}")
    Call<TurnoDTO> cancelarReserva(@Path("turnoId") Long turnoId);
    
    /**
     * Obtiene las próximas reservas del usuario
     * GET /api/reservas/mias
     */
    @GET("api/reservas/mias")
    Call<List<TurnoDTO>> obtenerMisReservas();
    
    // ========== TURNOS ==========
    
    /**
     * Obtiene todos los turnos de una clase específica
     * GET /turnos/clase/{claseId}
     */
    @GET("turnos/clase/{claseId}")
    Call<List<TurnoDTO>> obtenerTurnosPorClase(@Path("claseId") Long claseId);
    
    /**
     * Obtiene número de turnos confirmados para una clase
     * GET /turnos/clase/{claseId}/confirmados/count
     */
    @GET("turnos/clase/{claseId}/confirmados/count")
    Call<Integer> contarTurnosConfirmados(@Path("claseId") Long claseId);
    
    /**
     * Obtiene historial de asistencias del usuario
     * GET /turnos/asistencias/{userId}
     */
    @GET("turnos/asistencias/{userId}")
    Call<List<AsistenciaDTO>> obtenerAsistencias(@Path("userId") Long userId);
    
    /**
     * Obtiene historial de asistencias del usuario con filtro de fechas
     * GET /turnos/asistencias/{userId}?fechaDesde={fechaDesde}&fechaHasta={fechaHasta}
     */
    @GET("turnos/asistencias/{userId}")
    Call<List<AsistenciaDTO>> obtenerAsistenciasConFiltro(
        @Path("userId") Long userId,
        @Query("fechaDesde") String fechaDesde,
        @Query("fechaHasta") String fechaHasta
    );
    
    /**
     * Obtiene historial de asistencias del usuario con filtro de fechas y disciplina
     * GET /turnos/asistencias/{userId}?fechaDesde={fechaDesde}&fechaHasta={fechaHasta}&disciplina={disciplina}
     */
    @GET("turnos/asistencias/{userId}")
    Call<List<AsistenciaDTO>> obtenerAsistenciasConFiltroCompleto(
        @Path("userId") Long userId,
        @Query("fechaDesde") String fechaDesde,
        @Query("fechaHasta") String fechaHasta,
        @Query("disciplina") String disciplina
    );
    
    // ========== CLASIFICACIONES ==========
    
    /**
     * Crea una nueva clasificación para una clase
     * POST /clasificacion
     */
    @POST("clasificacion")
    @Headers("Content-Type: application/json")
    Call<Object> crearClasificacion(@Body Map<String, Object> clasificacion);
    
    /**
     * Obtiene todas las clasificaciones de un usuario específico
     * GET /clasificacion/{email}
     */
    @GET("clasificacion/{email}")
    Call<List<Object>> obtenerClasificacionesPorUsuario(@Path("email") String email);

    // ========== PREFERENCIAS / FILTRO DE USUARIO ==========

    /**
     * Crea o actualiza SOLO el filtro/preferencias del usuario
     */
    @POST("auth/upsert/preferencias/{id}")
    @Headers("Content-Type: application/json")
    Call<Void> upsertPreferencias(
            @Path("id") Long userId,
            @Body FiltroClaseUserDTO filtro,
            @Header("Authorization") String bearerToken
    );

    /**
     * Obtiene el filtro/preferencias actual del usuario
     */
    @GET("auth/get/preferencias/{id}")
    Call<FiltroClaseUserDTO> getPreferencias(
            @Path("id") Long userId,
            @Header("Authorization") String bearerToken
    );

    /**
     * Resetea el filtro/preferencias del usuario (vacía)
     */
    @DELETE("auth/delete/preferencias/{id}")
    Call<Void> resetPreferencias(
            @Path("id") Long userId,
            @Header("Authorization") String bearerToken
    );

// ========== CLASES SEGÚN PREFERENCIAS DEL USUARIO ==========

    /**
     * Trae clases aplicando el filtro guardado del usuario
     * que seria
     * 0 → cumple todas las referencias provistas por el usuario (sede + franja + disciplina).
     *
     * 1 → cumple sede + franja (si el usuario definió ambas).
     *
     * 2 → cumple exactamente dos referencias (cualquier par excepto el caso especial sede+franja del punto 2).
     *
     * 3 → cumple exactamente una referencia.
     *
     * 4 → no cumple ninguna (o el usuario no definió ninguna preferencia).
     */
    @GET("clases/home/{id}")
    Call<RespuestaPaginadaHome<ClaseDTO>> getClasesHome(
            @Path("id") Long userId
    );
}


