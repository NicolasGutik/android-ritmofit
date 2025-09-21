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
        private TextView tvFecha;
        private TextView tvEstado;
        
        public TurnoViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvDisciplina = itemView.findViewById(R.id.tv_disciplina);
            tvSede = itemView.findViewById(R.id.tv_sede);
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
            tvDisciplina.setText(turno.getDisciplina());
            tvSede.setText(turno.getSede());
            tvEstado.setText(turno.getEstado());
            
            // Formatear fecha
            String fechaFormateada = formatearFecha(turno.getFechaClase());
            tvFecha.setText(fechaFormateada);
            
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

