package CINEMARX.M5;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos MariaDB
 * Servidor: br1.aguilucho.ar
 * Base de datos: Cinemarx
 */
public class ConexionBD {
    // ============================================
    // CONFIGURACIÓN DEL SERVIDOR REMOTO
    // ============================================
    
    private static final String HOST = "br1.aguilucho.ar";
    private static final String PUERTO = "25584";
    private static final String NOMBRE_BD = "Cinemarx";
    private static final String USUARIO = "mod5_pagos_facturas";
    private static final String CONTRASENA = "Cnx!M5";
    
    // ============================================
    // URL DE CONEXIÓN PARA MARIADB
    // ============================================
    private static final String URL = "jdbc:mariadb://" + HOST + ":" + PUERTO + "/" + NOMBRE_BD + 
                                      "?useSSL=false" +
                                      "&allowPublicKeyRetrieval=true" +
                                      "&autoReconnect=true" +
                                      "&connectTimeout=30000" +
                                      "&useUnicode=true" +
                                      "&characterEncoding=UTF-8";
    
    private static Connection conexion = null;
    
    /**
     * Obtiene la conexión a la base de datos (Singleton)
     */
    public static Connection obtenerConexion() {
        if (conexion == null) {
            try {
                // Intentar primero con driver de MariaDB
                try {
                    Class.forName("org.mariadb.jdbc.Driver");
                    System.out.println("🔌 Usando driver de MariaDB");
                } catch (ClassNotFoundException e1) {
                    // Si no está MariaDB, usar MySQL (compatible)
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    System.out.println("🔌 Usando driver de MySQL (compatible con MariaDB)");
                }
                
                System.out.println("\n🔌 Conectando a base de datos remota...");
                System.out.println("   Host: " + HOST);
                System.out.println("   Puerto: " + PUERTO);
                System.out.println("   Base de datos: " + NOMBRE_BD);
                System.out.println("   Usuario: " + USUARIO);
                
                conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
                
                System.out.println("✓ Conexión exitosa a la base de datos Cinemarx\n");
                
            } catch (ClassNotFoundException e) {
                System.err.println("✗ Error: Driver de base de datos no encontrado");
                System.err.println("Necesitas uno de estos JARs en tu proyecto:");
                System.err.println("  - mariadb-java-client.jar (recomendado)");
                System.err.println("  - mysql-connector-java.jar (también funciona)");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("\n✗ ERROR AL CONECTAR CON LA BASE DE DATOS");
                System.err.println("════════════════════════════════════════");
                System.err.println("Servidor: " + HOST + ":" + PUERTO);
                System.err.println("Base de datos: " + NOMBRE_BD);
                System.err.println("Usuario: " + USUARIO);
                System.err.println("Código de error: " + e.getErrorCode());
                System.err.println("Mensaje: " + e.getMessage());
                System.err.println("════════════════════════════════════════\n");
                
                // Diagnósticos específicos
                if (e.getMessage().contains("Access denied")) {
                    System.err.println("⚠ PROBLEMA: Credenciales incorrectas");
                    System.err.println("   → Verifica usuario y contraseña");
                    
                } else if (e.getMessage().contains("Unknown database")) {
                    System.err.println("⚠ PROBLEMA: Base de datos no existe");
                    System.err.println("   → Verifica que 'Cinemarx' exista en el servidor");
                    
                } else if (e.getMessage().contains("Communications link failure") || 
                           e.getMessage().contains("Connection refused")) {
                    System.err.println("⚠ PROBLEMA: No se puede conectar al servidor");
                    System.err.println("   → ¿Tienes acceso a Internet?");
                    System.err.println("   → ¿El firewall está bloqueando el puerto " + PUERTO + "?");
                    System.err.println("   → ¿El servidor está activo?");
                    
                } else if (e.getMessage().contains("timeout")) {
                    System.err.println("⚠ PROBLEMA: Tiempo de conexión agotado");
                    System.err.println("   → El servidor puede estar lento o inaccesible");
                    System.err.println("   → Verifica tu conexión a Internet");
                }
                
                e.printStackTrace();
            }
        }
        return conexion;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                conexion = null;
                System.out.println("✓ Conexión cerrada");
            } catch (SQLException e) {
                System.err.println("✗ Error al cerrar la conexión");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Verifica si la conexión está activa
     */
    public static boolean verificarConexion() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Prueba la conexión y muestra información de la base de datos
     */
    public static void probarConexion() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   PRUEBA DE CONEXIÓN A BASE DE DATOS  ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        Connection conn = obtenerConexion();
        
        if (conn != null) {
            try {
                // Obtener información de la base de datos
                System.out.println("\n📊 Información de la conexión:");
                System.out.println("   Database: " + conn.getCatalog());
                System.out.println("   Motor: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("   Versión: " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("   Driver: " + conn.getMetaData().getDriverName());
                
                // Probar una consulta simple
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery("SELECT DATABASE() as db, VERSION() as version, USER() as user");
                if (rs.next()) {
                    System.out.println("\n✓ Consulta de prueba exitosa:");
                    System.out.println("   Base de datos activa: " + rs.getString("db"));
                    System.out.println("   Versión: " + rs.getString("version"));
                    System.out.println("   Usuario conectado: " + rs.getString("user"));
                }
                rs.close();
                stmt.close();
                
                // Verificar tablas principales
                System.out.println("\n📋 Verificando tablas principales:");
                String[] tablasRequeridas = {"Cliente", "Usuario", "MetodosPago", "Comprobante", 
                                             "Boleto", "Funcion", "Pelicula"};
                
                var dbMetaData = conn.getMetaData();
                for (String tabla : tablasRequeridas) {
                    var rsTabla = dbMetaData.getTables(NOMBRE_BD, null, tabla, new String[]{"TABLE"});
                    if (rsTabla.next()) {
                        System.out.println("   ✓ Tabla '" + tabla + "' existe");
                    } else {
                        System.out.println("   ✗ Tabla '" + tabla + "' NO EXISTE");
                    }
                    rsTabla.close();
                }
                
                System.out.println("\n✅ ¡CONEXIÓN EXITOSA! La aplicación está lista para usar.\n");
                
            } catch (SQLException e) {
                System.err.println("\n❌ Error al probar la conexión:");
                System.err.println("   " + e.getMessage());
                e.printStackTrace();
            }
            
            cerrarConexion();
        } else {
            System.err.println("\n❌ NO SE PUDO ESTABLECER LA CONEXIÓN");
            System.err.println("\n📝 VERIFICA:");
            System.err.println("1. Que tengas conexión a Internet");
            System.err.println("2. Que el driver mariadb-java-client.jar o mysql-connector-java.jar");
            System.err.println("   esté agregado a tu proyecto");
            System.err.println("3. Que las credenciales sean correctas");
            System.err.println("4. Que el servidor remoto esté activo\n");
        }
    }
    
    /**
     * Main para ejecutar prueba de conexión
     */
    public static void main(String[] args) {
        probarConexion();
    }
}