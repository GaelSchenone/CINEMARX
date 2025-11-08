package CINEMARX.M1;
import java.sql.*;

public class Registrarse {
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private int DNI;
    private Date FechaNacimiento;
    

    public Registrarse(String nombre, String correo, String contrasena, int DNI, Date FechaNacimiento) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.DNI = DNI;
        this.FechaNacimiento = FechaNacimiento;
    }

    public UsuarioCliente crearUsuario() {
        System.out.println("Usuario registrado correctamente: " + nombre);
        return new UsuarioCliente(nombre, apellido, correo, contrasena, DNI, FechaNacimiento);
    }
}




