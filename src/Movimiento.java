import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Movimiento implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tipo; // "Ingreso" o "Retirada"
    // Importe del movimiento
    private double cantidad;
    // Fecha y hora en que se creó el movimiento (formato yyyy-MM-dd HH:mm:ss)
    private String fecha;

    public Movimiento(String tipo, double cantidad) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /** Devuelve el tipo del movimiento. */
    public String getTipo() {
        return tipo;
    }

    /** Devuelve la cantidad del movimiento. */
    public double getCantidad() {
        return cantidad;
    }

    /** Devuelve la fecha (cadena) del movimiento. */
    public String getFecha() {
        return fecha;
    }

    /**
     * Representación legible del movimiento: fecha - tipo: cantidad €
     */
    @Override
    public String toString() {
        return fecha + " - " + tipo + ": " + String.format("%.2f", cantidad) + " €";
    }
}