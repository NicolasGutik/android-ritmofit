package com.ritmofit.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ritmofit.app.data.api.RitmoFitApiService;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class UserRepositoryImpl implements UserRepository {

    private final RitmoFitApiService api;

    @Inject
    public UserRepositoryImpl(RitmoFitApiService api) {
        this.api = api;
    }

    @Override
    public LiveData<ApiResult<UserDTO>> getUserById(Long id) {
        MutableLiveData<ApiResult<UserDTO>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        api.obtenerUsuarioPorId(id).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al obtener usuario"));
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                result.setValue(new ApiResult.Error<>("Error de conexión"));
            }
        });
        
        return result;
    }

    @Override
    public LiveData<ApiResult<String>> updateUser(Long id, UserDTO user) {
        MutableLiveData<ApiResult<String>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());

        api.actualizarUsuario(id, user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    result.setValue(new ApiResult.Success<>("Perfil actualizado correctamente"));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al actualizar"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage()));
            }
        });
        
        return result;
    }
}