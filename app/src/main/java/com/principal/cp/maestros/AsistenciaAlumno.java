package com.principal.cp.maestros;

public class AsistenciaAlumno {
    private int id;
    private String nombre;
    private boolean presente;
    private String observaciones;

    public AsistenciaAlumno(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.presente = true; // por defecto está presente
        this.observaciones = "";
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
