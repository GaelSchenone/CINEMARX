package cinemarx;
//Tengo este codigo de java desde netbeans, (usandolo en sistema operativo ubuntu de linux) 
//y quiero agregar funciones dentro de las pantallas que te muestran los botones de la barra lateral
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

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
    private final Color ACCENT_COLOR = new Color(239, 68, 68); // Rojo
    private final Color SECTION_TITLE_COLOR = new Color(200, 200, 200);
    
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
        
        // Crear TopBar
        createTopBar();
        
        // Crear Sidebar
        createSidebar();
        
        // Crear Panel de Contenido
        contentPanel = new JPanel();
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 30));
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void showContent(String buttonName) {
        // Limpiar el panel de contenido
        contentPanel.removeAll();
        
        // Crear un panel contenedor vertical
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BACKGROUND_COLOR);
        
        // Crear label con el nombre del botón
        JLabel titleLabel = new JLabel(buttonName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Crear campo de texto
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
        
        // Agregar componentes al contenedor
        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 15)));
        container.add(textField);
        
        // Agregar el contenedor al panel principal
        contentPanel.add(container);
        
        // Refrescar el panel
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void createTopBar() {
        topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(TOPBAR_COLOR);
        topBarPanel.setPreferredSize(new Dimension(getWidth(), 80));
        
        // Cargar imagen topbar completa
        try {
            URL imgURL = getClass().getResource("/java/com/example/pruebas/resources/TOPBAR.png");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                // Ajustar imagen al ancho completo manteniendo proporción
                Image img = icon.getImage();
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();
                
                // Calcular nueva altura manteniendo proporción
                int newWidth = getWidth();
                int newHeight = (originalHeight * newWidth) / originalWidth;
                
                // Si la altura calculada es mayor a 110, ajustar por altura
                if (newHeight > 110) {
                    newHeight = 110;
                    newWidth = (originalWidth * newHeight) / originalHeight;
                }
                
                Image scaledImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                topBarPanel.add(imageLabel, BorderLayout.CENTER);
            } else {
                // Fallback si no encuentra la imagen
                JLabel logoLabel = new JLabel("Panel Gestión - Imagen no encontrada");
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                logoLabel.setForeground(TEXT_COLOR);
                logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                topBarPanel.add(logoLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            // Fallback en caso de error
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
        
        // Sección Gestión
        addSectionTitle("Gestión");
        addMenuButton("Usuarios y Roles", "user.png");
        addMenuButton("Películas", "video.png");
        addMenuButton("Salas", "sala.png");
        addMenuButton("Personal", "staff.png");
        
        addSeparator();
        
        // Sección Reportes
        addSectionTitle("Reportes");
        addMenuButton("Estadísticas de ocupación", "Style=outline.png");
        addMenuButton("Ventas", "dollar-circle.png");
        addMenuButton("ABM", "logs.png");
        
        addSeparator();
        
        // Sección Seguridad
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
        
        // Cargar y establecer el icono
        try {
            URL iconURL = getClass().getResource("/cinemarx/resources/" + iconFileName);
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                // Redimensionar el icono a un tamaño pequeño (24x24 píxeles)
                Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(img));
                button.setIconTextGap(10); // Espacio entre el icono y el texto
            } else {
                System.out.println("No se encontró la imagen: " + iconFileName);
            }
        } catch (Exception e) {
            System.out.println("Error al cargar el icono: " + iconFileName);
            e.printStackTrace();
        }
        
        // Efecto hover
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
        
        // Action listener
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
        // Usar look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Crear y mostrar la ventana
        SwingUtilities.invokeLater(() -> {
            M6 frame = new M6();
            frame.setVisible(true);
        });
    }
}
