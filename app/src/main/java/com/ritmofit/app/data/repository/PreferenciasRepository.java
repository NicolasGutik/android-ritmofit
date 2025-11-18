package com.ritmofit.app.data.repository;

import androidx.lifecycle.LiveData;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.FiltroClaseUserDTO;

public interface PreferenciasRepository {
    LiveData<ApiResult<Void>> upsertPreferencias(Long userId, FiltroClaseUserDTO filtro);
    LiveData<ApiResult<FiltroClaseUserDTO>> getPreferencias(Long userId);
    LiveData<ApiResult<Void>> resetPreferencias(Long userId);
}
