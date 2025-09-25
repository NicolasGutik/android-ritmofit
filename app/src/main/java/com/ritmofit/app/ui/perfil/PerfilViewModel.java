package com.ritmofit.app.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.api.RitmoFitApiService;
import com.ritmofit.app.data.dto.UserResponse;
import com.ritmofit.app.model.User;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class PerfilViewModel extends ViewModel {

    private final RitmoFitApiService apiService;
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public PerfilViewModel(RitmoFitApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<User> getUser() { return user; }
    public LiveData<String> getError() { return error; }

    public void cargarPerfil(Long id) {
        apiService.obtenerUsuario(id).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse ur = response.body();

                    User u = new User();
                    u.setId(ur.getId());
                    u.setFirstName(ur.getFirstName());
                    u.setLastName(ur.getLastName());
                    u.setEmail(ur.getEmail());
                    u.setTelefono(ur.getTelefono());
                    u.setFoto(ur.getFoto());

                    user.postValue(u);
                } else {
                    error.postValue("Error al obtener usuario");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }
}
