package com.principal.cp.padres;

public class MateriaHijo {
    private int idAsignacion;
    private String nombreMateria;
    private String grado;
    private String seccion;

    public MateriaHijo(int idAsignacion, String nombreMateria, String grado, String seccion) {
        this.idAsignacion = idAsignacion;
        this.nombreMateria = nombreMateria;
        this.grado = grado;
        this.seccion = seccion;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public String getGrado() {
        return grado;
    }

    public String getSeccion() {
        return seccion;
    }
}
