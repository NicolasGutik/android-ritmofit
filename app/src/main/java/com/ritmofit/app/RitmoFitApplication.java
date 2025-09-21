package com.ritmofit.app;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class RitmoFitApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
