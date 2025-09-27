package com.ritmofit.app.session;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SessionManager {

    private static final String KEY_ACCESS = "jwt_token";
    private static final String KEY_REFRESH = "refresh_token";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences prefs;

    @Inject
    public SessionManager(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    // Tokens
    public void saveAccessToken(String token) { prefs.edit().putString(KEY_ACCESS, token).apply(); }
    public String getAccessToken() { return prefs.getString(KEY_ACCESS, null); }
    public void saveRefreshToken(String token) { prefs.edit().putString(KEY_REFRESH, token).apply(); }
    public String getRefreshToken() { return prefs.getString(KEY_REFRESH, null); }

    // Usuario
    public void saveUserId(Long id) { if (id != null) prefs.edit().putLong(KEY_USER_ID, id).apply(); }
    public Long getUserId() { return prefs.contains(KEY_USER_ID) ? prefs.getLong(KEY_USER_ID, -1L) : null; }

    public void clear() {
        prefs.edit().remove(KEY_ACCESS).remove(KEY_REFRESH).remove(KEY_USER_ID).apply();
    }
}
