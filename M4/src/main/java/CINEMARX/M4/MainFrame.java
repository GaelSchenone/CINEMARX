package CINEMARX.M4;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InputStream;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.HashSet;

/**
 * Frame principal que contiene el header compartido y el contenedor de vistas
 */
public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private HeaderPanel headerPanel;
    private CardLayout cardLayout;
    private HashSet<String> addedPanels = new HashSet<>();
    private LoadingPanel loadingPanel;
    
    public MainFrame() {
        setTitle("CINEMAR X");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0x2B2B2B));
        
        // Header compartido
        headerPanel = new HeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Contenedor de vistas
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(0x2B2B2B));
        
        // Panel de carga
        loadingPanel = new LoadingPanel();
        contentPanel.add(loadingPanel, "loading");
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    /**
     * Cambia el contenido del panel principal
     */
    public void cambiarContenido(JPanel nuevoPanel, String key) {
        // Muestra el panel de carga primero
        cardLayout.show(contentPanel, "loading");

        SwingWorker<JPanel, Void> worker = new SwingWorker<JPanel, Void>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                // En una aplicacion real, aqui se harian las tareas pesadas
                return nuevoPanel;
            }

            @Override
            protected void done() {
                try {
                    JPanel panel = get();
                    if (!addedPanels.contains(key)) {
                        contentPanel.add(panel, key);
                        addedPanels.add(key);
                    }
                    cardLayout.show(contentPanel, key);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }
    
    public void showLoading() {
        cardLayout.show(contentPanel, "loading");
    }

}

/**
 * Panel del header compartido
 */
class HeaderPanel extends JPanel {
    
    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x2B2B2B));
        setPreferredSize(new Dimension(0, 70));
        setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 40, 10, 40),
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE)
        ));
        
        initComponents();
    }
    
    private void initComponents() {
        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setBackground(new Color(0x2B2B2B));
        
        try {
            URL imageUrl = new URL("https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=logos%2FCINEMARX%20logotipo.png");
            InputStream in = imageUrl.openStream();
            BufferedImage originalImage = ImageIO.read(in);
            in.close();
            
            if (originalImage != null) {
                int newHeight = 45;
                int newWidth = (originalImage.getWidth() * newHeight) / originalImage.getHeight();
                Image logo = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(logo));
                logoPanel.add(logoLabel);
            } else {
                throw new Exception("Logo image could not be read.");
            }
        } catch (Exception e) {
            JLabel logoLabel = new JLabel("CINEMAR X");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 32));
            logoLabel.setForeground(Color.WHITE);
            logoPanel.add(logoLabel);
        }

        add(logoPanel, BorderLayout.WEST);
        
        // Menú
        JPanel menuContainer = new JPanel(new GridBagLayout());
        menuContainer.setBackground(new Color(0x2B2B2B));
        
        JPanel menu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 35, 0));
        menu.setBackground(new Color(0x2B2B2B));
        
        String[] items = {"PELICULAS", "BUFFET", "MEMBRESIA"};
        for (String item : items) {
            JLabel label = new JLabel(item);
            label.setFont(new Font("Arial", Font.BOLD, 15));
            label.setForeground(Color.WHITE);
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    label.setForeground(new Color(220, 50, 50));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    label.setForeground(Color.WHITE);
                }
            });
            
            menu.add(label);
        }
        
        // User icon
        try {
            URL userIconUrl = new URL("https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2Fuser.png");
            InputStream in = userIconUrl.openStream();
            BufferedImage userImage = ImageIO.read(in);
            in.close();
            
            if (userImage != null) {
                Image scaledUserIcon = userImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                JLabel userIcon = new JLabel(new ImageIcon(scaledUserIcon));
                userIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
                userIcon.setBorder(new EmptyBorder(0, 15, 0, 0));
                menu.add(userIcon);
            }
        } catch (Exception e) {
            JLabel userIcon = new JLabel("👤");
            userIcon.setFont(new Font("Arial", Font.PLAIN, 24));
            userIcon.setForeground(Color.WHITE);
            userIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
            userIcon.setBorder(new EmptyBorder(0, 15, 0, 0));
            menu.add(userIcon);
        }
        
        menuContainer.add(menu);
        add(menuContainer, BorderLayout.EAST);
    }
}