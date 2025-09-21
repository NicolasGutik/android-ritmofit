package com.ritmofit.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.ClaseDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClaseAdapter extends RecyclerView.Adapter<ClaseAdapter.ClaseViewHolder> {
    
    private List<ClaseDTO> clases = new ArrayList<>();
    private OnClaseClickListener listener;
    
    public interface OnClaseClickListener {
        void onClaseClick(ClaseDTO clase);
    }
    
    public ClaseAdapter(OnClaseClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ClaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clase, parent, false);
        return new ClaseViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ClaseViewHolder holder, int position) {
        ClaseDTO clase = clases.get(position);
        holder.bind(clase);
    }
    
    @Override
    public int getItemCount() {
        return clases.size();
    }
    
    public void updateClases(List<ClaseDTO> nuevasClases) {
        this.clases = nuevasClases;
        notifyDataSetChanged();
    }
    
    class ClaseViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvDisciplina;
        private TextView tvSede;
        private TextView tvFecha;
        private TextView tvProfesor;
        private TextView tvCupos;
        
        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvDisciplina = itemView.findViewById(R.id.tv_disciplina);
            tvSede = itemView.findViewById(R.id.tv_sede);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            tvProfesor = itemView.findViewById(R.id.tv_profesor);
            tvCupos = itemView.findViewById(R.id.tv_cupos);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onClaseClick(clases.get(position));
                }
            });
        }
        
        public void bind(ClaseDTO clase) {
            tvDisciplina.setText(clase.getDisciplina());
            tvSede.setText(clase.getSede());
            tvProfesor.setText(clase.getNombreProfesor());
            tvCupos.setText(String.format(Locale.getDefault(), 
                    "Cupos: %d/%d", clase.getCuposDisponibles(), clase.getCupos()));
            
            // Formatear fecha
            String fechaFormateada = formatearFecha(clase.getFecha());
            tvFecha.setText(fechaFormateada);
        }
        
        private String formatearFecha(String fecha) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                
                Date date = inputFormat.parse(fecha);
                if (date != null) {
                    return outputFormat.format(date);
                }
            } catch (ParseException e) {
                // Si no se puede parsear, devolver la fecha original
            }
            return fecha;
        }
    }
}

