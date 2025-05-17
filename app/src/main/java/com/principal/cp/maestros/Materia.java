package com.principal.cp.maestros;

public class Materia {
    private final String nombre;
    private final String grado;
    private final String seccion;
    public int id_materia;


    public Materia(String nombre, String grado, String seccion) {
        this.nombre = nombre;
        this.grado = grado;
        this.seccion = seccion;
        }
    @Override
    public String toString() {
        return nombre; // Esto es lo que se mostrará en el Spinner
    }

    public String getNombre() { return nombre; }
    public String getGrado() { return grado; }
    public String getSeccion() { return seccion; }
}
