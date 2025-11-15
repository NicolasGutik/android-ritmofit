package com.ritmofit.app.di;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ritmofit.app.data.api.RitmoFitApiService;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.content.SharedPreferences;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    
    private static final String TAG = "NetworkModule";
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // Emulador Android
    // Para emulador Android usar: "http://10.0.2.2:8081/"
    // Para dispositivo físico usar: "http://[TU_IP_LOCAL]:8081/"
    
    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }
    
    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(@ApplicationContext Context context, SharedPreferences encryptedPrefs) {
        // Interceptor para logging (solo en debug)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // Interceptor para agregar JWT automáticamente
        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();
            
            // Obtener token desde EncryptedSharedPreferences
            String token = encryptedPrefs.getString("jwt_token", null);
            
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("Content-Type", "application/json");
            
            // Agregar Authorization header si hay token
            if (token != null && !token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
                Log.d(TAG, "✅ JWT agregado a la petición: " + originalRequest.url());
                Log.d(TAG, "🔑 Token: " + token.substring(0, Math.min(20, token.length())) + "...");
                Log.d(TAG, "🔍 Headers que se enviarán: " + requestBuilder.build().headers());
                System.out.println("🔑 TOKEN COMPLETO PARA POSTMAN: " + token);
            } else {
                Log.d(TAG, "❌ No hay JWT disponible para: " + originalRequest.url());
                System.out.println("❌ NO HAY TOKEN DISPONIBLE");
            }
            
            return chain.proceed(requestBuilder.build());
        };
        
        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    
    @Provides
    @Singleton
    public RitmoFitApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(RitmoFitApiService.class);
    }
    
}


