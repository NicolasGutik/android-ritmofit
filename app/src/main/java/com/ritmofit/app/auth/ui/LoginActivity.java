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
    private MaterialButton btnSendOtp, btnLogin;
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
        
        authRepository.enviarOTP(email).observe(this, new Observer<ApiResult<String>>() {
            @Override
            public void onChanged(ApiResult<String> result) {
                showLoading(false);
                
                if (result instanceof ApiResult.Success) {
                    otpSent = true;
                    showOtpFields();
                    btnSendOtp.setText("Cambiar email");
                    Toast.makeText(LoginActivity.this, "Código OTP enviado a tu email", Toast.LENGTH_LONG).show();
                } else if (result instanceof ApiResult.Error) {
                    showError("Error al enviar OTP: " + ((ApiResult.Error<String>) result).getMessage());
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
                    // Login exitoso, navegar a MainActivity
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


