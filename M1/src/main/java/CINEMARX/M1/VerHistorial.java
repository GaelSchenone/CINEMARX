package CINEMARX.M1;

import javax.swing.JTextArea;

public class VerHistorial {
    private String historialTickets;
    private double totalGastado;
    private VerHistorialPuntos historialPuntos;

    public VerHistorial(String historialTickets, double totalGastado, VerHistorialPuntos historialPuntos) {
        this.historialTickets = historialTickets;
        this.totalGastado = totalGastado;
        this.historialPuntos = historialPuntos;
    }

    public void mostrarHistorial(JTextArea area) {
        area.append("🎟️ Tickets: " + historialTickets + "\n");
        area.append("💰 Total gastado: $" + totalGastado + "\n");
        historialPuntos.mostrarHistorialPuntos(area);
    }
}


