package com.ritmofit.app.data.repository;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.ritmofit.app.data.api.RitmoFitApiService;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.ResponseBody;
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

                    // 1) Guardar token (si vino)
                    String token = null;
                    if (responseData.containsKey("token")) {
                        Object t = responseData.get("token");
                        if (t instanceof String) {
                            token = (String) t;
                            guardarToken(token);
                        }
                    }

                    // 2) Guardar usuario si vino en la respuesta
                    boolean userGuardado = false;
                    if (responseData.containsKey("user")) {
                        Object userObj = responseData.get("user");
                        if (userObj instanceof Map) {
                            UserDTO user = gson.fromJson(gson.toJson(userObj), UserDTO.class);
                            guardarUsuario(user);
                            userGuardado = true;
                        }
                    }

                    // 3) Si NO vino 'user', lo traemos usando el id del JWT y lo cacheamos
                    if (!userGuardado && token != null) {
                        Long idFromJwt = com.ritmofit.app.util.JwtUtils.getUserIdFromToken(token);
                        if (idFromJwt != null) {
                            apiService.obtenerUsuarioPorId(idFromJwt).enqueue(new Callback<UserDTO>() {
                                @Override
                                public void onResponse(Call<UserDTO> call, Response<UserDTO> resp) {
                                    if (resp.isSuccessful() && resp.body() != null) {
                                        guardarUsuario(resp.body());
                                        Log.d(TAG, "✅ Usuario cacheado post-login: " + resp.body());
                                    } else {
                                        Log.w(TAG, "⚠️ No se pudo cachear usuario post-login. code=" + resp.code());
                                    }
                                }
                                @Override
                                public void onFailure(Call<UserDTO> call, Throwable t) {
                                    Log.e(TAG, "❌ Fallo al traer usuario post-login", t);
                                }
                            });
                        } else {
                            Log.w(TAG, "⚠️ No se pudo extraer id del JWT para traer usuario");
                        }
                    }

                    result.setValue(new ApiResult.Success<>(responseData));
                } else {
                    String errorMessage = "Error al validar OTP";
                    if (response.errorBody() != null) {
                        try { errorMessage = response.errorBody().string(); } catch (Exception e) { Log.e(TAG, "Error reading error body", e); }
                    }
                    result.setValue(new ApiResult.Error<>(errorMessage));
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Error al validar OTP", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
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
        
        apiService.actualizarUsuario(id, userDTO).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Actualizar usuario guardado
                    guardarUsuario(userDTO);
                    result.setValue(new ApiResult.Success<>("Usuario actualizado correctamente"));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al actualizar usuario: " + response.code()));
                }
            }
            
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error al actualizar usuario", t);
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        
        return result;
    }

    @Override
    public LiveData<ApiResult<UserDTO>> obtenerUsuarioActual() {
        MutableLiveData<ApiResult<UserDTO>> result = new MutableLiveData<>();

        // 1) Intentar cache
        UserDTO cache = obtenerUsuarioGuardado();
        if (cache != null) {
            result.setValue(new ApiResult.Success<>(cache));
            return result;
        }

        // 2) Fallback: sacar id del JWT (claim "sub")
        String token = obtenerToken();
        Long idFromJwt = com.ritmofit.app.util.JwtUtils.getUserIdFromToken(token);
        if (idFromJwt == null) {
            result.setValue(new ApiResult.Error<>("No hay usuario en caché ni id en token"));
            return result;
        }

        // 3) Traer del backend y cachear
        result.setValue(new ApiResult.Loading<>());
        apiService.obtenerUsuarioPorId(idFromJwt).enqueue(new retrofit2.Callback<UserDTO>() {
            @Override public void onResponse(retrofit2.Call<UserDTO> call, retrofit2.Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    guardarUsuario(response.body());
                    result.setValue(new ApiResult.Success<>(response.body()));
                } else {
                    result.setValue(new ApiResult.Error<>("Error al obtener usuario: " + response.code()));
                }
            }
            @Override public void onFailure(retrofit2.Call<UserDTO> call, Throwable t) {
                result.setValue(new ApiResult.Error<>("Error de conexión: " + t.getMessage(), t));
            }
        });
        return result;
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


