package com.ritmofit.app.data.repository;

import androidx.lifecycle.LiveData;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.dto.UserResponse;

import java.util.Map;

public interface AuthRepository {
    
    /**
     * Envía OTP al email del usuario
     */
    LiveData<ApiResult<Map<String, Object>>> enviarOTP(String email);
    
    /**
     * Valida OTP y obtiene JWT
     */
    LiveData<ApiResult<Map<String, Object>>> validarOTP(String email, String otp);
    
        /**
         * Registra un nuevo usuario
         */
        LiveData<ApiResult<Map<String, Object>>> registrarUsuario(UserDTO userDTO);
    
    /**
     * Actualiza datos del usuario
     */
    LiveData<ApiResult<String>> actualizarUsuario(Long id, UserDTO userDTO);
    
    /**
     * Obtiene el usuario actual
     */
    LiveData<ApiResult<UserResponse>> obtenerUsuario(Long id);
    
    /**
     * Verifica si el usuario está autenticado
     */
    boolean isUsuarioAutenticado();
    
    /**
     * Cierra sesión del usuario
     */
    void cerrarSesion();
    
    /**
     * Guarda el token JWT de forma segura
     */
    void guardarToken(String token);
    
    /**
     * Obtiene el token JWT guardado
     */
    String obtenerToken();
    
    /**
     * Guarda los datos del usuario de forma segura
     */
    void guardarUsuario(UserDTO usuario);
    
    /**
     * Obtiene los datos del usuario guardados
     */
    UserDTO obtenerUsuarioGuardado();
}


