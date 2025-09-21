package com.uade.exercises.home.model;

/**
 * Modelo de dominio para representar los detalles de un Pokémon.
 * Este modelo es independiente del modelo de la API.
 */
public class PokemonDetail {
    private int id;
    private String name;
    private int order;
    
    public PokemonDetail(int id, String name, int order) {
        this.id = id;
        this.name = name;
        this.order = order;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getOrder() {
        return order;
    }
    
    public String getFormattedName() {
        if (name != null && !name.isEmpty()) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return "";
    }
    
    public String getImageUrl() {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
    }
}
