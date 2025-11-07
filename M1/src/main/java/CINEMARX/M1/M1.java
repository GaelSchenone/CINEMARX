package CINEMARX.M1;

import javax.swing.SwingUtilities;
import javax.swing.*;
import java.sql.*;

/**
 * @author tm_berea
 */
public class M1 extends JFrame {
    
    private Connection connection;
    
    public M1() {
        initializeDatabase();
    }
    
    /**
     * Inicializa la conexión a la base de datos
     */
    private void initializeDatabase() {
        try {
            // Configuración de la conexión (ajusta según tu configuración)

            String url = "jdbc:mariadb://br1.aguilucho.ar:25584/Cinemarx";
            String usuario = "cnx_admin";
            String password = "CnxAdmin!620";
            
            connection = DriverManager.getConnection(url, usuario, password);
            System.out.println("Conexión a la base de datos establecida exitosamente.");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Error al conectar con la base de datos: " + e.getMessage(),
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Obtiene la conexión a la base de datos
     */
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // Iniciar la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            M1 app = new M1();
            VentanaPrincipal ventana = new VentanaPrincipal(app.getConnection());
            ventana.setVisible(true);
            
            // Cerrar conexión al cerrar la ventana
            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    app.closeConnection();
                }
            });
        });
    }
}