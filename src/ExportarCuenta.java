// Exporta un objeto Cuenta a CSV, XML y JSON.

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportarCuenta {

    // Exporta la cuenta a CSV (separador ';'), incluye movimientos y resumen.
    public static void exportarCSV(Cuenta cuenta, Path path) throws IOException {
        DecimalFormat df = new DecimalFormat("#.##");

        //listado de movimientos (ingresos y retiradas
        List<Movimiento> movimientos = cuenta.getMovimientos();
        double totalIngresos = 0;
        double totalRetiradas = 0;
        for (Movimiento m : movimientos) {
            if ("Ingreso".equalsIgnoreCase(m.getTipo())) totalIngresos += m.getCantidad();
            else if ("Retirada".equalsIgnoreCase(m.getTipo())) totalRetiradas += m.getCantidad();
        }

        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            // Cabecera con información de la cuenta
            w.write("Cliente;" + escapeCsv(cuenta.getCliente().getNombre()));
            w.newLine();
            w.write("Saldo;" + df.format(cuenta.getSaldo()));
            w.newLine();
            w.write("TotalMovimientos;" + movimientos.size());
            w.newLine();
            w.newLine();

            // Cabecera de movimientos
            w.write("Index;Tipo;Cantidad;Fecha");
            w.newLine();

            for (int i = 0; i < movimientos.size(); i++) {
                Movimiento m = movimientos.get(i);
                w.write(String.format("%d;%s;%s;%s",
                        i + 1,
                        escapeCsv(m.getTipo()),
                        df.format(m.getCantidad()),
                        escapeCsv(m.getFecha())
                ));
                w.newLine();
            }

            // Resumen como comentario al final
            w.newLine();
            w.write(String.format("# Resumen;Ingresos=%s;Retiradas=%s",
                    df.format(totalIngresos), df.format(totalRetiradas)));
            w.newLine();
        }
    }

    // Exporta la cuenta a XML (estructura clara con metadata y lista de movimientos).
    public static void exportarXML(Cuenta cuenta, Path path) throws IOException {
        DecimalFormat df = new DecimalFormat("#.##");
        List<Movimiento> movimientos = cuenta.getMovimientos();

        double totalIngresos = 0;
        double totalRetiradas = 0;
        for (Movimiento m : movimientos) {
            if ("Ingreso".equalsIgnoreCase(m.getTipo())) totalIngresos += m.getCantidad();
            else if ("Retirada".equalsIgnoreCase(m.getTipo())) totalRetiradas += m.getCantidad();
        }

        DateTimeFormatter fechaFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaHoy = LocalDate.now().format(fechaFmt);

        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            w.newLine();
            w.write("<cuenta>");
            w.newLine();

            w.write("  <metadata>");
            w.newLine();
            w.write("    <fecha>" + escapeXml(fechaHoy) + "</fecha>");
            w.newLine();
            w.write("    <cliente>" + escapeXml(cuenta.getCliente().getNombre()) + "</cliente>");
            w.newLine();
            w.write("    <saldo>" + df.format(cuenta.getSaldo()) + "</saldo>");
            w.newLine();
            w.write("    <totalMovimientos>" + movimientos.size() + "</totalMovimientos>");
            w.newLine();
            w.write("  </metadata>");
            w.newLine();

            w.write("  <movimientos>");
            w.newLine();
            for (Movimiento m : movimientos) {
                w.write(String.format("    <movimiento tipo=\"%s\">", escapeXml(m.getTipo())));
                w.newLine();
                w.write("      <cantidad>" + df.format(m.getCantidad()) + "</cantidad>");
                w.newLine();
                w.write("      <fecha>" + escapeXml(m.getFecha()) + "</fecha>");
                w.newLine();
                w.write("    </movimiento>");
                w.newLine();
            }
            w.write("  </movimientos>");
            w.newLine();

            w.write("  <resumen>");
            w.newLine();
            w.write("    <ingresos>" + df.format(totalIngresos) + "</ingresos>");
            w.newLine();
            w.write("    <retiradas>" + df.format(totalRetiradas) + "</retiradas>");
            w.newLine();
            w.write("  </resumen>");
            w.newLine();

            w.write("</cuenta>");
            w.newLine();
        }
    }

    // Exporta la cuenta a JSON (con formato legible y estadísticas).
    public static void exportarJSON(Cuenta cuenta, Path path) throws IOException {
        DecimalFormat df = new DecimalFormat("#.##");
        List<Movimiento> movimientos = cuenta.getMovimientos();

        double totalIngresos = 0;
        double totalRetiradas = 0;
        for (Movimiento m : movimientos) {
            if ("Ingreso".equalsIgnoreCase(m.getTipo())) totalIngresos += m.getCantidad();
            else if ("Retirada".equalsIgnoreCase(m.getTipo())) totalRetiradas += m.getCantidad();
        }

        DateTimeFormatter fechaFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaHoy = LocalDate.now().format(fechaFmt);

        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write("{");
            w.newLine();
            w.write("  \"cuenta\": {");
            w.newLine();

            // Metadata
            w.write("    \"metadata\": {");
            w.newLine();
            w.write("      \"fecha\": \"" + escapeJson(fechaHoy) + "\",");
            w.newLine();
            w.write("      \"cliente\": \"" + escapeJson(cuenta.getCliente().getNombre()) + "\",");
            w.newLine();
            w.write("      \"saldo\": " + df.format(cuenta.getSaldo()) + ",");
            w.newLine();
            w.write("      \"totalMovimientos\": " + movimientos.size());
            w.newLine();
            w.write("    },");
            w.newLine();

            // Movimientos
            w.write("    \"movimientos\": [");
            w.newLine();
            for (int i = 0; i < movimientos.size(); i++) {
                Movimiento m = movimientos.get(i);
                w.write("      {");
                w.newLine();
                w.write("        \"tipo\": \"" + escapeJson(m.getTipo()) + "\",");
                w.newLine();
                w.write("        \"cantidad\": " + df.format(m.getCantidad()) + ",");
                w.newLine();
                w.write("        \"fecha\": \"" + escapeJson(m.getFecha()) + "\"");
                w.newLine();
                w.write("      }" + (i < movimientos.size() - 1 ? "," : ""));
                w.newLine();
            }
            w.write("    ],");
            w.newLine();

            // Estadísticas
            w.write("    \"estadisticas\": {");
            w.newLine();
            w.write("      \"ingresosTotales\": " + df.format(totalIngresos) + ",");
            w.newLine();
            w.write("      \"retiradasTotales\": " + df.format(totalRetiradas) + ",");
            w.newLine();
            w.write("      \"saldo\": " + df.format(cuenta.getSaldo()));
            w.newLine();
            w.write("    }");
            w.newLine();

            w.write("  }");
            w.newLine();
            w.write("}");
            w.newLine();
        }
    }

    // Pequeñas utilidades de escape (CSV, XML, JSON)
    private static String escapeCsv(String s) {
        if (s == null) return "";
        // Se reemplazan ';' por ',' para no romper CSV y quotes si existen
        return s.replace(";", ",");
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}