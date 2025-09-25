package com.ritmofit.app.model;


public  class User {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String telefono;

    private String foto;

    public User(Long id, String email, String firstName, String lastName,
                        String telefono, String foto) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.telefono = telefono;
        this.foto = foto;
    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getFoto() {
        return foto;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

}
