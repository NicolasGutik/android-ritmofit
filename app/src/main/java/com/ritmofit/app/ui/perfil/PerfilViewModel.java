package com.ritmofit.app.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.repository.AuthRepository;
import com.ritmofit.app.data.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PerfilViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<Long> userIdLive = new MutableLiveData<>();
    public LiveData<ApiResult<UserDTO>> user;

    @Inject
    public PerfilViewModel(UserRepository userRepository, AuthRepository authRepository) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
        this.user = Transformations.switchMap(userIdLive, userRepository::getUserById);
        // Prefill ID if we have a saved user
        UserDTO saved = authRepository.obtenerUsuarioGuardado();
        if (saved != null && saved.getId() != null) {
            userIdLive.setValue(saved.getId());
        }
    }

    public void reload() {
        UserDTO saved = authRepository.obtenerUsuarioGuardado();
        if (saved != null && saved.getId() != null) {
            userIdLive.setValue(saved.getId());
        }
    }

    public LiveData<ApiResult<String>> update(UserDTO user) {
        Long id = user.getId();
        if (id == null) {
            UserDTO saved = authRepository.obtenerUsuarioGuardado();
            if (saved != null) id = saved.getId();
        }
        return userRepository.updateUser(id, user);
    }
}