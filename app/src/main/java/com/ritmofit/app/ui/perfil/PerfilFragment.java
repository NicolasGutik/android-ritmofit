package com.ritmofit.app.ui.perfil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    private EditText etEmail, etFirst, etLast, etTel, etFoto;
    private ProgressBar progress;
    private Button btnGuardar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);
        etEmail = view.findViewById(R.id.et_email);
        etFirst = view.findViewById(R.id.et_firstname);
        etLast = view.findViewById(R.id.et_lastname);
        etTel = view.findViewById(R.id.et_telefono);
        etFoto = view.findViewById(R.id.et_foto);
        progress = view.findViewById(R.id.progress);
        btnGuardar = view.findViewById(R.id.btn_guardar);

        observeUser();
        btnGuardar.setOnClickListener(v -> submit());
    }

    private void observeUser() {
        progress.setVisibility(View.VISIBLE);
        viewModel.user.observe(getViewLifecycleOwner(), result -> {
            if (result instanceof ApiResult.Loading) {
                progress.setVisibility(View.VISIBLE);
            } else if (result instanceof ApiResult.Success) {
                progress.setVisibility(View.GONE);
                UserDTO u = ((ApiResult.Success<UserDTO>) result).getData();
                if (u != null) {
                    if (u.getId() != null) {
                        // keep id inside a hidden tag for update
                        etEmail.setTag(u.getId());
                    }
                    etEmail.setText(nullSafe(u.getEmail()));
                    etFirst.setText(nullSafe(u.getFirstName()));
                    etLast.setText(nullSafe(u.getLastName()));
                    etTel.setText(nullSafe(u.getTelefono()));
                    etFoto.setText(nullSafe(u.getFoto()));
                }
            } else if (result instanceof ApiResult.Error) {
                progress.setVisibility(View.GONE);
                Toast.makeText(requireContext(), ((ApiResult.Error<?>) result).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submit() {
        UserDTO body = new UserDTO();
        Object idTag = etEmail.getTag();
        if (idTag instanceof Long) {
            body.setId((Long) idTag);
        } else if (idTag instanceof Integer) {
            body.setId(((Integer) idTag).longValue());
        }
        body.setEmail(etEmail.getText().toString().trim());
        body.setFirstName(etFirst.getText().toString().trim());
        body.setLastName(etLast.getText().toString().trim());
        body.setTelefono(etTel.getText().toString().trim());
        body.setFoto(etFoto.getText().toString().trim());

        progress.setVisibility(View.VISIBLE);
        viewModel.update(body).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof ApiResult.Loading) {
                progress.setVisibility(View.VISIBLE);
            } else if (result instanceof ApiResult.Success) {
                progress.setVisibility(View.GONE);
                String msg = ((ApiResult.Success<String>) result).getData();
                Toast.makeText(requireContext(), TextUtils.isEmpty(msg) ? "Actualizado" : msg, Toast.LENGTH_LONG).show();
                // Reload to reflect server state
                viewModel.reload();
            } else if (result instanceof ApiResult.Error) {
                progress.setVisibility(View.GONE);
                Toast.makeText(requireContext(), ((ApiResult.Error<String>) result).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String nullSafe(String s) { return s == null ? "" : s; }
}

