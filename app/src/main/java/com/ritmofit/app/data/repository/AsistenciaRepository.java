package com.ritmofit.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ritmofit.app.data.api.RitmoFitApiService;
import com.ritmofit.app.data.dto.AsistenciaDTO;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AsistenciaRepository {

    private final RitmoFitApiService apiService;

    @Inject
    public AsistenciaRepository(RitmoFitApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<AsistenciaDTO>> obtenerAsistencias(Long userId) {
        MutableLiveData<List<AsistenciaDTO>> liveData = new MutableLiveData<>();
        
        apiService.obtenerAsistencias(userId).enqueue(new Callback<List<AsistenciaDTO>>() {
            @Override
            public void onResponse(Call<List<AsistenciaDTO>> call, Response<List<AsistenciaDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<AsistenciaDTO>> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        
        return liveData;
    }

    public LiveData<List<AsistenciaDTO>> obtenerAsistenciasConFiltro(Long userId, String fechaDesde, String fechaHasta) {
        MutableLiveData<List<AsistenciaDTO>> liveData = new MutableLiveData<>();
        
        apiService.obtenerAsistenciasConFiltro(userId, fechaDesde, fechaHasta).enqueue(new Callback<List<AsistenciaDTO>>() {
            @Override
            public void onResponse(Call<List<AsistenciaDTO>> call, Response<List<AsistenciaDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<AsistenciaDTO>> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        
        return liveData;
    }
    
    public LiveData<List<AsistenciaDTO>> obtenerAsistenciasConFiltroCompleto(Long userId, String fechaDesde, String fechaHasta, String disciplina) {
        MutableLiveData<List<AsistenciaDTO>> liveData = new MutableLiveData<>();
        
        apiService.obtenerAsistenciasConFiltroCompleto(userId, fechaDesde, fechaHasta, disciplina).enqueue(new Callback<List<AsistenciaDTO>>() {
            @Override
            public void onResponse(Call<List<AsistenciaDTO>> call, Response<List<AsistenciaDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<AsistenciaDTO>> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        
        return liveData;
    }
}
