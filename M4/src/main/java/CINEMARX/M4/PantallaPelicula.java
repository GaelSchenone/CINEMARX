package CINEMARX.M4;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Panel de película (sin JFrame, solo el contenido)
 */
public class PantallaPelicula extends JPanel {

    private int idPelicula;
    private JPanel panelFechas;
    private JPanel panelHorarios;
    private JComboBox<String> cbIdioma;
    private JComboBox<String> cbFormato;
    private ButtonGroup grupoBotones;
    private Map<JButton, FuncionInfo> mapaFunciones;
    private JLabel titulo;
    private JTextArea sinopsisArea;
    private JLabel duracionLabel;
    private JLabel posterLabel;
    private java.util.Date selectedDate;
    private String selectedIdioma;
    private String selectedFormato;
    private JPanel panelTarjetasPeliculas;
    
    class FuncionInfo {
        int idFuncion;
        int idSala;
        String hora;
        
        FuncionInfo(int idFuncion, int idSala, String hora) {
            this.idFuncion = idFuncion;
            this.idSala = idSala;
            this.hora = hora;
        }
    }

    class PeliculaCard extends JPanel {
        public PeliculaCard(int peliculaId, String titulo, String urlImagen, String clasificacionEdad) {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(150, 270));
            setMinimumSize(new Dimension(150, 270));
            setMaximumSize(new Dimension(150, 270));

            JLabel posterLabel = new JLabel();
            posterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            posterLabel.setPreferredSize(new Dimension(130, 195));

            try {
                URL imageUrl = new URL(urlImagen);
                InputStream in = imageUrl.openStream();
                BufferedImage originalImage = ImageIO.read(in);
                in.close();
                if (originalImage != null) {
                    Image scaledImage = originalImage.getScaledInstance(130, 195, Image.SCALE_SMOOTH);
                    posterLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    posterLabel.setText("No Poster");
                }
            } catch (Exception e) {
                posterLabel.setText("No Poster");
                e.printStackTrace();
            }

            JLabel tituloLabel = new JLabel("<html><body style='width: 120px;'>" + titulo + "</body></html>");
            tituloLabel.setForeground(Color.WHITE);
            tituloLabel.setFont(new Font("Arial", Font.BOLD, 12));
            tituloLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel clasificacionLabel = new JLabel(clasificacionEdad);
            clasificacionLabel.setForeground(Color.LIGHT_GRAY);
            clasificacionLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            clasificacionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            add(posterLabel);
            add(Box.createRigidArea(new Dimension(0, 10)));
            add(tituloLabel);
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(clasificacionLabel);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    M4.mostrarPantallaPelicula(peliculaId);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(40, 40, 40));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.dispose();
        }
    }
    
    public PantallaPelicula(int idPelicula) {
        this.mapaFunciones = new HashMap<>();
        
        setLayout(new BorderLayout());
        setBackground(new Color(0x2B2B2B));
        
        inicializarUI();
        cargarPelicula(idPelicula);
    }

    public void cargarPelicula(int idPelicula) {
        this.idPelicula = idPelicula;

        new SwingWorker<Void, Void>() {
            private String tituloPelicula, sinopsisPelicula, duracionPelicula, posterUrl;
            private BufferedImage posterImage;

            @Override
            protected Void doInBackground() throws Exception {
                String sql = "SELECT Titulo, Sinopsis, Duracion, Imagen FROM Pelicula WHERE ID_Pelicula = ?";
                try (PreparedStatement ps = M4.getConexion().prepareStatement(sql)) {
                    ps.setInt(1, idPelicula);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            tituloPelicula = rs.getString("Titulo").toUpperCase();
                            sinopsisPelicula = rs.getString("Sinopsis");
                            int duracionMinutos = rs.getInt("Duracion");
                            int horas = duracionMinutos / 60;
                            int minutos = duracionMinutos % 60;
                            duracionPelicula = "DURACIÓN: " + String.format("%dh %dm", horas, minutos);
                            posterUrl = rs.getString("Imagen");
                        }
                    }
                }

                if (posterUrl != null && !posterUrl.isEmpty()) {
                    try {
                        URL imageUrl = new URL(posterUrl);
                        InputStream in = imageUrl.openStream();
                        posterImage = ImageIO.read(in);
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                cargarOtrasPeliculas();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    titulo.setText(tituloPelicula);
                    sinopsisArea.setText(sinopsisPelicula);
                    duracionLabel.setText(duracionPelicula);

                    if (posterImage != null) {
                        int newWidth = 300;
                        int newHeight = (posterImage.getHeight() * newWidth) / posterImage.getWidth();
                        Image scaledImage = posterImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                        posterLabel.setIcon(new ImageIcon(scaledImage));
                    } else {
                        posterLabel.setText("Póster no disponible");
                    }

                    cargarFunciones();

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PantallaPelicula.this, "Error al cargar datos: " + e.getMessage());
                }
            }
        }.execute();
    }
    
    private void inicializarUI() {
        // Main scrollable content container
        JPanel mainScrollableContent = new JPanel();
        mainScrollableContent.setLayout(new BoxLayout(mainScrollableContent, BoxLayout.Y_AXIS));
        mainScrollableContent.setBackground(new Color(0x2B2B2B));
        mainScrollableContent.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Two-column section
        JPanel twoColumnPanel = new JPanel(new BorderLayout(20, 20));
        twoColumnPanel.setBackground(new Color(0x2B2B2B));
        twoColumnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));

        JPanel leftPanel = crearPanelIzquierdo();
        twoColumnPanel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = crearPanelDerecho();
        twoColumnPanel.add(rightPanel, BorderLayout.CENTER);

        mainScrollableContent.add(twoColumnPanel);
        mainScrollableContent.add(Box.createRigidArea(new Dimension(0, 30)));

        // Separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(70, 70, 70));
        separator.setBackground(new Color(50, 50, 50));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        mainScrollableContent.add(separator);
        mainScrollableContent.add(Box.createRigidArea(new Dimension(0, 30)));

        // "Otras Peliculas" Section
        JPanel otrasPeliculasSection = crearPanelOtrasPeliculas();
        mainScrollableContent.add(otrasPeliculasSection);

        // Wrap in JScrollPane
        JScrollPane masterScrollPane = new JScrollPane(mainScrollableContent);
        masterScrollPane.setBorder(null);
        masterScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        masterScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        personalizarScrollBar(masterScrollPane.getVerticalScrollBar());

        add(masterScrollPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelOtrasPeliculas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x2B2B2B));
        panel.setBorder(new EmptyBorder(15, 0, 15, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        JLabel titulo = new JLabel("PELICULAS EN CARTELERA");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titulo, BorderLayout.NORTH);

        panelTarjetasPeliculas = new JPanel();
        panelTarjetasPeliculas.setLayout(new BoxLayout(panelTarjetasPeliculas, BoxLayout.X_AXIS));
        panelTarjetasPeliculas.setBackground(new Color(0x2B2B2B));

        JScrollPane scrollPane = new JScrollPane(panelTarjetasPeliculas);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        personalizarScrollBar(scrollPane.getHorizontalScrollBar());

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private void personalizarScrollBar(JScrollBar scrollBar) {
        scrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 60, 60);
                this.trackColor = new Color(0x2B2B2B);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
                g2.dispose();
            }
            
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });
    }

    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0x2B2B2B));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        
        posterLabel = new JLabel();
        posterLabel.setPreferredSize(new Dimension(300, 450));
        posterLabel.setMaximumSize(new Dimension(300, 450));
        posterLabel.setMinimumSize(new Dimension(300, 450));
        posterLabel.setBackground(new Color(50, 50, 50));
        posterLabel.setOpaque(true);
        posterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        posterLabel.setHorizontalAlignment(JLabel.CENTER);
        posterLabel.setVerticalAlignment(JLabel.CENTER);
        panel.add(posterLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        sinopsisArea = new JTextArea();
        sinopsisArea.setLineWrap(true);
        sinopsisArea.setWrapStyleWord(true);
        sinopsisArea.setEditable(false);
        sinopsisArea.setBackground(new Color(0x2B2B2B));
        sinopsisArea.setForeground(Color.WHITE);
        sinopsisArea.setFont(new Font("Arial", Font.PLAIN, 13));
        sinopsisArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        sinopsisArea.setBorder(null);
        
        JPanel sinopsisPanel = new JPanel(new BorderLayout());
        sinopsisPanel.setBackground(new Color(0x2B2B2B));
        sinopsisPanel.setPreferredSize(new Dimension(300, 150));
        sinopsisPanel.setMaximumSize(new Dimension(300, 150));
        sinopsisPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sinopsisPanel.add(sinopsisArea, BorderLayout.CENTER);
        panel.add(sinopsisPanel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        duracionLabel = new JLabel();
        duracionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        duracionLabel.setForeground(Color.WHITE);
        duracionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(duracionLabel);
        
        return panel;
    }
    
    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0x2B2B2B));
        
        titulo = new JLabel("CARGANDO...");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JLabel labelFechas = new JLabel("Fechas:");
        labelFechas.setFont(new Font("Arial", Font.BOLD, 18));
        labelFechas.setForeground(Color.WHITE);
        labelFechas.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(labelFechas);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panelFechas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFechas.setBackground(new Color(0x2B2B2B));
        panelFechas.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(panelFechas);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel filtrosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        filtrosPanel.setBackground(new Color(0x2B2B2B));
        filtrosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbIdioma = crearComboBox("Idioma");
        cbFormato = crearComboBox("Formato");

        cbIdioma.addActionListener(e -> {
            selectedIdioma = (String) cbIdioma.getSelectedItem();
            actualizarHorarios();
        });

        cbFormato.addActionListener(e -> {
            selectedFormato = (String) cbFormato.getSelectedItem();
            actualizarHorarios();
        });
        
        filtrosPanel.add(cbIdioma);
        filtrosPanel.add(cbFormato);
        panel.add(filtrosPanel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JLabel labelHorarios = new JLabel("Horarios:");
        labelHorarios.setFont(new Font("Arial", Font.BOLD, 18));
        labelHorarios.setForeground(Color.WHITE);
        labelHorarios.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(labelHorarios);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panelHorarios = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panelHorarios.setBackground(new Color(0x2B2B2B));
        panelHorarios.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(panelHorarios);
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        JButton btnComprar = new JButton("COMPRAR ENTRADAS") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(getBackground().brighter());
                } else {
                    g2.setColor(getBackground());
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnComprar.setFont(new Font("Arial", Font.BOLD, 16));
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setBackground(new Color(220, 50, 50));
        btnComprar.setPreferredSize(new Dimension(400, 50));
        btnComprar.setMaximumSize(new Dimension(400, 50));
        btnComprar.setFocusPainted(false);
        btnComprar.setBorderPainted(false);
        btnComprar.setContentAreaFilled(false);
        btnComprar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComprar.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnComprar.addActionListener(e -> {
            if (cbIdioma.getSelectedIndex() == 0) {
                CustomDialog dialog = new CustomDialog(M4.getMainFrame(), "Por favor, seleccione un idioma.");
                dialog.setVisible(true);
                return;
            }
            if (cbFormato.getSelectedIndex() == 0) {
                CustomDialog dialog = new CustomDialog(M4.getMainFrame(), "Por favor, seleccione un formato.");
                dialog.setVisible(true);
                return;
            }

            FuncionInfo seleccionada = obtenerFuncionSeleccionada();
            if (seleccionada != null) {
                abrirPantallaButacas(seleccionada);
            } else {
                CustomDialog dialog = new CustomDialog(M4.getMainFrame(), "Por favor, seleccione un horario.");
                dialog.setVisible(true);
            }
        });
        
        panel.add(btnComprar);
        
        return panel;
    }
    
    private JComboBox<String> crearComboBox(String nombre) {
        JComboBox<String> cb = new JComboBox<String>() {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(80, 80, 80));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
            }
            
            @Override
            public void setSelectedIndex(int index) {
                super.setSelectedIndex(index);
                if (index == 0) {
                    setForeground(new Color(150, 150, 150));
                } else {
                    setForeground(Color.WHITE);
                }
            }
        };
        cb.setOpaque(false);
        cb.setPreferredSize(new Dimension(200, 40));
        cb.setBackground(new Color(50, 50, 50));
        cb.setForeground(new Color(150, 150, 150));
        cb.setFont(new Font("Arial", Font.PLAIN, 14));
        
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (index == -1) {
                    if (cb.getSelectedIndex() == 0) {
                        setForeground(new Color(150, 150, 150));
                    } else {
                        setForeground(Color.WHITE);
                    }
                } else if (index == 0) {
                    setForeground(new Color(150, 150, 150));
                } else {
                    setForeground(Color.BLACK);
                }
                
                if (isSelected) {
                    setBackground(new Color(70, 70, 70));
                } else {
                    setBackground(new Color(50, 50, 50));
                }
                
                return this;
            }
        });

        for (Component comp : cb.getComponents()) {
            if (comp instanceof JButton) {
                ((JButton) comp).setBackground(new Color(50, 50, 50));
                ((JButton) comp).setBorderPainted(false);
                ((JButton) comp).setContentAreaFilled(false);
            }
        }

        cb.addItem(nombre);
        return cb;
    }
    

    
    private void cargarFunciones() {
        panelFechas.removeAll();

        new SwingWorker<Map<java.util.Date, java.util.List<String>>, Void>() {
            @Override
            protected java.util.Map<java.util.Date, java.util.List<String>> doInBackground() throws Exception {
                java.util.Map<java.util.Date, java.util.List<String>> fechas = new LinkedHashMap<>();
                String sql = "SELECT DISTINCT FechaFuncion FROM Funcion WHERE ID_Pelicula = ? ORDER BY FechaFuncion";
                try (PreparedStatement ps = M4.getConexion().prepareStatement(sql)) {
                    ps.setInt(1, idPelicula);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            fechas.put(rs.getDate("FechaFuncion"), new ArrayList<>());
                        }
                    }
                }
                return fechas;
            }

            @Override
            protected void done() {
                try {
                    Map<java.util.Date, java.util.List<String>> fechas = get();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
                    grupoBotones = new ButtonGroup();

                    for (java.util.Date fecha : fechas.keySet()) {
                        JButton btnFecha = crearBotonFecha(sdf.format(fecha));
                                            btnFecha.addActionListener(e -> {
                                                selectedDate = fecha;
                                                actualizarFiltros(fecha);
                                            });                        panelFechas.add(btnFecha);
                        grupoBotones.add(btnFecha);
                    }

                    if (panelFechas.getComponentCount() > 0) {
                        Component firstButton = panelFechas.getComponent(0);
                        if (firstButton instanceof JButton) {
                            ((JButton) firstButton).doClick();
                        }
                    }
                    panelFechas.revalidate();
                    panelFechas.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    
    private void actualizarHorarios() {
        panelHorarios.removeAll();
        mapaFunciones.clear();

        new SwingWorker<Map<JButton, FuncionInfo>, Void>() {
            @Override
            protected Map<JButton, FuncionInfo> doInBackground() throws Exception {
                Map<JButton, FuncionInfo> funciones = new LinkedHashMap<>();
                StringBuilder sqlBuilder = new StringBuilder(
                    "SELECT DISTINCT f.ID_Funcion, f.ID_Sala, f.HoraFuncion FROM Funcion f JOIN Sala s ON f.ID_Sala = s.ID_Sala WHERE f.ID_Pelicula = ?");
                
                java.util.List<Object> params = new ArrayList<>();
                params.add(idPelicula);

                if (selectedDate != null) {
                    sqlBuilder.append(" AND f.FechaFuncion = ?");
                    params.add(new java.sql.Date(selectedDate.getTime()));
                }
                if (selectedIdioma != null && !"Idioma".equals(selectedIdioma)) {
                    sqlBuilder.append(" AND f.Idioma = ?");
                    params.add(selectedIdioma);
                }
                if (selectedFormato != null && !"Formato".equals(selectedFormato)) {
                    sqlBuilder.append(" AND s.TipoDeSala = ?");
                    params.add(selectedFormato);
                }

                sqlBuilder.append(" ORDER BY f.HoraFuncion");

                try (PreparedStatement ps = M4.getConexion().prepareStatement(sqlBuilder.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        ps.setObject(i + 1, params.get(i));
                    }

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String hora = rs.getString("HoraFuncion");
                            JButton btnHora = crearBotonHorario(hora);
                            
                            FuncionInfo info = new FuncionInfo(
                                rs.getInt("ID_Funcion"),
                                rs.getInt("ID_Sala"),
                                hora
                            );
                            funciones.put(btnHora, info);
                        }
                    }
                }
                return funciones;
            }

            @Override
            protected void done() {
                try {
                    Map<JButton, FuncionInfo> funciones = get();
                    mapaFunciones.putAll(funciones);
                    for (JButton btnHora : funciones.keySet()) {
                        panelHorarios.add(btnHora);
                    }

                    if (panelHorarios.getComponentCount() > 0) {
                        Component firstButton = panelHorarios.getComponent(0);
                        if (firstButton instanceof JButton) {
                            ((JButton) firstButton).doClick();
                        }
                    }

                    panelHorarios.revalidate();
                    panelHorarios.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void actualizarFiltros(java.util.Date fecha) {
        new SwingWorker<Map<String, java.util.List<String>>, Void>() {
            @Override
            protected Map<String, java.util.List<String>> doInBackground() throws Exception {
                Map<String, java.util.List<String>> filtros = new HashMap<>();
                filtros.put("idiomas", new ArrayList<>());
                filtros.put("formatos", new ArrayList<>());

                String sqlLang = "SELECT DISTINCT Idioma FROM Funcion WHERE ID_Pelicula = ? AND FechaFuncion = ?";
                try (PreparedStatement psLang = M4.getConexion().prepareStatement(sqlLang)) {
                    psLang.setInt(1, idPelicula);
                    psLang.setDate(2, new java.sql.Date(fecha.getTime()));
                    try (ResultSet rsLang = psLang.executeQuery()) {
                        while (rsLang.next()) {
                            filtros.get("idiomas").add(rsLang.getString("Idioma"));
                        }
                    }
                }

                String sqlFormat = "SELECT DISTINCT s.TipoDeSala FROM Sala s INNER JOIN Funcion f ON s.ID_Sala = f.ID_Sala WHERE f.ID_Pelicula = ? AND f.FechaFuncion = ?";
                try (PreparedStatement psFormat = M4.getConexion().prepareStatement(sqlFormat)) {
                    psFormat.setInt(1, idPelicula);
                    psFormat.setDate(2, new java.sql.Date(fecha.getTime()));
                    try (ResultSet rsFormat = psFormat.executeQuery()) {
                        while (rsFormat.next()) {
                            filtros.get("formatos").add(rsFormat.getString("TipoDeSala"));
                        }
                    }
                }
                return filtros;
            }

            @Override
            protected void done() {
                try {
                    Map<String, java.util.List<String>> filtros = get();
                    ActionListener idiomaListener = cbIdioma.getActionListeners().length > 0 ? cbIdioma.getActionListeners()[0] : null;
                    ActionListener formatoListener = cbFormato.getActionListeners().length > 0 ? cbFormato.getActionListeners()[0] : null;
                    if (idiomaListener != null) cbIdioma.removeActionListener(idiomaListener);
                    if (formatoListener != null) cbFormato.removeActionListener(formatoListener);

                    cbIdioma.removeAllItems();
                    cbFormato.removeAllItems();
                    
                    cbIdioma.addItem("Idioma");
                    cbFormato.addItem("Formato");

                    for (String idioma : filtros.get("idiomas")) {
                        cbIdioma.addItem(idioma);
                    }
                    for (String formato : filtros.get("formatos")) {
                        cbFormato.addItem(formato);
                    }

                    cbIdioma.setSelectedIndex(0);
                    cbFormato.setSelectedIndex(0);
                    selectedIdioma = (String) cbIdioma.getSelectedItem();
                    selectedFormato = (String) cbFormato.getSelectedItem();

                    if (idiomaListener != null) cbIdioma.addActionListener(idiomaListener);
                    if (formatoListener != null) cbFormato.addActionListener(formatoListener);

                    actualizarHorarios();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void cargarOtrasPeliculas() {
        new SwingWorker<java.util.List<PeliculaCard>, Void>() {
            @Override
            protected java.util.List<PeliculaCard> doInBackground() throws Exception {
                java.util.List<PeliculaCard> cards = new ArrayList<>();
                String sql = "SELECT ID_Pelicula, Titulo, Imagen, ClasificacionEdad FROM Pelicula WHERE ID_Pelicula != ? ORDER BY RAND()";
                try (PreparedStatement ps = M4.getConexion().prepareStatement(sql)) {
                    ps.setInt(1, idPelicula);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            PeliculaCard card = new PeliculaCard(
                                rs.getInt("ID_Pelicula"),
                                rs.getString("Titulo"),
                                rs.getString("Imagen"),
                                rs.getString("ClasificacionEdad")
                            );
                            cards.add(card);
                        }
                    }
                }
                return cards;
            }

            @Override
            protected void done() {
                try {
                    java.util.List<PeliculaCard> cards = get();
                    panelTarjetasPeliculas.removeAll();
                    for (PeliculaCard card : cards) {
                        panelTarjetasPeliculas.add(card);
                        panelTarjetasPeliculas.add(Box.createRigidArea(new Dimension(15, 0)));
                    }
                    panelTarjetasPeliculas.revalidate();
                    panelTarjetasPeliculas.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    
    private JButton crearBotonFecha(String texto) {
        JButton btn = new JButton(texto) {
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
        btn.setPreferredSize(new Dimension(80, 40));
        btn.setBackground(new Color(50, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> {
            for (Component c : panelFechas.getComponents()) {
                if (c instanceof JButton) {
                    c.setBackground(new Color(50, 50, 50));
                }
            }
            btn.setBackground(new Color(80, 80, 80));
        });
        
        return btn;
    }
    
    private JButton crearBotonHorario(String texto) {
        JButton btn = new JButton(texto) {
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
        btn.setPreferredSize(new Dimension(90, 45));
        btn.setBackground(new Color(50, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> {
            for (Component c : panelHorarios.getComponents()) {
                if (c instanceof JButton) {
                    c.setBackground(new Color(50, 50, 50));
                }
            }
            btn.setBackground(new Color(220, 50, 50));
        });
        
        return btn;
    }
    
    private FuncionInfo obtenerFuncionSeleccionada() {
        for (Map.Entry<JButton, FuncionInfo> entry : mapaFunciones.entrySet()) {
            if (entry.getKey().getBackground().equals(new Color(220, 50, 50))) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    private void abrirPantallaButacas(FuncionInfo funcion) {
        M4.mostrarPantallaButacas(funcion.idFuncion, funcion.idSala, this.idPelicula);
    }
}