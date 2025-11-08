package CINEMARX.M6;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UsuariosYRoles {
    
    private M6 mainFrame;
    private JPanel contentPanel;
    
    public UsuariosYRoles(M6 mainFrame, JPanel contentPanel) {
        this.mainFrame = mainFrame;
        this.contentPanel = contentPanel;
    }
    
    public void mostrar() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(M6.BACKGROUND_COLOR);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título principal
        JLabel titleLabel = new JLabel("Clientes y Roles");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(M6.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel para la tabla (MÁS ANCHO)
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(M6.BACKGROUND_COLOR);
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tablePanel.setMaximumSize(new Dimension(800, 400));  // Aumentado el ancho
        tablePanel.setPreferredSize(new Dimension(800, 400));

        // Crear tabla
        String[] columnNames = {"ID Cliente", "DNI", "Nombre", "Apellido", "Membresía", "Boletos Comprados", "Mail", "Contraseña"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Agregar fila de encabezado
        Object[] headerRow = {"ID Cliente", "DNI", "Nombre", "Apellido", "Membresía", "Boletos Comprados", "Mail", "Contraseña"};
        tableModel.addRow(headerRow);

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(M6.TEXT_COLOR);
        table.setBackground(new Color(40, 40, 40));
        table.setGridColor(new Color(60, 60, 60));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ocultar encabezado de tabla
        table.setTableHeader(null);

        // Habilitar scroll horizontal y ajustar anchos de columna
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID Cliente
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // DNI
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Nombre
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Apellido
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Membresía
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // Boletos Comprados
        table.getColumnModel().getColumn(6).setPreferredWidth(200); // Mail (más ancho)
        table.getColumnModel().getColumn(7).setPreferredWidth(100); // Contraseña

        // SCROLL CON HORIZONTAL Y VERTICAL
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(M6.BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones (SOLO VER MÉTODOS DE PAGO)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(M6.BACKGROUND_COLOR);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton verMetodosBtn = UIHelpers.createButton("Ver Métodos de Pago");
        JButton reportesBtn = UIHelpers.createButton("Reportes");

        buttonPanel.add(verMetodosBtn);
        buttonPanel.add(reportesBtn);

        // Listener del botón
        verMetodosBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1 || selectedRow == 0) { // 0 es el header
                JOptionPane.showMessageDialog(mainFrame, 
                    "Seleccione un cliente para ver sus métodos de pago", 
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idCliente = (int) tableModel.getValueAt(selectedRow, 0);
            String nombreCompleto = tableModel.getValueAt(selectedRow, 2) + " " + 
                                   tableModel.getValueAt(selectedRow, 3);
            abrirVentanaMetodosPago(idCliente, nombreCompleto);
        });

        reportesBtn.addActionListener(e -> {
            try {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1 || selectedRow == 0) { // 0 es el header
                    JOptionPane.showMessageDialog(mainFrame,
                            "Seleccione un cliente para ver su reporte",
                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int idCliente = (int) tableModel.getValueAt(selectedRow, 0);
                ReporteCliente reporte = new ReporteCliente(mainFrame, mainFrame.getConnection(), idCliente);
                reporte.setVisible(true);
            } catch (SQLException ex) {
                System.getLogger(UsuariosYRoles.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });

        // Agregar componentes al contenedor
        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(tablePanel);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(buttonPanel);

        // ENVOLVER EL CONTAINER EN UN SCROLLPANE VERTICAL
        JScrollPane mainScrollPane = new JScrollPane(container);
        mainScrollPane.setBackground(M6.BACKGROUND_COLOR);
        mainScrollPane.getViewport().setBackground(M6.BACKGROUND_COLOR);
        mainScrollPane.setBorder(null);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentPanel.add(mainScrollPane);

        // Cargar datos
        cargarClientes(tableModel);
    }
    
    private void cargarClientes(DefaultTableModel tableModel) {
        // Limpiar tabla manteniendo el header
        tableModel.setRowCount(0);
        Object[] headerRow = {"ID Cliente", "DNI", "Nombre", "Apellido", "Membresía", "Boletos Comprados", "Mail", "Contraseña"};
        tableModel.addRow(headerRow);
        
        try {
            String query = "SELECT " +
                          "c.ID_Cliente, " +
                          "u.DNI, " +
                          "u.Nombre, " +
                          "u.Apellido, " +
                          "c.Mail, " +
                          "c.Contrasena, " +
                          "c.Membresia, " +
                          "COALESCE(SUM(cb.Cantidad), 0) as TotalBoletos " +
                          "FROM Cliente c " +
                          "INNER JOIN Usuario u ON c.DNI = u.DNI " +
                          "LEFT JOIN Comprobante comp ON c.ID_Cliente = comp.ID_Cliente " +
                          "LEFT JOIN Comprobante_Boleto cb ON comp.ID_Comprobante = cb.ID_Comprobante " +
                          "GROUP BY c.ID_Cliente, u.DNI, u.Nombre, u.Apellido, c.Membresia, c.Mail, c.Contrasena " +
                          "ORDER BY c.ID_Cliente";
            
            try (Statement stmt = mainFrame.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    String password = rs.getString("Contrasena");
                    String censoredPassword = "****" + password.substring(password.length() - 3);
                    Object[] row = {
                        rs.getInt("ID_Cliente"),
                        rs.getInt("DNI"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        rs.getString("Membresia"),
                        rs.getInt("TotalBoletos"),
                        rs.getString("Mail"),
                        censoredPassword
                    };
                    tableModel.addRow(row);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error al cargar clientes: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void abrirVentanaMetodosPago(int idCliente, String nombreCompleto) {
        // Crear ventana emergente
        JDialog dialog = new JDialog(mainFrame, "Métodos de Pago - " + nombreCompleto, true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(M6.BACKGROUND_COLOR);
        
        // Panel superior con imagen TOPBAR
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(M6.TOPBAR_COLOR);
        topPanel.setPreferredSize(new Dimension(800, 80));
        
        try {
            java.net.URL imgURL = new java.net.URL("https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=logos%2FCINEMARX%20imagotipo.png");
            java.io.InputStream imageIn = imgURL.openStream();
            java.awt.image.BufferedImage originalImage = javax.imageio.ImageIO.read(imageIn);
            imageIn.close();
            
            if (originalImage != null) {
                int maxHeight = 50;
                int newHeight = maxHeight;
                int newWidth = (originalImage.getWidth() * newHeight) / originalImage.getHeight();
                Image scaledImg = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
                imageLabel.setHorizontalAlignment(SwingConstants.LEFT);
                imageLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 0));
                topPanel.add(imageLabel, BorderLayout.WEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        dialog.add(topPanel, BorderLayout.NORTH);
        
        // Panel central con tabla de métodos de pago
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(M6.BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel infoLabel = UIHelpers.createLabel("Métodos de pago registrados:");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        centerPanel.add(infoLabel, BorderLayout.NORTH);
        
        // Tabla de métodos de pago
        String[] columnNames = {"ID", "Empresa", "Tipo", "Número", "Caducidad"};  // SIN "Titular"
        DefaultTableModel metodosPagoModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Agregar header
        Object[] headerRow = {"ID", "Empresa", "Tipo", "Número", "Caducidad"};  // SIN "Titular"
        metodosPagoModel.addRow(headerRow);

        
        JTable metodosPagoTable = new JTable(metodosPagoModel);
        metodosPagoTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        metodosPagoTable.setForeground(M6.TEXT_COLOR);
        metodosPagoTable.setBackground(new Color(40, 40, 40));
        metodosPagoTable.setGridColor(new Color(60, 60, 60));
        metodosPagoTable.setRowHeight(30);
        metodosPagoTable.setTableHeader(null);
        
        JScrollPane scrollPane = new JScrollPane(metodosPagoTable);
        scrollPane.setBackground(M6.BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        dialog.add(centerPanel, BorderLayout.CENTER);
        
        // Panel inferior con botón cerrar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(M6.BACKGROUND_COLOR);
        
        JButton cerrarBtn = UIHelpers.createButton("Cerrar");
        cerrarBtn.setBackground(new Color(100, 100, 100));
        cerrarBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cerrarBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Cargar métodos de pago
        cargarMetodosPago(idCliente, metodosPagoModel);
        
        dialog.setVisible(true);
    }
    
    private void cargarMetodosPago(int idCliente, DefaultTableModel tableModel) {
        // Limpiar y mantener header
        tableModel.setRowCount(0);
        Object[] headerRow = {"ID", "Empresa", "Tipo", "Número", "Caducidad"};  // SIN "Titular"
        tableModel.addRow(headerRow);

        try {
            String query = "SELECT ID_Metodo, Empresa, Tipo, Numero, FechaCaducidad " +  // SIN NombreTitular
                          "FROM MetodosPago " +
                          "WHERE ID_Cliente = ? " +
                          "ORDER BY ID_Metodo";

            try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
                pstmt.setInt(1, idCliente);
                ResultSet rs = pstmt.executeQuery();

                boolean hayMetodos = false;
                while (rs.next()) {
                    hayMetodos = true;
                    String numeroTarjeta = rs.getString("Numero");
                    // Ocultar dígitos excepto los últimos 4
                    String numeroOculto = "**** **** **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4);

                    Object[] row = {
                        rs.getInt("ID_Metodo"),
                        rs.getString("Empresa"),
                        rs.getString("Tipo"),
                        numeroOculto,
                        rs.getString("FechaCaducidad")  // SIN NombreTitular
                    };
                    tableModel.addRow(row);
                }

                if (!hayMetodos) {
                    Object[] emptyRow = {"---", "No hay métodos de pago registrados", "---", "---", "---"};  // 5 columnas ahora
                    tableModel.addRow(emptyRow);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error al cargar métodos de pago: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}