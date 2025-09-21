package com.uade.exercises.home.model.api;

import com.google.gson.annotations.SerializedName;

public class PokemonDetailResponse {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("order")
    private int order;
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getOrder() {
        return order;
    }
}
