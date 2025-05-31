package director;
public class AgregarMateria {
    public int id_materia;
    public String nombre;
    public String descripcion;

    public AgregarMateria(int id_materia, String nombre, String descripcion) {
        this.id_materia = id_materia;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}
