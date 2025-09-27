package com.ritmofit.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.TurnoDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TurnoAdapter extends RecyclerView.Adapter<TurnoAdapter.TurnoViewHolder> {
    
    private List<TurnoDTO> turnos = new ArrayList<>();
    private OnTurnoClickListener listener;
    
    public interface OnTurnoClickListener {
        void onTurnoClick(TurnoDTO turno);
    }
    
    public TurnoAdapter(OnTurnoClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TurnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_turno, parent, false);
        return new TurnoViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TurnoViewHolder holder, int position) {
        TurnoDTO turno = turnos.get(position);
        holder.bind(turno);
    }
    
    @Override
    public int getItemCount() {
        return turnos.size();
    }
    
    public void updateTurnos(List<TurnoDTO> nuevosTurnos) {
        this.turnos = nuevosTurnos;
        notifyDataSetChanged();
    }
    
    class TurnoViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvDisciplina;
        private TextView tvSede;
        private TextView tvProfesor;
        private TextView tvFecha;
        private TextView tvEstado;
        
        public TurnoViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvDisciplina = itemView.findViewById(R.id.tv_disciplina);
            tvSede = itemView.findViewById(R.id.tv_sede);
            tvProfesor = itemView.findViewById(R.id.tv_profesor);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTurnoClick(turnos.get(position));
                }
            });
        }
        
        public void bind(TurnoDTO turno) {
            // Validar que turno no sea null
            if (turno == null) {
                return;
            }
            
            // Log para ver qué datos llegan del backend
            System.out.println("🔍 DATOS DEL BACKEND:");
            System.out.println("ID: " + turno.getId());
            System.out.println("ClaseID: " + turno.getClaseId());
            System.out.println("Disciplina: " + turno.getDisciplina());
            System.out.println("Sede: " + turno.getSede());
            System.out.println("Profesor: " + turno.getProfesor());
            System.out.println("Estado: " + turno.getEstado());
            System.out.println("FechaClase: " + turno.getFechaClase());
            System.out.println("ClaseFecha: " + turno.getClaseFecha());
            
            // Mostrar datos del turno, si no hay datos usar claseId para generar datos
            String disciplina = turno.getDisciplina();
            if (disciplina == null || disciplina.trim().isEmpty()) {
                // Generar disciplina basada en claseId
                disciplina = "Clase " + (turno.getClaseId() != null ? turno.getClaseId() : "N/A");
            }
            tvDisciplina.setText(disciplina);
            
            String sede = turno.getSede();
            if (sede == null || sede.trim().isEmpty()) {
                sede = "Sede " + (turno.getClaseId() != null ? turno.getClaseId() : "N/A");
            }
            tvSede.setText(sede);
            
            String profesor = turno.getProfesor();
            if (profesor == null || profesor.trim().isEmpty()) {
                profesor = "Sin asignar";
            }
            tvProfesor.setText("Prof. " + profesor);
            
            tvEstado.setText(turno.getEstado() != null ? turno.getEstado() : "Sin estado");
            
            // Mostrar fecha real del backend (usar claseFecha si está disponible)
            String fecha = turno.getClaseFecha();
            if (fecha == null || fecha.trim().isEmpty()) {
                fecha = turno.getFechaClase();
            }
            if (fecha != null && !fecha.trim().isEmpty()) {
                // Formatear fecha ISO a formato legible
                String fechaFormateada = formatearFecha(fecha);
                tvFecha.setText(fechaFormateada);
            } else {
                tvFecha.setText("Sin fecha");
            }
            
            // Cambiar color según estado
            if (turno.isConfirmado()) {
                tvEstado.setTextColor(itemView.getContext().getColor(R.color.success));
            } else if (turno.isCancelado()) {
                tvEstado.setTextColor(itemView.getContext().getColor(R.color.error));
            } else {
                tvEstado.setTextColor(itemView.getContext().getColor(R.color.warning));
            }
        }
        
        private String formatearFecha(String fecha) {
            if (fecha == null || fecha.trim().isEmpty()) {
                return "Sin fecha";
            }
            
            try {
                // Manejar formato ISO 8601 con zona horaria
                String fechaLimpia = fecha.replace("T", " ").replace("Z", "").replace("+00:00", "");
                if (fechaLimpia.contains(".")) {
                    fechaLimpia = fechaLimpia.substring(0, fechaLimpia.indexOf("."));
                }
                
                // Formatear a dd/MM/yyyy HH:mm
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                
                Date date = inputFormat.parse(fechaLimpia);
                if (date != null) {
                    return outputFormat.format(date);
                }
            } catch (Exception e) {
                // Si no se puede parsear, devolver fecha original truncada
                if (fecha.length() > 16) {
                    return fecha.substring(0, 16).replace("T", " ");
                }
                return fecha;
            }
            return fecha;
        }
        
    }
}

