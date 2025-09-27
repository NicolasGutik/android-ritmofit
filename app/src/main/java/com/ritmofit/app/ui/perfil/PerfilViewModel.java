package com.ritmofit.app.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
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
    private final MutableLiveData<Long> userIdLive = new MutableLiveData<>();
    public final LiveData<ApiResult<UserDTO>> user;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    @Inject
    public PerfilViewModel(AuthRepository authRepository, UserRepository userRepository) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;

        user = Transformations.switchMap(userIdLive, id -> userRepository.getUserById(id));

        UserDTO saved = authRepository.obtenerUsuarioGuardado();
        if (saved != null && saved.getId() != null) {
            userIdLive.setValue(saved.getId());
        } else {
            String token = authRepository.obtenerToken();
            Long idFromJwt = JwtUtils.getUserIdFromToken(token);
            if (idFromJwt != null) {
                userIdLive.setValue(idFromJwt);
            }
        }
    }

    public void reload() {
        Long id = userIdLive.getValue();
        if (id != null) userIdLive.setValue(id);
    }

    public LiveData<ApiResult<String>> update(UserDTO user) {
        Long id = user.getId();
        if (id == null) {
            UserDTO saved = authRepository.obtenerUsuarioGuardado();
            if (saved != null) id = saved.getId();
            if (id == null) id = JwtUtils.getUserIdFromToken(authRepository.obtenerToken());
        }
        return userRepository.updateUser(id, user);
    }
}