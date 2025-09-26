package com.ritmofit.app.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ritmofit.app.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PerfilFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        
        // Configurar botón para ver asistencias
        view.findViewById(R.id.btnVerAsistencias).setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.asistenciasFragment);
        });
        
        // TODO: Implementar funcionalidad completa del perfil
        Toast.makeText(getContext(), "Perfil en desarrollo", Toast.LENGTH_SHORT).show();
        
        return view;
    }
}

