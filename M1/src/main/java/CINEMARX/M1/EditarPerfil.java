package CINEMARX.M1;

import javax.swing.JTextArea;

public class EditarPerfil {
    private String cambiosRealizados;
    private String fechaUltimaEdicion;

    public EditarPerfil(String cambiosRealizados, String fechaUltimaEdicion) {
        this.cambiosRealizados = cambiosRealizados;
        this.fechaUltimaEdicion = fechaUltimaEdicion;
    }

    public void mostrarCambios(JTextArea area) {
        area.append("🛠 Cambios en el perfil:\n");
        area.append("- " + cambiosRealizados + "\n");
        area.append("Fecha: " + fechaUltimaEdicion + "\n\n");
    }
}


