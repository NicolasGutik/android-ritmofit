package com.ritmofit.app.ui.perfil;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.material.textfield.TextInputEditText;
import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.dto.UserResponse;
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        perfilViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        // 1) Prefs únicas
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        Log.d("Perfil", "prefs: " +
                prefs.getString("email", "∅") + " | " +
                prefs.getString("nombre", "∅") + " | " +
                prefs.getString("apellido", "∅") + " | " +
                prefs.getString("telefono", "∅"));
        // 2) Rellenar inputs SOLO si hay valores guardados
        String email    = prefs.getString("email", "");
        String nombre   = prefs.getString("nombre", "");
        String apellido = prefs.getString("apellido", "");
        String telefono = prefs.getString("telefono", "");

        if (!email.isEmpty())    binding.ivProfileEmail.setText(email);
        if (!nombre.isEmpty())   binding.ivProfileFirstName.setText(nombre);
        if (!apellido.isEmpty()) binding.ivProfileLastName.setText(apellido);
        if (!telefono.isEmpty()) binding.ivProfilePhone.setText(telefono);

        // 3) Verificar sesión
        String token   = prefs.getString("jwt_token", null);
        String userIdS = prefs.getString("user_id", null);
        if (token == null || userIdS == null) {
            Toast.makeText(getContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show();
            return;
        }
        Long userId = Long.parseLong(userIdS);

        Log.d("Perfil", "Voy a observar perfilLive para userId=" + userId);
        Toast.makeText(getContext(), "Observando perfil...", Toast.LENGTH_SHORT).show();
        // 4) Refrescar desde backend (un solo observer)
        setLoading(true);
        perfilViewModel.perfilLive(userId).observe(getViewLifecycleOwner(), result -> {
            Log.d("Perfil", "Observer disparado. result=" + result);
            setLoading(false);

            if (result instanceof ApiResult.Success) {
                UserResponse u = ((ApiResult.Success<UserResponse>) result).getData();
                Log.d("Perfil", "API -> "
                        + u.getFirstName() + " | " + u.getLastName() + " | "
                        + u.getEmail() + " | " + u.getTelefono());

                if (u == null) {
                    Toast.makeText(getContext(), "Respuesta vacía", Toast.LENGTH_SHORT).show();
                    return;
                }

                String first = u.getFirstName() != null ? u.getFirstName() : "";
                String last  = u.getLastName()  != null ? u.getLastName()  : "";
                String mail  = u.getEmail()     != null ? u.getEmail()     : "";
                String phone = u.getTelefono()  != null ? u.getTelefono()  : "";

                // Poner el dato dentro de cada input
                if (!mail.isEmpty())   binding.ivProfileEmail.setText(mail);
                if (!first.isEmpty())  binding.ivProfileFirstName.setText(first);
                if (!last.isEmpty())   binding.ivProfileLastName.setText(last);
                if (!phone.isEmpty())  binding.ivProfilePhone.setText(phone);

                // Guardar para próximas aperturas
                prefs.edit()
                        .putString("email",    mail)
                        .putString("nombre",   first)
                        .putString("apellido", last)
                        .putString("telefono", phone)
                        .apply();

            } else if (result instanceof ApiResult.Error) {
                String msg = ((ApiResult.Error<?>) result).getMessage();
                Toast.makeText(getContext(), "Error cargando perfil: " + msg, Toast.LENGTH_LONG).show();
            }
            Log.d("Perfil", "Prefs -> token=" + (token != null) + ", userId=" + userIdS);
        });

        // 5) Guardar cambios (UN solo listener y usando las mismas prefs)
        binding.btnUpdateProfile.setOnClickListener(v -> {
            String emailNew = binding.ivProfileEmail.getText() != null ? binding.ivProfileEmail.getText().toString().trim() : "";
            String firstNew = binding.ivProfileFirstName.getText() != null ? binding.ivProfileFirstName.getText().toString().trim() : "";
            String lastNew  = binding.ivProfileLastName.getText() != null ? binding.ivProfileLastName.getText().toString().trim() : "";
            String phoneNew = binding.ivProfilePhone.getText() != null ? binding.ivProfilePhone.getText().toString().trim() : "";

            UserDTO dto = new UserDTO();
            dto.setEmail(emailNew);
            dto.setFirstName(firstNew);
            dto.setLastName(lastNew);
            dto.setTelefono(phoneNew);

            perfilViewModel.actualizarPerfil(userId, dto).observe(getViewLifecycleOwner(), res -> {
                if (res instanceof ApiResult.Success) {
                    Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    prefs.edit()
                            .putString("email",    emailNew)
                            .putString("nombre",   firstNew)
                            .putString("apellido", lastNew)
                            .putString("telefono", phoneNew)
                            .apply();
                } else if (res instanceof ApiResult.Error) {
                    Toast.makeText(getContext(), "Error: " + ((ApiResult.Error<?>) res).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private boolean isEmpty(TextInputEditText et) {
        CharSequence cs = et.getText();
        return cs == null || cs.toString().trim().isEmpty();
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private void setLoading(boolean show) {
        binding.progressProfile.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnUpdateProfile.setEnabled(!show);
        binding.ivProfileEmail.setEnabled(!show);
        binding.ivProfileFirstName.setEnabled(!show);
        binding.ivProfileLastName.setEnabled(!show);
        binding.ivProfilePhone.setEnabled(!show);
    }
}

