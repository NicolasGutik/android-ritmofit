package com.uade.exercises.home.service;

import com.uade.exercises.home.model.Pokemon;
import com.uade.exercises.home.model.PokemonDetail;
import com.uade.exercises.home.repository.PokemonRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PokemonServiceImpl implements PokemonService {
    
    private final PokemonRepository pokemonRepository;
    private static final int DEFAULT_LIMIT = 20;
    private static final int DEFAULT_OFFSET = 0;
    
    @Inject
    public PokemonServiceImpl(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }
    
    @Override
    public void getPokemonList(PokemonListCallback callback) {
        pokemonRepository.getPokemonList(DEFAULT_LIMIT, DEFAULT_OFFSET, new PokemonRepository.PokemonListCallback() {
            @Override
            public void onSuccess(List<Pokemon> pokemonList) {
                callback.onSuccess(pokemonList);
            }
            
            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    @Override
    public void getPokemonDetail(int pokemonId, PokemonDetailCallback callback) {
        pokemonRepository.getPokemonDetail(pokemonId, new PokemonRepository.PokemonDetailCallback() {
            @Override
            public void onSuccess(PokemonDetail pokemonDetail) {
                callback.onSuccess(pokemonDetail);
            }
            
            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}
