package com.principal.cp.maestros;

public class Actividad {
    public int id_actividad;
    public int id_asignacion;
    public String titulo;
    public String descripcion;
    public String tipo;
    public String fecha_entrega;

    public Actividad(int id_actividad, int id_asignacion, String titulo, String descripcion, String tipo, String fecha_entrega) {
        this.id_actividad = id_actividad;
        this.id_asignacion = id_asignacion;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fecha_entrega = fecha_entrega;
    }
}