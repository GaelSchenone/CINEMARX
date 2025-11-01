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
    
    // Configuración de base de datos
    private final String DB_URL = "jdbc:mariadb://br1.aguilucho.ar:25584/Cinemarx";
    private final String DB_USER = "cnx_admin";
    private final String DB_PASSWORD = "CnxAdmin!620";
    

    public M6() {
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Panel de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());
        
        createTopBar();
        createSidebar();
        
        contentPanel = new JPanel();
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 30));
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    private void showContent(String buttonName) {
        contentPanel.removeAll();
        
        if (buttonName.equals("Estadísticas de ocupación")) {
            showEstadisticasOcupacion();
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
    
    private void cargarSalas(JComboBox<SalaItem> comboBox) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
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
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
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
            
            if (tableModel.getRowCount() == 0) {
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
