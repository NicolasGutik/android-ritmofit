package com.ritmofit.app.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.ClaseDTO;
import com.ritmofit.app.data.repository.ClaseRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DetalleClaseViewModel extends ViewModel {
    
    private final ClaseRepository claseRepository;
    private final MutableLiveData<ApiResult<ClaseDTO>> clase = new MutableLiveData<>();
    
    @Inject
    public DetalleClaseViewModel(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }
    
    public LiveData<ApiResult<ClaseDTO>> getClase() {
        return clase;
    }
    
    public void cargarDetalleClase(Long claseId) {
        LiveData<ApiResult<ClaseDTO>> result = claseRepository.obtenerDetalleClase(claseId);
        
        // Observar el resultado y actualizar el MutableLiveData
        result.observeForever(apiResult -> clase.setValue(apiResult));
    }
}
