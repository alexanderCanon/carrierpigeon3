package com.principal.cp.maestros;

public class Materia {
    private final String nombre;
    private final String grado;
    private final String seccion;
    public int id_asignacion;


    public Materia(int id_asignacion, String nombre, String grado, String seccion) {
        this.id_asignacion = id_asignacion;
        this.nombre = nombre;
        this.grado = grado;
        this.seccion = seccion;
        }

    public Materia(String nombre, String grado, String seccion) {
        this.nombre = nombre;
        this.grado = grado;
        this.seccion = seccion;
    }

    @Override
    public String toString() {
        return nombre + " - " + grado + " " + seccion;
    }

    public int getId_asignacion() {
        return id_asignacion;
    }

    public void setId_asignacion(int id_asignacion) {
        this.id_asignacion = id_asignacion;
    }

    public String getNombre() { return nombre; }
    public String getGrado() { return grado; }
    public String getSeccion() { return seccion; }
}



