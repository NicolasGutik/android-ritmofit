package com.ritmofit.app.ui.perfil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.ritmofit.app.R;
import com.ritmofit.app.session.SessionManager; // ✅ import correcto
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PerfilFragment extends Fragment {

    @Inject SessionManager sessionManager; // ✅ inyectar la dependencia, no el módulo

    private PerfilViewModel vm;

    private TextInputEditText etNombre, etApellido, etEmail, etTelefono;
    private Button btnGuardar;
    private ProgressBar progress;

    private Long userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        vm = new ViewModelProvider(this).get(PerfilViewModel.class);

        etNombre   = v.findViewById(R.id.et_first_name);
        etApellido = v.findViewById(R.id.et_last_name);
        etEmail    = v.findViewById(R.id.et_email);
        etTelefono = v.findViewById(R.id.et_phone);
        btnGuardar = v.findViewById(R.id.btn_guardar);
        progress   = v.findViewById(R.id.progress);

        // Leer ID del usuario desde SessionManager
        userId = obtenerUserId();
        if (userId == null || userId <= 0L) {
            Toast.makeText(getContext(), "No se encontró el usuario.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cargar datos
        mostrarCarga(true);
        vm.getUser(userId).observe(getViewLifecycleOwner(), result -> {
            mostrarCarga(false);
            if (result instanceof ApiResult.Success) {
                UserDTO user = ((ApiResult.Success<UserDTO>) result).getData();
                poblarFormulario(user);
            } else if (result instanceof ApiResult.Error) {
                Toast.makeText(getContext(),
                        ((ApiResult.Error<?>) result).getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        // Guardar cambios
        btnGuardar.setOnClickListener(view -> {
            UserDTO dto = leerFormulario();
            if (!validar(dto)) return;

            mostrarCarga(true);
            vm.updateUser(userId, dto).observe(getViewLifecycleOwner(), result -> {
                mostrarCarga(false);
                if (result instanceof ApiResult.Success) {
                    String msg = ((ApiResult.Success<String>) result).getData();
                    Toast.makeText(getContext(),
                            TextUtils.isEmpty(msg) ? "Actualizado" : msg,
                            Toast.LENGTH_SHORT).show();
                } else if (result instanceof ApiResult.Error) {
                    Toast.makeText(getContext(),
                            ((ApiResult.Error<?>) result).getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private Long obtenerUserId() {
        // Lee el userId que guardaste al loguear
        return sessionManager.getUserId(); // devuelve null si no existe
    }

    private void poblarFormulario(UserDTO u) {
        etNombre.setText(u.getFirstName());
        etApellido.setText(u.getLastName());
        etEmail.setText(u.getEmail());
        etTelefono.setText(u.getTelefono());
    }

    private UserDTO leerFormulario() {
        UserDTO dto = new UserDTO();
        dto.setId(userId);
        dto.setFirstName(etNombre.getText().toString().trim());
        dto.setLastName(etApellido.getText().toString().trim());
        dto.setEmail(etEmail.getText().toString().trim());
        dto.setTelefono(etTelefono.getText().toString().trim());
        return dto;
    }

    private boolean validar(UserDTO dto) {
        if (TextUtils.isEmpty(dto.getFirstName())) { etNombre.setError("Requerido"); return false; }
        if (TextUtils.isEmpty(dto.getLastName()))   { etApellido.setError("Requerido"); return false; }
        if (TextUtils.isEmpty(dto.getEmail()))      { etEmail.setError("Requerido"); return false; }
        return true;
    }

    private void mostrarCarga(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        btnGuardar.setEnabled(!show);
    }
}

