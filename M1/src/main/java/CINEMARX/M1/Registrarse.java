package CINEMARX.M1;

public class Registrarse {
    private String nombre;
    private String correo;
    private String contrasena;

    public Registrarse(String nombre, String correo, String contrasena) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public UsuarioCliente crearUsuario() {
        System.out.println("Usuario registrado correctamente: " + nombre);
        return new UsuarioCliente(nombre, correo, contrasena, "2025-10-28");
    }
}




