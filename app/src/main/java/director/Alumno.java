package director;

public class Alumno {
    private int id;
    private String nombre;
    private String grado;
    private String seccion;

    public Alumno(int id, String nombre, String grado, String seccion) {
        this.id = id;
        this.nombre = nombre;
        this.grado = grado;
        this.seccion = seccion;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getGrado() { return grado; }
    public String getSeccion() { return seccion; }
}
