package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteCliente extends JDialog {

    public ReporteCliente(Frame owner, Connection connection, int idCliente) {
        super(owner, "Reporte de Cliente", true);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);

        try {
            // Obtener nombre del cliente
            String nombreCliente = getNombreCliente(connection, idCliente);
            setTitle("Reporte de " + nombreCliente);

            // Gasto Total
            double gastoTotal = getGastoTotal(connection, idCliente);
            JLabel gastoLabel = new JLabel(String.format("Gasto Total: $%.2f", gastoTotal));
            gastoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            contentPanel.add(gastoLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // Géneros Favoritos
            List<String> generos = getGenerosFavoritos(connection, idCliente);
            JLabel generosLabel = new JLabel("Géneros Favoritos:");
            generosLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            contentPanel.add(generosLabel);

            if (generos.isEmpty()) {
                contentPanel.add(new JLabel("No hay datos de géneros."));
            } else {
                for (String genero : generos) {
                    contentPanel.add(new JLabel("- " + genero));
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose();
        }
    }

    private String getNombreCliente(Connection connection, int idCliente) throws SQLException {
        String query = "SELECT Nombre, Apellido FROM Usuario u JOIN Cliente c ON u.DNI = c.DNI WHERE c.ID_Cliente = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Nombre") + " " + rs.getString("Apellido");
            }
        }
        return "Desconocido";
    }

    private double getGastoTotal(Connection connection, int idCliente) throws SQLException {
        double gastoBoletos = 0;
        double gastoProductos = 0;

        // Gasto en boletos
        String queryBoletos = "SELECT SUM(f.Precio * cb.Cantidad) FROM Comprobante_Boleto cb " +
                              "JOIN Boleto b ON cb.ID_Boleto = b.ID_Boleto " +
                              "JOIN Funcion f ON b.ID_Funcion = f.ID_Funcion " +
                              "JOIN Comprobante c ON cb.ID_Comprobante = c.ID_Comprobante " +
                              "WHERE c.ID_Cliente = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(queryBoletos)) {
            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                gastoBoletos = rs.getDouble(1);
            }
        }

        // Gasto en productos
        String queryProductos = "SELECT SUM(p.Precio * cp.Cantidad) FROM Comprobante_Producto cp " +
                                "JOIN Producto p ON cp.ID_Prod = p.ID_Prod " +
                                "JOIN Comprobante c ON cp.ID_Comprobante = c.ID_Comprobante " +
                                "WHERE c.ID_Cliente = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(queryProductos)) {
            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                gastoProductos = rs.getDouble(1);
            }
        }

        return gastoBoletos + gastoProductos;
    }

    private List<String> getGenerosFavoritos(Connection connection, int idCliente) throws SQLException {
        List<String> generos = new ArrayList<>();
        String query = "SELECT p.Genero, COUNT(DISTINCT b.ID_Funcion) AS Vistas " +
                       "FROM Boleto b " +
                       "JOIN Funcion f ON b.ID_Funcion = f.ID_Funcion " +
                       "JOIN Pelicula p ON f.ID_Pelicula = p.ID_Pelicula " +
                       "WHERE b.ID_Cliente = ? AND p.Genero IS NOT NULL " +
                       "GROUP BY p.Genero " +
                       "ORDER BY Vistas DESC " +
                       "LIMIT 2";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idCliente);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                generos.add(rs.getString("Genero"));
            }
        }
        return generos;
    }
}