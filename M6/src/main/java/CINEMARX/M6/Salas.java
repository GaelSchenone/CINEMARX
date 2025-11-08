package CINEMARX.M6;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Salas {

    private M6 mainFrame;
    private JPanel contentPanel;

    public Salas(M6 mainFrame, JPanel contentPanel) {
        this.mainFrame = mainFrame;
        this.contentPanel = contentPanel;
    }

    public void mostrar() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(M6.BACKGROUND_COLOR);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Gestión de Salas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(M6.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(M6.BACKGROUND_COLOR);
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] columnNames = {"ID Sala", "Número", "Tipo de Sala", "Butacas"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        UIHelpers.styleTable(table);
        table.setTableHeader(null);

        JScrollPane scrollPane = new JScrollPane(table);
        UIHelpers.styleScrollPane(scrollPane);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(M6.BACKGROUND_COLOR);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton altaBtn = UIHelpers.createButton("Alta");
        JButton bajaBtn = UIHelpers.createButton("Baja");
        JButton modificacionBtn = UIHelpers.createButton("Modificación");

        buttonPanel.add(altaBtn);
        buttonPanel.add(bajaBtn);
        buttonPanel.add(modificacionBtn);

        altaBtn.addActionListener(e -> {
            AltaSala altaSala = new AltaSala(mainFrame);
            altaSala.setVisible(true);
            cargarSalas(tableModel);
        });

        bajaBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Seleccione una sala para dar de baja", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idSala = (int) tableModel.getValueAt(selectedRow, 0);
            int numeroSala = (int) tableModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "¿Está seguro de que desea dar de baja esta sala?", "Confirmar Baja", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM Sala WHERE ID_Sala = ?";
                    try (java.sql.PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
                        pstmt.setInt(1, idSala);
                        pstmt.executeUpdate();
                        Logger.log(mainFrame.getConnection(), "Baja de Sala: ID=" + idSala + ", Número=" + numeroSala);
                        JOptionPane.showMessageDialog(mainFrame, "Sala dada de baja exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarSalas(tableModel);
                    }
                } catch (java.sql.SQLException ex) {
                    JOptionPane.showMessageDialog(mainFrame, "Error al dar de baja la sala: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        modificacionBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Seleccione una sala para modificar", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idSala = (int) tableModel.getValueAt(selectedRow, 0);
            ModificacionSala modificacionSala = new ModificacionSala(mainFrame, idSala);
            modificacionSala.setVisible(true);
            cargarSalas(tableModel);
        });

        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(tablePanel);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(buttonPanel);

        contentPanel.add(container);

        cargarSalas(tableModel);
    }

    private void cargarSalas(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        Object[] headerRow = {"ID Sala", "Número", "Tipo de Sala", "Butacas"};
        tableModel.addRow(headerRow);
        try {
            String query = "SELECT ID_Sala, Numero, TipoDeSala, CantButacas FROM Sala WHERE ID_Cine = 1 ORDER BY Numero";
            try (Statement stmt = mainFrame.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("ID_Sala"),
                        rs.getInt("Numero"),
                        rs.getString("TipoDeSala"),
                        rs.getInt("CantButacas")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error al cargar salas: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
