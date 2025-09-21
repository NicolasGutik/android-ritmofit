package com.uade.exercises.di;

import android.content.Context;

import com.uade.exercises.auth.repository.TokenRepository;
import com.uade.exercises.auth.repository.TokenRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class TokenModule {
    @Provides
    @Singleton
    public TokenRepository provideTokenRepository(@ApplicationContext Context context) {
        return new TokenRepositoryImpl(context);
    }
}
