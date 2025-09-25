package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("email")
    private String email;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("foto")
    private String foto;

    // Constructores
    public UserResponse() {}

    public UserResponse(Long id, String email, String firstName, String lastName,
                   String telefono, String foto) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.telefono = telefono;
        this.foto = foto;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getNombreCompleto() {
        return firstName + " " + lastName;
    }
}
