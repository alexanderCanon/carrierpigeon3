package com.principal.cp.chat;

public class Mensaje {

    private int emisor;        // ID del usuario que envía el mensaje
    private String mensaje;    // Contenido del mensaje
    private long timestamp;    // Fecha/hora en milisegundos

    public Mensaje() {
        // Constructor vacío necesario para Firebase
    }

    public Mensaje(int emisor, String mensaje, long timestamp) {
        this.emisor = emisor;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
    }

    public int getEmisor() {
        return emisor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
