package com.ritmofit.app.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.repository.AuthRepository;
import com.ritmofit.app.data.repository.UserRepository;
import com.ritmofit.app.util.JwtUtils;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PerfilViewModel extends ViewModel {
    
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final MutableLiveData<ApiResult<UserDTO>> user = new MutableLiveData<>();
    private final MutableLiveData<ApiResult<String>> updateResult = new MutableLiveData<>();

    @Inject
    public PerfilViewModel(UserRepository userRepository, AuthRepository authRepository) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
        
        // Cargar datos iniciales
        cargarUsuario();
    }

    public LiveData<ApiResult<UserDTO>> getUser() {
        return user;
    }

    public LiveData<ApiResult<String>> getUpdateResult() {
        return updateResult;
    }

    private void cargarUsuario() {
        String token = authRepository.obtenerToken();
        Long userId = JwtUtils.getUserIdFromToken(token);
        
        if (userId != null) {
            userRepository.getUserById(userId).observeForever(result -> {
                user.setValue(result);
            });
        }
    }

    public void updateUser(UserDTO userData) {
        String token = authRepository.obtenerToken();
        Long userId = JwtUtils.getUserIdFromToken(token);
        
        if (userId != null) {
            userRepository.updateUser(userId, userData).observeForever(result -> {
                updateResult.setValue(result);
                
                // Si la actualización fue exitosa, recargar los datos del usuario
                if (result instanceof ApiResult.Success) {
                    cargarUsuario();
                }
            });
        }
    }

    public void reload() {
        cargarUsuario();
    }
}