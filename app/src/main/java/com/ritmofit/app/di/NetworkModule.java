package com.ritmofit.app.di;

import android.content.Context;
import android.util.Log;

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

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    
    private static final String TAG = "NetworkModule";
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // Para emulador Android
    // Para dispositivo físico usar: "http://[TU_IP_LOCAL]:8080/"
    
    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(@ApplicationContext Context context) {
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
            
            // TODO: Obtener token desde EncryptedSharedPreferences
            // Por ahora, no agregamos token
            Request newRequest = originalRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .build();
            
            return chain.proceed(newRequest);
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
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    @Provides
    @Singleton
    public RitmoFitApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(RitmoFitApiService.class);
    }
}


