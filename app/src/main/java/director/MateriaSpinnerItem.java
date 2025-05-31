package director;

public class MateriaSpinnerItem {
    public int id;
    public String nombre;

    public MateriaSpinnerItem(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
