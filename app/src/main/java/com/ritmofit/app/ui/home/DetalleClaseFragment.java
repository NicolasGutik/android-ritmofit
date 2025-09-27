package com.ritmofit.app.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ritmofit.app.R;
import com.ritmofit.app.databinding.FragmentDetalleClaseBinding;
import com.ritmofit.app.data.dto.ClaseDTO;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetalleClaseFragment extends Fragment {
    
    private DetalleClaseViewModel viewModel;
    private FragmentDetalleClaseBinding binding;
    private long claseId;
    private ClaseDTO claseActual;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DetalleClaseViewModel.class);
        
        // Obtener ID de la clase desde los argumentos
        if (getArguments() != null) {
            claseId = getArguments().getLong("claseId");
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalleClaseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupClickListeners();
        
        // Observar cambios en la clase
        viewModel.getClase().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof com.ritmofit.app.data.dto.ApiResult.Loading) {
                showLoading(true);
            } else if (result instanceof com.ritmofit.app.data.dto.ApiResult.Success) {
                showLoading(false);
                com.ritmofit.app.data.dto.ApiResult.Success<ClaseDTO> success = 
                    (com.ritmofit.app.data.dto.ApiResult.Success<ClaseDTO>) result;
                mostrarClase(success.getData());
            } else if (result instanceof com.ritmofit.app.data.dto.ApiResult.Error) {
                showLoading(false);
                com.ritmofit.app.data.dto.ApiResult.Error<ClaseDTO> error = 
                    (com.ritmofit.app.data.dto.ApiResult.Error<ClaseDTO>) result;
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        
        // Cargar detalle de la clase
        if (claseId > 0) {
            viewModel.cargarDetalleClase(claseId);
        }
    }
    
    private void setupClickListeners() {
        binding.btnReservar.setOnClickListener(v -> {
            // TODO: Implementar reserva
            Toast.makeText(getContext(), "Funcionalidad de reserva en desarrollo", Toast.LENGTH_SHORT).show();
        });
        
        binding.btnVerUbicacion.setOnClickListener(v -> openLocationInMaps());
    }
    
    private void mostrarClase(ClaseDTO clase) {
        claseActual = clase;

        String disciplina = clase.getDisciplina();
        String sede = clase.getSede();
        String lugar = clase.getLugar();
        String duracion = clase.getDuracion();
        String profesor = clase.getNombreProfesor();

        binding.tvDisciplina.setText(disciplina != null ? disciplina : getString(R.string.clases_title));
        binding.tvSede.setText(sede != null && !sede.isEmpty() ? sede : getString(R.string.item_clase_lugar_pending));
        binding.tvLugar.setText(lugar != null && !lugar.isEmpty() ? lugar : getString(R.string.item_clase_lugar_pending));
        binding.tvFecha.setText(formatearFecha(clase.getFecha()));

        binding.tvDuracion.setText(duracion != null && !duracion.isEmpty()
                ? getString(R.string.item_clase_duracion, duracion)
                : getString(R.string.item_clase_duracion_pending));

        binding.tvProfesor.setText(profesor != null && !profesor.isEmpty()
                ? getString(R.string.item_clase_profesor, profesor)
                : getString(R.string.item_clase_profesor_pending));

        int cuposTotales = clase.getCupos() != null ? clase.getCupos() : 0;
        int cuposDisponibles = clase.getCuposDisponibles() != null ? clase.getCuposDisponibles() : 0;
        if (cuposTotales < 0) cuposTotales = 0;
        if (cuposDisponibles < 0) cuposDisponibles = 0;
        if (cuposDisponibles > cuposTotales && cuposTotales > 0) {
            cuposDisponibles = cuposTotales;
        }

        binding.tvCupos.setText(getString(R.string.detalle_cupos_resumen, cuposDisponibles, cuposTotales));

        int max = Math.max(cuposTotales, 1);
        binding.progressCupos.setMax(max);
        int ocupados = Math.max(cuposTotales - cuposDisponibles, 0);
        binding.progressCupos.setProgressCompat(Math.min(ocupados, max), true);

        binding.tvEstadoCupos.setText(obtenerMensajeDisponibilidad(cuposTotales, cuposDisponibles));

        boolean hayCupos = cuposDisponibles > 0;
        binding.btnReservar.setEnabled(hayCupos);
        binding.btnReservar.setText(hayCupos
                ? getString(R.string.reservar_button)
                : getString(R.string.detalle_button_sin_cupos));
    }
    
    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.layoutContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private String formatearFecha(String fecha) {
        try {
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("EEE dd MMM '•' HH:mm", java.util.Locale.getDefault());
            java.util.Date date = inputFormat.parse(fecha);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (java.text.ParseException e) {
            // Si no se puede parsear, devolver la fecha original
        }
        return fecha;
    }

    private void openLocationInMaps() {
        String query = buildLocationQuery();
        if (query == null || query.isEmpty()) {
            Toast.makeText(getContext(), R.string.detalle_ubicacion_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (getActivity() != null && mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query)));
            startActivity(webIntent);
        }
    }

    private String buildLocationQuery() {
        if (claseActual == null) {
            return null;
        }

        String disciplina = sanitize(claseActual.getDisciplina());
        String lugar = sanitize(claseActual.getLugar());
        String sede = sanitize(claseActual.getSede());

        StringBuilder builder = new StringBuilder();

        if (disciplina != null) {
            builder.append(disciplina);
        }

        if (lugar != null) {
            if (builder.length() > 0) {
                builder.append(" - ");
            }
            builder.append(lugar);
        }

        if (sede != null) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(sede);
        }

        if (builder.length() > 0) {
            builder.append(", RitmoFit");
            return builder.toString();
        }

        return null;
    }

    private String sanitize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String obtenerMensajeDisponibilidad(int cuposTotales, int cuposDisponibles) {
        if (cuposTotales <= 0) {
            return getString(R.string.detalle_estado_cupos_alto);
        }

        float ratio = cuposDisponibles / (float) cuposTotales;
        if (ratio <= 0.15f) {
            return getString(R.string.detalle_estado_cupos_bajo);
        } else if (ratio <= 0.45f) {
            return getString(R.string.detalle_estado_cupos_medio);
        } else {
            return getString(R.string.detalle_estado_cupos_alto);
        }
    }
}
