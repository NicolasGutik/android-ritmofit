package com.ritmofit.app.data.repository;

import androidx.lifecycle.LiveData;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;

public interface UserRepository {
    LiveData<ApiResult<UserDTO>> obtenerUserById(Long id);
    LiveData<ApiResult<String>> actualizarUsuario(Long id, UserDTO userDTO);
}
