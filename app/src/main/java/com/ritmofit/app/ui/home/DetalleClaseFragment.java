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
        
        binding.btnVerUbicacion.setOnClickListener(v -> {
            // Intent implícito para abrir Google Maps
            String ubicacion = "Sede Norte, Buenos Aires"; // TODO: Obtener ubicación real
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(ubicacion));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback a navegador web
                Intent webIntent = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(ubicacion)));
                startActivity(webIntent);
            }
        });
    }
    
    private void mostrarClase(ClaseDTO clase) {
        binding.tvDisciplina.setText(clase.getDisciplina());
        binding.tvSede.setText(clase.getSede());
        binding.tvLugar.setText(clase.getLugar());
        binding.tvFecha.setText(formatearFecha(clase.getFecha()));
        binding.tvDuracion.setText(clase.getDuracion());
        binding.tvProfesor.setText(clase.getNombreProfesor());
        binding.tvCupos.setText(String.format("Cupos disponibles: %d de %d", 
                clase.getCuposDisponibles(), clase.getCupos()));
        
        // Habilitar/deshabilitar botón de reserva según disponibilidad
        binding.btnReservar.setEnabled(clase.getCuposDisponibles() > 0);
        
        if (clase.getCuposDisponibles() <= 0) {
            binding.btnReservar.setText("Sin cupos disponibles");
        }
    }
    
    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.layoutContent.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private String formatearFecha(String fecha) {
        try {
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm", java.util.Locale.getDefault());
            
            java.util.Date date = inputFormat.parse(fecha);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (java.text.ParseException e) {
            // Si no se puede parsear, devolver la fecha original
        }
        return fecha;
    }
}

