package com.ritmofit.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.ClaseDTO;
import com.ritmofit.app.ui.adapters.ClaseAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    
    private HomeViewModel viewModel;
    private RecyclerView recyclerView;
    private ClaseAdapter adapter;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view_clases);
        setupRecyclerView();
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Observar cambios en las clases
        viewModel.getClases().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof com.ritmofit.app.data.dto.ApiResult.Loading) {
                // Mostrar loading
                Toast.makeText(getContext(), "Cargando clases...", Toast.LENGTH_SHORT).show();
            } else if (result instanceof com.ritmofit.app.data.dto.ApiResult.Success) {
                com.ritmofit.app.data.dto.ApiResult.Success<com.ritmofit.app.data.dto.RespuestaPaginadaDTO<ClaseDTO>> success = 
                    (com.ritmofit.app.data.dto.ApiResult.Success<com.ritmofit.app.data.dto.RespuestaPaginadaDTO<ClaseDTO>>) result;
                adapter.updateClases(success.getData().getContenido());
            } else if (result instanceof com.ritmofit.app.data.dto.ApiResult.Error) {
                com.ritmofit.app.data.dto.ApiResult.Error<com.ritmofit.app.data.dto.RespuestaPaginadaDTO<ClaseDTO>> error = 
                    (com.ritmofit.app.data.dto.ApiResult.Error<com.ritmofit.app.data.dto.RespuestaPaginadaDTO<ClaseDTO>>) result;
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        
        // Cargar clases iniciales
        viewModel.cargarClases();
    }
    
    private void setupRecyclerView() {
        adapter = new ClaseAdapter(clase -> {
            // Navegar al detalle de la clase
            Bundle bundle = new Bundle();
            bundle.putLong("claseId", clase.getId());
            Navigation.findNavController(getView()).navigate(R.id.action_home_to_detalle, bundle);
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
