package com.ritmofit.app.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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


    private Observer<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> observer;

    @Inject
    public HomeViewModel(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }

    public LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> getClases() {
        return clases;
    }

    public void cargarClases() {
        FiltroClaseDTO filtro = new FiltroClaseDTO();
        filtro.setPage(0);
        filtro.setSize(20);

        LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> result =
                claseRepository.obtenerClases(filtro);

        // Antes de agregar un nuevo observer, eliminamos el anterior
        if (observer != null) {
            result.removeObserver(observer);
        }

        observer = apiResult -> clases.setValue(apiResult);
        result.observeForever(observer);
    }

    public void filtrarClases(String sede, String disciplina) {
        FiltroClaseDTO filtro = new FiltroClaseDTO();
        filtro.setSede(sede);
        filtro.setDisciplina(disciplina);
        filtro.setPage(0);
        filtro.setSize(20);

        LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> result =
                claseRepository.obtenerClases(filtro);

        if (observer != null) {
            result.removeObserver(observer);
        }

        observer = apiResult -> clases.setValue(apiResult);
        result.observeForever(observer);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (observer != null) {
            // Eliminamos el observer cuando el ViewModel muere
            clases.removeObserver(observer);
        }
    }
}

