package CINEMARX.M6;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Logs {

    private M6 mainFrame;
    private JPanel contentPanel;

    public Logs(M6 mainFrame, JPanel contentPanel) {
        this.mainFrame = mainFrame;
        this.contentPanel = contentPanel;
    }

    public void mostrar() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(M6.BACKGROUND_COLOR);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Logs de Acciones");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(M6.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(M6.BACKGROUND_COLOR);
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tablePanel.setMaximumSize(new Dimension(800, 450));  // Aumentado el ancho y alto
        tablePanel.setPreferredSize(new Dimension(800, 450));

        String[] columnNames = {"ID Log", "Fecha y Hora", "Descripción"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        UIHelpers.styleTable(table);
        table.setTableHeader(null);

        // Ajustar anchos de columna
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID Log
        table.getColumnModel().getColumn(1).setPreferredWidth(180); // Fecha y Hora
        table.getColumnModel().getColumn(2).setPreferredWidth(540); // Descripción (todo el ancho restante)

        JScrollPane scrollPane = new JScrollPane(table);
        UIHelpers.styleScrollPane(scrollPane);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(tablePanel);

        contentPanel.add(container);

        cargarLogs(tableModel);
    }

    private void cargarLogs(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        Object[] headerRow = {"ID Log", "Fecha y Hora", "Descripción"};
        tableModel.addRow(headerRow);
        try {
            String query = "SELECT ID_Log, FechaCambio, Descripcion FROM Logs ORDER BY FechaCambio DESC";
            try (Statement stmt = mainFrame.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("ID_Log"),
                        rs.getTimestamp("FechaCambio").toString(),
                        rs.getString("Descripcion")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error al cargar logs: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
