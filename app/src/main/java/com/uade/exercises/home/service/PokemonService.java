package com.uade.exercises.home.service;

import com.uade.exercises.home.model.Pokemon;
import com.uade.exercises.home.model.PokemonDetail;

import java.util.List;

public interface PokemonService {
    
    /**
     * Obtiene una lista de Pokémon
     * @param callback Callback para manejar el resultado
     */
    void getPokemonList(PokemonListCallback callback);
    
    /**
     * Obtiene los detalles de un Pokémon específico
     * @param pokemonId ID del Pokémon
     * @param callback Callback para manejar el resultado
     */
    void getPokemonDetail(int pokemonId, PokemonDetailCallback callback);
    
    interface PokemonListCallback {
        void onSuccess(List<Pokemon> pokemonList);
        void onError(String errorMessage);
    }
    
    interface PokemonDetailCallback {
        void onSuccess(PokemonDetail pokemonDetail);
        void onError(String errorMessage);
    }
}
