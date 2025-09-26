package com.ritmofit.app.ui.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.ClaseDTO;
import com.ritmofit.app.data.dto.RespuestaPaginadaDTO;
import com.ritmofit.app.ui.adapters.ClaseAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private final List<String> sedesDisponibles = new ArrayList<>();
    private final List<String> disciplinasDisponibles = new ArrayList<>();
    private final SimpleDateFormat dialogDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private HomeViewModel viewModel;
    private RecyclerView recyclerView;
    private ClaseAdapter adapter;
    private TextView tvEmpty;
    private MaterialButton btnFiltros;
    private TextView tvFiltersSummary;

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
        tvEmpty = view.findViewById(R.id.tv_empty);
        btnFiltros = view.findViewById(R.id.btn_filtros);
        tvFiltersSummary = view.findViewById(R.id.tv_filters_summary);

        if (sedesDisponibles.isEmpty()) {
            sedesDisponibles.add(getString(R.string.home_filter_option_all));
        }
        if (disciplinasDisponibles.isEmpty()) {
            disciplinasDisponibles.add(getString(R.string.home_filter_option_all));
        }

        setupRecyclerView();
        btnFiltros.setOnClickListener(v -> showFilterDialog());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getClases().observe(getViewLifecycleOwner(), this::handleClasesResult);

        viewModel.getSedes().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof ApiResult.Success) {
                List<String> data = ((ApiResult.Success<List<String>>) result).getData();
                actualizarOpciones(sedesDisponibles, data);
            }
        });

        viewModel.getDisciplinas().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof ApiResult.Success) {
                List<String> data = ((ApiResult.Success<List<String>>) result).getData();
                actualizarOpciones(disciplinasDisponibles, data);
            }
        });

        viewModel.getFiltroResumen().observe(getViewLifecycleOwner(), resumen -> {
            if (TextUtils.isEmpty(resumen)) {
                tvFiltersSummary.setVisibility(View.GONE);
                btnFiltros.setText(R.string.home_button_filtro);
            } else {
                tvFiltersSummary.setVisibility(View.VISIBLE);
                tvFiltersSummary.setText(resumen);
                btnFiltros.setText(R.string.home_button_filtro_applied);
            }
        });

        viewModel.iniciar();
    }

    private void handleClasesResult(ApiResult<RespuestaPaginadaDTO<ClaseDTO>> result) {
        if (result instanceof ApiResult.Loading) {
            mostrarEstadoCarga();
        } else if (result instanceof ApiResult.Success) {
            ApiResult.Success<RespuestaPaginadaDTO<ClaseDTO>> success = (ApiResult.Success<RespuestaPaginadaDTO<ClaseDTO>>) result;
            List<ClaseDTO> contenido = success.getData() != null ? success.getData().getContenido() : null;
            if (contenido == null) {
                contenido = new ArrayList<>();
            }
            adapter.updateClases(contenido);
            if (contenido.isEmpty()) {
                mostrarEstadoVacio(getString(R.string.empty_list));
            } else {
                mostrarLista();
            }
        } else if (result instanceof ApiResult.Error) {
            ApiResult.Error<RespuestaPaginadaDTO<ClaseDTO>> error = (ApiResult.Error<RespuestaPaginadaDTO<ClaseDTO>>) result;
            String message = error.getMessage() != null ? error.getMessage() : getString(R.string.error_network);
            mostrarEstadoVacio(message);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarEstadoCarga() {
        if (tvEmpty != null) {
            tvEmpty.setText(R.string.error_loading);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        recyclerView.setVisibility(View.GONE);
    }

    private void mostrarEstadoVacio(String mensaje) {
        if (tvEmpty != null) {
            if (TextUtils.isEmpty(mensaje)) {
                tvEmpty.setText(R.string.empty_list);
            } else {
                tvEmpty.setText(mensaje);
            }
            tvEmpty.setVisibility(View.VISIBLE);
        }
        recyclerView.setVisibility(View.GONE);
    }

    private void mostrarLista() {
        recyclerView.setVisibility(View.VISIBLE);
        if (tvEmpty != null) {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        adapter = new ClaseAdapter(clase -> {
            Bundle bundle = new Bundle();
            bundle.putLong("claseId", clase.getId());
            Navigation.findNavController(recyclerView).navigate(R.id.action_home_to_detalle, bundle);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void showFilterDialog() {
        if (getContext() == null) {
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filtros_clases, null);

        MaterialAutoCompleteTextView disciplinaInput = dialogView.findViewById(R.id.autocomplete_disciplina);
        MaterialAutoCompleteTextView sedeInput = dialogView.findViewById(R.id.autocomplete_sede);
        MaterialButton btnSeleccionarFecha = dialogView.findViewById(R.id.btn_seleccionar_fecha);
        MaterialButton btnLimpiarFecha = dialogView.findViewById(R.id.btn_limpiar_fecha);
        TextView tvFechaSeleccionada = dialogView.findViewById(R.id.tv_fecha_seleccionada);

        ArrayAdapter<String> disciplinasAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, crearOpcionesConDefault(disciplinasDisponibles));
        ArrayAdapter<String> sedesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, crearOpcionesConDefault(sedesDisponibles));

        disciplinaInput.setAdapter(disciplinasAdapter);
        sedeInput.setAdapter(sedesAdapter);

        String filtroDisciplina = viewModel.getFiltroDisciplina();
        String filtroSede = viewModel.getFiltroSede();
        Long filtroFecha = viewModel.getFiltroFechaMillis();

        disciplinaInput.setText(filtroDisciplina != null ? filtroDisciplina : getString(R.string.home_filter_option_all), false);
        sedeInput.setText(filtroSede != null ? filtroSede : getString(R.string.home_filter_option_all), false);

        final Long[] seleccionFecha = new Long[]{filtroFecha};
        actualizarLabelFecha(tvFechaSeleccionada, seleccionFecha[0]);

        btnSeleccionarFecha.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.home_filter_fecha_picker_title);
            if (seleccionFecha[0] != null) {
                builder.setSelection(seleccionFecha[0]);
            }
            MaterialDatePicker<Long> datePicker = builder.build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                seleccionFecha[0] = selection;
                actualizarLabelFecha(tvFechaSeleccionada, seleccionFecha[0]);
            });
            datePicker.show(getChildFragmentManager(), "filter_date_picker");
        });

        btnLimpiarFecha.setOnClickListener(v -> {
            seleccionFecha[0] = null;
            actualizarLabelFecha(tvFechaSeleccionada, null);
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.home_filter_dialog_title)
                .setView(dialogView)
                .setPositiveButton(R.string.home_filter_apply, (d, which) -> {
                    String seleccionDisciplina = normalizarSeleccion(disciplinaInput.getText());
                    String seleccionSede = normalizarSeleccion(sedeInput.getText());
                    viewModel.aplicarFiltros(seleccionSede, seleccionDisciplina, seleccionFecha[0]);
                })
                .setNeutralButton(R.string.home_filter_reset, (d, which) -> viewModel.aplicarFiltros(null, null, null))
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.show();
    }

    private List<String> crearOpcionesConDefault(List<String> origen) {
        List<String> opciones = new ArrayList<>();
        if (origen == null || origen.isEmpty()) {
            opciones.add(getString(R.string.home_filter_option_all));
        } else {
            opciones.addAll(origen);
        }
        return opciones;
    }

    private void actualizarOpciones(List<String> destino, List<String> nuevos) {
        destino.clear();
        destino.add(getString(R.string.home_filter_option_all));
        if (nuevos != null) {
            for (String item : nuevos) {
                if (item != null && !item.trim().isEmpty()) {
                    destino.add(item);
                }
            }
        }
    }

    private void actualizarLabelFecha(TextView textView, @Nullable Long fechaMillis) {
        if (fechaMillis == null) {
            textView.setText(R.string.home_filter_fecha_none);
        } else {
            textView.setText(dialogDateFormat.format(new Date(fechaMillis)));
        }
    }

    @Nullable
    private String normalizarSeleccion(CharSequence value) {
        if (value == null) {
            return null;
        }
        String texto = value.toString().trim();
        if (texto.isEmpty()) {
            return null;
        }
        if (texto.equalsIgnoreCase(getString(R.string.home_filter_option_all))) {
            return null;
        }
        return texto;
    }
}
