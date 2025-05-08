package com.principal.cp;
public class AnuncioPrueba {
    private int id;
    private String titulo;
    private String contenido;
    private String fechaPublicacion;

    public AnuncioPrueba(int id, String titulo, String contenido, String fechaPublicacion) {
        this.id = id;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }
}

