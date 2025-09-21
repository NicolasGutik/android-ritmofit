package com.uade.exercises.home.repository;

import com.uade.exercises.home.model.Pokemon;
import com.uade.exercises.home.model.PokemonDetail;

import java.util.List;

public interface PokemonRepository {
    
    /**
     * Obtiene una lista de Pokémon
     * @param limit Cantidad máxima de Pokémon a obtener
     * @param offset Posición desde donde comenzar a obtener Pokémon
     * @param callback Callback para manejar el resultado
     */
    void getPokemonList(int limit, int offset, PokemonListCallback callback);
    
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
