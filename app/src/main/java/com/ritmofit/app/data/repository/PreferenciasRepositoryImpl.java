package com.ritmofit.app.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ritmofit.app.data.api.RitmoFitApiService;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.FiltroClaseUserDTO;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class PreferenciasRepositoryImpl implements PreferenciasRepository {

    private static final String TAG = "PreferenciasRepository";
    private final RitmoFitApiService api;
    private final AuthRepository authRepo; // para obtener el token almacenado

    @Inject
    public PreferenciasRepositoryImpl(RitmoFitApiService api, AuthRepository authRepo) {
        this.api = api;
        this.authRepo = authRepo;
    }

    private String bearer() {
        String t = authRepo.obtenerToken();
        return (t == null || t.isEmpty()) ? null : "Bearer " + t;
    }

    @Override
    public LiveData<ApiResult<Void>> upsertPreferencias(Long userId, FiltroClaseUserDTO filtro) {
        MutableLiveData<ApiResult<Void>> ld = new MutableLiveData<>(new ApiResult.Loading<>());
        api.upsertPreferencias(userId, filtro, bearer()).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) {
                    ld.setValue(new ApiResult.Success<>(null));
                } else {
                    ld.setValue(new ApiResult.Error<>("Error upsert preferencias: " + resp.code()));
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "upsertPreferencias fail", t);
                ld.setValue(new ApiResult.Error<>("Conexión: " + t.getMessage(), t));
            }
        });
        return ld;
    }

    @Override
    public LiveData<ApiResult<FiltroClaseUserDTO>> getPreferencias(Long userId) {
        MutableLiveData<ApiResult<FiltroClaseUserDTO>> ld = new MutableLiveData<>(new ApiResult.Loading<>());
        api.getPreferencias(userId, bearer()).enqueue(new Callback<FiltroClaseUserDTO>() {
            @Override public void onResponse(Call<FiltroClaseUserDTO> call, Response<FiltroClaseUserDTO> resp) {
                if (resp.isSuccessful() && resp.body()!=null) {
                    ld.setValue(new ApiResult.Success<>(resp.body()));
                } else {
                    ld.setValue(new ApiResult.Error<>("Error get preferencias: " + resp.code()));
                }
            }
            @Override public void onFailure(Call<FiltroClaseUserDTO> call, Throwable t) {
                Log.e(TAG, "getPreferencias fail", t);
                ld.setValue(new ApiResult.Error<>("Conexión: " + t.getMessage(), t));
            }
        });
        return ld;
    }

    @Override
    public LiveData<ApiResult<Void>> resetPreferencias(Long userId) {
        MutableLiveData<ApiResult<Void>> ld = new MutableLiveData<>(new ApiResult.Loading<>());
        api.resetPreferencias(userId, bearer()).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) {
                    ld.setValue(new ApiResult.Success<>(null));
                } else {
                    ld.setValue(new ApiResult.Error<>("Error reset preferencias: " + resp.code()));
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "resetPreferencias fail", t);
                ld.setValue(new ApiResult.Error<>("Conexión: " + t.getMessage(), t));
            }
        });
        return ld;
    }
}