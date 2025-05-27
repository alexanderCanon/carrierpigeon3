package com.principal.cp.maestros;

public class Actividad {
    public int id;
    public int id_materia;
    public String titulo;
    public String descripcion;
    public String tipo;
    public String fecha_entrega;

    public Actividad(int id, int id_materia, String titulo, String descripcion, String tipo, String fecha_entrega) {
        this.id = id;
        this.id_materia = id_materia;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fecha_entrega = fecha_entrega;
    }
}

