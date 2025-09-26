package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;

public class AsistenciaDTO {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("userId")
    private Long userId;
    
    @SerializedName("userEmail")
    private String userEmail;
    
    @SerializedName("userName")
    private String userName;
    
    @SerializedName("claseId")
    private Long claseId;
    
    @SerializedName("claseSede")
    private String claseSede;
    
    @SerializedName("claseDisciplina")
    private String claseDisciplina;
    
    @SerializedName("claseFecha")
    private String claseFecha;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("fechaCreacion")
    private String fechaCreacion;
    
    @SerializedName("asistencia")
    private Boolean asistencia;
    
    // Constructores
    public AsistenciaDTO() {}
    
    public AsistenciaDTO(Long id, Long userId, String userEmail, String userName, 
                        Long claseId, String claseSede, String claseDisciplina, 
                        String claseFecha, String estado, String fechaCreacion, Boolean asistencia) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.claseId = claseId;
        this.claseSede = claseSede;
        this.claseDisciplina = claseDisciplina;
        this.claseFecha = claseFecha;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.asistencia = asistencia;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public Long getClaseId() { return claseId; }
    public void setClaseId(Long claseId) { this.claseId = claseId; }
    
    public String getClaseSede() { return claseSede; }
    public void setClaseSede(String claseSede) { this.claseSede = claseSede; }
    
    public String getClaseDisciplina() { return claseDisciplina; }
    public void setClaseDisciplina(String claseDisciplina) { this.claseDisciplina = claseDisciplina; }
    
    public String getClaseFecha() { return claseFecha; }
    public void setClaseFecha(String claseFecha) { this.claseFecha = claseFecha; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Boolean getAsistencia() { return asistencia; }
    public void setAsistencia(Boolean asistencia) { this.asistencia = asistencia; }
    
    // Métodos de conveniencia para compatibilidad con el adapter
    public String getDisciplina() { return claseDisciplina; }
    public String getSede() { return claseSede; }
    public String getFechaClase() { return claseFecha; }
}
