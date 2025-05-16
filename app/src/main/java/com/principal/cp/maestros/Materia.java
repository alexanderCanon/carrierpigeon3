package com.principal.cp.maestros;

public class Materia {
    public int id_materia;
    public String nombre;

    @Override
    public String toString() {
        return nombre; // Esto es lo que se mostrará en el Spinner
    }
}
