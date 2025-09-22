package com.ritmofit.app.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ritmofit.app.data.api.RitmoFitApiService;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.ReservaDTO;
import com.ritmofit.app.data.dto.TurnoDTO;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ReservaRepositoryImpl implements ReservaRepository {
    
    private static final String TAG = "ReservaRepository";
    
    private final RitmoFitApiService apiService;
    
    @Inject
    public ReservaRepositoryImpl(RitmoFitApiService apiService) {
        this.apiService = apiService;
    }
    
    @Override
    public LiveData<ApiResult<TurnoDTO>> crearReserva(ReservaDTO reservaDTO) {
        MutableLiveData<ApiResult<TurnoDTO>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.crearReserva(reservaDTO).enqueue(new Callback<TurnoDTO>() {
            @Override
            public void onResponse(Call<TurnoDTO> call, Response<TurnoDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al crear reserva: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<TurnoDTO> call, Throwable t) {
                Log.e(TAG, "Error al crear reserva", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
    
    @Override
    public LiveData<ApiResult<TurnoDTO>> cancelarReserva(Long turnoId) {
        MutableLiveData<ApiResult<TurnoDTO>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.cancelarReserva(turnoId).enqueue(new Callback<TurnoDTO>() {
            @Override
            public void onResponse(Call<TurnoDTO> call, Response<TurnoDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al cancelar reserva: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<TurnoDTO> call, Throwable t) {
                Log.e(TAG, "Error al cancelar reserva", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
    
    @Override
    public LiveData<ApiResult<List<TurnoDTO>>> obtenerMisReservas() {
        MutableLiveData<ApiResult<List<TurnoDTO>>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.obtenerMisReservas().enqueue(new Callback<List<TurnoDTO>>() {
            @Override
            public void onResponse(Call<List<TurnoDTO>> call, Response<List<TurnoDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al obtener reservas: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<List<TurnoDTO>> call, Throwable t) {
                Log.e(TAG, "Error al obtener reservas", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
    
    @Override
    public LiveData<ApiResult<Object>> crearClasificacion(Map<String, Object> clasificacion) {
        MutableLiveData<ApiResult<Object>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.crearClasificacion(clasificacion).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al crear clasificación: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "Error al crear clasificación", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
    
    @Override
    public LiveData<ApiResult<List<Object>>> obtenerMisClasificaciones(String email) {
        MutableLiveData<ApiResult<List<Object>>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.obtenerClasificacionesPorUsuario(email).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al obtener clasificaciones: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {
                Log.e(TAG, "Error al obtener clasificaciones", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
}

