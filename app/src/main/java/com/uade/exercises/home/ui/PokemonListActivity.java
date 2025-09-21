package com.uade.exercises.home.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.uade.exercises.R;
import com.uade.exercises.auth.repository.TokenRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokemonListActivity extends AppCompatActivity {

    private NavController navController;
    
    @Inject
    TokenRepository tokenRepository;
    
    private static final String TAG = "PokemonListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_pokemon_list);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
                
        if (tokenRepository.hasToken()) {
            String token = tokenRepository.getToken();
            Log.d(TAG, "Token recuperado: " + token);
        } else {
            Log.d(TAG, "No hay token almacenado");
        }
    }
}
