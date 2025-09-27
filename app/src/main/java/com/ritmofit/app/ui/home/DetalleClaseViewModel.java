package com.ritmofit.app.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.ClaseDTO;
import com.ritmofit.app.data.dto.ReservaDTO;
import com.ritmofit.app.data.dto.TurnoDTO;
import com.ritmofit.app.data.repository.ClaseRepository;
import com.ritmofit.app.data.repository.ReservaRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DetalleClaseViewModel extends ViewModel {
    
    private final ClaseRepository claseRepository;
    private final ReservaRepository reservaRepository;
    private final MutableLiveData<ApiResult<ClaseDTO>> clase = new MutableLiveData<>();
    private final MutableLiveData<ApiResult<TurnoDTO>> reserva = new MutableLiveData<>();
    
    @Inject
    public DetalleClaseViewModel(ClaseRepository claseRepository, ReservaRepository reservaRepository) {
        this.claseRepository = claseRepository;
        this.reservaRepository = reservaRepository;
    }
    
    public LiveData<ApiResult<ClaseDTO>> getClase() {
        return clase;
    }
    
    public LiveData<ApiResult<TurnoDTO>> getReserva() {
        return reserva;
    }
    
    public void cargarDetalleClase(Long claseId) {
        LiveData<ApiResult<ClaseDTO>> result = claseRepository.obtenerDetalleClase(claseId);
        
        // Observar el resultado y actualizar el MutableLiveData
        result.observeForever(apiResult -> clase.setValue(apiResult));
    }
    
    public void crearReserva(Long claseId) {
        System.out.println("🔍 DetalleClaseViewModel - crearReserva llamado con claseId: " + claseId);
        System.out.println("🔍 DetalleClaseViewModel - reservaRepository: " + (reservaRepository != null ? "OK" : "NULL"));
        
        ReservaDTO reservaDTO = new ReservaDTO(claseId);
        System.out.println("🔍 DetalleClaseViewModel - ReservaDTO creado: " + reservaDTO);
        
        LiveData<ApiResult<TurnoDTO>> result = reservaRepository.crearReserva(reservaDTO);
        System.out.println("🔍 DetalleClaseViewModel - Llamada a reservaRepository.crearReserva realizada");
        
        // Observar el resultado y actualizar el MutableLiveData
        result.observeForever(apiResult -> {
            System.out.println("🔍 DetalleClaseViewModel - Resultado recibido: " + apiResult);
            reserva.setValue(apiResult);
        });
    }
}

