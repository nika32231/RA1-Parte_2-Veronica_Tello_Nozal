import java.io.Serializable;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    // Nombre del cliente
    private String nombre;


    public Cliente(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el nombre del cliente.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el nombre del cliente.
     * @param nombre Nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Representación en texto del cliente (útil para imprimir).
     */
    @Override
    public String toString() {
        return "Cliente: " + nombre;
    }
}