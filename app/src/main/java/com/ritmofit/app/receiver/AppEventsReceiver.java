package com.ritmofit.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AppEventsReceiver extends BroadcastReceiver {
    
    private static final String TAG = "AppEventsReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        
        if (action == null) {
            return;
        }
        
        switch (action) {
            case Intent.ACTION_BATTERY_LOW:
                Log.d(TAG, "Batería baja detectada");
                Toast.makeText(context, "Batería baja - Modo ahorro activado", Toast.LENGTH_LONG).show();
                break;
                
            case "android.net.conn.CONNECTIVITY_ACTION":
                Log.d(TAG, "Cambio de conectividad detectado");
                // Verificar estado de conectividad
                boolean isConnected = android.net.ConnectivityManager.CONNECTIVITY_ACTION.equals(action);
                String message = isConnected ? "Conectividad restaurada" : "Sin conexión a internet";
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                break;
                
            default:
                Log.d(TAG, "Acción no manejada: " + action);
                break;
        }
    }
}
