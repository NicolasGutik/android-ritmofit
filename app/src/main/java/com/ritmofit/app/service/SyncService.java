package com.ritmofit.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.ritmofit.app.R;
import com.ritmofit.app.data.repository.ClaseRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SyncService extends Service {
    
    private static final String TAG = "SyncService";
    private static final String CHANNEL_ID = "sync_channel";
    private static final int NOTIFICATION_ID = 1;
    
    @Inject
    ClaseRepository claseRepository;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SyncService creado");
        createNotificationChannel();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SyncService iniciado");
        
        // Crear notificación foreground
        Notification notification = createNotification("Sincronizando datos...");
        startForeground(NOTIFICATION_ID, notification);
        
        // Ejecutar sincronización en background
        new Thread(() -> {
            try {
                // Simular trabajo de sincronización
                Thread.sleep(3000);
                
                // TODO: Implementar sincronización real de datos
                Log.d(TAG, "Sincronización completada");
                
                // Actualizar notificación
                updateNotification("Sincronización completada");
                
            } catch (InterruptedException e) {
                Log.e(TAG, "Error en sincronización", e);
            } finally {
                // Parar el servicio
                stopSelf();
            }
        }).start();
        
        return START_NOT_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SyncService destruido");
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sync Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    private Notification createNotification(String contentText) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("RitmoFit Sync")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
    
    private void updateNotification(String contentText) {
        Notification notification = createNotification(contentText);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, notification);
        }
    }
}

