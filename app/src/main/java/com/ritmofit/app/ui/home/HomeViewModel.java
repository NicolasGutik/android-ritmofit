package com.ritmofit.app.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.ClaseDTO;
import com.ritmofit.app.data.dto.FiltroClaseDTO;
import com.ritmofit.app.data.dto.RespuestaPaginadaDTO;
import com.ritmofit.app.data.repository.ClaseRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    
    private final ClaseRepository claseRepository;
    private final MutableLiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> clases = new MutableLiveData<>();
    
    @Inject
    public HomeViewModel(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }
    
    public LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> getClases() {
        return clases;
    }
    
    public void cargarClases() {
        // Crear filtro por defecto (todas las clases)
        FiltroClaseDTO filtro = new FiltroClaseDTO();
        filtro.setPagina(0);
        filtro.setTamanio(20);
        
        LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> result = claseRepository.obtenerClases(filtro);
        
        // Observar el resultado y actualizar el MutableLiveData
        result.observeForever(apiResult -> clases.setValue(apiResult));
    }
    
    public void filtrarClases(String sede, String disciplina) {
        FiltroClaseDTO filtro = new FiltroClaseDTO();
        filtro.setSede(sede);
        filtro.setDisciplina(disciplina);
        filtro.setPagina(0);
        filtro.setTamanio(20);
        
        LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> result = claseRepository.obtenerClases(filtro);
        result.observeForever(apiResult -> clases.setValue(apiResult));
    }
}
