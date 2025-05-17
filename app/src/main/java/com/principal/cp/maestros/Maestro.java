package com.principal.cp.maestros;

public class Maestro {
    private int id_usuario;
    private String nombre;
    private String apellido;
    private String correo;

    public Maestro(int id_usuario, String nombre, String apellido, String correo) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
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

    public String getCorreo() {
        return correo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
