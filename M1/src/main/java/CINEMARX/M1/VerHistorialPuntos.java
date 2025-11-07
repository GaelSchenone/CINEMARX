package CINEMARX.M1;

import javax.swing.JTextArea;

public class VerHistorialPuntos {
    private String movimientos;
    private int puntosAcumulados;

    public VerHistorialPuntos(String movimientos, int puntosAcumulados) {
        this.movimientos = movimientos;
        this.puntosAcumulados = puntosAcumulados;
    }

    public void mostrarHistorialPuntos(JTextArea area) {
        area.append("Historial de puntos:\n");
        area.append("- Movimientos: " + movimientos + "\n");
        area.append("- Puntos acumulados: " + puntosAcumulados + "\n\n");
    }
}


