package com.ritmofit.app.ui.reservas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.TurnoDTO;
import com.ritmofit.app.ui.adapters.TurnoAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MisReservasFragment extends Fragment {
    
    private MisReservasViewModel viewModel;
    private RecyclerView recyclerView;
    private TurnoAdapter adapter;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MisReservasViewModel.class);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_reservas, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view_reservas);
        setupRecyclerView();
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Observar cambios en las reservas
        viewModel.getReservas().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof com.ritmofit.app.data.dto.ApiResult.Loading) {
                // Mostrar loading
                Toast.makeText(getContext(), "Cargando reservas...", Toast.LENGTH_SHORT).show();
            } else if (result instanceof com.ritmofit.app.data.dto.ApiResult.Success) {
                com.ritmofit.app.data.dto.ApiResult.Success<java.util.List<TurnoDTO>> success = 
                    (com.ritmofit.app.data.dto.ApiResult.Success<java.util.List<TurnoDTO>>) result;
                adapter.updateTurnos(success.getData());
            } else if (result instanceof com.ritmofit.app.data.dto.ApiResult.Error) {
                com.ritmofit.app.data.dto.ApiResult.Error<java.util.List<TurnoDTO>> error = 
                    (com.ritmofit.app.data.dto.ApiResult.Error<java.util.List<TurnoDTO>>) result;
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        
        // Cargar reservas
        viewModel.cargarReservas();
    }
    
    private void setupRecyclerView() {
        adapter = new TurnoAdapter(turno -> {
            // TODO: Implementar cancelación de reserva
            Toast.makeText(getContext(), "Cancelar reserva: " + turno.getId(), Toast.LENGTH_SHORT).show();
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
