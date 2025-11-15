package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;

public class ClaseDTO {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("sede")
    private String sede;
    
    @SerializedName("lugar")
    private String lugar;
    
    @SerializedName("fecha")
    private String fecha;
    
    @SerializedName("duracion")
    private String duracion;
    
    @SerializedName("disciplina")
    private String disciplina;
    
    @SerializedName("cupos")
    private Integer cupos;
    
    @SerializedName("cuposDisponibles")
    private Integer cuposDisponibles;
    
    @SerializedName("nombreProfesor")
    private String nombreProfesor;
    
    @SerializedName("profesorId")
    private Long profesorId;
    
    // Constructores
    public ClaseDTO() {}
    
    public ClaseDTO(Long id, String sede, String lugar, String fecha, String duracion, 
                   String disciplina, Integer cupos, Integer cuposDisponibles, 
                   String nombreProfesor, Long profesorId) {
        this.id = id;
        this.sede = sede;
        this.lugar = lugar;
        this.fecha = fecha;
        this.duracion = duracion;
        this.disciplina = disciplina;
        this.cupos = cupos;
        this.cuposDisponibles = cuposDisponibles;
        this.nombreProfesor = nombreProfesor;
        this.profesorId = profesorId;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }
    
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    
    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }
    
    public Integer getCupos() { return cupos; }
    public void setCupos(Integer cupos) { this.cupos = cupos; }
    
    public Integer getCuposDisponibles() { return cuposDisponibles; }
    public void setCuposDisponibles(Integer cuposDisponibles) { this.cuposDisponibles = cuposDisponibles; }
    
    public String getNombreProfesor() { return nombreProfesor; }
    public void setNombreProfesor(String nombreProfesor) { this.nombreProfesor = nombreProfesor; }
    
    public Long getProfesorId() { return profesorId; }
    public void setProfesorId(Long profesorId) { this.profesorId = profesorId; }
    
    @Override
    public String toString() {
        return "ClaseDTO{" +
                "id=" + id +
                ", sede='" + sede + '\'' +
                ", lugar='" + lugar + '\'' +
                ", fecha='" + fecha + '\'' +
                ", duracion='" + duracion + '\'' +
                ", disciplina='" + disciplina + '\'' +
                ", cupos=" + cupos +
                ", cuposDisponibles=" + cuposDisponibles +
                ", nombreProfesor='" + nombreProfesor + '\'' +
                ", profesorId=" + profesorId +
                '}';
    }
}


