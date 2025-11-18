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
    public OkHttpClient provideOkHttpClient(
            @ApplicationContext Context context,
            SharedPreferences encryptedPrefs // <-- VIENE del StorageModule
    ) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, message));
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS); // para ver Authorization

        Interceptor authInterceptor = chain -> {
            Request original = chain.request();

            String token = encryptedPrefs.getString("jwt_token", null); // misma key que usa AuthRepositoryImpl
            Request.Builder rb = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");

            if (token != null && !token.isEmpty()) {
                rb.header("Authorization", "Bearer " + token);
                Log.d(TAG, "✅ JWT agregado a: " + original.url());
                Log.d(TAG, "🔑 Token head: " + token.substring(0, Math.min(20, token.length())) + "...");
            } else {
                Log.d(TAG, "❌ No hay JWT para: " + original.url());
            }

            return chain.proceed(rb.build());
        };

        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)     // agrega el Bearer
                .addInterceptor(logging)             // imprime headers (incluye Authorization)
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


