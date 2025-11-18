package com.ritmofit.app.ui.home;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.ClaseDTO;
import com.ritmofit.app.data.dto.FiltroClaseDTO;
import com.ritmofit.app.data.dto.RespuestaPaginadaDTO;
import com.ritmofit.app.data.dto.RespuestaPaginadaHome;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.repository.AuthRepository;
import com.ritmofit.app.data.repository.ClaseRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private static final int PAGE_DEFAULT = 0;
    private static final int SIZE_DEFAULT = 20;

    private final ClaseRepository claseRepository;
    private final AuthRepository authRepository;
    private final MediatorLiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> clases = new MediatorLiveData<>();
    private LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> clasesSource;

    private final MediatorLiveData<ApiResult<List<String>>> sedes = new MediatorLiveData<>();
    private LiveData<ApiResult<List<String>>> sedesSource;

    private final MediatorLiveData<ApiResult<List<String>>> disciplinas = new MediatorLiveData<>();
    private LiveData<ApiResult<List<String>>> disciplinasSource;

    private final MutableLiveData<String> filtroResumen = new MutableLiveData<>();
    private final MutableLiveData<Long> filtroFechaMillis = new MutableLiveData<>(null);

    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private String filtroSede;
    private String filtroDisciplina;

    private boolean usarPreferencias = true;
    private boolean initialized = false;

    @Inject
    public HomeViewModel(ClaseRepository claseRepository, AuthRepository authRepository) {
        this.claseRepository = claseRepository;
        this.authRepository = authRepository;
    }

    public LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> getClases() {
        return clases;
    }

    public LiveData<ApiResult<List<String>>> getSedes() {
        return sedes;
    }

    public LiveData<ApiResult<List<String>>> getDisciplinas() {
        return disciplinas;
    }

    public LiveData<String> getFiltroResumen() {
        return filtroResumen;
    }

    public void iniciar() {
        if (!initialized) {
            initialized = true;
            usarPreferencias = true;
            cargarClasesPorPreferencias();
            cargarOpcionesFiltros();
        } else {
            refrescar();
        }
    }

    public void refrescar() {
        if (usarPreferencias) {
            cargarClasesPorPreferencias();
        } else {
            aplicarFiltros(filtroSede, filtroDisciplina, filtroFechaMillis.getValue());
        }
    }

    public void recargarClases() {
        aplicarFiltros(filtroSede, filtroDisciplina, filtroFechaMillis.getValue());
    }

    public void aplicarFiltros(@Nullable String sede, @Nullable String disciplina, @Nullable Long fechaMillis) {
        usarPreferencias = false;
        filtroSede = normalizarTexto(sede);
        filtroDisciplina = normalizarTexto(disciplina);
        filtroFechaMillis.setValue(fechaMillis);

        FiltroClaseDTO filtro = new FiltroClaseDTO();
        filtro.setPage(PAGE_DEFAULT);
        filtro.setSize(SIZE_DEFAULT);

        if (filtroSede != null) {
            filtro.setSede(filtroSede);
        }

        if (filtroDisciplina != null) {
            filtro.setDisciplina(filtroDisciplina);
        }

        if (fechaMillis != null) {
            String fechaDesde = construirFecha(fechaMillis, true);
            String fechaHasta = construirFecha(fechaMillis, false);
            filtro.setFechaDesde(fechaDesde);
            filtro.setFechaHasta(fechaHasta);
        }

        adjuntarClases(claseRepository.obtenerClases(filtro));
        actualizarResumen();
    }



    @Nullable
    public String getFiltroSede() {
        return filtroSede;
    }

    @Nullable
    public String getFiltroDisciplina() {
        return filtroDisciplina;
    }

    @Nullable
    public Long getFiltroFechaMillis() {
        return filtroFechaMillis.getValue();
    }

    private void cargarOpcionesFiltros() {
        if (sedesSource == null) {
            sedesSource = claseRepository.obtenerSedes();
            sedes.addSource(sedesSource, sedes::setValue);
        }

        if (disciplinasSource == null) {
            disciplinasSource = claseRepository.obtenerDisciplinas();
            disciplinas.addSource(disciplinasSource, disciplinas::setValue);
        }
    }

    private void adjuntarClases(LiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> nuevaFuente) {
        if (clasesSource != null) {
            clases.removeSource(clasesSource);
        }
        clasesSource = nuevaFuente;
        clases.addSource(clasesSource, clases::setValue);
    }

    private void actualizarResumen() {
        StringBuilder builder = new StringBuilder();

        if (filtroDisciplina != null) {
            builder.append("Disciplina: ").append(filtroDisciplina);
        }

        if (filtroSede != null) {
            if (builder.length() > 0) {
                builder.append(" • ");
            }
            builder.append("Sede: ").append(filtroSede);
        }

        Long fechaMillis = filtroFechaMillis.getValue();
        if (fechaMillis != null) {
            if (builder.length() > 0) {
                builder.append(" • ");
            }
            builder.append("Fecha: ").append(displayDateFormat.format(new Date(fechaMillis)));
        }

        if (builder.length() == 0) {
            filtroResumen.setValue(null);
        } else {
            filtroResumen.setValue(builder.toString());
        }
    }

    private String construirFecha(long fechaMillis, boolean inicioDelDia) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fechaMillis);
        if (inicioDelDia) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        }
        return apiDateFormat.format(calendar.getTime());
    }

    @Nullable
    private String normalizarTexto(@Nullable String valor) {
        if (valor == null) {
            return null;
        }
        String trimmed = valor.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void cargarClasesPorPreferencias() {
        usarPreferencias = true;
        LiveData<ApiResult<UserDTO>> userLive = authRepository.obtenerUsuarioActual();

        // Nos enganchamos a ese LiveData temporalmente
        MediatorLiveData<ApiResult<RespuestaPaginadaDTO<ClaseDTO>>> bridge = new MediatorLiveData<>();
        bridge.setValue(new ApiResult.Loading<>());

        bridge.addSource(userLive, userRes -> {
            if (userRes instanceof ApiResult.Success) {
                Long userId = ((ApiResult.Success<UserDTO>) userRes).getData().getId();
                // ya tenemos el userId → pedimos las clases por preferencias
                LiveData<ApiResult<RespuestaPaginadaHome<ClaseDTO>>> src = claseRepository.obtenerClasesHome(userId);

                bridge.addSource(src, r -> {
                    if (r instanceof ApiResult.Loading) {
                        bridge.setValue(new ApiResult.Loading<>());
                    } else if (r instanceof ApiResult.Error) {
                        ApiResult.Error<?> e = (ApiResult.Error<?>) r;
                        bridge.setValue(new ApiResult.Error<>(e.getMessage(), e.getThrowable()));
                    } else if (r instanceof ApiResult.Success) {
                        RespuestaPaginadaHome<ClaseDTO> home = ((ApiResult.Success<RespuestaPaginadaHome<ClaseDTO>>) r).getData();
                        RespuestaPaginadaDTO<ClaseDTO> dto = new RespuestaPaginadaDTO<>();
                        dto.setContenido(home.getContenido());
                        dto.setPagina(home.getPagina());
                        dto.setTamanio(home.getTamanio());
                        dto.setTotalElementos(home.getTotalElementos());
                        dto.setTotalPaginas(home.getTotalPaginas());
                        dto.setPrimera(home.isPrimera());
                        dto.setUltima(home.isUltima());
                        dto.setTieneAnterior(home.isTieneAnterior());
                        dto.setTieneSiguiente(home.isTieneSiguiente());
                        bridge.setValue(new ApiResult.Success<>(dto));
                    }
                });

                // ya no necesitamos seguir escuchando al user
                bridge.removeSource(userLive);

            } else if (userRes instanceof ApiResult.Error) {
                ApiResult.Error<?> e = (ApiResult.Error<?>) userRes;
                bridge.setValue(new ApiResult.Error<>("No se pudo obtener el usuario actual", e.getThrowable()));
                bridge.removeSource(userLive);
            }
        });

        // conecta el bridge con tu Mediator principal
        adjuntarClases(bridge);
        filtroResumen.setValue("Preferencias del usuario");
    }
}
