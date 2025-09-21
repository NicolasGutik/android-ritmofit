package com.ritmofit.app.data.repository;

import androidx.lifecycle.LiveData;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.ClaseDTO;
import com.ritmofit.app.data.dto.FiltroClaseDTO;
import com.ritmofit.app.data.dto.RespuestaPaginadaDTO;
import com.ritmofit.app.data.dto.TurnoDTO;

import java.util.List;

public interface ClaseRepository {
    
    /**
     * Obtiene listado paginado y filtrado de clases
     */
    LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> obtenerClases(FiltroClaseDTO filtro);
    
    /**
     * Obtiene detalle de una clase específica
     */
    LiveData<ApiResult<ClaseDTO>> obtenerDetalleClase(Long id);
    
    /**
     * Obtiene todas las sedes disponibles
     */
    LiveData<ApiResult<List<String>>> obtenerSedes();
    
    /**
     * Obtiene todas las disciplinas disponibles
     */
    LiveData<ApiResult<List<String>>> obtenerDisciplinas();
    
    /**
     * Obtiene turnos de una clase específica
     */
    LiveData<ApiResult<List<TurnoDTO>>> obtenerTurnosPorClase(Long claseId);
    
    /**
     * Obtiene número de turnos confirmados para una clase
     */
    LiveData<ApiResult<Integer>> contarTurnosConfirmados(Long claseId);
}


