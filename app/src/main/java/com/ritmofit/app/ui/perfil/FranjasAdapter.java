package com.ritmofit.app.ui.perfil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ritmofit.app.R;
import com.ritmofit.app.data.dto.FranjaDTO;
import java.util.List;

public class FranjasAdapter extends RecyclerView.Adapter<FranjasAdapter.VH> {

    public interface OnRemove {
        void onRemove(int position);
    }

    private final List<FranjaDTO> data;
    private final OnRemove onRemove;

    public FranjasAdapter(List<FranjaDTO> data, OnRemove onRemove) {
        this.data = data;
        this.onRemove = onRemove;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        MaterialButton btn;
        VH(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_franja);
            btn = itemView.findViewById(R.id.btn_quitar_franja);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_franja, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FranjaDTO f = data.get(position);
        holder.tv.setText(f.getHoraInicial() + " - " + f.getHoraFinal());
        holder.btn.setOnClickListener(_v -> onRemove.onRemove(holder.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() { return data.size(); }
}
