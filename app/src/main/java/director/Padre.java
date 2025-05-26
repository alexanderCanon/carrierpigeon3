package director;

public class Padre {
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String estado;
    private String dpi;

    private int idPadre;
    public Padre(String nombre, String apellido, String email, String telefono, String estado, String dpi) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.estado = estado;
        this.dpi = dpi;
    }

    public Padre(int id, String nombre) {
        this.idPadre = id;
        this.nombre = nombre;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getEstado() { return estado; }
    public String getDpi() { return dpi; }
    public int getId() { return idPadre; }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public void setEstado(String estado) { this.estado = estado;}
    public void setDpi(String dpi) { this.dpi = dpi;}

    @Override
    public String toString() {
        return nombre;
    }

}


