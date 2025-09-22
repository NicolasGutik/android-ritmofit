package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;

public class FiltroClaseDTO {
    @SerializedName("sede")
    private String sede;
    
    @SerializedName("disciplina")
    private String disciplina;
    
    @SerializedName("fechaDesde")
    private String fechaDesde;
    
    @SerializedName("fechaHasta")
    private String fechaHasta;
    
    @SerializedName("page")
    private Integer page = 0;
    
    @SerializedName("size")
    private Integer size = 10;
    
    // Constructores
    public FiltroClaseDTO() {}
    
    public FiltroClaseDTO(String sede, String disciplina, String fechaDesde, 
                         String fechaHasta, Integer page, Integer size) {
        this.sede = sede;
        this.disciplina = disciplina;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.page = page;
        this.size = size;
    }
    
    // Getters y Setters
    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }
    
    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }
    
    public String getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(String fechaDesde) { this.fechaDesde = fechaDesde; }
    
    public String getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(String fechaHasta) { this.fechaHasta = fechaHasta; }
    
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    
    @Override
    public String toString() {
        return "FiltroClaseDTO{" +
                "sede='" + sede + '\'' +
                ", disciplina='" + disciplina + '\'' +
                ", fechaDesde='" + fechaDesde + '\'' +
                ", fechaHasta='" + fechaHasta + '\'' +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}


