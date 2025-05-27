package com.principal.cp.maestros;

public class ActividadNota {
    public int id_actividad;
    public String titulo;
    public String fecha_entrega;

    public ActividadNota(int id, String titulo, String fecha) {
        this.id_actividad = id;
        this.titulo = titulo;
        this.fecha_entrega = fecha;
    }

    @Override
    public String toString() {
        return titulo + " (" + fecha_entrega + ")";
    }
}
