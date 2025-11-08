package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.awt.image.BufferedImage;


public class M6 extends JFrame {
    
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JPanel topBarPanel;
    
    // Colores
    public static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    public static final Color SIDEBAR_COLOR = new Color(40, 40, 40);
    public static final Color TOPBAR_COLOR = new Color(45, 45, 45);
    public static final Color BUTTON_COLOR = new Color(50, 50, 50);
    public static final Color BUTTON_HOVER_COLOR = Color.decode("#2B2B2B");
    public static final Color TEXT_COLOR = new Color(220, 220, 220);
    public static final Color FIELDTEXT_COLOR = new Color(120,120,120);
    public static final Color ACCENT_COLOR = new Color(239, 68, 68);
    public static final Color SECTION_TITLE_COLOR = new Color(200, 200, 200);
    
    // Configuración de base de datos
    private final String DB_URL = "jdbc:mariadb://br1.aguilucho.ar:25584/Cinemarx";
    private final String DB_USER = "cnx_admin";
    private final String DB_PASSWORD = "CnxAdmin!620";
    
    // Conexión persistente
    private Connection connection;
    
    public M6() {
        try {
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
    
    private void initDatabaseConnection() throws SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Conexión a base de datos establecida correctamente");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de MariaDB no encontrado: " + e.getMessage());
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initDatabaseConnection();
        }
        return connection;
    }
    
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
    
    private void showContent(String buttonName) {
        contentPanel.removeAll();

        if (buttonName.equals("Estadísticas")) {
            new EstadisticasOcupacion(this, contentPanel).mostrar();
        } else if (buttonName.equals("Ventas")) {
            new Ventas(this, contentPanel).mostrar();
        } else if (buttonName.equals("ABM")) {
            new ABM(this, contentPanel).mostrar();
        } else if (buttonName.equals("Personal")) {
            new Personal(this, contentPanel).mostrar();
        } else if (buttonName.equals("Clientes y Roles")){
            new UsuariosYRoles(this, contentPanel).mostrar();
        } else if (buttonName.equals("Películas")){
            new Peliculas(this, contentPanel).mostrar();
        } else if (buttonName.equals("Salas")){
            new Salas(this, contentPanel).mostrar();
        } else if (buttonName.equals("Productos")){
            new Productos(this, contentPanel).mostrar();
        } else if (buttonName.equals("Logs de acciones")){
            new Logs(this, contentPanel).mostrar();
        } else {
            showDefaultContent(buttonName);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
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
            URL imgURL = new URL("https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=logos%2FCINEMARX%20imagotipo.png");
            InputStream imageIn = imgURL.openStream();
            BufferedImage originalImage = ImageIO.read(imageIn);
            imageIn.close();

            if (originalImage != null) {
                int originalWidth = originalImage.getWidth();
                int originalHeight = originalImage.getHeight();

                // Altura máxima con margen (topbar es 80, dejamos margen arriba y abajo)
                int maxHeight = 50; // Reducido de 110 a 50 para dejar espacio
                int newHeight = maxHeight;
                int newWidth = (originalWidth * newHeight) / originalHeight;

                Image scaledImg = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
                imageLabel.setHorizontalAlignment(SwingConstants.LEFT);

                // Agregar márgenes: 15px arriba, 15px abajo, 15px izquierda, 0px derecha
                imageLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 0));

                topBarPanel.add(imageLabel, BorderLayout.WEST); // Cambié de CENTER a WEST
            } else {
                throw new Exception("La imagen no pudo ser leída.");
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
        
        add(topBarPanel, BorderLayout.NORTH);
    }
    
    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(280, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        JScrollPane scrollPane = new JScrollPane(sidebarPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        addSectionTitle("Gestión");
        addMenuButton("Clientes y Roles", "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2FM6%2Fuser.png");
        addMenuButton("Películas", "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2FM6%2Fvideo.png");
        addMenuButton("Personal", "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2FM6%2Fstaff.png");
        addMenuButton("Salas", "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2FM6%2Fsala.png");
        addMenuButton("Productos", "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2FM6%2Fprod.png");
        
        addSeparator();
        
        addSectionTitle("Reportes");
        addMenuButton("Estadísticas", "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2FM6%2FStyleoutline.png");
        addMenuButton("Ventas", "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2FM6%2Fdollar-circle.png");
        addMenuButton("ABM", "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2FM6%2Fabm.png");
        
        addSeparator();
        
        addSectionTitle("Seguridad");
        addMenuButton("Logs de acciones", "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2FM6%2Flogs.png");
        
        add(scrollPane, BorderLayout.WEST);
    }
    
    private void addSectionTitle(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(SECTION_TITLE_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 0));
        sidebarPanel.add(titleLabel);
    }
    
    private void addMenuButton(String text, String imageUrl) {
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
        
        // Cargar icono desde URL
        try {
            URL iconURL = new URL(imageUrl);
            InputStream iconIn = iconURL.openStream();
            BufferedImage iconImage = ImageIO.read(iconIn);
            iconIn.close();
            
            if (iconImage != null) {
                Image scaledIcon = iconImage.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledIcon));
                button.setIconTextGap(10);
            }
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + imageUrl);
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