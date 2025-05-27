package director;

public class MaestroSeleccionable {
    private int id;
    private String nombre;
    private boolean seleccionado;

    public MaestroSeleccionable(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.seleccionado = false;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
}
