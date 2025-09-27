package com.ritmofit.app.ui.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.ritmofit.app.R;
import com.ritmofit.app.auth.ui.LoginActivity;
import com.ritmofit.app.session.SessionManager;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";

    @Inject SessionManager sessionManager;     // para leer userId / limpiar rápido si querés
    @Inject AuthRepository authRepository;     // para cerrar sesión (borra token/usuario)

    private PerfilViewModel vm;

    // UI
    private TextInputEditText etNombre, etApellido, etEmail, etTelefono;
    private Button btnGuardar, btnLogout;
    private ProgressBar progress;

    private Long userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
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
        btnLogout  = v.findViewById(R.id.btn_logout);   // asegúrate de tener este id en el XML
        progress   = v.findViewById(R.id.progress);

        // 1) Obtener el ID de usuario persistido
        userId = obtenerUserId();
        Log.d(TAG, "userId=" + userId);

        if (userId == null || userId <= 0L) {
            Toast.makeText(requireContext(), "No se encontró el usuario.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2) Cargar datos del usuario
        mostrarCarga(true);
        vm.getUser(userId).observe(getViewLifecycleOwner(), result -> {
            mostrarCarga(false);
            if (result instanceof ApiResult.Success) {
                UserDTO user = ((ApiResult.Success<UserDTO>) result).getData();
                poblarFormulario(user);
            } else if (result instanceof ApiResult.Error) {
                Toast.makeText(requireContext(),
                        ((ApiResult.Error<?>) result).getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        // 3) Guardar cambios
        btnGuardar.setOnClickListener(view -> {
            UserDTO dto = leerFormulario();
            if (!validar(dto)) return;

            mostrarCarga(true);
            vm.updateUser(userId, dto).observe(getViewLifecycleOwner(), result -> {
                mostrarCarga(false);
                if (result instanceof ApiResult.Success) {
                    String msg = ((ApiResult.Success<String>) result).getData();
                    Toast.makeText(requireContext(),
                            TextUtils.isEmpty(msg) ? "Actualizado" : msg,
                            Toast.LENGTH_SHORT).show();
                } else if (result instanceof ApiResult.Error) {
                    Toast.makeText(requireContext(),
                            ((ApiResult.Error<?>) result).getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        });

        // 4) Cerrar sesión
        btnLogout.setOnClickListener(v1 -> confirmarLogout());
    }

    private Long obtenerUserId() {
        // Leemos el id persistido al momento del login (guardado en prefs/SessionManager)
        return sessionManager.getUserId();
    }

    private void poblarFormulario(UserDTO u) {
        if (u == null) return;
        etNombre.setText(u.getFirstName());
        etApellido.setText(u.getLastName());
        etEmail.setText(u.getEmail());
        etTelefono.setText(u.getTelefono());
    }

    private UserDTO leerFormulario() {
        UserDTO dto = new UserDTO();
        dto.setId(userId);
        dto.setFirstName(etNombre.getText() == null ? "" : etNombre.getText().toString().trim());
        dto.setLastName(etApellido.getText() == null ? "" : etApellido.getText().toString().trim());
        dto.setEmail(etEmail.getText() == null ? "" : etEmail.getText().toString().trim());
        dto.setTelefono(etTelefono.getText() == null ? "" : etTelefono.getText().toString().trim());
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
        if (btnGuardar != null) btnGuardar.setEnabled(!show);
        if (btnLogout  != null) btnLogout.setEnabled(!show);
    }

    private void confirmarLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que querés salir de la cuenta?")
                .setPositiveButton("Sí", (d, w) -> logout())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void logout() {
        try {
            // Limpia token y datos guardados
            authRepository.cerrarSesion();
            // Si además querés asegurarte:
            // sessionManager.clear();
        } catch (Exception ignored) {}

        // Navegar a Login limpiando el back stack
        Intent i = new Intent(requireContext(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        requireActivity().finish();
    }
}

