package com.ritmofit.app.ui.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PerfilFragment extends Fragment {

    private PerfilViewModel viewModel;
    private EditText etEmail, etNombre, etApellido, etTelefono, etFoto;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);
        
        etEmail = view.findViewById(R.id.et_email);
        etNombre = view.findViewById(R.id.et_firstname);
        etApellido = view.findViewById(R.id.et_lastname);
        etTelefono = view.findViewById(R.id.et_telefono);
        etFoto = view.findViewById(R.id.et_foto);
        
        Button btnGuardar = view.findViewById(R.id.btn_guardar);
        btnGuardar.setOnClickListener(v -> guardarCambios());
        
        // Observar datos del usuario
        viewModel.getUser().observe(getViewLifecycleOwner(), this::mostrarUsuario);
        
        // Observar resultado de actualización
        viewModel.getUpdateResult().observe(getViewLifecycleOwner(), this::mostrarResultado);
    }

    private void mostrarUsuario(ApiResult<UserDTO> result) {
        if (result instanceof ApiResult.Success) {
            UserDTO user = ((ApiResult.Success<UserDTO>) result).getData();
            etEmail.setText(user.getEmail());
            etNombre.setText(user.getFirstName());
            etApellido.setText(user.getLastName());
            etTelefono.setText(user.getTelefono());
            etFoto.setText(user.getFoto());
        }
    }

    private void mostrarResultado(ApiResult<String> result) {
        if (result instanceof ApiResult.Success) {
            Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
            // Recargar los datos después de actualizar exitosamente
            viewModel.reload();
        } else if (result instanceof ApiResult.Error) {
            Toast.makeText(getContext(), "Error: " + ((ApiResult.Error<String>) result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarCambios() {
        UserDTO userData = new UserDTO();
        userData.setEmail(etEmail.getText().toString());
        userData.setFirstName(etNombre.getText().toString());
        userData.setLastName(etApellido.getText().toString());
        userData.setTelefono(etTelefono.getText().toString());
        userData.setFoto(etFoto.getText().toString());
        
        viewModel.updateUser(userData);
    }
}

