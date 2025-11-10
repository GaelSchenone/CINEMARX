package CINEMARX.M5;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal del módulo M5 - Sistema de Pagos y Facturas
 * CinemarX - Sistema de gestión de cine
 * 
 * Esta clase arranca la aplicación de finalización de compra
 */
public class M5 {
    
    /**
     * Método principal que inicia la aplicación
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Mostrar información de inicio
        mostrarBanner();
        
        // Verificar conexión a la base de datos antes de iniciar la GUI
        System.out.println("Verificando conexión a la base de datos...");
        if (!verificarConexionInicial()) {
            System.err.println("\n❌ ERROR CRÍTICO: No se pudo conectar a la base de datos");
            System.err.println("La aplicación no puede iniciarse sin conexión a la base de datos.\n");
            System.exit(1);
        }
        
        // Iniciar la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Establecer Look and Feel del sistema operativo
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    
                    // Crear y mostrar la ventana principal
                    VentanaFinalizarCompra ventana = new VentanaFinalizarCompra();
                    ventana.setVisible(true);
                    
                    System.out.println("✓ Aplicación iniciada correctamente\n");
                    
                } catch (Exception e) {
                    System.err.println("❌ Error al iniciar la interfaz gráfica:");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
    }
    
    /**
     * Verifica que la conexión a la base de datos esté disponible
     * @return true si la conexión es exitosa, false en caso contrario
     */
    private static boolean verificarConexionInicial() {
        try {
            java.sql.Connection conn = ConexionBD.obtenerConexion();
            
            if (conn == null) {
                return false;
            }
            
            // Verificar que la conexión esté activa
            if (conn.isClosed()) {
                System.err.println("La conexión está cerrada");
                return false;
            }
            
            // Hacer una consulta simple para verificar conectividad
            try (java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery("SELECT 1")) {
                if (rs.next()) {
                    System.out.println("✓ Conexión a la base de datos verificada\n");
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            System.err.println("Error al verificar conexión: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Muestra el banner de inicio de la aplicación
     */
    private static void mostrarBanner() {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║                                                ║");
        System.out.println("║              🎬 C I N E M A R X 🎬              ║");
        System.out.println("║                                                ║");
        System.out.println("║        Sistema de Pagos y Facturas (M5)       ║");
        System.out.println("║                                                ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        System.out.println("Iniciando aplicación...\n");
    }
}