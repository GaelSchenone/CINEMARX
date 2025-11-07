package CINEMARX.M4;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase principal del sistema de Cine Cinemar X
 * Gestiona la conexión a la base de datos y el contenedor SPA
 */
public class M4 {
    private static Connection conexion;
    private static final String URL = "jdbc:mariadb://br1.aguilucho.ar:25584/Cinemarx";
    private static final String USER = "cnx_admin";
    private static final String PASSWORD = "CnxAdmin!620";
    private static MainFrame mainFrame;
    private static Map<String, JPanel> panelCache = new HashMap<>();
    private static OrderDetails orderDetails;
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            SwingUtilities.invokeLater(() -> {
                mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                mainFrame.showLoading();

                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        conectarBaseDatos();
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get(); // Check for exceptions
                            mostrarPantallaPelicula(1);
                        } catch (Exception e) {
                            mostrarError("Error al conectar a la base de datos", e);
                        }
                    }
                }.execute();
            });
            
        } catch (Exception e) {
            mostrarError("Error al iniciar la aplicación", e);
        }
    }
    
    private static void conectarBaseDatos() {
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✓ Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            mostrarError("Error al conectar con la base de datos", e);
            System.exit(1);
        }
    }
    
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectarBaseDatos();
            }
        } catch (SQLException e) {
            mostrarError("Error al verificar la conexión", e);
        }
        return conexion;
    }
    
    /**
     * Navega a la pantalla de película
     */
    public static void mostrarPantallaPelicula(int idPelicula) {
        if (mainFrame != null) {
            String key = "pelicula";
            PantallaPelicula panel;
            if (!panelCache.containsKey(key)) {
                panel = new PantallaPelicula(idPelicula);
                panelCache.put(key, panel);
            } else {
                panel = (PantallaPelicula) panelCache.get(key);
                panel.cargarPelicula(idPelicula);
            }
            mainFrame.cambiarContenido(panel, key);
        }
    }
    
    /**
     * Navega a la pantalla de butacas
     */
    public static void mostrarPantallaButacas(int idFuncion, int idSala, int idPelicula) {
        if (mainFrame != null) {
            String key = "butacas-" + idFuncion;
            // No cacheamos la pantalla de butacas para que se actualice siempre
            JPanel panelButacas = new PantallaButacas(idFuncion, idSala, idPelicula);
            mainFrame.cambiarContenido(panelButacas, key);
        }
    }
    
    /**
     * Obtiene el frame principal
     */
    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static OrderDetails getOrderDetails(int idFuncion) {
        if (orderDetails == null) {
            orderDetails = new OrderDetails(idFuncion);
        }
        return orderDetails;
    }
    
    private static void mostrarError(String mensaje, Exception e) {
        System.err.println("ERROR: " + mensaje);
        e.printStackTrace();
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                mensaje + "\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        });
    }
    
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("✓ Conexión cerrada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cerrarConexion();
        }));
    }
}