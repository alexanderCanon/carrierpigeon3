package com.principal.cp.maestros;

public class Maestro {
    private int id_usuario;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String dpi;
    private String estado;

    public Maestro(int id_usuario, String nombre, String apellido, String email, String telefono, String dpi, String estado) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.dpi = dpi;
        this.estado = estado;
    }

    public int getIdUsuario() {
        return id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDpi() {
        return dpi;
    }

    public String getEstado() {
        return estado;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
