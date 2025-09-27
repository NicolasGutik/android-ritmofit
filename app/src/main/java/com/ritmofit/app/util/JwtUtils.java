package com.ritmofit.app.util;

import android.util.Base64;
import android.util.Log;
import org.json.JSONObject;

public class JwtUtils {
    public static Long getUserIdFromToken(String jwt) {
        try {
            if (jwt == null || !jwt.contains(".")) return null;
            String[] parts = jwt.split("\\.");
            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP));
            JSONObject obj = new JSONObject(payload);
            String sub = obj.optString("sub", null);
            if (sub == null || sub.isEmpty()) return null;
            return Long.valueOf(sub);
        } catch (Exception e) {
            Log.e("JwtUtils", "Error parsing JWT", e);
            return null;
        }
    }
}