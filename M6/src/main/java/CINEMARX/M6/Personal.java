package CINEMARX.M6;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Personal {
    
    private M6 mainFrame;
    private JPanel contentPanel;
    
    public Personal(M6 mainFrame, JPanel contentPanel) {
        this.mainFrame = mainFrame;
        this.contentPanel = contentPanel;
    }
    
    public void mostrar() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(M6.BACKGROUND_COLOR);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título principal
        JLabel titleLabel = new JLabel("Gestión de Personal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(M6.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel para la tabla
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(M6.BACKGROUND_COLOR);
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tablePanel.setMaximumSize(new Dimension(800, 400));
        tablePanel.setPreferredSize(new Dimension(800, 400));

        // Crear tabla
        String[] columnNames = {"DNI", "Nombre", "Apellido", "Rol", "Mail", "Contraseña"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Agregar la primera fila con los nombres de las columnas
        Object[] headerRow = {"DNI", "Nombre", "Apellido", "Rol", "Mail", "Contraseña"};
        tableModel.addRow(headerRow);

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(M6.TEXT_COLOR);
        table.setBackground(new Color(40, 40, 40));
        table.setGridColor(new Color(60, 60, 60));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setTableHeader(null);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(M6.BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
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

        // Listeners de botones
        altaBtn.addActionListener(e -> new PersonalDialogs(mainFrame).abrirVentanaAltaPersonal(tableModel));

        bajaBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Seleccione un registro para eliminar", 
                    "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            eliminarPersonal(table, tableModel, selectedRow);
        });

        modificacionBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Seleccione un registro para modificar", 
                    "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new PersonalDialogs(mainFrame).abrirVentanaModificacionPersonal(table, tableModel, selectedRow);
        });

        // Agregar componentes al contenedor
        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(tablePanel);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(buttonPanel);

        contentPanel.add(container);

        // Cargar datos
        cargarPersonal(tableModel);
    }
    
    public void cargarPersonal(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);

        Object[] headerRow = {"DNI", "Nombre", "Apellido", "Rol", "Mail", "Contraseña"};
        tableModel.addRow(headerRow);

        try {
            // Cargar administradores primero
            String queryAdmin = "SELECT u.DNI, u.Nombre, u.Apellido, a.Mail, a.Contrasena " +
                               "FROM Administrador a " +
                               "INNER JOIN Usuario u ON a.DNI = u.DNI " +
                               "ORDER BY u.Apellido, u.Nombre";

            try (Statement stmt = mainFrame.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(queryAdmin)) {

                while (rs.next()) {
                    String password = rs.getString("Contrasena");
                    String censoredPassword = "****" + password.substring(password.length() - 3);
                    Object[] row = {
                        rs.getInt("DNI"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        "Administrador",
                        rs.getString("Mail"),
                        censoredPassword
                    };
                    tableModel.addRow(row);
                }
            }

            // Cargar empleados después
            String queryEmp = "SELECT u.DNI, u.Nombre, u.Apellido " +
                             "FROM Empleado e " +
                             "INNER JOIN Usuario u ON e.DNI = u.DNI " +
                             "ORDER BY u.Apellido, u.Nombre";

            try (Statement stmt = mainFrame.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(queryEmp)) {

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("DNI"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        "Empleado",
                        "",
                        ""
                    };
                    tableModel.addRow(row);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error al cargar personal: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
// CONTINUACIÓN DE Personal.java - Agregar este método a la clase Personal

    private void eliminarPersonal(JTable table, DefaultTableModel tableModel, int selectedRow) {
        // Obtener datos de la fila seleccionada
        int dni = (int) tableModel.getValueAt(selectedRow, 0);
        String nombre = (String) tableModel.getValueAt(selectedRow, 1);
        String apellido = (String) tableModel.getValueAt(selectedRow, 2);
        String rol = (String) tableModel.getValueAt(selectedRow, 3);

        // Mensaje de confirmación
        int confirm = JOptionPane.showConfirmDialog(mainFrame, 
            "¿Seguro que desea eliminar este registro?\n\n" +
            "DNI: " + dni + "\n" +
            "Nombre: " + nombre + " " + apellido + "\n" +
            "Rol: " + rol,
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Primero eliminar de la tabla específica (Empleado o Administrador)
                String queryDelete;
                if (rol.equals("Administrador")) {
                    queryDelete = "DELETE FROM Administrador WHERE DNI = ?";
                } else {
                    queryDelete = "DELETE FROM Empleado WHERE DNI = ?";
                }

                try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(queryDelete)) {
                    pstmt.setInt(1, dni);
                    pstmt.executeUpdate();
                }

                // Luego eliminar de Usuario
                String queryDeleteUsuario = "DELETE FROM Usuario WHERE DNI = ?";
                try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(queryDeleteUsuario)) {
                    pstmt.setInt(1, dni);
                    pstmt.executeUpdate();
                }

                Logger.log(mainFrame.getConnection(), "Baja de " + rol + ": DNI=" + dni + ", Nombre=" + nombre + " " + apellido);

                JOptionPane.showMessageDialog(mainFrame, 
                    rol + " eliminado exitosamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);

                cargarPersonal(tableModel);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Error al eliminar: " + e.getMessage(), 
                    "Error de Base de Datos", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}

// FIN DE LA CLASE Personal.java