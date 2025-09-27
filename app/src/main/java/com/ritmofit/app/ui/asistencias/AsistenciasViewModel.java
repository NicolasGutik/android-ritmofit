package com.ritmofit.app.ui.asistencias;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.dto.AsistenciaDTO;
import com.ritmofit.app.data.repository.AsistenciaRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AsistenciasViewModel extends ViewModel {

    private final AsistenciaRepository asistenciaRepository;
    
    private final MutableLiveData<List<AsistenciaDTO>> _asistencias = new MutableLiveData<>();
    public LiveData<List<AsistenciaDTO>> asistencias = _asistencias;
    
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;
    
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    @Inject
    public AsistenciasViewModel(AsistenciaRepository asistenciaRepository) {
        this.asistenciaRepository = asistenciaRepository;
    }

    public void obtenerAsistencias(Long userId) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        asistenciaRepository.obtenerAsistencias(userId).observeForever(asistencias -> {
            _isLoading.setValue(false);
            if (asistencias != null) {
                _asistencias.setValue(asistencias);
            } else {
                _error.setValue("Error al cargar asistencias");
            }
        });
    }

    public void obtenerAsistenciasConFiltro(Long userId, String fechaDesde, String fechaHasta) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        asistenciaRepository.obtenerAsistenciasConFiltro(userId, fechaDesde, fechaHasta).observeForever(asistencias -> {
            _isLoading.setValue(false);
            if (asistencias != null) {
                _asistencias.setValue(asistencias);
            } else {
                _error.setValue("Error al cargar asistencias con filtro");
            }
        });
    }
    
    public void obtenerAsistenciasConFiltroCompleto(Long userId, String fechaDesde, String fechaHasta, String disciplina) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        asistenciaRepository.obtenerAsistenciasConFiltroCompleto(userId, fechaDesde, fechaHasta, disciplina).observeForever(asistencias -> {
            _isLoading.setValue(false);
            if (asistencias != null) {
                _asistencias.setValue(asistencias);
            } else {
                _error.setValue("Error al cargar asistencias con filtro completo");
            }
        });
    }
}
