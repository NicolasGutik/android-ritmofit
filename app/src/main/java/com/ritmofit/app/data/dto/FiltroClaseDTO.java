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
    
    @SerializedName("pagina")
    private Integer pagina = 0;
    
    @SerializedName("tamanio")
    private Integer tamanio = 10;
    
    // Constructores
    public FiltroClaseDTO() {}
    
    public FiltroClaseDTO(String sede, String disciplina, String fechaDesde, 
                         String fechaHasta, Integer pagina, Integer tamanio) {
        this.sede = sede;
        this.disciplina = disciplina;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.pagina = pagina;
        this.tamanio = tamanio;
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
    
    public Integer getPagina() { return pagina; }
    public void setPagina(Integer pagina) { this.pagina = pagina; }
    
    public Integer getTamanio() { return tamanio; }
    public void setTamanio(Integer tamanio) { this.tamanio = tamanio; }
    
    @Override
    public String toString() {
        return "FiltroClaseDTO{" +
                "sede='" + sede + '\'' +
                ", disciplina='" + disciplina + '\'' +
                ", fechaDesde='" + fechaDesde + '\'' +
                ", fechaHasta='" + fechaHasta + '\'' +
                ", pagina=" + pagina +
                ", tamanio=" + tamanio +
                '}';
    }
}


