package com.ritmofit.app.ui.perfil;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.repository.AuthRepository;
import com.ritmofit.app.data.repository.UserRepository;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PerfilViewModel extends ViewModel {

    private static final String TAG = "PerfilViewModel";

    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<Long> userIdLive = new MutableLiveData<>();
    public LiveData<ApiResult<UserDTO>> user; // NO final, lo inicializamos en el ctor

    @Inject
    public PerfilViewModel(AuthRepository authRepository, UserRepository userRepository) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;   // ✅ asignación que faltaba

        // Recién ahora armamos el switchMap, después de asignar el repo
        this.user = Transformations.switchMap(userIdLive, id -> {
            if (id == null) return new MutableLiveData<>();
            return this.userRepository.getUserById(id);
        });

        initUserId();
    }

    private void initUserId() {
        try {
            UserDTO saved = authRepository.obtenerUsuarioGuardado();
            if (saved != null && saved.getId() != null) {
                userIdLive.setValue(saved.getId());
                return;
            }
        } catch (Exception ignored) {}

        String token = null;
        try { token = authRepository.obtenerToken(); } catch (Exception ignored) {}
        Long idFromJwt = extractUserIdFromJwt(token);
        if (idFromJwt != null) userIdLive.setValue(idFromJwt);
    }

    public void reload() {
        Long id = userIdLive.getValue();
        if (id != null) userIdLive.setValue(id);
        else initUserId();
    }

    public LiveData<ApiResult<String>> update(UserDTO user) {
        Long id = user.getId();
        if (id == null) {
            try {
                UserDTO saved = authRepository.obtenerUsuarioGuardado();
                if (saved != null && saved.getId() != null) id = saved.getId();
            } catch (Exception ignored) {}
            if (id == null) id = extractUserIdFromJwt(authRepository.obtenerToken());
        }
        if (id != null) user.setId(id);
        return userRepository.updateUser(id, user); // ajustá el método si tu repo difiere
    }

    private Long extractUserIdFromJwt(String token) {
        if (TextUtils.isEmpty(token)) return null;
        try {
            if (token.startsWith("Bearer ")) token = token.substring("Bearer ".length());
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            String json = new String(Base64.decode(parts[1],
                    Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(json);
            if (obj.has("sub")) {
                Long v = asLongFlexible(obj.opt("sub"));
                if (v != null) return v;
            }
            if (obj.has("id")) {
                Long v = asLongFlexible(obj.opt("id"));
                if (v != null) return v;
            }
        } catch (Exception e) {
            Log.w(TAG, "No se pudo parsear el JWT", e);
        }
        return null;
    }

    private Long asLongFlexible(Object v) {
        try {
            if (v instanceof Number) return ((Number) v).longValue();
            String s = String.valueOf(v);
            if (TextUtils.isEmpty(s)) return null;
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            return null;
        }
    }
}