package com.ritmofit.app.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ritmofit.app.MainActivity;
import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.repository.AuthRepository;

import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    
    private static final String TAG = "LoginActivity";
    
    @Inject
    AuthRepository authRepository;
    
    private TextInputLayout tilEmail, tilOtp;
    private TextInputEditText etEmail, etOtp;
    private MaterialButton btnSendOtp, btnLogin, btnRegister;
    private ProgressBar progressBar;
    private TextView tvError;
    
    private boolean otpSent = false;
    private String currentEmail;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        tilEmail = findViewById(R.id.til_email);
        tilOtp = findViewById(R.id.til_otp);
        etEmail = findViewById(R.id.et_email);
        etOtp = findViewById(R.id.et_otp);
        btnSendOtp = findViewById(R.id.btn_send_otp);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
        tvError = findViewById(R.id.tv_error);
    }
    
    private void setupClickListeners() {
        btnSendOtp.setOnClickListener(v -> {
            if (!otpSent) {
                sendOtp();
            } else {
                resetForm();
            }
        });
        
        btnLogin.setOnClickListener(v -> validateOtp());
        btnRegister.setOnClickListener(v -> goToRegister());
    }
    
    private void sendOtp() {
        String email = etEmail.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Ingresa tu email");
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email inválido");
            return;
        }
        
        tilEmail.setError(null);
        currentEmail = email;
        
        showLoading(true);
        hideError();
        
        authRepository.enviarOTP(email).observe(this, new Observer<ApiResult<Map<String, Object>>>() {
            @Override
            public void onChanged(ApiResult<Map<String, Object>> result) {
                showLoading(false);
                
                if (result instanceof ApiResult.Success) {
                    Map<String, Object> responseData = ((ApiResult.Success<Map<String, Object>>) result).getData();
                    String message = (String) responseData.get("message");
                    
                    // Usuario existe: mostrar campos OTP
                    otpSent = true;
                    showOtpFields();
                    btnSendOtp.setText("Cambiar email");
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                } else if (result instanceof ApiResult.Error) {
                    showError("Error: " + ((ApiResult.Error<Map<String, Object>>) result).getMessage());
                }
            }
        });
    }

    private void validateOtp() {
        String otp = etOtp.getText().toString().trim();

        if (TextUtils.isEmpty(otp)) {
            tilOtp.setError("Ingresa el código OTP");
            return;
        }
        if (otp.length() != 6) {
            tilOtp.setError("El código debe tener 6 dígitos");
            return;
        }

        tilOtp.setError(null);
        showLoading(true);
        hideError();

        authRepository.validarOTP(currentEmail, otp).observe(this, new Observer<ApiResult<Map<String, Object>>>() {
            @Override
            public void onChanged(ApiResult<Map<String, Object>> result) {
                showLoading(false);

                if (result instanceof ApiResult.Success) {
                    Map<String, Object> responseData = ((ApiResult.Success<Map<String, Object>>) result).getData();

                    // --- 1) TOKEN ---
                    String token = null;
                    if (responseData != null) {
                        Object t1 = responseData.get("token");
                        Object t2 = responseData.get("jwt");
                        Object t3 = responseData.get("accessToken");
                        token = (t1 != null ? String.valueOf(t1)
                                : (t2 != null ? String.valueOf(t2)
                                : (t3 != null ? String.valueOf(t3) : null)));
                    }

                    if (!TextUtils.isEmpty(token)) {
                        // Guardar SIEMPRE sin "Bearer "
                        if (token.startsWith("Bearer ")) {
                            token = token.substring("Bearer ".length());
                        }
                        authRepository.guardarToken(token);
                        Log.d(TAG, "Token guardado (len=" + token.length() + ")");
                    } else {
                        Log.w(TAG, "No vino token en la respuesta de validarOTP");
                    }

                    // --- 2) (Opcional) USUARIO ---
                    if (responseData != null && responseData.containsKey("user")) {
                        Object userObj = responseData.get("user");
                        if (userObj instanceof Map) {
                            UserDTO user = mapToUserDto((Map<String, Object>) userObj);
                            if (user != null) {
                                authRepository.guardarUsuario(user);
                                Log.d(TAG, "Usuario guardado: id=" + user.getId());
                            }
                        } else {
                            Log.w(TAG, "Campo 'user' no es un Map. Tipo=" + (userObj != null ? userObj.getClass() : "null"));
                        }
                    }

                    Toast.makeText(LoginActivity.this, "¡Bienvenido a RitmoFit!", Toast.LENGTH_SHORT).show();
                    navigateToMain();

                } else if (result instanceof ApiResult.Error) {
                    showError("Código OTP inválido. Intenta nuevamente.");
                }
            }
        });
    }
    
    private void showOtpFields() {
        tilOtp.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
    }
    
    private void goToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    
    private void resetForm() {
        otpSent = false;
        currentEmail = null;
        tilOtp.setVisibility(View.GONE);
        btnLogin.setVisibility(View.GONE);
        btnSendOtp.setText("Enviar código OTP");
        etEmail.setText("");
        etOtp.setText("");
        hideError();
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSendOtp.setEnabled(!show);
        btnLogin.setEnabled(!show);
        etEmail.setEnabled(!show);
        etOtp.setEnabled(!show);
    }
    
    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
    
    private void hideError() {
        tvError.setVisibility(View.GONE);
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private UserDTO mapToUserDto(Map<String, Object> map) {
        try {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            String json = gson.toJson(map);                   // Map -> JSON
            return gson.fromJson(json, UserDTO.class);        // JSON -> UserDTO
        } catch (Exception e) {
            Log.e(TAG, "Error mapeando 'user' a UserDTO", e);
            return null;
        }
    }
}


