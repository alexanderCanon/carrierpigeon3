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

    public int getId_actividad() {
        return id_actividad;
    }

    public void setId_actividad(int id_actividad) {
        this.id_actividad = id_actividad;
    }

    public int getId_asignacion() {
        return id_asignacion;
    }

    public void setId_asignacion(int id_asignacion) {
        this.id_asignacion = id_asignacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(String fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }
}