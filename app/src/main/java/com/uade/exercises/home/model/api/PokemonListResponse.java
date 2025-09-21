package com.uade.exercises.home.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonListResponse {
    
    @SerializedName("count")
    private int count;
    
    @SerializedName("next")
    private String next;
    
    @SerializedName("previous")
    private String previous;
    
    @SerializedName("results")
    private List<PokemonResult> results;
    
    public int getCount() {
        return count;
    }
    
    public String getNext() {
        return next;
    }
    
    public String getPrevious() {
        return previous;
    }
    
    public List<PokemonResult> getResults() {
        return results;
    }
    
    public static class PokemonResult {
        @SerializedName("name")
        private String name;
        
        @SerializedName("url")
        private String url;
        
        public String getName() {
            return name;
        }
        
        public String getUrl() {
            return url;
        }
    }
}
