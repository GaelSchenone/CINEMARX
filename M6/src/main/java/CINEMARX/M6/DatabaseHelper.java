package CINEMARX.M6;

import javax.swing.*;
import java.sql.*;

/**
 * Clase auxiliar para operaciones de base de datos comunes
 */
public class DatabaseHelper {
    
    public static void cargarSalas(M6 mainFrame, JComboBox<SalaItem> comboBox) {
        try (Statement stmt = mainFrame.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID_Sala, Numero, TipoDeSala, CantButacas FROM Sala ORDER BY ID_Sala")) {
            
            while (rs.next()) {
                int idSala = rs.getInt("ID_Sala");
                int numero = rs.getInt("Numero");
                String tipo = rs.getString("TipoDeSala");
                int cantButacas = rs.getInt("CantButacas");
                
                comboBox.addItem(new SalaItem(idSala, numero, tipo, cantButacas));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error al cargar las salas: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public static void cargarPeliculas(M6 mainFrame, JComboBox<PeliculaItem> comboBox) {
        try (Statement stmt = mainFrame.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID_Pelicula, Titulo FROM Pelicula ORDER BY Titulo")) {

            while (rs.next()) {
                comboBox.addItem(new PeliculaItem(rs.getInt("ID_Pelicula"), rs.getString("Titulo")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error al cargar películas: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public static void cargarCarteleras(M6 mainFrame, JComboBox<CarteleraItem> comboBox) {
        try (Statement stmt = mainFrame.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID_Cartelera, FechaInicio, FechaFin FROM Cartelera ORDER BY ID_Cartelera")) {

            while (rs.next()) {
                comboBox.addItem(new CarteleraItem(
                    rs.getInt("ID_Cartelera"), 
                    rs.getString("FechaInicio"), 
                    rs.getString("FechaFin")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error al cargar carteleras: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public static void cargarFunciones(M6 mainFrame, JComboBox<FuncionItem> comboBox) {
        try {
            String query = "SELECT f.ID_Funcion, p.Titulo, f.FechaFuncion, f.HoraFuncion, s.Numero " +
                          "FROM Funcion f " +
                          "INNER JOIN Pelicula p ON f.ID_Pelicula = p.ID_Pelicula " +
                          "INNER JOIN Sala s ON f.ID_Sala = s.ID_Sala " +
                          "ORDER BY f.FechaFuncion DESC, f.HoraFuncion DESC";

            try (Statement stmt = mainFrame.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    int idFuncion = rs.getInt("ID_Funcion");
                    String titulo = rs.getString("Titulo");
                    String fecha = rs.getString("FechaFuncion");
                    String hora = rs.getString("HoraFuncion");
                    int numeroSala = rs.getInt("Numero");

                    comboBox.addItem(new FuncionItem(idFuncion, titulo, fecha, hora, numeroSala));
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error al cargar las funciones: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public static void cargarCines(M6 mainFrame, JComboBox<CineItem> comboBox) {
        try (Statement stmt = mainFrame.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID_Cine, Nombre FROM Cine ORDER BY Nombre")) {

            while (rs.next()) {
                comboBox.addItem(new CineItem(rs.getInt("ID_Cine"), rs.getString("Nombre")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error al cargar cines: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static boolean isPeliculaInFuncion(Connection connection, int idPelicula) throws SQLException {
        String query = "SELECT COUNT(*) FROM Funcion WHERE ID_Pelicula = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idPelicula);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}