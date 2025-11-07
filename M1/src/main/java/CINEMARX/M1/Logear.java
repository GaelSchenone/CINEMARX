package CINEMARX.M1;

import java.util.ArrayList;

public class Logear {
    private ArrayList<UsuarioCliente> usuariosRegistrados;
    private UsuarioCliente usuarioActual;

    public Logear() {
        usuariosRegistrados = new ArrayList<>();
    }

    // ================= REGISTRAR USUARIO =================
public boolean registrarUsuario(String nombre, String correo, String contrasena) {
    // Validar si correo ya existe
    for (UsuarioCliente u : usuariosRegistrados) {
        if (u.getCorreo().equalsIgnoreCase(correo)) {
            return false; // correo duplicado
        }
    }

    // Validar que sea @gmail.com
    if (!correo.toLowerCase().endsWith("@gmail.com")) {
        return false; // correo no válido
    }

    // Crear usuario
    UsuarioCliente nuevo = new UsuarioCliente(nombre, correo, contrasena, null);
    usuariosRegistrados.add(nuevo);
    return true;
}


    // ================= INICIAR SESIÓN =================
    public boolean iniciarSesion(String correo, String contrasena) {
        for (UsuarioCliente u : usuariosRegistrados) {
            if (u.getCorreo().equalsIgnoreCase(correo) && u.getContrasena().equals(contrasena)) {
                usuarioActual = u;
                return true;
            }
        }
        return false;
    }

    public UsuarioCliente getUsuarioActual() {
        return usuarioActual;
    }
}
