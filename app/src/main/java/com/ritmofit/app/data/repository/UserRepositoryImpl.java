package com.ritmofit.app.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ritmofit.app.data.api.RitmoFitApiService;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepositoryImpl implements UserRepository{
    private final RitmoFitApiService api;

    @Inject
    public UserRepositoryImpl(RitmoFitApiService api) {
        this.api = api;
    }

    @Override
    public LiveData<ApiResult<UserDTO>> obtenerUserById(Long id) {
        MutableLiveData<ApiResult<UserDTO>> resultLiveData = new MutableLiveData<>();

        api.obtenerUserById(id).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.postValue(new ApiResult.Success<>(response.body()));
                } else {
                    resultLiveData.postValue(new ApiResult.Error<>(
                            "Error al obtener usuario: " + response.code()
                    ));
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                resultLiveData.postValue(new ApiResult.Error<>("Fallo en la petición: " + t.getMessage()));
            }
        });

        return resultLiveData;
    }

    @Override
    public LiveData<ApiResult<String>> actualizarUsuario(Long id, UserDTO userDTO) {
        MutableLiveData<ApiResult<String>> resultLiveData = new MutableLiveData<>();

        api.actualizarUsuario(id, userDTO).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.postValue(new ApiResult.Success<>(response.body()));
                } else {
                    resultLiveData.postValue(new ApiResult.Error<>(
                            "Error al actualizar usuario: " + response.code()
                    ));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                resultLiveData.postValue(new ApiResult.Error<>("Fallo en la petición: " + t.getMessage()));
            }
        });

        return resultLiveData;
    }
}
