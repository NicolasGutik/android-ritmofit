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

    @Inject
    public PerfilViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    // ✅ Solo devuelve el LiveData del repo. Nada de binding ni prefs acá.
    public LiveData<ApiResult<UserResponse>> perfilLive(Long id) {
        return authRepository.obtenerUsuario(id);
    }

    // (opcional) actualizar perfil
    public LiveData<ApiResult<String>> actualizarPerfil(Long id, UserDTO dto) {
        return authRepository.actualizarUsuario(id, dto);
    }
}

