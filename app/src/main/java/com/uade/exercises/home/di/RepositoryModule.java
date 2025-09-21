package com.uade.exercises.home.di;

import com.uade.exercises.home.repository.PokemonRepository;
import com.uade.exercises.home.repository.PokemonRepositoryImpl;
import com.uade.exercises.home.service.PokemonService;
import com.uade.exercises.home.service.PokemonServiceImpl;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    @Singleton
    public abstract PokemonRepository bindPokemonRepository(PokemonRepositoryImpl pokemonRepositoryImpl);

    @Binds
    @Singleton
    public abstract PokemonService bindPokemonService(PokemonServiceImpl pokemonServiceImpl);
}
