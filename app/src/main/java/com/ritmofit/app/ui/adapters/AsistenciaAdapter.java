package com.ritmofit.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.AsistenciaDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AsistenciaAdapter extends RecyclerView.Adapter<AsistenciaAdapter.AsistenciaViewHolder> {
    
    private List<AsistenciaDTO> asistencias = new ArrayList<>();
    
    @NonNull
    @Override
    public AsistenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_asistencia, parent, false);
        return new AsistenciaViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AsistenciaViewHolder holder, int position) {
        AsistenciaDTO asistencia = asistencias.get(position);
        holder.bind(asistencia);
    }
    
    @Override
    public int getItemCount() {
        return asistencias.size();
    }
    
    public void updateAsistencias(List<AsistenciaDTO> nuevasAsistencias) {
        this.asistencias = nuevasAsistencias;
        notifyDataSetChanged();
    }
    
    static class AsistenciaViewHolder extends RecyclerView.ViewHolder {
        
        private TextView textViewDisciplina;
        private TextView textViewSede;
        private TextView textViewFechaClase;
        private TextView textViewEstado;
        
        public AsistenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDisciplina = itemView.findViewById(R.id.textViewDisciplina);
            textViewSede = itemView.findViewById(R.id.textViewSede);
            textViewFechaClase = itemView.findViewById(R.id.textViewFechaClase);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
        }
        
        public void bind(AsistenciaDTO asistencia) {
            textViewDisciplina.setText(asistencia.getClaseDisciplina());
            textViewSede.setText(asistencia.getClaseSede());
            textViewFechaClase.setText(formatFechaSolo(asistencia.getClaseFecha()));
            textViewEstado.setText("Asistió");
            textViewEstado.setTextColor(itemView.getContext().getColor(R.color.success));
        }
        
        private String formatFechaSolo(String fechaISO) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputFormat.parse(fechaISO);
                return outputFormat.format(date);
            } catch (ParseException e) {
                return fechaISO; // Si no se puede parsear, devolver la fecha original
            }
        }
    }
}
