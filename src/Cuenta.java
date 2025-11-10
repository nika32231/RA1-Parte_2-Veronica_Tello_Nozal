import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cuenta implements Serializable {
    private static final long serialVersionUID = 1L;

    private Cliente cliente;
    // Lista de movimientos (historial)
    private ArrayList<Movimiento> movimientos;
    private double saldo;

    public Cuenta(Cliente cliente) {
        this.cliente = cliente;
        this.movimientos = new ArrayList<>();
        this.saldo = 0.0;
    }

    public void ingresar(double cantidad) {
        if (cantidad > 0) {
            movimientos.add(new Movimiento("Ingreso", cantidad));
            saldo += cantidad;
            System.out.println("Ingreso realizado correctamente: " + String.format("%.2f", cantidad) + " €");
        } else {
            System.out.println("La cantidad debe ser positiva.");
        }
    }

    public void retirar(double cantidad) {
        if (cantidad > 0) {
            if (cantidad <= saldo) {
                movimientos.add(new Movimiento("Retirada", cantidad));
                saldo -= cantidad;
                System.out.println("Retirada realizada correctamente: " + String.format("%.2f", cantidad) + " €");
            } else {
                System.out.println("No hay saldo suficiente para retirar esa cantidad.");
            }
        } else {
            System.out.println("La cantidad debe ser positiva.");
        }
    }

    /** Devuelve el saldo actual de la cuenta. */
    public double getSaldo() {
        return saldo;
    }

    /** Devuelve la lista de movimientos (historial). */
    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    /** Devuelve el cliente (titular). */
    public Cliente getCliente() {
        return cliente;
    }

    /** Cambia el titular de la cuenta. */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }


    public void addMovimiento(Movimiento m) {
        if (m != null) {
            movimientos.add(m);
            if ("Ingreso".equalsIgnoreCase(m.getTipo())) {
                saldo += m.getCantidad();
            } else if ("Retirada".equalsIgnoreCase(m.getTipo())) {
                saldo -= m.getCantidad();
            }
        }
    }
}