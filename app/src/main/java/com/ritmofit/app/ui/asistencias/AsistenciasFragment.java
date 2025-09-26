package com.ritmofit.app.ui.asistencias;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.AsistenciaDTO;
import com.ritmofit.app.ui.adapters.AsistenciaAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AsistenciasFragment extends Fragment {
    
    private RecyclerView recyclerViewAsistencias;
    private TextView textViewEmpty;
    private TextView textViewFiltroFechas;
    private MaterialButton btnFiltrarFechas;
    private MaterialButton btnLimpiarFiltros;
    private AsistenciaAdapter adapter;
    
    private Calendar fechaDesde = Calendar.getInstance();
    private Calendar fechaHasta = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asistencias, container, false);
        
        // Inicializar componentes
        initializeViews(view);
        setupRecyclerView();
        setupDateFilter();
        loadDummyData();
        
        return view;
    }
    
    private void initializeViews(View view) {
        recyclerViewAsistencias = view.findViewById(R.id.recyclerViewAsistencias);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        textViewFiltroFechas = view.findViewById(R.id.textViewFiltroFechas);
        btnFiltrarFechas = view.findViewById(R.id.btnFiltrarFechas);
        btnLimpiarFiltros = view.findViewById(R.id.btnLimpiarFiltros);
    }
    
    private void setupRecyclerView() {
        adapter = new AsistenciaAdapter();
        recyclerViewAsistencias.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAsistencias.setAdapter(adapter);
    }
    
    private void setupDateFilter() {
        // Configurar fechas por defecto (último mes)
        fechaHasta.setTimeInMillis(System.currentTimeMillis());
        fechaDesde.setTimeInMillis(System.currentTimeMillis());
        fechaDesde.add(Calendar.MONTH, -1);
        
        updateFilterText();
        
        // Botón para cambiar fechas
        btnFiltrarFechas.setOnClickListener(v -> showDateRangePicker());
        
        // Botón para limpiar filtros
        btnLimpiarFiltros.setOnClickListener(v -> clearFilters());
    }
    
    private void showDateRangePicker() {
        // Mostrar selector de fecha desde
        DatePickerDialog datePickerDesde = new DatePickerDialog(
            getContext(),
            (view, year, month, dayOfMonth) -> {
                fechaDesde.set(year, month, dayOfMonth);
                // Después de seleccionar fecha desde, mostrar selector de fecha hasta
                showDatePickerHasta();
            },
            fechaDesde.get(Calendar.YEAR),
            fechaDesde.get(Calendar.MONTH),
            fechaDesde.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDesde.setTitle("Fecha desde");
        datePickerDesde.show();
    }
    
    private void showDatePickerHasta() {
        DatePickerDialog datePickerHasta = new DatePickerDialog(
            getContext(),
            (view, year, month, dayOfMonth) -> {
                fechaHasta.set(year, month, dayOfMonth);
                updateFilterText();
                applyFilters();
            },
            fechaHasta.get(Calendar.YEAR),
            fechaHasta.get(Calendar.MONTH),
            fechaHasta.get(Calendar.DAY_OF_MONTH)
        );
        datePickerHasta.setTitle("Fecha hasta");
        datePickerHasta.show();
    }
    
    private void updateFilterText() {
        String fechaDesdeStr = dateFormat.format(fechaDesde.getTime());
        String fechaHastaStr = dateFormat.format(fechaHasta.getTime());
        textViewFiltroFechas.setText("Filtro: " + fechaDesdeStr + " - " + fechaHastaStr);
    }
    
    private void clearFilters() {
        // Resetear a último mes
        fechaHasta.setTimeInMillis(System.currentTimeMillis());
        fechaDesde.setTimeInMillis(System.currentTimeMillis());
        fechaDesde.add(Calendar.MONTH, -1);
        
        updateFilterText();
        applyFilters();
        Toast.makeText(getContext(), "Filtros limpiados", Toast.LENGTH_SHORT).show();
    }
    
    private void applyFilters() {
        // TODO: Aquí implementarás la llamada al GET con los filtros
        // Por ahora solo mostramos un mensaje
        String fechaDesdeStr = dateFormat.format(fechaDesde.getTime());
        String fechaHastaStr = dateFormat.format(fechaHasta.getTime());
        
        Toast.makeText(getContext(), 
            "Filtrando desde " + fechaDesdeStr + " hasta " + fechaHastaStr, 
            Toast.LENGTH_SHORT).show();
        
        // Aquí en el futuro harás la llamada a la API
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
