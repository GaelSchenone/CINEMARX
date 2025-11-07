package CINEMARX.M4;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * Panel de selección de butacas (sin JFrame, solo el contenido)
 */
public class PantallaButacas extends JPanel {
    private int idFuncion;
    private int idSala;
    private int idPelicula;
    private int cantButacas;
    private Set<String> butacasOcupadas;
    private Set<ButacaBoton> butacasSeleccionadas;
    private JPanel panelButacas;
    
    class ButacaBoton extends JButton {
        String id;
        boolean ocupada;
        boolean isSelected;
        private Color currentBackgroundColor;

        ButacaBoton(String id) {
            this.id = id;
            this.ocupada = false;
            this.isSelected = false;
            configurarEstilo();
        }

        void configurarEstilo() {
            setPreferredSize(new Dimension(18, 18));
            setFont(new Font("Arial", Font.BOLD, 9));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (ocupada) {
                currentBackgroundColor = new Color(100, 100, 100);
                setForeground(new Color(60, 60, 60));
                setEnabled(false);
            } else {
                currentBackgroundColor = Color.WHITE;
                setForeground(new Color(50, 50, 50));
            }
        }

        void seleccionar() {
            currentBackgroundColor = new Color(220, 50, 50);
            setForeground(Color.WHITE);
            isSelected = true;
            repaint();
        }

        void deseleccionar() {
            if (!ocupada) {
                currentBackgroundColor = Color.WHITE;
                setForeground(new Color(50, 50, 50));
                isSelected = false;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            g2.setColor(currentBackgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            super.paintComponent(g);

            if (isSelected) {
                g2.setColor(new Color(0x6C0002));
                g2.setStroke(new BasicStroke(2));

                int w = getWidth();
                int h = getHeight();

                g2.drawLine(w / 4, h / 2, w / 2, h * 3 / 4);
                g2.drawLine(w / 2, h * 3 / 4, w * 3 / 4, h / 4);
            }
            
            g2.dispose();
        }
    }
    
    public PantallaButacas(int idFuncion, int idSala, int idPelicula) {
        this.idFuncion = idFuncion;
        this.idSala = idSala;
        this.idPelicula = idPelicula;
        this.butacasOcupadas = new HashSet<>();
        this.butacasSeleccionadas = new HashSet<>();
        
        System.out.println("PantallaButacas: idFuncion = " + idFuncion);

        setLayout(new BorderLayout());
        setBackground(new Color(0x2B2B2B));
        
        // Muestra un indicador de carga
        JLabel loadingLabel = new JLabel("Cargando butacas...", JLabel.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        loadingLabel.setForeground(Color.WHITE);
        add(loadingLabel, BorderLayout.CENTER);

        new SwingWorker<JPanel, Void>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                cargarButacasOcupadas();
                cargarCantButacas();
                System.out.println("Butacas ocupadas: " + butacasOcupadas);
                return createUI();
            }

            @Override
            protected void done() {
                try {
                    JPanel mainPanel = get();
                    removeAll();
                    add(mainPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private JPanel createUI() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(inicializarUI(), BorderLayout.CENTER);
        return panel;
    }
    
    private void cargarButacasOcupadas() {
        try {
            String sql = "SELECT NumeroButaca FROM Boleto WHERE ID_Funcion = ?";
            
            PreparedStatement ps = M4.getConexion().prepareStatement(sql);
            ps.setInt(1, idFuncion);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                butacasOcupadas.add(rs.getString("NumeroButaca"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void cargarCantButacas() {
        try {
            String sql = "SELECT CantButacas FROM Sala WHERE ID_Sala = ?";
            PreparedStatement ps = M4.getConexion().prepareStatement(sql);
            ps.setInt(1, idSala);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                cantButacas = rs.getInt("CantButacas");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel inicializarUI() {
        JPanel scrollableContent = new JPanel(new BorderLayout(20, 20));
        scrollableContent.setBackground(new Color(0x2B2B2B));
        scrollableContent.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Top panel with back button and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // Back button
        try {
            URL backIconUrl = new URL("https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2Farrow-left.png");
            InputStream backIn = backIconUrl.openStream();
            BufferedImage backIconOriginal = ImageIO.read(backIn);
            backIn.close();
            
            if (backIconOriginal != null) {
                Image backIconScaled = backIconOriginal.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JLabel backButton = new JLabel(new ImageIcon(backIconScaled));
                backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                backButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        M4.mostrarPantallaPelicula(idPelicula);
                    }
                });
                topPanel.add(backButton, BorderLayout.WEST);
            }
        } catch (Exception e) {
            // Fallback text button
            JButton backButton = new JButton("<");
            backButton.setFont(new Font("Arial", Font.BOLD, 24));
            backButton.setForeground(Color.WHITE);
            backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            backButton.addActionListener(e2 -> M4.mostrarPantallaPelicula(idPelicula));
            topPanel.add(backButton, BorderLayout.WEST);
        }

        // Título
        JLabel titulo = new JLabel("Selecciona tu butaca", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        topPanel.add(titulo, BorderLayout.CENTER);

        scrollableContent.add(topPanel, BorderLayout.NORTH);

        // Panel central con butacas
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(0x2B2B2B));

        // Pantalla
        JPanel panelPantalla = crearPanelPantalla();
        centerPanel.add(panelPantalla, BorderLayout.NORTH);

        // Butacas
        panelButacas = crearPanelButacas();
        centerPanel.add(panelButacas, BorderLayout.CENTER);

        scrollableContent.add(centerPanel, BorderLayout.CENTER);

        // Panel inferior - Leyenda y botón
        JPanel bottomPanel = crearPanelInferior();
        scrollableContent.add(bottomPanel, BorderLayout.SOUTH);

        // Scroll pane
        JScrollPane mainScrollPane = new JScrollPane(scrollableContent);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(mainScrollPane, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private JPanel crearPanelPantalla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x2B2B2B));
        panel.setBorder(new EmptyBorder(20, 0, 30, 0));

        try {
            URL imageUrl = new URL("https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=M4%2FPantallaSala.png");
            InputStream in = imageUrl.openStream();
            BufferedImage originalImage = ImageIO.read(in);
            in.close();

            if (originalImage != null) {
                int newWidth = 600;
                int newHeight = (originalImage.getHeight() * newWidth) / originalImage.getWidth();
                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                panel.add(imageLabel, BorderLayout.CENTER);
            } else {
                throw new Exception("Screen image could not be read.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JLabel lblPantalla = new JLabel("PANTALLA (Error al cargar imagen)", JLabel.CENTER);
            lblPantalla.setFont(new Font("Arial", Font.BOLD, 14));
            lblPantalla.setForeground(Color.RED);
            panel.add(lblPantalla, BorderLayout.CENTER);
        }

        return panel;
    }
    
    private JPanel crearPanelButacas() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x2B2B2B));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);

        int numMainRows = (cantButacas > 9) ? (cantButacas - 9) / 17 : 0;

        // Fila frontal (A)
        gbc.gridy = 0;
        for (int i = 0; i < 9; i++) {
            gbc.gridx = i + 5; // Centered in a 19-column grid
            String id = "A" + (i + 1);
            agregarButaca(panel, id, gbc);
        }

        // Separador de filas
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 19;
        panel.add(Box.createVerticalStrut(15), gbc);
        gbc.gridwidth = 1;

        // Filas principales
        for (int row = 0; row < numMainRows; row++) {
            gbc.gridy++;
            char letra = (char) ('B' + row);

            // Sección izquierda (3)
            for (int i = 0; i < 3; i++) {
                gbc.gridx = i;
                String id = letra + "" + (i + 1);
                agregarButaca(panel, id, gbc);
            }

            // Pasillo izquierdo
            gbc.gridx = 3;
            panel.add(Box.createHorizontalStrut(18), gbc);

            // Sección central (11)
            for (int i = 0; i < 11; i++) {
                gbc.gridx = i + 4;
                String id = letra + "" + (i + 4);
                agregarButaca(panel, id, gbc);
            }

            // Pasillo derecho
            gbc.gridx = 15;
            panel.add(Box.createHorizontalStrut(18), gbc);

            // Sección derecha (3)
            for (int i = 0; i < 3; i++) {
                gbc.gridx = i + 16;
                String id = letra + "" + (i + 15);
                agregarButaca(panel, id, gbc);
            }
        }

        return panel;
    }
    
    private void agregarButaca(JPanel panel, String id, GridBagConstraints gbc) {
        ButacaBoton btn = new ButacaBoton(id);
        
        if (butacasOcupadas.contains(id)) {
            btn.ocupada = true;
            btn.configurarEstilo();
        } else {
            btn.addActionListener(e -> {
                if (butacasSeleccionadas.contains(btn)) {
                    butacasSeleccionadas.remove(btn);
                    btn.deseleccionar();
                } else {
                    butacasSeleccionadas.add(btn);
                    btn.seleccionar();
                }
            });
        }
        
        panel.add(btn, gbc);
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x2B2B2B));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Leyenda
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        leyenda.setBackground(new Color(0x2B2B2B));
        
        leyenda.add(crearItemLeyenda("Disponible", Color.WHITE, false));
        leyenda.add(crearItemLeyenda("No disponible", new Color(100, 100, 100), false));
        leyenda.add(crearItemLeyenda("Seleccionado", new Color(220, 50, 50), true));
        
        panel.add(leyenda, BorderLayout.NORTH);
        
        // Panel de compra
        JPanel panelCompra = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        panelCompra.setBackground(new Color(0x2B2B2B));
        
        JButton btnSiguiente = new JButton("SIGUIENTE") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isArmed()) {
                    g.setColor(getBackground().darker());
                } else {
                    g.setColor(getBackground());
                }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSiguiente.setFont(new Font("Arial", Font.BOLD, 16));
        btnSiguiente.setForeground(Color.WHITE);
        btnSiguiente.setBackground(new Color(220, 50, 50));
        btnSiguiente.setPreferredSize(new Dimension(200, 50));
        btnSiguiente.setFocusPainted(false);
        btnSiguiente.setBorderPainted(false);
        btnSiguiente.setContentAreaFilled(false);
        btnSiguiente.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnSiguiente.addActionListener(e -> {
            if (butacasSeleccionadas.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor seleccione al menos una butaca", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            guardarButacasEnOrden();
        });
        panelCompra.add(btnSiguiente);
        
        panel.add(panelCompra, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearItemLeyenda(String texto, Color color, boolean isSelected) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        item.setBackground(new Color(0x2B2B2B));
        
        LeyendaButaca circulo = new LeyendaButaca(color, isSelected);
        
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        
        item.add(circulo);
        item.add(label);
        
        return item;
    }
    
    class LeyendaButaca extends JPanel {
        private Color color;
        private boolean isSelected;

        LeyendaButaca(Color color, boolean isSelected) {
            this.color = color;
            this.isSelected = isSelected;
            setPreferredSize(new Dimension(25, 25));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

            if (isSelected) {
                g2.setColor(new Color(0x6C0002));
                g2.setStroke(new BasicStroke(2));
                int w = getWidth();
                int h = getHeight();
                g2.drawLine(w / 4, h / 2, w / 2, h * 3 / 4);
                g2.drawLine(w / 2, h * 3 / 4, w * 3 / 4, h / 4);
            }
            g2.dispose();
        }
    }

    private void guardarButacasEnOrden() {
        OrderDetails order = M4.getOrderDetails(idFuncion);
        for (ButacaBoton btn : butacasSeleccionadas) {
            Boleto boleto = new Boleto(btn.id, idFuncion, null);
            order.addBoleto(boleto);
        }

        System.out.println("Boletos guardados en la orden: " + order.getBoletos().size());

        // Aquí se navegaría a la siguiente pantalla (productos)
        // Por ahora, solo mostramos un mensaje
        JOptionPane.showMessageDialog(this, 
            "Butacas seleccionadas. Siguiente paso: seleccionar productos.", 
            "Paso Siguiente", JOptionPane.INFORMATION_MESSAGE);
    }


}