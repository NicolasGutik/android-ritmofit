package com.uade.exercises.home.model;

/**
 * Modelo de dominio para representar un Pokémon.
 * Este modelo es independiente del modelo de la API.
 */
public class Pokemon {
    private String name;
    private String url;
    private int id;
    
    public Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
        // Extraer el ID del Pokémon de la URL
        String[] urlParts = url.split("/");
        try {
            this.id = Integer.parseInt(urlParts[urlParts.length - 1]);
        } catch (NumberFormatException e) {
            this.id = 0;
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getUrl() {
        return url;
    }
    
    public int getId() {
        return id;
    }
    
    public String getImageUrl() {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
    }
    
    public String getFormattedName() {
        if (name != null && !name.isEmpty()) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return "";
    }
}
