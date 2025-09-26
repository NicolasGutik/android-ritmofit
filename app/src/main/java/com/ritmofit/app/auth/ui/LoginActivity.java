package com.ritmofit.app.auth.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

                    // El backend devuelve "token", "userId" y además los datos del usuario
                    String token = (String) responseData.get("token");
                    String userId = String.valueOf(responseData.get("userId"));

                    // Nuevos campos que ya devuelve tu API
                    String firstName = (String) responseData.get("firstName");
                    String lastName  = (String) responseData.get("lastName");
                    String emailResp = (String) responseData.get("email");
                    String telefono  = (String) responseData.get("telefono");

                    SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
                    prefs.edit()
                            .putString("jwt_token", token)
                            .putString("user_id", userId)
                            .putString("nombre", firstName != null ? firstName : "")
                            .putString("apellido", lastName != null ? lastName : "")
                            .putString("email", emailResp != null ? emailResp : currentEmail) // fallback
                            .putString("telefono", telefono != null ? telefono : "")
                            .apply();

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
}


