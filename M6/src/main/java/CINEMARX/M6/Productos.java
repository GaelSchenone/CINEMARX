package CINEMARX.M6;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class Productos {

    private M6 m6;
    private JPanel contentPanel;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton, updateButton, salesByFunctionButton;
    private final String[] COLUMN_NAMES = {"ID", "Nombre", "Precio", "Categoría", "Cantidad Vendida"};

    public Productos(M6 m6, JPanel contentPanel) {
        this.m6 = m6;
        this.contentPanel = contentPanel;
    }

    public void mostrar() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(M6.BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Gestión de Productos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(M6.TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setBackground(M6.SIDEBAR_COLOR);
        productTable.setForeground(M6.TEXT_COLOR);
        productTable.setSelectionBackground(M6.ACCENT_COLOR.darker());
        productTable.setSelectionForeground(Color.WHITE);
        productTable.setGridColor(M6.BACKGROUND_COLOR);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productTable.setRowHeight(25);
        productTable.setTableHeader(null);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.getViewport().setBackground(M6.BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(M6.SIDEBAR_COLOR, 1));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(M6.BACKGROUND_COLOR);

        addButton = createStyledButton("Alta");
        deleteButton = createStyledButton("Baja");
        updateButton = createStyledButton("Modificación");
        salesByFunctionButton = createStyledButton("Producto mas vendido");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(salesByFunctionButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        updateButton.addActionListener(e -> updateProduct());
        salesByFunctionButton.addActionListener(e -> showSalesByFunction());

        // Enable/disable buttons based on selection
        productTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = productTable.getSelectedRow() > 0;
            deleteButton.setEnabled(rowSelected);
            updateButton.setEnabled(rowSelected);
        });

        // Initial button state
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);
        salesByFunctionButton.setEnabled(true); // Siempre habilitado

        loadProducts();

        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(M6.ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(M6.ACCENT_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(M6.ACCENT_COLOR);
            }
        });
        return button;
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        tableModel.addRow(COLUMN_NAMES);
        
        try (Connection conn = m6.getConnection()) {
            String sql = "SELECT p.ID_Prod, p.Nombre, p.Precio, p.Categoria, COALESCE(SUM(cp.Cantidad), 0) AS CantidadVendida " +
                         "FROM Producto p " +
                         "LEFT JOIN Comprobante_Producto cp ON p.ID_Prod = cp.ID_Prod " +
                         "GROUP BY p.ID_Prod, p.Nombre, p.Precio, p.Categoria " +
                         "ORDER BY p.ID_Prod";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("ID_Prod"));
                    row.add(rs.getString("Nombre"));
                    row.add(rs.getDouble("Precio"));
                    row.add(rs.getString("Categoria"));
                    row.add(rs.getInt("CantidadVendida"));
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(contentPanel, "Error al cargar los productos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProduct() {
        showProductDialog(null);
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow <= 0) {
            return;
        }
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(contentPanel, "¿Está seguro de que desea eliminar este producto?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = m6.getConnection()) {
                String sql = "DELETE FROM Producto WHERE ID_Prod = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, productId);
                    pstmt.executeUpdate();
                    loadProducts();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(contentPanel, "Error al eliminar el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow <= 0) {
            return;
        }
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        showProductDialog(productId);
    }

    private void showProductDialog(Integer productId) {
        JDialog dialog = new JDialog(m6, productId == null ? "Alta de Producto" : "Modificación de Producto", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(m6);
        dialog.getContentPane().setBackground(M6.BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        formPanel.setBackground(M6.BACKGROUND_COLOR);

        JLabel nameLabel = new JLabel("Nombre:");
        nameLabel.setForeground(M6.TEXT_COLOR);
        JTextField nameField = new JTextField();
        nameField.setBackground(M6.BUTTON_COLOR);
        nameField.setForeground(M6.TEXT_COLOR);
        nameField.setCaretColor(M6.TEXT_COLOR);
        nameField.setBorder(BorderFactory.createLineBorder(M6.SIDEBAR_COLOR));

        JLabel priceLabel = new JLabel("Precio:");
        priceLabel.setForeground(M6.TEXT_COLOR);
        JTextField priceField = new JTextField();
        priceField.setBackground(M6.BUTTON_COLOR);
        priceField.setForeground(M6.TEXT_COLOR);
        priceField.setCaretColor(M6.TEXT_COLOR);
        priceField.setBorder(BorderFactory.createLineBorder(M6.SIDEBAR_COLOR));

        JLabel categoryLabel = new JLabel("Categoría:");
        categoryLabel.setForeground(M6.TEXT_COLOR);
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"Snack", "Bebida", "Combo", "Comida"});
        categoryComboBox.setBackground(M6.BUTTON_COLOR);
        categoryComboBox.setForeground(M6.TEXT_COLOR);
        categoryComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(M6.ACCENT_COLOR.darker());
                    setForeground(Color.WHITE);
                } else {
                    setBackground(M6.BUTTON_COLOR);
                    setForeground(M6.TEXT_COLOR);
                }
                return this;
            }
        });
        categoryComboBox.setBorder(BorderFactory.createLineBorder(M6.SIDEBAR_COLOR));

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);

        if (productId != null) {
            try (Connection conn = m6.getConnection()) {
                String sql = "SELECT Nombre, Precio, Categoria FROM Producto WHERE ID_Prod = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, productId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            nameField.setText(rs.getString("Nombre"));
                            priceField.setText(String.valueOf(rs.getDouble("Precio")));
                            categoryComboBox.setSelectedItem(rs.getString("Categoria"));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error al cargar los datos del producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                dialog.dispose();
                return;
            }
        }

        JPanel buttonDialogPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonDialogPanel.setBackground(M6.BACKGROUND_COLOR);
        JButton saveButton = createStyledButton("Guardar");
        JButton cancelButton = createStyledButton("Cancelar");
        buttonDialogPanel.add(saveButton);
        buttonDialogPanel.add(cancelButton);

        saveButton.addActionListener(e -> {
            saveProduct(productId, nameField.getText(), priceField.getText(), (String) categoryComboBox.getSelectedItem());
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonDialogPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void saveProduct(Integer productId, String name, String price, String category) {
        try (Connection conn = m6.getConnection()) {
            if (productId == null) { // Insert
                String sql = "INSERT INTO Producto (Nombre, Precio, Categoria) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setDouble(2, Double.parseDouble(price));
                    pstmt.setString(3, category);
                    pstmt.executeUpdate();
                }
            } else { // Update
                String sql = "UPDATE Producto SET Nombre = ?, Precio = ?, Categoria = ? WHERE ID_Prod = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setDouble(2, Double.parseDouble(price));
                    pstmt.setString(3, category);
                    pstmt.setInt(4, productId);
                    pstmt.executeUpdate();
                }
            }
            loadProducts();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(contentPanel, "Error al guardar el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSalesByFunction() {
        JDialog dialog = new JDialog(m6, "Resumen de Ventas por Función", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(m6);
        dialog.getContentPane().setBackground(M6.BACKGROUND_COLOR);

        String[] columnNames = {"Función (Película, Fecha, Hora, Sala)", "Producto más Vendido"};
        DefaultTableModel salesTableModel = new DefaultTableModel(columnNames, 0);
        JTable salesTable = new JTable(salesTableModel);
        salesTable.setBackground(M6.SIDEBAR_COLOR);
        salesTable.setForeground(M6.TEXT_COLOR);
        salesTable.setSelectionBackground(M6.ACCENT_COLOR.darker());
        salesTable.setSelectionForeground(Color.WHITE);
        salesTable.setGridColor(M6.BACKGROUND_COLOR);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salesTable.setRowHeight(25);
        salesTable.setTableHeader(null); // Ocultar el header por defecto

        // Añadir el header como primera fila
        salesTableModel.addRow(columnNames);

        try (ResultSet rs = DatabaseHelper.getResumenVentasProductosPorFuncion(m6.getConnection())) {
            while (rs.next()) {
                String titulo = rs.getString("Titulo");
                String fecha = rs.getString("FechaFuncion");
                String hora = rs.getString("HoraFuncion");
                int salaNumero = rs.getInt("SalaNumero");
                String producto = rs.getString("ProductoMasVendido");

                String funcionInfo = String.format("%s (%s, %s, Sala %d)", titulo, fecha, hora, salaNumero);
                String productoInfo = (producto == null || producto.isEmpty()) ? "Sin ventas de productos" : producto;

                salesTableModel.addRow(new Object[]{funcionInfo, productoInfo});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error al obtener el resumen de ventas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            dialog.dispose();
            return;
        }

        if (salesTableModel.getRowCount() == 0) {
            JLabel noSalesLabel = new JLabel("No hay datos de funciones para mostrar.", SwingConstants.CENTER);
            noSalesLabel.setForeground(M6.TEXT_COLOR);
            dialog.add(noSalesLabel);
        } else {
            JScrollPane salesScrollPane = new JScrollPane(salesTable);
            salesScrollPane.getViewport().setBackground(M6.BACKGROUND_COLOR);
            salesScrollPane.setBorder(BorderFactory.createLineBorder(M6.SIDEBAR_COLOR, 1));
            dialog.add(salesScrollPane);
        }

        dialog.setVisible(true);
    }
}
