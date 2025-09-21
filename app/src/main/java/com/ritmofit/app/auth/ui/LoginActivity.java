package com.ritmofit.app.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ritmofit.app.MainActivity;
import com.ritmofit.app.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Por ahora, navegamos directamente a MainActivity
        // TODO: Implementar flujo de autenticación completo
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
