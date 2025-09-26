package com.ritmofit.app.ui.asistencias;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.AsistenciaDTO;
import com.ritmofit.app.ui.adapters.AsistenciaAdapter;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AsistenciasFragment extends Fragment {
    
    private RecyclerView recyclerViewAsistencias;
    private TextView textViewEmpty;
    private AsistenciaAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asistencias, container, false);
        
        // Inicializar componentes
        initializeViews(view);
        setupRecyclerView();
        loadDummyData();
        
        return view;
    }
    
    private void initializeViews(View view) {
        recyclerViewAsistencias = view.findViewById(R.id.recyclerViewAsistencias);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
    }
    
    private void setupRecyclerView() {
        adapter = new AsistenciaAdapter();
        recyclerViewAsistencias.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAsistencias.setAdapter(adapter);
    }
    
    private void loadDummyData() {
        // Lista vacía - sin datos de prueba
        List<AsistenciaDTO> asistencias = new ArrayList<>();
        adapter.updateAsistencias(asistencias);
        
        // Mostrar mensaje de lista vacía
        if (asistencias.isEmpty()) {
            recyclerViewAsistencias.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewAsistencias.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }
}
