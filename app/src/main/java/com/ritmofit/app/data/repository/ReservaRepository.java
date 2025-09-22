package com.ritmofit.app.data.repository;

import androidx.lifecycle.LiveData;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.ReservaDTO;
import com.ritmofit.app.data.dto.TurnoDTO;

import java.util.List;
import java.util.Map;

public interface ReservaRepository {
    
    /**
     * Crea una nueva reserva
     */
    LiveData<ApiResult<TurnoDTO>> crearReserva(ReservaDTO reservaDTO);
    
    /**
     * Cancela una reserva
     */
    LiveData<ApiResult<TurnoDTO>> cancelarReserva(Long turnoId);
    
    /**
     * Obtiene las próximas reservas del usuario
     */
    LiveData<ApiResult<List<TurnoDTO>>> obtenerMisReservas();
    
    /**
     * Crea una clasificación para una clase
     */
    LiveData<ApiResult<Object>> crearClasificacion(Map<String, Object> clasificacion);
    
    /**
     * Obtiene las clasificaciones del usuario
     */
    LiveData<ApiResult<List<Object>>> obtenerMisClasificaciones(String email);
}


