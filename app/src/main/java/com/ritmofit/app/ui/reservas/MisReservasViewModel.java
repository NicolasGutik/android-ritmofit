package com.ritmofit.app.ui.reservas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.TurnoDTO;
import com.ritmofit.app.data.repository.ReservaRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MisReservasViewModel extends ViewModel {
    
    private final ReservaRepository reservaRepository;
    private final MutableLiveData<ApiResult<List<TurnoDTO>>> reservas = new MutableLiveData<>();
    
    @Inject
    public MisReservasViewModel(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }
    
    public LiveData<ApiResult<List<TurnoDTO>>> getReservas() {
        return reservas;
    }
    
    public void cargarReservas() {
        reservaRepository.obtenerMisReservas().observeForever(apiResult -> {
            reservas.setValue(apiResult);
        });
    }
    
    public void refrescarReservas() {
        cargarReservas();
    }
}

