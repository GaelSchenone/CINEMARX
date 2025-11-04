package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;

public class Ventas {
    
    private M6 mainFrame;
    private JPanel contentPanel;
    
    public Ventas(M6 mainFrame, JPanel contentPanel) {
        this.mainFrame = mainFrame;
        this.contentPanel = contentPanel;
    }
    
    public void mostrar() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(M6.BACKGROUND_COLOR);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título principal
        JLabel titleLabel = new JLabel("Ventas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(M6.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel de ventas totales
        JPanel ventasTotalesPanel = createVentasPanel("Ventas Totales", true);
        ventasTotalesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel de ventas de boletos
        JPanel ventasBoletosPanel = createVentasPanel("Ventas de Boletos", false);
        ventasBoletosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel de ventas de productos
        JPanel ventasProductosPanel = createVentasPanel("Ventas de Productos", false);
        ventasProductosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel para función específica
        JPanel funcionPanel = new JPanel();
        funcionPanel.setLayout(new BoxLayout(funcionPanel, BoxLayout.Y_AXIS));
        funcionPanel.setBackground(new Color(45, 45, 45));
        funcionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        funcionPanel.setMaximumSize(new Dimension(700, 200));
        funcionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel funcionTitleLabel = new JLabel("Ventas por Función");
        funcionTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        funcionTitleLabel.setForeground(M6.SECTION_TITLE_COLOR);
        funcionTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ComboBox de funciones
        JPanel comboPanel = new JPanel();
        comboPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 10));
        comboPanel.setBackground(new Color(45, 45, 45));
        comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel funcionLabel = new JLabel("Seleccione una Función: ");
        funcionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        funcionLabel.setForeground(M6.TEXT_COLOR);

        JComboBox<FuncionItem> funcionComboBox = new JComboBox<>();
        funcionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        funcionComboBox.setPreferredSize(new Dimension(400, 35));
        funcionComboBox.setBackground(new Color(50, 50, 50));
        funcionComboBox.setForeground(Color.BLACK);

        comboPanel.add(funcionLabel);
        comboPanel.add(funcionComboBox);

        // Labels para mostrar resultados de función
        JLabel cantidadBoletosLabel = new JLabel("Cantidad de boletos: 0");
        cantidadBoletosLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cantidadBoletosLabel.setForeground(M6.TEXT_COLOR);
        cantidadBoletosLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        cantidadBoletosLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dineroFuncionLabel = new JLabel("Dinero obtenido: $0.00");
        dineroFuncionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dineroFuncionLabel.setForeground(M6.TEXT_COLOR);
        dineroFuncionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        funcionPanel.add(funcionTitleLabel);
        funcionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        funcionPanel.add(comboPanel);
        funcionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        funcionPanel.add(cantidadBoletosLabel);
        funcionPanel.add(dineroFuncionLabel);

        // Cargar datos
        cargarDatosVentas(ventasTotalesPanel, ventasBoletosPanel, ventasProductosPanel);
        DatabaseHelper.cargarFunciones(mainFrame, funcionComboBox);

        // Listener para cambios en el ComboBox
        funcionComboBox.addActionListener(e -> {
            FuncionItem selectedFuncion = (FuncionItem) funcionComboBox.getSelectedItem();
            if (selectedFuncion != null) {
                cargarVentasFuncion(selectedFuncion.getId(), cantidadBoletosLabel, dineroFuncionLabel);
            }
        });

        // Agregar componentes al contenedor principal
        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(ventasTotalesPanel);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(ventasBoletosPanel);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(ventasProductosPanel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(funcionPanel);

        contentPanel.add(container);

        // Cargar datos iniciales de función si hay funciones disponibles
        if (funcionComboBox.getItemCount() > 0) {
            FuncionItem firstFuncion = funcionComboBox.getItemAt(0);
            cargarVentasFuncion(firstFuncion.getId(), cantidadBoletosLabel, dineroFuncionLabel);
        }
    }

    private JPanel createVentasPanel(String titulo, boolean soloTotal) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(45, 45, 45));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setMaximumSize(new Dimension(700, soloTotal ? 80 : 120));

        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(M6.SECTION_TITLE_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel montoLabel = new JLabel("$0.00");
        montoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        montoLabel.setForeground(M6.ACCENT_COLOR);
        montoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        montoLabel.setName("monto");

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        if (!soloTotal) {
            JLabel cantidadLabel = new JLabel("Cantidad vendida: 0");
            cantidadLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cantidadLabel.setForeground(M6.TEXT_COLOR);
            cantidadLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            cantidadLabel.setName("cantidad");
            panel.add(cantidadLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        panel.add(montoLabel);

        return panel;
    }
// CONTINUACIÓN DE Ventas.java - Agregar estos métodos a la clase Ventas

    private void cargarDatosVentas(JPanel ventasTotalesPanel, JPanel ventasBoletosPanel, JPanel ventasProductosPanel) {
        DecimalFormat df = new DecimalFormat("#,##0.00");

        try {
            // Calcular ventas totales de boletos
            String queryBoletos = "SELECT SUM(cb.Cantidad * b.Precio) as TotalBoletos, SUM(cb.Cantidad) as CantidadBoletos " +
                                 "FROM Comprobante_Boleto cb " +
                                 "INNER JOIN Boleto b ON cb.ID_Boleto = b.ID_Boleto";

            double totalBoletos = 0;
            int cantidadBoletos = 0;

            try (Statement stmt = mainFrame.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(queryBoletos)) {
                if (rs.next()) {
                    totalBoletos = rs.getDouble("TotalBoletos");
                    cantidadBoletos = rs.getInt("CantidadBoletos");
                }
            }

            // Calcular ventas totales de productos
            String queryProductos = "SELECT SUM(cp.Cantidad * p.Precio) as TotalProductos, SUM(cp.Cantidad) as CantidadProductos " +
                                   "FROM Comprobante_Producto cp " +
                                   "INNER JOIN Producto p ON cp.ID_Prod = p.ID_Prod";

            double totalProductos = 0;
            int cantidadProductos = 0;

            try (Statement stmt = mainFrame.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(queryProductos)) {
                if (rs.next()) {
                    totalProductos = rs.getDouble("TotalProductos");
                    cantidadProductos = rs.getInt("CantidadProductos");
                }
            }

            // Actualizar ventas totales
            double ventasTotales = totalBoletos + totalProductos;
            for (Component comp : ventasTotalesPanel.getComponents()) {
                if (comp instanceof JLabel && "monto".equals(comp.getName())) {
                    ((JLabel) comp).setText("$" + df.format(ventasTotales));
                }
            }

            // Actualizar ventas de boletos
            for (Component comp : ventasBoletosPanel.getComponents()) {
                if (comp instanceof JLabel) {
                    if ("monto".equals(comp.getName())) {
                        ((JLabel) comp).setText("$" + df.format(totalBoletos));
                    } else if ("cantidad".equals(comp.getName())) {
                        ((JLabel) comp).setText("Cantidad vendida: " + cantidadBoletos);
                    }
                }
            }

            // Actualizar ventas de productos
            for (Component comp : ventasProductosPanel.getComponents()) {
                if (comp instanceof JLabel) {
                    if ("monto".equals(comp.getName())) {
                        ((JLabel) comp).setText("$" + df.format(totalProductos));
                    } else if ("cantidad".equals(comp.getName())) {
                        ((JLabel) comp).setText("Cantidad vendida: " + cantidadProductos);
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error al cargar datos de ventas: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarVentasFuncion(int idFuncion, JLabel cantidadLabel, JLabel dineroLabel) {
        DecimalFormat df = new DecimalFormat("#,##0.00");

        try {
            String query = "SELECT COUNT(b.ID_Boleto) as Cantidad, SUM(b.Precio) as Total " +
                          "FROM Boleto b " +
                          "WHERE b.ID_Funcion = ?";

            try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
                pstmt.setInt(1, idFuncion);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int cantidad = rs.getInt("Cantidad");
                    double total = rs.getDouble("Total");

                    cantidadLabel.setText("Cantidad de boletos: " + cantidad);
                    dineroLabel.setText("Dinero obtenido: $" + df.format(total));
                } else {
                    cantidadLabel.setText("Cantidad de boletos: 0");
                    dineroLabel.setText("Dinero obtenido: $0.00");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error al cargar ventas de función: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

// FIN DE LA CLASE Ventas.java