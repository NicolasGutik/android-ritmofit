package com.ritmofit.app.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.ritmofit.app.data.api.RitmoFitApiService;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.ClaseDTO;
import com.ritmofit.app.data.dto.FiltroClaseDTO;
import com.ritmofit.app.data.dto.RespuestaPaginadaDTO;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ClaseRepositoryImpl implements ClaseRepository {
    
    private static final String TAG = "ClaseRepository";
    
    private final RitmoFitApiService apiService;
    private final Gson gson;
    
    @Inject
    public ClaseRepositoryImpl(RitmoFitApiService apiService, Gson gson) {
        this.apiService = apiService;
        this.gson = gson;
    }
    
    @Override
    public LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> obtenerClases(FiltroClaseDTO filtro) {
        MutableLiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.obtenerClases(filtro).enqueue(new Callback<RespuestaPaginadaDTO<ClaseDTO>>() {
            @Override
            public void onResponse(Call<RespuestaPaginadaDTO<ClaseDTO>> call, Response<RespuestaPaginadaDTO<ClaseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al obtener clases: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<RespuestaPaginadaDTO<ClaseDTO>> call, Throwable t) {
                Log.e(TAG, "Error al obtener clases", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
    
    @Override
    public LiveData<ApiResult<ClaseDTO>> obtenerDetalleClase(Long id) {
        MutableLiveData<ApiResult<ClaseDTO>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.obtenerDetalleClase(id).enqueue(new Callback<ClaseDTO>() {
            @Override
            public void onResponse(Call<ClaseDTO> call, Response<ClaseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al obtener detalle de clase: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<ClaseDTO> call, Throwable t) {
                Log.e(TAG, "Error al obtener detalle de clase", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
    
    @Override
    public LiveData<ApiResult<List<String>>> obtenerSedes() {
        MutableLiveData<ApiResult<List<String>>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.obtenerSedes().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al obtener sedes: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "Error al obtener sedes", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
    
    @Override
    public LiveData<ApiResult<List<String>>> obtenerDisciplinas() {
        MutableLiveData<ApiResult<List<String>>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.obtenerDisciplinas().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al obtener disciplinas: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "Error al obtener disciplinas", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
    
    @Override
    public LiveData<ApiResult<List<Object>>> obtenerTurnosPorClase(Long claseId) {
        MutableLiveData<ApiResult<List<Object>>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.obtenerTurnosPorClase(claseId).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al obtener turnos: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {
                Log.e(TAG, "Error al obtener turnos", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
    
    @Override
    public LiveData<ApiResult<Integer>> contarTurnosConfirmados(Long claseId) {
        MutableLiveData<ApiResult<Integer>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.contarTurnosConfirmados(claseId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al contar turnos: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e(TAG, "Error al contar turnos", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
}

