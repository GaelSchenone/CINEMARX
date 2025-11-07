package CINEMARX.M1;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UsuarioCliente {
    private String nombre;
    private String correo;
    private String contrasena;
    private String fechaRegistro;
    private boolean esVIP;

    private RolVIP rolVIP;
    private VerHistorial historial;

    // ==================== CONSTRUCTOR ====================
    public UsuarioCliente(String nombre, String correo, String contrasena, String fechaRegistro) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;

        if (fechaRegistro == null || fechaRegistro.isEmpty()) {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            this.fechaRegistro = LocalDate.now().format(formato);
        } else {
            this.fechaRegistro = fechaRegistro;
        }

        this.esVIP = false;
    }

    // ==================== GETTERS ====================
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
    public boolean isEsVIP() { return esVIP; }
    public RolVIP getRolVIP() { return rolVIP; }
    public VerHistorial getHistorial() { return historial; }
    public String getFechaRegistro() { return fechaRegistro; }

    // ==================== SETTERS ====================
    public void setNombre(String nuevoNombre) { this.nombre = nuevoNombre; }
    public void setCorreo(String nuevoCorreo) { this.correo = nuevoCorreo; }
    public void setContrasena(String nuevaPass) { this.contrasena = nuevaPass; }

    // ==================== MÉTODOS ====================
    public void activarVIP(RolVIP rolVIP) {
        this.rolVIP = rolVIP;
        this.esVIP = true;
    }

    public void setHistorial(VerHistorial historial) {
        this.historial = historial;
    }
}
