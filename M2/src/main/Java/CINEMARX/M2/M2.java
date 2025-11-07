/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CINEMARX.M2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos MariaDB
 */
public class M2 {
    
    // ==========================================
    // CONFIGURACIÓN DE LA BASE DE DATOS
    // ==========================================
    
    private static final String DB_URL = "jdbc:mariadb://br1.aguilucho.ar:25584/Cinemarx?connectTimeout=10000&socketTimeout=30000";
    private static final String DB_USER = "mod2_carteleras_peliculas";
    private static final String DB_PASSWORD = "Cnx!M2";
    
    /**
     * Obtiene una conexión a la base de datos
     * IMPORTANTE: Crea una NUEVA conexión cada vez para evitar problemas de socket
     * @return Connection o null si hay error
     */
    public static Connection obtenerConexion() {
        try {
            // Cargar el driver de MariaDB
            Class.forName("org.mariadb.jdbc.Driver");
            
            // Crear NUEVA conexión cada vez (no reutilizar)
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ Conexión exitosa a la base de datos CineMarx");
            
            return conn;
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERROR: No se encontró el driver de MariaDB");
            System.err.println("   Asegúrate de tener el JAR mariadb-java-client en las librerías");
            e.printStackTrace();
            return null;
            
        } catch (SQLException e) {
            System.err.println("❌ ERROR: No se pudo conectar a la base de datos");
            System.err.println("   Verifica:");
            System.err.println("   - Que MariaDB esté ejecutándose");
            System.err.println("   - URL: " + DB_URL);
            System.err.println("   - Usuario: " + DB_USER);
            System.err.println("   - Que la base de datos 'Cinemarx' exista");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Cierra una conexión específica
     * @param conn Conexión a cerrar
     */
    public static void cerrarConexion(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("🔒 Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar la conexión");
            e.printStackTrace();
        }
    }
    
    /**
     * Método de prueba para verificar la conexión
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  PRUEBA DE CONEXIÓN A MARIADB");
        System.out.println("========================================\n");
        
        Connection conn = obtenerConexion();
        
        if (conn != null) {
            System.out.println("\n✅ ¡CONEXIÓN EXITOSA!");
            System.out.println("📊 Base de datos: Cinemarx");
            System.out.println("🔗 URL: " + DB_URL);
            
            // Mostrar información de la BD
            try {
                System.out.println("📌 Versión de MariaDB: " + 
                    conn.getMetaData().getDatabaseProductVersion());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            cerrarConexion(conn);
            
        } else {
            System.out.println("\n❌ NO SE PUDO CONECTAR");
            System.out.println("\n📋 CHECKLIST:");
            System.out.println("   [ ] MariaDB está corriendo");
            System.out.println("   [ ] La base de datos 'Cinemarx' existe");
            System.out.println("   [ ] Usuario y contraseña son correctos");
            System.out.println("   [ ] El driver mariadb-java-client.jar está agregado");
        }
        
        System.out.println("\n========================================");
    }
}