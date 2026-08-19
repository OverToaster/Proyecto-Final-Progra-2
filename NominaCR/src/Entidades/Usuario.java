package Entidades;

/**
 * Representa una cuenta de acceso al sistema (login).
 */
public class Usuario {

    private int id;
    private String username, password, rol;

    public Usuario() {
        limpiar();
    }

    public final void limpiar() {
        this.id = 0;
        this.username = "";
        this.password = "";
        this.rol = "";
    }

    @Override
    public String toString() {
        return "Usuario id=" + this.id + " Username=" + this.username + " Rol=" + this.rol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
