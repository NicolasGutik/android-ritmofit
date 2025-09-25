package com.ritmofit.app.ui.perfil;

import android.content.SharedPreferences;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



import com.ritmofit.app.R;
import com.ritmofit.app.databinding.FragmentPerfilBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel perfilViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);

        String nombre = prefs.getString("nombre", "");
        String apellido = prefs.getString("apellido", "");
        String email = prefs.getString("email", "");
        String telefono = prefs.getString("telefono", "");

        // Mostrar en la UI
        binding.ivProfileFistName.setText(nombre);
        binding.ivProfileLastName.setText(apellido);
        binding.ivProfileEmail.setText(email);
        binding.ivProfilePhone.setText(telefono);

        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        perfilViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        // 1. Recuperar id user
        SharedPreferences prefs = requireActivity().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        Long userId = Long.valueOf(prefs.getString("user_id", null));

        // 2. Llamar al perfil si hay id
        if (userId != null) {
            perfilViewModel.cargarPerfil(userId);
        } else {
            Toast.makeText(getContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show();
        }

        // 3. Observar datos del usuario
        perfilViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            binding.ivProfileFistName.setText(user.getFirstName());
            binding.ivProfileLastName.setText(user.getLastName());
            binding.ivProfileEmail.setText(user.getEmail());
            binding.ivProfilePhone.setText(user.getTelefono());
        });

        // 4. Observar errores
        perfilViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    
}

