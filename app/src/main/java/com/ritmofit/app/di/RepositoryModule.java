package com.ritmofit.app.di;

import android.content.SharedPreferences;

import com.ritmofit.app.data.api.RitmoFitApiService;
import com.ritmofit.app.data.repository.AuthRepository;
import com.ritmofit.app.data.repository.AuthRepositoryImpl;
import com.ritmofit.app.data.repository.ClaseRepository;
import com.ritmofit.app.data.repository.ClaseRepositoryImpl;
import com.ritmofit.app.data.repository.ReservaRepository;
import com.ritmofit.app.data.repository.ReservaRepositoryImpl;
import com.ritmofit.app.data.repository.UserRepository;
import com.ritmofit.app.data.repository.UserRepositoryImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {
    
    @Binds
    @Singleton
    public abstract AuthRepository bindAuthRepository(AuthRepositoryImpl authRepositoryImpl);

    @Binds
    @Singleton
    public abstract UserRepository bindUserRepository(UserRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract ClaseRepository bindClaseRepository(ClaseRepositoryImpl claseRepositoryImpl);
    
    @Binds
    @Singleton
    public abstract ReservaRepository bindReservaRepository(ReservaRepositoryImpl reservaRepositoryImpl);
}


