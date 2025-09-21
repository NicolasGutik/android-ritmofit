package com.uade.exercises.home.repository;

import com.uade.exercises.home.model.Pokemon;
import com.uade.exercises.home.model.PokemonDetail;
import com.uade.exercises.home.model.api.PokemonDetailResponse;
import com.uade.exercises.home.model.api.PokemonListResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class PokemonRepositoryImpl implements PokemonRepository {
    
    private final PokemonServiceApi pokemonServiceApi;
    
    @Inject
    public PokemonRepositoryImpl(PokemonServiceApi pokemonServiceApi) {
        this.pokemonServiceApi = pokemonServiceApi;
    }
    
    @Override
    public void getPokemonList(int limit, int offset, PokemonListCallback callback) {
        pokemonServiceApi.getPokemonList(limit, offset).enqueue(new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pokemon> pokemonList = new ArrayList<>();
                    for (PokemonListResponse.PokemonResult result : response.body().getResults()) {
                        pokemonList.add(new Pokemon(result.getName(), result.getUrl()));
                    }
                    callback.onSuccess(pokemonList);
                } else {
                    callback.onError("Error al obtener la lista de Pokémon: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }
    
    @Override
    public void getPokemonDetail(int pokemonId, PokemonDetailCallback callback) {
        pokemonServiceApi.getPokemonDetail(pokemonId).enqueue(new Callback<PokemonDetailResponse>() {
            @Override
            public void onResponse(Call<PokemonDetailResponse> call, Response<PokemonDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonDetailResponse detailResponse = response.body();
                    PokemonDetail pokemonDetail = new PokemonDetail(
                            detailResponse.getId(),
                            detailResponse.getName(),
                            detailResponse.getOrder()
                    );
                    callback.onSuccess(pokemonDetail);
                } else {
                    callback.onError("Error al obtener los detalles del Pokémon: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<PokemonDetailResponse> call, Throwable t) {
                callback.onError("Error de conexión: " + t.getMessage());
            }
        });
    }
}
