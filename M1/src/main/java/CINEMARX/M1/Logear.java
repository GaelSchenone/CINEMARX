package CINEMARX.M1;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;

public class Logear {
    private ArrayList<UsuarioCliente> usuariosRegistrados;
    private UsuarioCliente usuarioActual;
    private DatabaseHelper dbHelper;
    
    public Logear(Connection connection) {
        usuariosRegistrados = new ArrayList<>();
        this.dbHelper = new DatabaseHelper(connection);
    }

    // ================= REGISTRAR USUARIO =================
    public boolean registrarUsuario(String nombre, String apellido, String correo, String contrasena, int DNI, Date fechaNacimiento) {
        // Validar que sea @gmail.com
        if (!correo.toLowerCase().endsWith("@gmail.com")) {
            return false; // correo no válido
        }
        
        // Intentar registrar en la base de datos
        return dbHelper.registrarCliente(nombre, apellido, correo, contrasena, DNI, fechaNacimiento);
    }

    // ================= INICIAR SESIÓN =================
    public boolean iniciarSesion(String correo, String contrasena) {
        // Validar credenciales contra la base de datos
        UsuarioCliente usuario = dbHelper.validarCredenciales(correo, contrasena);
        
        if (usuario != null) {
            usuarioActual = usuario;
            return true;
        }
        
        return false;
    }

    public UsuarioCliente getUsuarioActual() {
        return usuarioActual;
    }
    
    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }
}