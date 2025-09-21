package com.ritmofit.app.ui.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ritmofit.app.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HistorialFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);
        
        // TODO: Implementar funcionalidad completa del historial
        Toast.makeText(getContext(), "Historial en desarrollo", Toast.LENGTH_SHORT).show();
        
        return view;
    }
}

