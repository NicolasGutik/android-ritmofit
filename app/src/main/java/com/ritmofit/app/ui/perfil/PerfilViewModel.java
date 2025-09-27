package com.ritmofit.app.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PerfilViewModel extends ViewModel {

    private final UserRepository repo;

    @Inject
    public PerfilViewModel(UserRepository repo) {
        this.repo = repo;
    }

    public LiveData<ApiResult<UserDTO>> getUser(Long id) {
        return repo.obtenerUserById(id);
    }

    public LiveData<ApiResult<String>> updateUser(Long id, UserDTO dto) {
        return repo.actualizarUsuario(id, dto);
    }
}
