package com.ritmofit.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.AsistenciaDTO;

import java.util.ArrayList;
import java.util.List;

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
        private TextView textViewLugar;
        private TextView textViewProfesor;
        private TextView textViewFechaClase;
        private TextView textViewFechaAsistencia;
        private TextView textViewEstado;
        
        public AsistenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDisciplina = itemView.findViewById(R.id.textViewDisciplina);
            textViewSede = itemView.findViewById(R.id.textViewSede);
            textViewLugar = itemView.findViewById(R.id.textViewLugar);
            textViewProfesor = itemView.findViewById(R.id.textViewProfesor);
            textViewFechaClase = itemView.findViewById(R.id.textViewFechaClase);
            textViewFechaAsistencia = itemView.findViewById(R.id.textViewFechaAsistencia);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
        }
        
        public void bind(AsistenciaDTO asistencia) {
            textViewDisciplina.setText(asistencia.getDisciplina());
            textViewSede.setText(asistencia.getSede());
            textViewLugar.setText(asistencia.getLugar());
            textViewProfesor.setText(asistencia.getProfesor());
            textViewFechaClase.setText(asistencia.getFechaClase());
            textViewFechaAsistencia.setText(asistencia.getFechaAsistencia());
            textViewEstado.setText(asistencia.getEstado());
        }
    }
}
