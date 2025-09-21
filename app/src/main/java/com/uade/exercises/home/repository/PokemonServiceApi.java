package com.uade.exercises.home.repository;

import com.uade.exercises.home.model.api.PokemonDetailResponse;
import com.uade.exercises.home.model.api.PokemonListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokemonServiceApi {
    
    @GET("pokemon")
    Call<PokemonListResponse> getPokemonList(@Query("limit") int limit, @Query("offset") int offset);
    
    @GET("pokemon/{id}")
    Call<PokemonDetailResponse> getPokemonDetail(@Path("id") int pokemonId);
}
