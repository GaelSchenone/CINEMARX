
package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportePelicula extends JDialog {

    public ReportePelicula(Frame owner, Connection connection, int idPelicula) {
        super(owner, "Reporte de Película", true);
        setSize(400, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);

        try {
            // Obtener detalles de la película
            String queryPelicula = "SELECT Titulo, Genero, Sinopsis FROM Pelicula WHERE ID_Pelicula = ?";
            PreparedStatement stmtPelicula = connection.prepareStatement(queryPelicula);
            stmtPelicula.setInt(1, idPelicula);
            ResultSet rsPelicula = stmtPelicula.executeQuery();

            if (rsPelicula.next()) {
                String titulo = rsPelicula.getString("Titulo");
                String genero = rsPelicula.getString("Genero");
                String sinopsis = rsPelicula.getString("Sinopsis");

                // Título de la película
                JLabel tituloLabel = new JLabel(titulo);
                tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(tituloLabel);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                // Sección de Viabilidad
                JPanel viabilidadPanel = new JPanel();
                viabilidadPanel.setLayout(new BoxLayout(viabilidadPanel, BoxLayout.Y_AXIS));
                viabilidadPanel.setBorder(BorderFactory.createTitledBorder("Resumen de Viabilidad"));
                viabilidadPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

                double totalRecaudado = calcularTotalRecaudado(connection, idPelicula);
                JLabel recaudadoLabel = new JLabel("Total Recaudado: $" + String.format("%.2f", totalRecaudado));
                viabilidadPanel.add(recaudadoLabel);

                String rentabilidad;
                if (totalRecaudado > 60000) {
                    rentabilidad = "Muy rentable";
                } else if (totalRecaudado > 30000) {
                    rentabilidad = "Rentable";
                } else if (totalRecaudado >= 15000) {
                    rentabilidad = "Poco rentable";
                } else {
                    rentabilidad = "Nada rentable";
                }
                JLabel rentabilidadLabel = new JLabel("Estado: " + rentabilidad);
                viabilidadPanel.add(rentabilidadLabel);

                contentPanel.add(viabilidadPanel);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                // Sinopsis
                JTextArea sinopsisArea = new JTextArea(sinopsis);
                sinopsisArea.setWrapStyleWord(true);
                sinopsisArea.setLineWrap(true);
                sinopsisArea.setEditable(false);
                sinopsisArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                JScrollPane sinopsisScrollPane = new JScrollPane(sinopsisArea);
                sinopsisScrollPane.setBorder(BorderFactory.createTitledBorder("Sinopsis"));
                contentPanel.add(sinopsisScrollPane);

            } else {
                JOptionPane.showMessageDialog(this, "No se encontró la película.", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el reporte: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose();
        }
    }

    private double calcularTotalRecaudado(Connection connection, int idPelicula) throws SQLException {
        String query = "SELECT SUM(cb.Cantidad * f.Precio) AS TotalRecaudado " +
                       "FROM Comprobante_Boleto cb " +
                       "JOIN Boleto b ON cb.ID_Boleto = b.ID_Boleto " +
                       "JOIN Funcion f ON b.ID_Funcion = f.ID_Funcion " +
                       "WHERE f.ID_Pelicula = ?";
        
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, idPelicula);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getDouble("TotalRecaudado");
        } else {
            return 0.0;
        }
    }
}
