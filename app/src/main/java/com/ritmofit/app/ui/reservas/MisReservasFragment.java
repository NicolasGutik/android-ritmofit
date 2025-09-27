package com.ritmofit.app.ui.reservas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.TurnoDTO;
import com.ritmofit.app.ui.adapters.TurnoAdapter;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MisReservasFragment extends Fragment {
    
    private MisReservasViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
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
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);
        
        setupRecyclerView();
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Observar cambios en las reservas
        viewModel.getReservas().observe(getViewLifecycleOwner(), this::handleReservasResult);
        
        // Cargar reservas
        viewModel.cargarReservas();
    }
    
    private void handleReservasResult(ApiResult<List<TurnoDTO>> result) {
        if (result instanceof ApiResult.Loading) {
            showLoading(true);
        } else if (result instanceof ApiResult.Success) {
            showLoading(false);
            ApiResult.Success<List<TurnoDTO>> success = (ApiResult.Success<List<TurnoDTO>>) result;
            List<TurnoDTO> reservas = success.getData();
            
            if (reservas != null && !reservas.isEmpty()) {
                adapter.updateTurnos(reservas);
                showEmptyState(false);
            } else {
                showEmptyState(true);
            }
        } else if (result instanceof ApiResult.Error) {
            showLoading(false);
            ApiResult.Error<List<TurnoDTO>> error = (ApiResult.Error<List<TurnoDTO>>) result;
            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            showEmptyState(true);
        }
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    
    private void showEmptyState(boolean show) {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void setupRecyclerView() {
        adapter = new TurnoAdapter(turno -> {
            // Mostrar detalles de la reserva
            String mensaje = String.format("Reserva ID: %d\nClase: %s\nFecha: %s\nEstado: %s", 
                turno.getId(), 
                turno.getDisciplina(), 
                turno.getFechaClase(), 
                turno.getEstado());
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}

