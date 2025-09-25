package com.ritmofit.app.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.api.RitmoFitApiService;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.dto.UserResponse;
import com.ritmofit.app.data.repository.AuthRepository;
import com.ritmofit.app.model.User;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class PerfilViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<UserResponse> user = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public PerfilViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    /** Exponé el LiveData que viene del repo y observá en el Fragment con lifecycle */
    public LiveData<ApiResult<UserResponse>> perfilLive(Long id) {
        // Se asume que el repo envía el header Authorization correctamente
        return authRepository.obtenerUsuario(id);
    }

    public void cargarPerfil(Long id, String token) {
        // usar el repositorio y observar el resultado
        authRepository.obtenerUsuario(id).observeForever(result -> {
            if (result instanceof ApiResult.Success) {
                user.setValue(((ApiResult.Success<UserResponse>) result).getData());
            } else if (result instanceof ApiResult.Error) {
                error.setValue(((ApiResult.Error<?>) result).getMessage());
            }
        });
    }

    public LiveData<ApiResult<String>> actualizarPerfil(Long id, UserDTO dto) {
        return authRepository.actualizarUsuario(id, dto);
    }

    public LiveData<UserResponse> getUser() {
        return user;
    }

    public LiveData<String> getError() {
        return error;
    }
}

