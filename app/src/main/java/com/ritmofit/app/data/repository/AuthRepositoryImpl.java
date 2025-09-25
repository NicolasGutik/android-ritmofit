package com.ritmofit.app.data.repository;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.ritmofit.app.data.api.RitmoFitApiService;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.dto.UserResponse;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthRepositoryImpl implements AuthRepository {
    
    private static final String TAG = "AuthRepository";
    private static final String KEY_JWT_TOKEN = "jwt_token";
    private static final String KEY_USER_DATA = "user_data";
    
    private final RitmoFitApiService apiService;
    private final SharedPreferences encryptedPrefs;
    private final Gson gson;
    
    @Inject
    public AuthRepositoryImpl(RitmoFitApiService apiService, SharedPreferences encryptedPrefs, Gson gson) {
        this.apiService = apiService;
        this.encryptedPrefs = encryptedPrefs;
        this.gson = gson;
    }
    
    @Override
    public LiveData<ApiResult<Map<String, Object>>> enviarOTP(String email) {
        MutableLiveData<ApiResult<Map<String, Object>>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        
        apiService.enviarOTP(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseData = response.body();
                    Log.d(TAG, "🔍 Respuesta del servidor: " + responseData);
                    result.setValue(new ApiResult.Success<>(responseData));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al procesar solicitud: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Error al enviar OTP", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }
    
    @Override
    public LiveData<ApiResult<Map<String, Object>>> validarOTP(String email, String otp) {
        MutableLiveData<ApiResult<Map<String, Object>>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("code", otp);
        
        apiService.validarOTP(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseData = response.body();
                    
                    // Guardar token y datos del usuario
                    if (responseData.containsKey("token")) {
                        String token = (String) responseData.get("token");
                        guardarToken(token);
                    }
                    
                    if (responseData.containsKey("user")) {
                        Object userObj = responseData.get("user");
                        if (userObj instanceof Map) {
                            UserDTO user = gson.fromJson(gson.toJson(userObj), UserDTO.class);
                            guardarUsuario(user);
                        }
                    }
                    
                    result.setValue(new ApiResult.Success<>(responseData));
                } else {
                    String errorMessage = "Error al validar OTP";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    result.setValue(new ApiResult.Error<>(errorMessage));
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Error al validar OTP", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(),  t));
            }
        });
        
        return result;
    }
    
    @Override
    public LiveData<ApiResult<Map<String, Object>>> registrarUsuario(UserDTO userDTO) {
        MutableLiveData<ApiResult<Map<String, Object>>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());

        apiService.registrarUsuario(userDTO).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseData = response.body();
                    Log.d(TAG, "🔍 Respuesta del registro: " + responseData);
                    result.setValue(new ApiResult.Success<>(responseData));
                } else {
                    String errorMessage = "Error al registrar usuario: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    result.setValue(new ApiResult.Error<>(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Error al registrar usuario", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });

        return result;
    }
    
    @Override
    public LiveData<ApiResult<String>> actualizarUsuario(Long id, UserDTO userDTO) {
        MutableLiveData<ApiResult<String>> result = new MutableLiveData<>();
        result.setValue(new ApiResult.Loading<>());
        
        apiService.actualizarUsuario(id, userDTO).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // Actualizar usuario guardado
                    guardarUsuario(userDTO);
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al actualizar usuario: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Error al actualizar usuario", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }

    @Override
    public LiveData<ApiResult<UserResponse>> obtenerUsuario(Long id) {
        MutableLiveData<ApiResult<UserResponse>> liveData = new MutableLiveData<>();

        String token = obtenerToken(); // desde EncryptedPrefs
        if (token == null || token.isEmpty()) {
            liveData.postValue(new ApiResult.Error<>("No hay sesión activa"));
            return liveData;
        }

        Log.d("AuthRepository", "➡️ GET /auth/" + id);
        Log.d("AuthRepository", "➡️ Auth header: Bearer " +
                (token.length() > 10 ? token.substring(0,10) + "..." : token));

        apiService.obtenerUsuario(id, "Bearer " + token)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        Log.d("AuthRepository", "⬅️ response code: " + response.code());
                        if (!response.isSuccessful()) {
                            try {
                                String err = response.errorBody() != null ? response.errorBody().string() : "(sin body)";
                                Log.e("AuthRepository", "❌ errorBody: " + err);
                            } catch (Exception ignore) {}
                            liveData.postValue(new ApiResult.Error<>("Error al obtener usuario: " + response.code()));
                            return;
                        }
                        UserResponse body = response.body();
                        Log.d("AuthRepository", "✅ body: " + (body != null ? body.toString() : "null"));
                        if (body != null) {
                            liveData.postValue(new ApiResult.Success<>(body));
                        } else {
                            liveData.postValue(new ApiResult.Error<>("Respuesta vacía"));
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Log.e("AuthRepository", "❌ onFailure: " + t.getMessage(), t);
                        liveData.postValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage()));
                    }
                });

        return liveData;
    }


    @Override
    public boolean isUsuarioAutenticado() {
        String token = obtenerToken();
        return token != null && !token.isEmpty();
    }
    
    @Override
    public void cerrarSesion() {
        encryptedPrefs.edit()
                .remove(KEY_JWT_TOKEN)
                .remove(KEY_USER_DATA)
                .apply();
    }
    
    @Override
    public void guardarToken(String token) {
        encryptedPrefs.edit()
                .putString(KEY_JWT_TOKEN, token)
                .apply();
        
        // Log para testing en Postman
        Log.i(TAG, "🔑 TOKEN GUARDADO (para Postman): " + token);
        System.out.println("🔑 TOKEN PARA POSTMAN: " + token);
    }
    
    @Override
    public String obtenerToken() {
        return encryptedPrefs.getString(KEY_JWT_TOKEN, null);
    }
    
    @Override
    public void guardarUsuario(UserDTO usuario) {
        String userJson = gson.toJson(usuario);
        encryptedPrefs.edit()
                .putString(KEY_USER_DATA, userJson)
                .apply();
    }

    @Override
    public UserDTO obtenerUsuarioGuardado() {
        String userJson = encryptedPrefs.getString(KEY_USER_DATA, null);
        if (userJson != null) {
            try {
                return gson.fromJson(userJson, UserDTO.class);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing user data", e);
            }
        }
        return null;
    }
}


