package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;

public class UserDTO {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("apellido")
    private String apellido;
    
    @SerializedName("telefono")
    private String telefono;
    
    @SerializedName("foto")
    private String foto;
    
    // Constructores
    public UserDTO() {}
    
    public UserDTO(Long id, String email, String nombre, String apellido, 
                   String telefono, String foto) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.foto = foto;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", telefono='" + telefono + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }
}
