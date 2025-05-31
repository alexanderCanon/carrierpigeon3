package com.principal.cp.maestros;

public class AlumnoAviso {
    public int id;
    public String nombre;
    public String apellido;
    public boolean seleccionado;

    public AlumnoAviso(int id, String nombre, String apellido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.seleccionado = false;
    }
}
