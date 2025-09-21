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
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    
    @Inject
    AuthRepository authRepository;
    
    private TextInputLayout tilEmail, tilFirstName, tilLastName;
    private TextInputEditText etEmail, etFirstName, etLastName;
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
        tilFirstName = findViewById(R.id.til_first_name);
        tilLastName = findViewById(R.id.til_last_name);
        etEmail = findViewById(R.id.et_email);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
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
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        
        // Validaciones
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email es requerido");
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email inválido");
            return;
        }
        
        if (TextUtils.isEmpty(firstName)) {
            tilFirstName.setError("Nombre es requerido");
            return;
        }
        
        if (TextUtils.isEmpty(lastName)) {
            tilLastName.setError("Apellido es requerido");
            return;
        }
        
        // Limpiar errores
        tilEmail.setError(null);
        tilFirstName.setError(null);
        tilLastName.setError(null);
        
        showLoading(true);
        hideError();
        
        // Crear UserDTO para registro
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        
        Log.d(TAG, "🔍 Registrando usuario: " + userDTO.toString());
        
        authRepository.registrarUsuario(userDTO).observe(this, new Observer<ApiResult<Map<String, Object>>>() {
            @Override
            public void onChanged(ApiResult<Map<String, Object>> result) {
                showLoading(false);
                
                if (result instanceof ApiResult.Success) {
                    Map<String, Object> responseData = ((ApiResult.Success<Map<String, Object>>) result).getData();
                    String message = (String) responseData.get("message");
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                    // Volver al login
                    goBackToLogin();
                } else if (result instanceof ApiResult.Error) {
                    showError("Error al registrarse: " + ((ApiResult.Error<Map<String, Object>>) result).getMessage());
                }
            }
        });
    }
    
    private void goBackToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
        btnBackToLogin.setEnabled(!show);
        etEmail.setEnabled(!show);
        etFirstName.setEnabled(!show);
        etLastName.setEnabled(!show);
    }
    
    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
    
    private void hideError() {
        tvError.setVisibility(View.GONE);
    }
}
