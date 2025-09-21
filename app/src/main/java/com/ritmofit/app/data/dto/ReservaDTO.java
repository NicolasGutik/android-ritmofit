package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;

public class ReservaDTO {
    @SerializedName("claseId")
    private Long claseId;
    
    // Constructores
    public ReservaDTO() {}
    
    public ReservaDTO(Long claseId) {
        this.claseId = claseId;
    }
    
    // Getters y Setters
    public Long getClaseId() { return claseId; }
    public void setClaseId(Long claseId) { this.claseId = claseId; }
    
    @Override
    public String toString() {
        return "ReservaDTO{" +
                "claseId=" + claseId +
                '}';
    }
}
