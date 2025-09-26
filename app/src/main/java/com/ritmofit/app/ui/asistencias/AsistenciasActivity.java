package com.ritmofit.app.ui.asistencias;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.AsistenciaDTO;
import com.ritmofit.app.ui.adapters.AsistenciaAdapter;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AsistenciasActivity extends AppCompatActivity {
    
    private RecyclerView recyclerViewAsistencias;
    private TextView textViewEmpty;
    private AsistenciaAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencias);
        
        // Configurar toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mis Asistencias");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Inicializar componentes
        initializeViews();
        setupRecyclerView();
        loadDummyData();
    }
    
    private void initializeViews() {
        recyclerViewAsistencias = findViewById(R.id.recyclerViewAsistencias);
        textViewEmpty = findViewById(R.id.textViewEmpty);
    }
    
    private void setupRecyclerView() {
        adapter = new AsistenciaAdapter();
        recyclerViewAsistencias.setLayoutManager(new LinearLayoutManager(this));
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
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
