package com.ritmofit.app.ui.perfil;

import static android.content.Context.MODE_PRIVATE;

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

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);

        // Placeholders
        binding.ivProfileEmail.setText(prefs.getString("email","" ));
        binding.ivProfileFistName.setText(prefs.getString("nombre", "tuo"));
        binding.ivProfileLastName.setText(prefs.getString("apellido", ""));
        binding.ivProfilePhone.setText(prefs.getString("telefono", ""));

        binding.btnUpdateProfile.setOnClickListener(v -> {
            String email = binding.ivProfileEmail.getText() != null ? binding.ivProfileEmail.getText().toString().trim() : "";
            String firstName = binding.ivProfileFistName.getText() != null ? binding.ivProfileFistName.getText().toString().trim() : "";
            String lastName = binding.ivProfileLastName.getText() != null ? binding.ivProfileLastName.getText().toString().trim() : "";
            String telefono = binding.ivProfilePhone.getText() != null ? binding.ivProfilePhone.getText().toString().trim() : "";

            UserDTO dto = new UserDTO();
            dto.setEmail(email);
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            dto.setTelefono(telefono);

            SharedPreferences prefs2 = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
            Long userId = Long.parseLong(prefs2.getString("user_id", "0"));

            perfilViewModel.actualizarPerfil(userId, dto).observe(getViewLifecycleOwner(), result -> {
                if (result instanceof ApiResult.Success) {
                    Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();

                    // Guardar local para próxima vez
                    prefs2.edit()
                            .putString("email", email)
                            .putString("nombre", firstName)
                            .putString("apellido", lastName)
                            .putString("telefono", telefono)
                            .apply();
                } else if (result instanceof ApiResult.Error) {
                    Toast.makeText(getContext(), "Error: " + ((ApiResult.Error<?>) result).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        String token   = prefs.getString("jwt_token", null);
        String userIdS = prefs.getString("user_id", null);

        if (token == null || userIdS == null) {
            Toast.makeText(getContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = Long.parseLong(userIdS);

        setLoading(true);
        perfilViewModel.perfilLive(userId).observe(getViewLifecycleOwner(), result -> {
            setLoading(false);
            if (result instanceof ApiResult.Success) {
                UserResponse u = ((ApiResult.Success<UserResponse>) result).getData();
                if (u == null) {
                    Toast.makeText(getContext(), "Respuesta vacía", Toast.LENGTH_SHORT).show();
                    return;
                }
                String first = u.getFirstName() != null ? u.getFirstName() : "";
                String last  = u.getLastName()  != null ? u.getLastName()  : "";
                String mail  = u.getEmail()     != null ? u.getEmail()     : "";
                String phone = u.getTelefono()  != null ? u.getTelefono()  : "";

                binding.ivProfileFistName.setText(first);
                binding.ivProfileLastName.setText(last);
                binding.ivProfileEmail.setText(mail);
                binding.ivProfilePhone.setText(phone);

                prefs.edit()
                        .putString("nombre", first)
                        .putString("apellido", last)
                        .putString("email", mail)
                        .putString("telefono", phone)
                        .apply();

            } else if (result instanceof ApiResult.Error) {
                String msg = ((ApiResult.Error<?>) result).getMessage();
                Toast.makeText(getContext(), "Error cargando perfil: " + msg, Toast.LENGTH_LONG).show();
            }
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
        binding.ivProfileFistName.setEnabled(!show);
        binding.ivProfileLastName.setEnabled(!show);
        binding.ivProfilePhone.setEnabled(!show);
    }
}

