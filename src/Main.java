import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class Main {
    // Carpeta donde se almacenan datos y exportaciones
    private static final String CARPETA_DATOS = "datos";
    // Archivo donde se serializa la instancia de Cuenta
    private static final String ARCHIVO_CUENTA = CARPETA_DATOS + File.separator + "cuenta.dat";

    public static void main(String[] args) {
        // Scanner para leer entradas del usuario desde consola
        Scanner scanner = new Scanner(System.in);
        // Intentamos cargar una cuenta existente; si no hay, se crea una nueva
        Cuenta cuenta = cargarCuenta();

        if (cuenta == null) {
            // Si no se cargó ninguna cuenta, pedimos nombre y creamos una
            System.out.print("Introduce el nombre del cliente: ");
            String nombre = scanner.nextLine().trim();
            if (nombre.isEmpty()) nombre = "Cliente sin nombre";
            cuenta = new Cuenta(new Cliente(nombre));
            System.out.println("Cuenta nueva creada para: " + cuenta.getCliente().getNombre());
        }

        int opcion;
        do {
            // Menú principal con opciones básicas de gestión de la cuenta
            System.out.println("\n--- Menú ---");
            System.out.println("1. Ingresar dinero");
            System.out.println("2. Retirar dinero");
            System.out.println("3. Consultar saldo");
            System.out.println("4. Ver movimientos");
            System.out.println("5. Exportar a CSV/XML/JSON");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            //si no se introduce un numero del 0 al 5 se entrara a un loop hasta que se haga
            while (!scanner.hasNextInt()) {
                System.out.print("Introduce un número válido para la opción: ");
                scanner.next();
            }
            opcion = scanner.nextInt();
            scanner.nextLine(); // consumir salto de línea

            switch (opcion) {
                case 1:
                    // Pedimos cantidad y realizamos ingreso
                    double ingreso = pedirCantidad(scanner, "Cantidad a ingresar: ");
                    cuenta.ingresar(ingreso);
                    break;
                case 2:
                    // Pedimos cantidad y realizamos retirada
                    double retiro = pedirCantidad(scanner, "Cantidad a retirar: ");
                    cuenta.retirar(retiro);
                    break;
                case 3:
                    // Mostramos el saldo con dos decimales
                    System.out.println("Saldo actual: " + String.format("%.2f", cuenta.getSaldo()) + " €");
                    break;
                case 4:
                    // Listado de movimientos almacenados
                    System.out.println("--- Movimientos ---");
                    if (cuenta.getMovimientos().isEmpty()) {
                        System.out.println("No hay movimientos registrados.");
                    } else {
                        for (Movimiento m : cuenta.getMovimientos()) {
                            System.out.println(m);
                        }
                    }
                    break;
                case 5:
                    // Exportar a archivos legibles
                    exportarArchivos(cuenta);
                    break;
                case 0:
                    // Salir del programa
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }

        } while (opcion != 0);

        // Guardar la cuenta serializada al finalizar
        if (guardarCuenta(cuenta)) {
            System.out.println("Cuenta guardada correctamente en: " + ARCHIVO_CUENTA);
        } else {
            System.out.println("Error al guardar la cuenta serializada.");
        }

        // Exportar automáticamente al salir (CSV, XML, JSON)
        exportarArchivos(cuenta);

        // Cerrar el scanner para liberar recurso
        scanner.close();
    }

    /**
     * Lee una cantidad numérica positiva desde consola. Repite hasta que la entrada es válida.
     */
    private static double pedirCantidad(Scanner scanner, String mensaje) {
        double cantidad = 0.0;
        boolean valido = false;
        while (!valido) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                cantidad = Double.parseDouble(linea);
                if (cantidad <= 0) {
                    System.out.println("La cantidad debe ser un número positivo.");
                } else {
                    valido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Introduce un número (por ejemplo 100 o 12.50).");
            }
        }
        return cantidad;
    }

    /**
     * Intenta cargar la cuenta serializada desde disco. Si la carpeta no existe la crea.
     * Devuelve null si no hay archivo o si ocurre un error.
     */
    private static Cuenta cargarCuenta() {
        File carpeta = new File(CARPETA_DATOS);
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }

        File file = new File(ARCHIVO_CUENTA);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Cuenta) {
                System.out.println("Cuenta cargada correctamente desde: " + file.getAbsolutePath());
                return (Cuenta) obj;
            } else {
                System.out.println("El archivo no contiene una Cuenta válida.");
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar la cuenta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Guarda la cuenta en disco serializada (archivo cuenta.dat). Crea la carpeta si hace falta.
     * Devuelve true si se guardó correctamente.
     */
    private static boolean guardarCuenta(Cuenta cuenta) {
        File carpeta = new File(CARPETA_DATOS);
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CUENTA))) {
            oos.writeObject(cuenta);
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar la cuenta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exporta la cuenta a CSV, XML y JSON dentro de la carpeta de datos.
     * Imprime las rutas de los archivos generados o un error si falla la escritura.
     */
    private static void exportarArchivos(Cuenta cuenta) {
        try {
            Path csv = Paths.get(CARPETA_DATOS, "cuenta.csv");
            Path xml = Paths.get(CARPETA_DATOS, "cuenta.xml");
            Path json = Paths.get(CARPETA_DATOS, "cuenta.json");

            ExportarCuenta.exportarCSV(cuenta, csv);
            ExportarCuenta.exportarXML(cuenta, xml);
            ExportarCuenta.exportarJSON(cuenta, json);

            System.out.println("Exportación completada:");
            System.out.println(" - " + csv.toAbsolutePath());
            System.out.println(" - " + xml.toAbsolutePath());
            System.out.println(" - " + json.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error escribiendo archivos de exportación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}