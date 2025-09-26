package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;

public class AsistenciaDTO {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("disciplina")
    private String disciplina;
    
    @SerializedName("sede")
    private String sede;
    
    @SerializedName("lugar")
    private String lugar;
    
    @SerializedName("profesor")
    private String profesor;
    
    @SerializedName("fechaClase")
    private String fechaClase;
    
    @SerializedName("fechaAsistencia")
    private String fechaAsistencia;
    
    @SerializedName("estado")
    private String estado;
    
    // Constructores
    public AsistenciaDTO() {}
    
    public AsistenciaDTO(Long id, String disciplina, String sede, String lugar, 
                        String profesor, String fechaClase, String fechaAsistencia, String estado) {
        this.id = id;
        this.disciplina = disciplina;
        this.sede = sede;
        this.lugar = lugar;
        this.profesor = profesor;
        this.fechaClase = fechaClase;
        this.fechaAsistencia = fechaAsistencia;
        this.estado = estado;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }
    
    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }
    
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    
    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }
    
    public String getFechaClase() { return fechaClase; }
    public void setFechaClase(String fechaClase) { this.fechaClase = fechaClase; }
    
    public String getFechaAsistencia() { return fechaAsistencia; }
    public void setFechaAsistencia(String fechaAsistencia) { this.fechaAsistencia = fechaAsistencia; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
