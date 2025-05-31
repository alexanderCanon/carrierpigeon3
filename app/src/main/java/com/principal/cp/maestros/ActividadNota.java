package com.principal.cp.maestros;

public class ActividadNota {
    public int id_actividad;
    public String titulo;
    public String fecha_entrega;
    public double valor_total; // ← Nuevo campo

    public ActividadNota(int id_actividad, String titulo, String fecha_entrega, double valor_total) {
        this.id_actividad = id_actividad;
        this.titulo = titulo;
        this.fecha_entrega = fecha_entrega;
        this.valor_total = valor_total;
    }

    public double getValor_total() {
        return valor_total;
    }

    public void setValor_total(double valor_total) {
        this.valor_total = valor_total;
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

