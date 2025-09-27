package com.ritmofit.app.auth.ui;

import android.content.Intent;
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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {
    
    @Inject
    AuthRepository authRepository;
    
    private TextInputLayout tilEmail, tilName, tilLastName, tilPhone;
    private TextInputEditText etEmail, etName, etLastName, etPhone;
    private MaterialButton btnRegister, btnBackToLogin;
    private ProgressBar progressBar;
    private TextView tvError;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        tilEmail = findViewById(R.id.til_email);
        tilName = findViewById(R.id.til_name);
        tilLastName = findViewById(R.id.til_lastname);
        tilPhone = findViewById(R.id.til_phone);
        
        etEmail = findViewById(R.id.et_email);
        etName = findViewById(R.id.et_name);
        etLastName = findViewById(R.id.et_lastname);
        etPhone = findViewById(R.id.et_phone);
        
        btnRegister = findViewById(R.id.btn_register);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);
        
        progressBar = findViewById(R.id.progress_bar);
        tvError = findViewById(R.id.tv_error);
    }
    
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> registerUser());
        btnBackToLogin.setOnClickListener(v -> goBackToLogin());
    }
    
    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        
        if (!validateForm(email, name, lastName, phone)) {
            return;
        }
        
        // Crear UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setNombre(name);
        userDTO.setApellido(lastName);
        userDTO.setTelefono(phone);
        
        showLoading(true);
        hideError();
        
        authRepository.registrarUsuario(userDTO).observe(this, new Observer<ApiResult<String>>() {
            @Override
            public void onChanged(ApiResult<String> result) {
                showLoading(false);
                
                if (result instanceof ApiResult.Success) {
                    Toast.makeText(RegisterActivity.this, "¡Cuenta creada! Revisa tu email", Toast.LENGTH_LONG).show();
                    goBackToLogin();
                } else if (result instanceof ApiResult.Error) {
                    String errorMessage = ((ApiResult.Error<String>) result).getMessage();
                    showError(errorMessage);
                }
            }
        });
    }
    
    private boolean validateForm(String email, String name, String lastName, String phone) {
        boolean isValid = true;
        
        // Validar email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("El email es obligatorio");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email inválido");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }
        
        // Validar nombre
        if (TextUtils.isEmpty(name)) {
            tilName.setError("El nombre es obligatorio");
            isValid = false;
        } else if (name.length() < 2) {
            tilName.setError("El nombre debe tener al menos 2 caracteres");
            isValid = false;
        } else {
            tilName.setError(null);
        }
        
        // Validar apellido
        if (TextUtils.isEmpty(lastName)) {
            tilLastName.setError("El apellido es obligatorio");
            isValid = false;
        } else if (lastName.length() < 2) {
            tilLastName.setError("El apellido debe tener al menos 2 caracteres");
            isValid = false;
        } else {
            tilLastName.setError(null);
        }
        
        // Validar teléfono
        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("El teléfono es obligatorio");
            isValid = false;
        } else if (!phone.startsWith("+") || phone.length() < 10) {
            tilPhone.setError("Formato: +54911234567");
            isValid = false;
        } else {
            tilPhone.setError(null);
        }
        
        return isValid;
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
        etEmail.setEnabled(!show);
        etName.setEnabled(!show);
        etLastName.setEnabled(!show);
        etPhone.setEnabled(!show);
    }
    
    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
    
    private void hideError() {
        tvError.setVisibility(View.GONE);
    }
    
    private void goBackToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}