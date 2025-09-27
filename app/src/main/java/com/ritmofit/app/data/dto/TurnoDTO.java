package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;

public class TurnoDTO {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("claseId")
    private Long claseId;
    
    @SerializedName("userId")
    private Long userId;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("fechaReserva")
    private String fechaReserva;
    
    @SerializedName("fechaClase")
    private String fechaClase;
    
    @SerializedName("claseFecha")
    private String claseFecha;
    
    @SerializedName("claseSede")
    private String sede;
    
    @SerializedName("claseDisciplina")
    private String disciplina;
    
    @SerializedName("lugar")
    private String lugar;
    
    @SerializedName("profesorNombre")
    private String profesor;
    
    // Constructores
    public TurnoDTO() {}
    
    public TurnoDTO(Long id, Long claseId, Long userId, String estado, 
                   String fechaReserva, String fechaClase, String sede, 
                   String disciplina, String lugar, String profesor) {
        this.id = id;
        this.claseId = claseId;
        this.userId = userId;
        this.estado = estado;
        this.fechaReserva = fechaReserva;
        this.fechaClase = fechaClase;
        this.sede = sede;
        this.disciplina = disciplina;
        this.lugar = lugar;
        this.profesor = profesor;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getClaseId() { return claseId; }
    public void setClaseId(Long claseId) { this.claseId = claseId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(String fechaReserva) { this.fechaReserva = fechaReserva; }
    
    public String getFechaClase() { return fechaClase; }
    public void setFechaClase(String fechaClase) { this.fechaClase = fechaClase; }
    
    public String getClaseFecha() { return claseFecha; }
    public void setClaseFecha(String claseFecha) { this.claseFecha = claseFecha; }
    
    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }
    
    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }
    
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    
    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }
    
    public boolean isConfirmado() {
        return "CONFIRMADO".equals(estado);
    }
    
    public boolean isCancelado() {
        return "CANCELADO".equals(estado);
    }
    
    @Override
    public String toString() {
        return "TurnoDTO{" +
                "id=" + id +
                ", claseId=" + claseId +
                ", userId=" + userId +
                ", estado='" + estado + '\'' +
                ", fechaReserva='" + fechaReserva + '\'' +
                ", fechaClase='" + fechaClase + '\'' +
                ", sede='" + sede + '\'' +
                ", disciplina='" + disciplina + '\'' +
                ", lugar='" + lugar + '\'' +
                ", profesor='" + profesor + '\'' +
                '}';
    }
}


