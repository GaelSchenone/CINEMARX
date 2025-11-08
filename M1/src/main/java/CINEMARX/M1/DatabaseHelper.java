package CINEMARX.M1;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Clase auxiliar para operaciones de base de datos del módulo M1
 * Gestiona el registro, login y actualización de datos de clientes
 */
public class DatabaseHelper {
    
    private Connection connection;
    
    // Constructor que recibe la conexión
    public DatabaseHelper(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Registra un nuevo cliente en la base de datos
     * @return true si el registro fue exitoso, false si el correo ya existe
     */
    public boolean registrarCliente(String nombre, String apellido, String correo, String contrasena, int DNI, Date FechaNacimiento) {
        // Validar que el correo no exista
        if (existeCorreo(correo)) {
            return false;
        }
        
        // Validar que el DNI no exista
        if (existeDNI(DNI)) {
            return false;
        }
        
        try {
            connection.setAutoCommit(false);
            
            // 1. Crear el usuario en la tabla Usuario
            String sqlUsuario = "INSERT INTO Usuario (DNI, FechaNac, Nombre, Apellido) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlUsuario)) {
                pstmt.setInt(1, DNI);
                pstmt.setDate(2, FechaNacimiento);
                pstmt.setString(3, nombre);
                pstmt.setString(4, apellido); 
                pstmt.executeUpdate();
            }
            
            // 2. Crear el cliente vinculado al usuario
            String sqlCliente = "INSERT INTO Cliente (DNI, Membresia, Mail, Contrasena) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlCliente)) {
                pstmt.setInt(1, DNI);
                pstmt.setString(2, "NO VIP"); // Membresía por defecto
                pstmt.setString(3, correo);
                pstmt.setString(4, contrasena);
                pstmt.executeUpdate();
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Verifica si un correo ya está registrado
     */
    private boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM Cliente WHERE Mail = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Verifica si un DNI ya está registrado
     */
    private boolean existeDNI(int DNI) {
        String sql = "SELECT COUNT(*) FROM Usuario WHERE DNI = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, DNI);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Valida las credenciales de inicio de sesión
     * @return UsuarioCliente si las credenciales son correctas, null si no
     */
    public UsuarioCliente validarCredenciales(String correo, String contrasena) {
        String sql = "SELECT c.ID_Cliente, c.DNI, c.Membresia, c.Mail, c.Contrasena, u.Nombre, u.Apellido, u.FechaNac " +
                     "FROM Cliente c " +
                     "INNER JOIN Usuario u ON c.DNI = u.DNI " +
                     "WHERE c.Mail = ? AND c.Contrasena = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            pstmt.setString(2, contrasena);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("Nombre");
                    String apellido = rs.getString("Apellido");
                    int dni = rs.getInt("DNI");
                    Date fechaNac = rs.getDate("FechaNac");
                    
                    // Crear objeto UsuarioCliente con los datos de la BD
                    UsuarioCliente usuario = new UsuarioCliente(
                        nombre,
                        apellido,
                        correo,
                        contrasena,
                        dni,
                        fechaNac
                    );
                    
                    return usuario;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Actualiza los datos de un cliente (versión completa)
     */
    public boolean actualizarDatosCliente(String correoActual, String nuevoNombre, String nuevoApellido,
                                         String nuevoCorreo, String nuevaContrasena, int nuevoDNI, Date nuevaFechaNac) {
        try {
            connection.setAutoCommit(false);
            
            // Obtener el DNI actual del cliente
            int dniActual = -1;
            String sqlGetDNI = "SELECT DNI FROM Cliente WHERE Mail = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlGetDNI)) {
                pstmt.setString(1, correoActual);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        dniActual = rs.getInt("DNI");
                    } else {
                        connection.rollback();
                        connection.setAutoCommit(true);
                        return false;
                    }
                }
            }
            
            // Si el DNI cambió, verificar que el nuevo no exista
            if (nuevoDNI != dniActual && existeDNI(nuevoDNI)) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
            
            // Actualizar Usuario (nombre, apellido, DNI y fecha de nacimiento)
            String sqlUsuario = "UPDATE Usuario SET Nombre = ?, Apellido = ?, DNI = ?, FechaNac = ? WHERE DNI = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlUsuario)) {
                pstmt.setString(1, nuevoNombre);
                pstmt.setString(2, nuevoApellido);
                pstmt.setInt(3, nuevoDNI);
                pstmt.setDate(4, nuevaFechaNac);
                pstmt.setInt(5, dniActual);
                pstmt.executeUpdate();
            }
            
            // Actualizar Cliente (mail, contraseña y DNI)
            String sqlCliente = "UPDATE Cliente SET Mail = ?, Contrasena = ?, DNI = ? WHERE DNI = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlCliente)) {
                pstmt.setString(1, nuevoCorreo);
                pstmt.setString(2, nuevaContrasena);
                pstmt.setInt(3, nuevoDNI);
                pstmt.setInt(4, dniActual);
                pstmt.executeUpdate();
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene los puntos acumulados de un cliente
     */
    public int obtenerPuntosCliente(String correo) {
        // Por ahora retorna un valor fijo, pero podrías implementar
        // un sistema de puntos en una tabla separada
        return 17802;
    }
    
    /**
     * Registra un canje de puntos
     */
    public boolean registrarCanje(String correo, String producto, int puntos) {
        // Implementar lógica para guardar canjes en la base de datos
        // Podrías crear una tabla "Canjes" para esto
        return true;
    }
    
    /**
     * Obtiene el historial de compras de un cliente
     */
    public ResultSet obtenerHistorialCompras(String correo) {
        String sql = "SELECT b.ID_Boleto, b.ID_Funcion, f.FechaFuncion, f.Precio " +
                     "FROM Boleto b " +
                     "INNER JOIN Funcion f ON b.ID_Funcion = f.ID_Funcion " +
                     "INNER JOIN Cliente c ON b.ID_Cliente = c.ID_Cliente " +
                     "WHERE c.Mail = ? " +
                     "ORDER BY f.FechaFuncion DESC";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, correo);
            return pstmt.executeQuery();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtiene los datos personales del usuario (nombre y apellido de la tabla Usuario)
     */
    public String[] obtenerDatosPersonales(String correo) {
        String sql = "SELECT u.Nombre, u.Apellido FROM Usuario u " +
                     "INNER JOIN Cliente c ON u.DNI = c.DNI " +
                     "WHERE c.Mail = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("Nombre");
                    String apellido = rs.getString("Apellido");
                    return new String[]{nombre, apellido};
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new String[]{"", ""};
    }
}