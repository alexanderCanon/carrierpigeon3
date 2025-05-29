package com.principal.cp.maestros;

public class ActividadNota {
    public int id_actividad;
    public String titulo;
    public String fecha_entrega;

    public ActividadNota(int id_actividad, String titulo, String fecha_entrega) {
        this.id_actividad = id_actividad;
        this.titulo = titulo;
        this.fecha_entrega = fecha_entrega;
    }

    @Override
    public String toString() {
        return titulo + " (" + fecha_entrega + ")";
    }

    public int getId_actividad() {
        return id_actividad;
    }

    public void setId_actividad(int id_actividad) {
        this.id_actividad = id_actividad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(String fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }
}

