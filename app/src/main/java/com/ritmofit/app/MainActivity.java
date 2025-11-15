package com.ritmofit.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.ritmofit.app.auth.ui.LoginActivity;
import com.ritmofit.app.data.repository.AuthRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    
    @Inject
    AuthRepository authRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Verificar si el usuario está autenticado
        if (!authRepository.isUsuarioAutenticado()) {
            // Redirigir al login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        // Configurar Navigation Component
        setupNavigation();
    }
    
    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_view);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            if (bottomNavigationView == null) {
                return;
            }
            NavigationUI.setupWithNavController(bottomNavigationView, navController);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);

                if (!handled && item.getItemId() == R.id.homeFragment) {
                    handled = navController.popBackStack(R.id.homeFragment, false);
                }

                return handled;
            });

            bottomNavigationView.setOnItemReselectedListener(item -> {
                if (!navController.popBackStack(item.getItemId(), false)) {
                    NavigationUI.onNavDestinationSelected(item, navController);
                }
            });
        }
    }
}
