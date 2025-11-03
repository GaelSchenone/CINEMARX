package CINEMARX.M6;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;

public class M6 extends JFrame {
    
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JPanel topBarPanel;
    
    // Colores
    private final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private final Color SIDEBAR_COLOR = new Color(40, 40, 40);
    private final Color TOPBAR_COLOR = new Color(45, 45, 45);
    private final Color BUTTON_COLOR = new Color(50, 50, 50);
    private final Color BUTTON_HOVER_COLOR = Color.decode("#2B2B2B");
    private final Color TEXT_COLOR = new Color(220, 220, 220);
    private final Color FIELDTEXT_COLOR = new Color(120,120,120);
    private final Color ACCENT_COLOR = new Color(239, 68, 68);
    private final Color SECTION_TITLE_COLOR = new Color(200, 200, 200);
    
    // Configuración de base de datos - NUEVAS CREDENCIALES
    private final String DB_URL = "jdbc:mariadb://br1.aguilucho.ar:25584/Cinemarx";
    private final String DB_USER = "cnx_admin";
    private final String DB_PASSWORD = "CnxAdmin!620";
    
    // Conexión persistente
    private Connection connection;
    
    public M6() {
        try {
            // Establecer conexión al inicializar
            initDatabaseConnection();
            initComponents();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error al conectar con la base de datos:\n" + e.getMessage(),
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Inicializa la conexión a la base de datos
     */
    private void initDatabaseConnection() throws SQLException {
        try {
            // Cargar el driver de MariaDB
            Class.forName("org.mariadb.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Conexión a base de datos establecida correctamente");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de MariaDB no encontrado: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la conexión actual o crea una nueva si está cerrada
     */
    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initDatabaseConnection();
        }
        return connection;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    private void closeDatabaseConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión a base de datos cerrada correctamente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void initComponents() {
        setTitle("Panel de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());
        
        // Agregar listener para cerrar la conexión al cerrar la ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                closeDatabaseConnection();
            }
        });
        
        createTopBar();
        createSidebar();
        
        contentPanel = new JPanel();
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 30));
        add(contentPanel, BorderLayout.CENTER);
    }
    
    // Modificar el método showContent existente:
    private void showContent(String buttonName) {
        contentPanel.removeAll();

        if (buttonName.equals("Estadísticas de ocupación")) {
            showEstadisticasOcupacion();
        } else if (buttonName.equals("Ventas")) {
            showVentas();
        } else if (buttonName.equals("ABM")) {
            showABM();
        } else {
            showDefaultContent(buttonName);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showEstadisticasOcupacion() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BACKGROUND_COLOR);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Estadísticas de Ocupación");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Panel para el ComboBox
        JPanel comboPanel = new JPanel();
        comboPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 10));
        comboPanel.setBackground(BACKGROUND_COLOR);
        comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel salaLabel = new JLabel("Seleccione una Sala: ");
        salaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        salaLabel.setForeground(TEXT_COLOR);
        
        JComboBox<SalaItem> salaComboBox = new JComboBox<>();
        salaComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salaComboBox.setPreferredSize(new Dimension(200, 35));
        salaComboBox.setBackground(new Color(50, 50, 50));
        salaComboBox.setForeground(TEXT_COLOR);
        
        comboPanel.add(salaLabel);
        comboPanel.add(salaComboBox);
        
        // Panel para la tabla de estadísticas
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tablePanel.setMaximumSize(new Dimension(900, 400));
        tablePanel.setPreferredSize(new Dimension(900, 400));
        
        // Crear tabla
        String[] columnNames = {"ID Función", "Película", "Fecha", "Hora", "Butacas Ocupadas", "Total Butacas", "Ocupación %"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Agregar la primera fila con los nombres de las columnas
        Object[] headerRow = {"ID Función", "Película", "Fecha", "Hora", "Butacas Ocup.", "Total Butac.", "Ocupación %"};
        tableModel.addRow(headerRow);
        
        JTable table = new JTable(tableModel);
        // COLOR DE FONDO Y TEXTO DE LAS CELDAS DE LA TABLA
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(TEXT_COLOR);  // Color del texto de las celdas (blanco)
        table.setBackground(new Color(40, 40, 40));  // Color de fondo de las celdas (gris oscuro)
        table.setGridColor(new Color(60, 60, 60));
        table.setRowHeight(30);
        
        // OCULTAR EL ENCABEZADO DE LA TABLA
        table.setTableHeader(null);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Cargar salas en el ComboBox
        cargarSalas(salaComboBox);
        
        // Listener para cambios en el ComboBox
        salaComboBox.addActionListener(e -> {
            SalaItem selectedSala = (SalaItem) salaComboBox.getSelectedItem();
            if (selectedSala != null) {
                cargarEstadisticasSala(selectedSala.getId(), tableModel);
            }
        });
        
        // Agregar componentes al contenedor
        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(comboPanel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(tablePanel);
        
        contentPanel.add(container);
        
        // Cargar datos iniciales si hay salas
        if (salaComboBox.getItemCount() > 0) {
            SalaItem firstSala = salaComboBox.getItemAt(0);
            cargarEstadisticasSala(firstSala.getId(), tableModel);
        }
    }
    private void showVentas() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BACKGROUND_COLOR);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título principal
        JLabel titleLabel = new JLabel("Ventas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
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
        funcionTitleLabel.setForeground(SECTION_TITLE_COLOR);
        funcionTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ComboBox de funciones
        JPanel comboPanel = new JPanel();
        comboPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 10));
        comboPanel.setBackground(new Color(45, 45, 45));
        comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel funcionLabel = new JLabel("Seleccione una Función: ");
        funcionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        funcionLabel.setForeground(TEXT_COLOR);

        JComboBox<FuncionItem> funcionComboBox = new JComboBox<>();
        funcionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        funcionComboBox.setPreferredSize(new Dimension(400, 35));
        funcionComboBox.setBackground(new Color(50, 50, 50));
        funcionComboBox.setForeground(TEXT_COLOR);

        comboPanel.add(funcionLabel);
        comboPanel.add(funcionComboBox);

        // Labels para mostrar resultados de función
        JLabel cantidadBoletosLabel = new JLabel("Cantidad de boletos: 0");
        cantidadBoletosLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cantidadBoletosLabel.setForeground(TEXT_COLOR);
        cantidadBoletosLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        cantidadBoletosLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dineroFuncionLabel = new JLabel("Dinero obtenido: $0.00");
        dineroFuncionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dineroFuncionLabel.setForeground(TEXT_COLOR);
        dineroFuncionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        funcionPanel.add(funcionTitleLabel);
        funcionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        funcionPanel.add(comboPanel);
        funcionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        funcionPanel.add(cantidadBoletosLabel);
        funcionPanel.add(dineroFuncionLabel);

        // Cargar datos
        cargarDatosVentas(ventasTotalesPanel, ventasBoletosPanel, ventasProductosPanel);
        cargarFunciones(funcionComboBox);

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
        titleLabel.setForeground(SECTION_TITLE_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel montoLabel = new JLabel("$0.00");
        montoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        montoLabel.setForeground(ACCENT_COLOR);
        montoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        montoLabel.setName("monto");

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        if (!soloTotal) {
            JLabel cantidadLabel = new JLabel("Cantidad vendida: 0");
            cantidadLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cantidadLabel.setForeground(TEXT_COLOR);
            cantidadLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            cantidadLabel.setName("cantidad");
            panel.add(cantidadLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        panel.add(montoLabel);

        return panel;
    }

    private void cargarDatosVentas(JPanel ventasTotalesPanel, JPanel ventasBoletosPanel, JPanel ventasProductosPanel) {
        DecimalFormat df = new DecimalFormat("#,##0.00");

        try {
            // Calcular ventas totales de boletos
            String queryBoletos = "SELECT SUM(cb.Cantidad * b.Precio) as TotalBoletos, SUM(cb.Cantidad) as CantidadBoletos " +
                                 "FROM Comprobante_Boleto cb " +
                                 "INNER JOIN Boleto b ON cb.ID_Boleto = b.ID_Boleto";

            double totalBoletos = 0;
            int cantidadBoletos = 0;

            try (Statement stmt = getConnection().createStatement();
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

            try (Statement stmt = getConnection().createStatement();
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
            JOptionPane.showMessageDialog(this, 
                "Error al cargar datos de ventas: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarFunciones(JComboBox<FuncionItem> comboBox) {
        try {
            String query = "SELECT f.ID_Funcion, p.Titulo, f.FechaFuncion, f.HoraFuncion, s.Numero " +
                          "FROM Funcion f " +
                          "INNER JOIN Pelicula p ON f.ID_Pelicula = p.ID_Pelicula " +
                          "INNER JOIN Sala s ON f.ID_Sala = s.ID_Sala " +
                          "ORDER BY f.FechaFuncion DESC, f.HoraFuncion DESC";

            try (Statement stmt = getConnection().createStatement();
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
            JOptionPane.showMessageDialog(this, 
                "Error al cargar las funciones: " + e.getMessage(), 
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

            try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
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
            JOptionPane.showMessageDialog(this, 
                "Error al cargar ventas de función: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Clase auxiliar para items del ComboBox de Funciones
    class FuncionItem {
        private int id;
        private String titulo;
        private String fecha;
        private String hora;
        private int numeroSala;

        public FuncionItem(int id, String titulo, String fecha, String hora, int numeroSala) {
            this.id = id;
            this.titulo = titulo;
            this.fecha = fecha;
            this.hora = hora;
            this.numeroSala = numeroSala;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return titulo + " - " + fecha + " " + hora + " (Sala " + numeroSala + ")";
        }
    }
    private void showABM() {
    JPanel container = new JPanel();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.setBackground(BACKGROUND_COLOR);
    container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Título principal
    JLabel titleLabel = new JLabel("ABM - Alta, Baja, Modificación");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setForeground(TEXT_COLOR);
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    // Panel de selección
    JPanel selectionPanel = new JPanel();
    selectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
    selectionPanel.setBackground(BACKGROUND_COLOR);
    selectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    JLabel operacionLabel = new JLabel("Operación:");
    operacionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    operacionLabel.setForeground(TEXT_COLOR);
    
    String[] operaciones = {"Alta", "Baja", "Modificación"};
    JComboBox<String> operacionComboBox = new JComboBox<>(operaciones);
    operacionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    operacionComboBox.setPreferredSize(new Dimension(150, 35));
    operacionComboBox.setBackground(new Color(50, 50, 50));
    operacionComboBox.setForeground(TEXT_COLOR);
    
    JLabel entidadLabel = new JLabel("Entidad:");
    entidadLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    entidadLabel.setForeground(TEXT_COLOR);
    
    String[] entidades = {"Función", "Película"};
    JComboBox<String> entidadComboBox = new JComboBox<>(entidades);
    entidadComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    entidadComboBox.setPreferredSize(new Dimension(150, 35));
    entidadComboBox.setBackground(new Color(50, 50, 50));
    entidadComboBox.setForeground(TEXT_COLOR);
    
    selectionPanel.add(operacionLabel);
    selectionPanel.add(operacionComboBox);
    selectionPanel.add(Box.createRigidArea(new Dimension(20, 0)));
    selectionPanel.add(entidadLabel);
    selectionPanel.add(entidadComboBox);
    
    // Panel de contenido dinámico
    JPanel dynamicPanel = new JPanel();
    dynamicPanel.setLayout(new BoxLayout(dynamicPanel, BoxLayout.Y_AXIS));
    dynamicPanel.setBackground(BACKGROUND_COLOR);
    dynamicPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    // Listener para cambios en los ComboBox
    ActionListener updateListener = e -> {
        String operacion = (String) operacionComboBox.getSelectedItem();
        String entidad = (String) entidadComboBox.getSelectedItem();
        actualizarPanelABM(dynamicPanel, operacion, entidad);
    };
    
    operacionComboBox.addActionListener(updateListener);
    entidadComboBox.addActionListener(updateListener);
    
    // Agregar componentes al contenedor principal
    container.add(titleLabel);
    container.add(Box.createRigidArea(new Dimension(0, 20)));
    container.add(selectionPanel);
    container.add(Box.createRigidArea(new Dimension(0, 20)));
    container.add(dynamicPanel);
    
    contentPanel.add(container);
    
    // Cargar contenido inicial
    actualizarPanelABM(dynamicPanel, "Alta", "Función");
}

private void actualizarPanelABM(JPanel dynamicPanel, String operacion, String entidad) {
    dynamicPanel.removeAll();
    
    if (operacion.equals("Alta")) {
        if (entidad.equals("Función")) {
            mostrarAltaFuncion(dynamicPanel);
        } else {
            mostrarAltaPelicula(dynamicPanel);
        }
    } else if (operacion.equals("Baja")) {
        if (entidad.equals("Función")) {
            mostrarBajaFuncion(dynamicPanel);
        } else {
            mostrarBajaPelicula(dynamicPanel);
        }
    } else if (operacion.equals("Modificación")) {
        if (entidad.equals("Función")) {
            mostrarModificacionFuncion(dynamicPanel);
        } else {
            mostrarModificacionPelicula(dynamicPanel);
        }
    }
    
    dynamicPanel.revalidate();
    dynamicPanel.repaint();
}

// ==================== ALTA FUNCIÓN ====================
private void mostrarAltaFuncion(JPanel panel) {
    JPanel formPanel = new JPanel();
    formPanel.setLayout(new GridBagLayout());
    formPanel.setBackground(new Color(45, 45, 45));
    formPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));
    formPanel.setMaximumSize(new Dimension(700, 400));
    formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    
    // Hora
    gbc.gridx = 0; gbc.gridy = 0;
    formPanel.add(createLabel("Hora (HH:MM:SS):"), gbc);
    gbc.gridx = 1;
    JTextField horaField = createTextField();
    formPanel.add(horaField, gbc);
    
    // Fecha
    gbc.gridx = 0; gbc.gridy = 1;
    formPanel.add(createLabel("Fecha (YYYY-MM-DD):"), gbc);
    gbc.gridx = 1;
    JTextField fechaField = createTextField();
    formPanel.add(fechaField, gbc);
    
    // Estado
    gbc.gridx = 0; gbc.gridy = 2;
    formPanel.add(createLabel("Estado:"), gbc);
    gbc.gridx = 1;
    JTextField estadoField = createTextField();
    formPanel.add(estadoField, gbc);
    
    // Película
    gbc.gridx = 0; gbc.gridy = 3;
    formPanel.add(createLabel("Película:"), gbc);
    gbc.gridx = 1;
    JComboBox<PeliculaItem> peliculaCombo = new JComboBox<>();
    styleComboBox(peliculaCombo);
    cargarPeliculas(peliculaCombo);
    formPanel.add(peliculaCombo, gbc);
    
    // Sala
    gbc.gridx = 0; gbc.gridy = 4;
    formPanel.add(createLabel("Sala:"), gbc);
    gbc.gridx = 1;
    JComboBox<SalaItem> salaCombo = new JComboBox<>();
    styleComboBox(salaCombo);
    cargarSalas(salaCombo);
    formPanel.add(salaCombo, gbc);
    
    // Cartelera
    gbc.gridx = 0; gbc.gridy = 5;
    formPanel.add(createLabel("Cartelera:"), gbc);
    gbc.gridx = 1;
    JComboBox<CarteleraItem> carteleraCombo = new JComboBox<>();
    styleComboBox(carteleraCombo);
    cargarCarteleras(carteleraCombo);
    formPanel.add(carteleraCombo, gbc);
    
    // Botón
    gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
    JButton guardarBtn = createButton("Guardar Función");
    formPanel.add(guardarBtn, gbc);
    
    guardarBtn.addActionListener(e -> {
        try {
            String hora = horaField.getText().trim();
            String fecha = fechaField.getText().trim();
            String estado = estadoField.getText().trim();
            PeliculaItem pelicula = (PeliculaItem) peliculaCombo.getSelectedItem();
            SalaItem sala = (SalaItem) salaCombo.getSelectedItem();
            CarteleraItem cartelera = (CarteleraItem) carteleraCombo.getSelectedItem();
            
            if (hora.isEmpty() || fecha.isEmpty() || pelicula == null || sala == null || cartelera == null) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos obligatorios", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String query = "INSERT INTO Funcion (HoraFuncion, FechaFuncion, Estado, ID_Pelicula, ID_Sala, ID_Cartelera) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
                pstmt.setString(1, hora);
                pstmt.setString(2, fecha);
                pstmt.setString(3, estado.isEmpty() ? null : estado);
                pstmt.setInt(4, pelicula.getId());
                pstmt.setInt(5, sala.getId());
                pstmt.setInt(6, cartelera.getId());
                
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Función creada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                // Limpiar campos
                horaField.setText("");
                fechaField.setText("");
                estadoField.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al crear función: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    });
    
    panel.add(formPanel);
}

// ==================== ALTA PELÍCULA ====================
    private void mostrarAltaPelicula(JPanel panel) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setMaximumSize(new Dimension(700, 300));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Título:"), gbc);
        gbc.gridx = 1;
        JTextField tituloField = createTextField();
        formPanel.add(tituloField, gbc);

        // Género
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createLabel("Género:"), gbc);
        gbc.gridx = 1;
        JTextField generoField = createTextField();
        formPanel.add(generoField, gbc);

        // Clasificación
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createLabel("Clasificación de Edad:"), gbc);
        gbc.gridx = 1;
        JTextField clasificacionField = createTextField();
        formPanel.add(clasificacionField, gbc);

        // Estado
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createLabel("Estado:"), gbc);
        gbc.gridx = 1;
        JTextField estadoField = createTextField();
        formPanel.add(estadoField, gbc);

        // Botón
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton guardarBtn = createButton("Guardar Película");
        formPanel.add(guardarBtn, gbc);

        guardarBtn.addActionListener(e -> {
            try {
                String titulo = tituloField.getText().trim();
                String genero = generoField.getText().trim();
                String clasificacion = clasificacionField.getText().trim();
                String estado = estadoField.getText().trim();

                if (titulo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El título es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String query = "INSERT INTO Pelicula (Titulo, Genero, ClasificacionEdad, Estado) VALUES (?, ?, ?, ?)";

                try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
                    pstmt.setString(1, titulo);
                    pstmt.setString(2, genero.isEmpty() ? null : genero);
                    pstmt.setString(3, clasificacion.isEmpty() ? null : clasificacion);
                    pstmt.setString(4, estado.isEmpty() ? null : estado);

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Película creada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    // Limpiar campos
                    tituloField.setText("");
                    generoField.setText("");
                    clasificacionField.setText("");
                    estadoField.setText("");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al crear película: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        panel.add(formPanel);
    }
    private void mostrarBajaFuncion(JPanel panel) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setMaximumSize(new Dimension(700, 200));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = createLabel("Seleccione la función a eliminar:");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(infoLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JComboBox<FuncionItem> funcionCombo = new JComboBox<>();
        styleComboBox(funcionCombo);
        funcionCombo.setMaximumSize(new Dimension(650, 35));
        funcionCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cargarFunciones(funcionCombo);
        formPanel.add(funcionCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton eliminarBtn = createButton("Eliminar Función");
        eliminarBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(eliminarBtn);

        eliminarBtn.addActionListener(e -> {
            FuncionItem funcion = (FuncionItem) funcionCombo.getSelectedItem();
            if (funcion == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una función", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar esta función?\n" + funcion.toString(), 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM Funcion WHERE ID_Funcion = ?";
                    try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
                        pstmt.setInt(1, funcion.getId());
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Función eliminada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                        // Recargar ComboBox
                        funcionCombo.removeAllItems();
                        cargarFunciones(funcionCombo);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar función: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        panel.add(formPanel);
    }

    // ==================== BAJA PELÍCULA ====================
    private void mostrarBajaPelicula(JPanel panel) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setMaximumSize(new Dimension(700, 200));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = createLabel("Seleccione la película a eliminar:");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(infoLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JComboBox<PeliculaItem> peliculaCombo = new JComboBox<>();
        styleComboBox(peliculaCombo);
        peliculaCombo.setMaximumSize(new Dimension(650, 35));
        peliculaCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cargarPeliculas(peliculaCombo);
        formPanel.add(peliculaCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton eliminarBtn = createButton("Eliminar Película");
        eliminarBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(eliminarBtn);

        eliminarBtn.addActionListener(e -> {
            PeliculaItem pelicula = (PeliculaItem) peliculaCombo.getSelectedItem();
            if (pelicula == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una película", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar esta película?\n" + pelicula.toString(), 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM Pelicula WHERE ID_Pelicula = ?";
                    try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
                        pstmt.setInt(1, pelicula.getId());
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Película eliminada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                        // Recargar ComboBox
                        peliculaCombo.removeAllItems();
                        cargarPeliculas(peliculaCombo);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar película: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        panel.add(formPanel);
    }

    // ==================== MODIFICACIÓN FUNCIÓN ====================
    private void mostrarModificacionFuncion(JPanel panel) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setMaximumSize(new Dimension(700, 500));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = createLabel("Seleccione la función a modificar:");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(infoLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JComboBox<FuncionItem> funcionCombo = new JComboBox<>();
        styleComboBox(funcionCombo);
        funcionCombo.setMaximumSize(new Dimension(650, 35));
        funcionCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cargarFunciones(funcionCombo);
        formPanel.add(funcionCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel de formulario con campos
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(new Color(45, 45, 45));
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(createLabel("Hora:"), gbc);
        gbc.gridx = 1;
        JTextField horaField = createTextField();
        fieldsPanel.add(horaField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(createLabel("Fecha:"), gbc);
        gbc.gridx = 1;
        JTextField fechaField = createTextField();
        fieldsPanel.add(fechaField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        fieldsPanel.add(createLabel("Estado:"), gbc);
        gbc.gridx = 1;
        JTextField estadoField = createTextField();
        fieldsPanel.add(estadoField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        fieldsPanel.add(createLabel("Película:"), gbc);
        gbc.gridx = 1;
        JComboBox<PeliculaItem> peliculaCombo = new JComboBox<>();
        styleComboBox(peliculaCombo);
        cargarPeliculas(peliculaCombo);
        fieldsPanel.add(peliculaCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        fieldsPanel.add(createLabel("Sala:"), gbc);
        gbc.gridx = 1;
        JComboBox<SalaItem> salaCombo = new JComboBox<>();
        styleComboBox(salaCombo);
        cargarSalas(salaCombo);
        fieldsPanel.add(salaCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        fieldsPanel.add(createLabel("Cartelera:"), gbc);
        gbc.gridx = 1;
        JComboBox<CarteleraItem> carteleraCombo = new JComboBox<>();
        styleComboBox(carteleraCombo);
        cargarCarteleras(carteleraCombo);
        fieldsPanel.add(carteleraCombo, gbc);

        formPanel.add(fieldsPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton actualizarBtn = createButton("Actualizar Función");
        actualizarBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(actualizarBtn);

        // Listener para cargar datos al seleccionar función
        funcionCombo.addActionListener(e -> {
            FuncionItem funcion = (FuncionItem) funcionCombo.getSelectedItem();
            if (funcion != null) {
                cargarDatosFuncion(funcion.getId(), horaField, fechaField, estadoField, 
                                  peliculaCombo, salaCombo, carteleraCombo);
            }
        });

        // Cargar datos iniciales
        if (funcionCombo.getItemCount() > 0) {
            FuncionItem firstFuncion = funcionCombo.getItemAt(0);
            cargarDatosFuncion(firstFuncion.getId(), horaField, fechaField, estadoField, 
                              peliculaCombo, salaCombo, carteleraCombo);
        }

        actualizarBtn.addActionListener(e -> {
            FuncionItem funcion = (FuncionItem) funcionCombo.getSelectedItem();
            if (funcion == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una función", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String query = "UPDATE Funcion SET HoraFuncion = ?, FechaFuncion = ?, Estado = ?, " +
                              "ID_Pelicula = ?, ID_Sala = ?, ID_Cartelera = ? WHERE ID_Funcion = ?";

                try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
                    pstmt.setString(1, horaField.getText().trim());
                    pstmt.setString(2, fechaField.getText().trim());
                    pstmt.setString(3, estadoField.getText().trim());
                    pstmt.setInt(4, ((PeliculaItem) peliculaCombo.getSelectedItem()).getId());
                    pstmt.setInt(5, ((SalaItem) salaCombo.getSelectedItem()).getId());
                    pstmt.setInt(6, ((CarteleraItem) carteleraCombo.getSelectedItem()).getId());
                    pstmt.setInt(7, funcion.getId());

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Función actualizada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    // Recargar ComboBox
                    funcionCombo.removeAllItems();
                    cargarFunciones(funcionCombo);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar función: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        panel.add(formPanel);
    }
        // ==================== MODIFICACIÓN PELÍCULA ====================
    private void mostrarModificacionPelicula(JPanel panel) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setMaximumSize(new Dimension(700, 450));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = createLabel("Seleccione la película a modificar:");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(infoLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JComboBox<PeliculaItem> peliculaCombo = new JComboBox<>();
        styleComboBox(peliculaCombo);
        peliculaCombo.setMaximumSize(new Dimension(650, 35));
        peliculaCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cargarPeliculas(peliculaCombo);
        formPanel.add(peliculaCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel de formulario con campos
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(new Color(45, 45, 45));
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(createLabel("Título:"), gbc);
        gbc.gridx = 1;
        JTextField tituloField = createTextField();
        fieldsPanel.add(tituloField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(createLabel("Género:"), gbc);
        gbc.gridx = 1;
        JTextField generoField = createTextField();
        fieldsPanel.add(generoField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        fieldsPanel.add(createLabel("Clasificación:"), gbc);
        gbc.gridx = 1;
        JTextField clasificacionField = createTextField();
        fieldsPanel.add(clasificacionField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        fieldsPanel.add(createLabel("Estado:"), gbc);
        gbc.gridx = 1;
        JTextField estadoField = createTextField();
        fieldsPanel.add(estadoField, gbc);

        formPanel.add(fieldsPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton actualizarBtn = createButton("Actualizar Película");
        actualizarBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(actualizarBtn);

        // Listener para cargar datos al seleccionar película
        peliculaCombo.addActionListener(e -> {
            PeliculaItem pelicula = (PeliculaItem) peliculaCombo.getSelectedItem();
            if (pelicula != null) {
                cargarDatosPelicula(pelicula.getId(), tituloField, generoField, clasificacionField, estadoField);
            }
        });

        // Cargar datos iniciales
        if (peliculaCombo.getItemCount() > 0) {
            PeliculaItem firstPelicula = peliculaCombo.getItemAt(0);
            cargarDatosPelicula(firstPelicula.getId(), tituloField, generoField, clasificacionField, estadoField);
        }

        actualizarBtn.addActionListener(e -> {
            PeliculaItem pelicula = (PeliculaItem) peliculaCombo.getSelectedItem();
            if (pelicula == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una película", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String query = "UPDATE Pelicula SET Titulo = ?, Genero = ?, ClasificacionEdad = ?, Estado = ? WHERE ID_Pelicula = ?";

                try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
                    pstmt.setString(1, tituloField.getText().trim());
                    pstmt.setString(2, generoField.getText().trim());
                    pstmt.setString(3, clasificacionField.getText().trim());
                    pstmt.setString(4, estadoField.getText().trim());
                    pstmt.setInt(5, pelicula.getId());

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Película actualizada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    // Recargar ComboBox
                    peliculaCombo.removeAllItems();
                    cargarPeliculas(peliculaCombo);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar película: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        panel.add(formPanel);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private void cargarDatosFuncion(int idFuncion, JTextField horaField, JTextField fechaField, 
                                    JTextField estadoField, JComboBox<PeliculaItem> peliculaCombo, 
                                    JComboBox<SalaItem> salaCombo, JComboBox<CarteleraItem> carteleraCombo) {
        try {
            String query = "SELECT HoraFuncion, FechaFuncion, Estado, ID_Pelicula, ID_Sala, ID_Cartelera " +
                          "FROM Funcion WHERE ID_Funcion = ?";

            try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
                pstmt.setInt(1, idFuncion);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    horaField.setText(rs.getString("HoraFuncion"));
                    fechaField.setText(rs.getString("FechaFuncion"));
                    estadoField.setText(rs.getString("Estado") != null ? rs.getString("Estado") : "");

                    // Seleccionar película
                    int idPelicula = rs.getInt("ID_Pelicula");
                    for (int i = 0; i < peliculaCombo.getItemCount(); i++) {
                        if (peliculaCombo.getItemAt(i).getId() == idPelicula) {
                            peliculaCombo.setSelectedIndex(i);
                            break;
                        }
                    }

                    // Seleccionar sala
                    int idSala = rs.getInt("ID_Sala");
                    for (int i = 0; i < salaCombo.getItemCount(); i++) {
                        if (salaCombo.getItemAt(i).getId() == idSala) {
                            salaCombo.setSelectedIndex(i);
                            break;
                        }
                    }

                    // Seleccionar cartelera
                    int idCartelera = rs.getInt("ID_Cartelera");
                    for (int i = 0; i < carteleraCombo.getItemCount(); i++) {
                        if (carteleraCombo.getItemAt(i).getId() == idCartelera) {
                            carteleraCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos de función: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarDatosPelicula(int idPelicula, JTextField tituloField, JTextField generoField, 
                                     JTextField clasificacionField, JTextField estadoField) {
        try {
            String query = "SELECT Titulo, Genero, ClasificacionEdad, Estado FROM Pelicula WHERE ID_Pelicula = ?";

            try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
                pstmt.setInt(1, idPelicula);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    tituloField.setText(rs.getString("Titulo"));
                    generoField.setText(rs.getString("Genero") != null ? rs.getString("Genero") : "");
                    clasificacionField.setText(rs.getString("ClasificacionEdad") != null ? rs.getString("ClasificacionEdad") : "");
                    estadoField.setText(rs.getString("Estado") != null ? rs.getString("Estado") : "");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos de película: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarPeliculas(JComboBox<PeliculaItem> comboBox) {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID_Pelicula, Titulo FROM Pelicula ORDER BY Titulo")) {

            while (rs.next()) {
                comboBox.addItem(new PeliculaItem(rs.getInt("ID_Pelicula"), rs.getString("Titulo")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar películas: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarCarteleras(JComboBox<CarteleraItem> comboBox) {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID_Cartelera, FechaInicio, FechaFin FROM Cartelera ORDER BY ID_Cartelera")) {

            while (rs.next()) {
                comboBox.addItem(new CarteleraItem(
                    rs.getInt("ID_Cartelera"), 
                    rs.getString("FechaInicio"), 
                    rs.getString("FechaFin")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar carteleras: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(FIELDTEXT_COLOR);
        textField.setBackground(new Color(50, 50, 50));
        textField.setCaretColor(FIELDTEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return textField;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });

        return button;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(new Color(50, 50, 50));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setPreferredSize(new Dimension(300, 35));
    }

    // ==================== CLASES AUXILIARES ====================

    class PeliculaItem {
        private int id;
        private String titulo;

        public PeliculaItem(int id, String titulo) {
            this.id = id;
            this.titulo = titulo;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return titulo;
        }
    }

    class CarteleraItem {
        private int id;
        private String fechaInicio;
        private String fechaFin;

        public CarteleraItem(int id, String fechaInicio, String fechaFin) {
            this.id = id;
            this.fechaInicio = fechaInicio;
            this.fechaFin = fechaFin;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Cartelera " + id + " (" + fechaInicio + " - " + fechaFin + ")";
        }
    }
    private void cargarSalas(JComboBox<SalaItem> comboBox) {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID_Sala, Numero, TipoDeSala, CantButacas FROM Sala ORDER BY ID_Sala")) {
            
            while (rs.next()) {
                int idSala = rs.getInt("ID_Sala");
                int numero = rs.getInt("Numero");
                String tipo = rs.getString("TipoDeSala");
                int cantButacas = rs.getInt("CantButacas");
                
                comboBox.addItem(new SalaItem(idSala, numero, tipo, cantButacas));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar las salas: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cargarEstadisticasSala(int idSala, DefaultTableModel tableModel) {
        // Limpiar todas las filas excepto mantener el header como primera fila
        tableModel.setRowCount(0);
        
        // Volver a agregar la fila de encabezado
        Object[] headerRow = {"ID Función", "Película", "Fecha", "Hora", "Butacas Ocup.", "Total Butac.", "Ocupación %"};
        tableModel.addRow(headerRow);
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        String query = "SELECT f.ID_Funcion, p.Titulo, f.FechaFuncion, f.HoraFuncion, " +
                       "s.CantButacas, COUNT(b.ID_Boleto) as ButacasOcupadas " +
                       "FROM Funcion f " +
                       "INNER JOIN Pelicula p ON f.ID_Pelicula = p.ID_Pelicula " +
                       "INNER JOIN Sala s ON f.ID_Sala = s.ID_Sala " +
                       "LEFT JOIN Boleto b ON f.ID_Funcion = b.ID_Funcion " +
                       "WHERE f.ID_Sala = ? " +
                       "GROUP BY f.ID_Funcion, p.Titulo, f.FechaFuncion, f.HoraFuncion, s.CantButacas " +
                       "ORDER BY f.FechaFuncion DESC, f.HoraFuncion DESC";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            
            pstmt.setInt(1, idSala);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int idFuncion = rs.getInt("ID_Funcion");
                String titulo = rs.getString("Titulo");
                String fecha = rs.getString("FechaFuncion");
                String hora = rs.getString("HoraFuncion");
                int cantButacas = rs.getInt("CantButacas");
                int butacasOcupadas = rs.getInt("ButacasOcupadas");
                
                double porcentaje = (cantButacas > 0) ? (butacasOcupadas * 100.0 / cantButacas) : 0;
                
                Object[] row = {
                    idFuncion,
                    titulo,
                    fecha,
                    hora,
                    butacasOcupadas,
                    cantButacas,
                    df.format(porcentaje) + "%"
                };
                
                tableModel.addRow(row);
            }
            
            if (tableModel.getRowCount() == 1) { // Solo tiene el header
                Object[] emptyRow = {"---", "No hay funciones para esta sala", "---", "---", "---", "---", "---"};
                tableModel.addRow(emptyRow);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar estadísticas: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void showDefaultContent(String buttonName) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel(buttonName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField textField = new JTextField(30);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setForeground(FIELDTEXT_COLOR);
        textField.setBackground(new Color(50, 50, 50));
        textField.setCaretColor(FIELDTEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setMaximumSize(new Dimension(400, 40));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(textField);
        
        contentPanel.add(container);
    }
    
    private void createTopBar() {
        topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(TOPBAR_COLOR);
        topBarPanel.setPreferredSize(new Dimension(getWidth(), 80));
        
        try {
            URL imgURL = getClass().getResource("/cinemarx/resources/TOPBAR.png");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage();
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();
                
                int newWidth = getWidth();
                int newHeight = (originalHeight * newWidth) / originalWidth;
                
                if (newHeight > 110) {
                    newHeight = 110;
                    newWidth = (originalWidth * newHeight) / originalHeight;
                }
                
                Image scaledImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                topBarPanel.add(imageLabel, BorderLayout.CENTER);
            } else {
                JLabel logoLabel = new JLabel("Panel Gestión - Imagen no encontrada");
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                logoLabel.setForeground(TEXT_COLOR);
                logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                topBarPanel.add(logoLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            JLabel logoLabel = new JLabel("Panel Gestión - Error al cargar imagen");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            logoLabel.setForeground(TEXT_COLOR);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            topBarPanel.add(logoLabel, BorderLayout.CENTER);
            e.printStackTrace();
        }
        
        add(topBarPanel, BorderLayout.NORTH);
    }
    
    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(280, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        addSectionTitle("Gestión");
        addMenuButton("Usuarios y Roles", "user.png");
        addMenuButton("Películas", "video.png");
        addMenuButton("Salas", "sala.png");
        addMenuButton("Personal", "staff.png");
        
        addSeparator();
        
        addSectionTitle("Reportes");
        addMenuButton("Estadísticas de ocupación", "Style=outline.png");
        addMenuButton("Ventas", "dollar-circle.png");
        addMenuButton("ABM", "logs.png");
        
        addSeparator();
        
        addSectionTitle("Seguridad");
        addMenuButton("Logs de acciones", "user.png");
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private void addSectionTitle(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(SECTION_TITLE_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 0));
        sidebarPanel.add(titleLabel);
    }
    
    private void addMenuButton(String text, String iconFileName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setForeground(TEXT_COLOR);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 45));
        button.setPreferredSize(new Dimension(250, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        try {
            URL iconURL = getClass().getResource("/cinemarx/resources/" + iconFileName);
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(img));
                button.setIconTextGap(10);
            } else {
                System.out.println("No se encontró la imagen: " + iconFileName);
            }
        } catch (Exception e) {
            System.out.println("Error al cargar el icono: " + iconFileName);
            e.printStackTrace();
        }
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
                button.repaint();
            }
        });
        
        button.addActionListener(e -> {
            System.out.println("Clicked: " + text);
            showContent(text);
        });
        
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(button);
    }
    
    private void addSeparator() {
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(250, 1));
        separator.setForeground(new Color(80, 80, 80));
        sidebarPanel.add(separator);
    }
    
    // Clase auxiliar para items del ComboBox
    class SalaItem {
        private int id;
        private int numero;
        private String tipo;
        private int cantButacas;
        
        public SalaItem(int id, int numero, String tipo, int cantButacas) {
            this.id = id;
            this.numero = numero;
            this.tipo = tipo;
            this.cantButacas = cantButacas;
        }
        
        public int getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return "Sala " + numero + " - " + tipo + " (" + cantButacas + " butacas)";
        }
    }
    
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            M6 frame = new M6();
            frame.setVisible(true);
        });
    }
}