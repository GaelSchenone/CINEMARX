package CINEMARX.M3;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.Normalizer; 

// ******************************************************************************
// NOTA: Se asume la existenciae las clases 'ConexionBD', 'Producto', y 
// 'CustomizationDialog' para que este código compile y funcione correctamente.
// ******************************************************************************

public class BuffetFrame extends JFrame {
    private JPanel panelContenido;
    private Map<Integer, List<String>> carrito = new HashMap<>(); 
    private Map<Integer, JLabel> contadoresProductos = new HashMap<>();
    private int idClienteActual = 1; 
    
    // --- VARIABLES DE FILTRO Y VISTA ---
    private String categoriaSeleccionada = "Todos"; 
    private static final String[] CATEGORIAS_FILTRO = {"Todos", "Combos", "Bebidas", "Snacks", "Comida", "Otros"};
    private String modoVisualizacion = "CUADRICULA"; 
    
    // --- Componente del buscador (DEBE SER GLOBAL) ---
    private JTextField txtBuscador; 

    // URLs de imágenes (Asegúrate de que estas URLs sean accesibles)
    private static final String BASE_IMAGE_URL = 
        "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=M7%2F";
        
    private static final String LOGO_URL = 
        "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=M7%2F02.png";

    public BuffetFrame() {
        initComponents();
    }

    public BuffetFrame(int idCliente) {
        this.idClienteActual = idCliente;
        initComponents();
    }

    private void initComponents() {
        setTitle("Cinemar X - Buffet");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(45, 45, 45));

        JPanel header = createHeader();
        mainPanel.add(header, BorderLayout.NORTH);

        // Creamos y añadimos el panel de controles (Filtros, Buscador, Vistas)
        JPanel controlPanel = createControlPanel();
        
        // Creamos un contenedor para apilar los controles y el scroll
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setBackground(new Color(45, 45, 45));
        
        centerContainer.add(controlPanel);

        panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(new Color(45, 45, 45));
        panelContenido.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); 

        JScrollPane scroll = new JScrollPane(panelContenido);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        scroll.setBackground(new Color(45, 45, 45));
        scroll.getViewport().setBackground(new Color(45, 45, 45));

        centerContainer.add(scroll); 
        
        mainPanel.add(centerContainer, BorderLayout.CENTER);
        add(mainPanel);

        cargarProductosPorCategoria(null); 
    }
    
    private JPanel createControlPanel() {
        // Usamos GridBagLayout para control preciso de pesos y posiciones
        JPanel controlPanel = new JPanel(new GridBagLayout()); 
        controlPanel.setBackground(new Color(45, 45, 45));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50)); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.fill = GridBagConstraints.NONE;

        // --- 1. FILTRO POR CATEGORÍA (JComboBox) - EN LA IZQUIERDA ---
        JComboBox<String> cmbFiltro = new JComboBox<>(CATEGORIAS_FILTRO);
        cmbFiltro.setFont(new Font("Arial", Font.PLAIN, 16));
        cmbFiltro.setBackground(new Color(60, 60, 60));
        cmbFiltro.setForeground(Color.WHITE);
        cmbFiltro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmbFiltro.setPreferredSize(new Dimension(150, 45));

        cmbFiltro.addActionListener(e -> {
            categoriaSeleccionada = (String) cmbFiltro.getSelectedItem();
            cargarProductosPorCategoria(txtBuscador.getText()); 
        });
        gbc.gridx = 0; // Columna 0 (Extremo izquierdo)
        gbc.anchor = GridBagConstraints.WEST; // Ancla a la izquierda
        controlPanel.add(cmbFiltro, gbc);
        
        // --- 2. ESPACIO IZQUIERDO (Peso para empujar el Buscador al centro) ---
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        controlPanel.add(Box.createRigidArea(new Dimension(0, 0)), gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        // --- 3. BUSCADOR (Campo de Texto) - CENTRADO Y ANCHO A 50 COLUMNAS ---
        txtBuscador = new JTextField();
        txtBuscador.setFont(new Font("Arial", Font.PLAIN, 16));
        txtBuscador.setForeground(Color.WHITE);
        txtBuscador.setBackground(new Color(60, 60, 60));
        txtBuscador.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipadx = 150; // Padding interno horizontal (aumenta el ancho)
        gbc.ipady = 5;   // Padding interno vertical (aumenta el alto)
        controlPanel.add(txtBuscador, gbc);
        
        // --- 4. BOTÓN BUSCAR - CENTRADO ---
        JButton btnBuscar = new JButton("BUSCAR"); 
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 14)); 
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setBackground(new Color(80, 80, 80));
        btnBuscar.setPreferredSize(new Dimension(80, 45)); 
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnBuscar.addActionListener(e -> {
            cargarProductosPorCategoria(txtBuscador.getText());
        });
        gbc.gridx = 3; // Columna 3
        gbc.anchor = GridBagConstraints.CENTER; // Ancla al centro
        controlPanel.add(btnBuscar, gbc);

        // --- 5. ESPACIO CENTRAL (Peso para empujar los botones de vista a la derecha) ---
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 4;
        controlPanel.add(Box.createRigidArea(new Dimension(0, 0)), gbc); 
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        
        // Los siguientes componentes mantienen el anclaje a la derecha
        gbc.anchor = GridBagConstraints.EAST; 
        
        // --- 6. Botones de Visualización (Cuadrícula) - SIN EMOJIS ---
        JButton btnCuadricula = new JButton("Cuadrícula"); 
        btnCuadricula.setFont(new Font("Arial", Font.PLAIN, 14));
        btnCuadricula.setForeground(Color.WHITE);
        btnCuadricula.setBackground(new Color(80, 80, 80));
        btnCuadricula.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCuadricula.setFocusPainted(false);
        btnCuadricula.addActionListener(e -> {
            modoVisualizacion = "CUADRICULA";
            cargarProductosPorCategoria(txtBuscador.getText()); 
        });
        gbc.gridx = 5;
        controlPanel.add(btnCuadricula, gbc);
        
        // --- 7. Botones de Visualización (Lista) - SIN EMOJIS ---
        JButton btnLista = new JButton("Lista"); 
        btnLista.setFont(new Font("Arial", Font.PLAIN, 14));
        btnLista.setForeground(Color.WHITE);
        btnLista.setBackground(new Color(80, 80, 80));
        btnLista.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLista.setFocusPainted(false);
        btnLista.addActionListener(e -> {
            modoVisualizacion = "LISTA";
            cargarProductosPorCategoria(txtBuscador.getText()); 
        });
        gbc.gridx = 6;
        controlPanel.add(btnLista, gbc);
        
        return controlPanel;
    }


    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(45, 45, 45));
        header.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));

        // --- Panel Izquierdo (Solo Botón Volver) ---
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setBackground(new Color(45, 45, 45));

        JButton btnVolver = new JButton("←");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 28));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setBackground(new Color(45, 45, 45));
        btnVolver.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setContentAreaFilled(false);
        btnVolver.addActionListener(e -> dispose());
        leftPanel.add(btnVolver);
        
        header.add(leftPanel, BorderLayout.WEST); 

        // Carga asíncrona del logo
        JLabel lblLogo = new JLabel("Cargando Logo...", SwingConstants.CENTER); 
        lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        lblLogo.setForeground(Color.LIGHT_GRAY);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        cargarLogo(LOGO_URL, lblLogo); 
        header.add(lblLogo, BorderLayout.CENTER);

        // --- Panel Derecho (Solo Botón Continuar) ---
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(new Color(45, 45, 45));
        
        // Botón Continuar (EL ÚNICO QUE SE MANTIENE)
        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 16));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setBackground(new Color(220, 50, 50));
        btnContinuar.setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));
        btnContinuar.setFocusPainted(false);
        btnContinuar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnContinuar.addActionListener(e -> continuarCompra());
        
        rightPanel.add(btnContinuar);

        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }
    
    private void cargarLogo(String logoUrl, JLabel lblLogo) {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                return new ImageIcon(new java.net.URL(logoUrl));
            }

            @Override
            protected void done() {
                try {
                    ImageIcon originalIcon = get();
                    if (originalIcon != null && originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        Image originalImage = originalIcon.getImage();
                        int width = 350; // Usar dimensiones fijas o dinámicas según diseño
                        int height = 55;
                        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        
                        lblLogo.setIcon(new ImageIcon(scaledImage));
                        lblLogo.setText(null); 
                        
                    } else {
                        lblLogo.setText("CINEMARX");
                        lblLogo.setFont(new Font("Arial", Font.BOLD, 32));
                        lblLogo.setForeground(Color.WHITE);
                    }
                } catch (Exception e) {
                    lblLogo.setText("CINEMARX");
                    lblLogo.setFont(new Font("Arial", Font.BOLD, 32));
                    lblLogo.setForeground(Color.WHITE);
                    System.err.println("Error al cargar logo desde URL: " + e.getMessage());
                }
            }
        }.execute();
    }


    // --- MÉTODO CARGA DE PRODUCTOS ---
    private void cargarProductosPorCategoria(String filtroBusqueda) {
        panelContenido.removeAll();
        
        // --- TÍTULO PRINCIPAL (Inicia el contenido del scroll) ---
        JLabel lblTitulo = new JLabel("Acompaña tu película con algo para picar:"); 
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panelContenido.add(lblTitulo);
        
        Map<String, List<Producto>> productosPorCategoria = new HashMap<>();

        final String filtroNormalizado = normalizarTexto(filtroBusqueda);
        
        // NOTA: Asegúrate de que tu clase ConexionBD.getConexion() esté disponible
        try (Connection con = ConexionBD.getConexion()) {
            if (con == null) {
                throw new Exception("No se pudo conectar a la base de datos.");
            }

            String query = "SELECT ID_Prod, Nombre, Precio FROM Producto ORDER BY Nombre";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                // Asumiendo que existe una clase Producto con estos campos
                Producto p = new Producto(
                    rs.getInt("ID_Prod"),
                    rs.getString("Nombre"),
                    rs.getDouble("Precio"),
                    determinarCategoria(rs.getString("Nombre")),
                    "" 
                );

                if (!filtroNormalizado.isEmpty()) {
                    String nombreNormalizado = normalizarTexto(p.getNombre());
                    if (!nombreNormalizado.contains(filtroNormalizado)) {
                        continue; 
                    }
                }
                
                String categoria = p.getCategoria();
                if (!productosPorCategoria.containsKey(categoria)) {
                    productosPorCategoria.put(categoria, new ArrayList<>());
                }
                productosPorCategoria.get(categoria).add(p);
            }

        } catch (Exception e) {
            System.err.println("❌ Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }

        mostrarProductosPorCategoria(productosPorCategoria);
    }

    private String normalizarTexto(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalizado = normalizado.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalizado.toLowerCase();
    }

    private String determinarCategoria(String nombreProducto) {
        String nombre = nombreProducto.toLowerCase();
        
        if (nombre.contains("combo") || nombre.contains("pack") || nombre.contains("pareja")) {
            return "Combos";
        } else if (nombre.contains("coca") || nombre.contains("pepsi") || 
                    nombre.contains("sprite") || nombre.contains("agua") || 
                    nombre.contains("jugo") || nombre.contains("gaseosa")) {
            return "Bebidas";
        } else if (nombre.contains("pochoclo") || nombre.contains("nachos") || 
                    nombre.contains("palomitas") || nombre.contains("papas") || 
                    nombre.contains("fritas") || nombre.contains("maní") ||
                    nombre.contains("hot dog") || nombre.contains("pancho")) { 
            return "Snacks";
        } else if (nombre.contains("chocolate") || nombre.contains("caramelo") || 
                   nombre.contains("gomita") || nombre.contains("dulce") ||
                   nombre.contains("pizza") || nombre.contains("hamburguesa") ||
                   nombre.contains("nugget") || nombre.contains("galleta")) {
            return "Comida"; 
        }
        
        return "Otros";
    }

    private void mostrarProductosPorCategoria(Map<String, List<Producto>> productosPorCategoria) {
        String[] ordenCategorias = {"Combos", "Bebidas", "Snacks", "Comida", "Otros"}; 
        
        boolean filtrarPorCategoria = !categoriaSeleccionada.equals("Todos");

        for (String categoria : ordenCategorias) {
            
            if (filtrarPorCategoria && !categoria.equals(categoriaSeleccionada)) {
                continue; 
            }
            
            if (productosPorCategoria.containsKey(categoria)) {
                
                JLabel lblCategoria = new JLabel("— " + categoria.toUpperCase() + " —");
                lblCategoria.setFont(new Font("Arial", Font.BOLD, 22));
                lblCategoria.setForeground(new Color(220, 50, 50));
                lblCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
                lblCategoria.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
                panelContenido.add(lblCategoria);
                
                List<Producto> productos = productosPorCategoria.get(categoria);

                JPanel panelCategoria = new JPanel();
                panelCategoria.setBackground(new Color(45, 45, 45));
                panelCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
                panelCategoria.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

                if (modoVisualizacion.equals("CUADRICULA")) {
                    panelCategoria.setLayout(new FlowLayout(FlowLayout.LEFT, 25, 25));
                } else if (modoVisualizacion.equals("LISTA")) {
                    panelCategoria.setLayout(new BoxLayout(panelCategoria, BoxLayout.Y_AXIS));
                }

                for (Producto p : productos) {
                    JPanel card = crearTarjetaProducto(p);
                    panelCategoria.add(card);
                }

                panelContenido.add(panelCategoria);
            }
        }

        panelContenido.revalidate();
        panelContenido.repaint();
    }
    
    private JPanel crearControlPanel(Producto p) {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        controlPanel.setBackground(new Color(45, 45, 45));
        controlPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnRestar = new JButton("-");
        btnRestar.setFont(new Font("Arial", Font.BOLD, 24));
        btnRestar.setForeground(Color.WHITE);
        btnRestar.setBackground(new Color(80, 80, 80));
        btnRestar.setPreferredSize(new Dimension(45, 45));
        btnRestar.setFocusPainted(false);
        btnRestar.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
        btnRestar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRestar.addActionListener(e -> restarDelCarrito(p));

        int cantidadActual = carrito.containsKey(p.getId()) ? carrito.get(p.getId()).size() : 0;
        JLabel lblContador = new JLabel(String.valueOf(cantidadActual));
        
        lblContador.setFont(new Font("Arial", Font.BOLD, 18));
        lblContador.setForeground(Color.WHITE);
        lblContador.setPreferredSize(new Dimension(30, 45));
        lblContador.setHorizontalAlignment(SwingConstants.CENTER);
        contadoresProductos.put(p.getId(), lblContador);

        JButton btnAgregar = new JButton("+");
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 24));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setBackground(new Color(220, 50, 50));
        btnAgregar.setPreferredSize(new Dimension(45, 45));
        btnAgregar.setFocusPainted(false);
        btnAgregar.setBorder(BorderFactory.createLineBorder(new Color(220, 50, 50), 2));
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnAgregar.addActionListener(e -> {
            // Asumiendo la existencia de CustomizationDialog
            CustomizationDialog dialog = new CustomizationDialog(this, p); 
            dialog.setVisible(true);
            
            if (dialog.isAceptado()) {
                manejarPersonalizacion(p, dialog.getOpcionSeleccionada());
            }
        });

        controlPanel.add(btnRestar);
        controlPanel.add(lblContador);
        controlPanel.add(btnAgregar);
        
        return controlPanel;
    }
    
    private JPanel crearTarjetaProducto(Producto p) {
        JPanel card = new JPanel(); 
        card.setBackground(new Color(45, 45, 45));
        
        if (modoVisualizacion.equals("CUADRICULA")) {
            card.setLayout(new BorderLayout());
            card.setPreferredSize(new Dimension(260, 400));
            card.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1)); 
        } else { // LISTA
            card.setLayout(new BorderLayout(20, 0)); 
            card.setPreferredSize(new Dimension(800, 120)); 
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        }

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setBackground(new Color(235, 225, 210));
        
        if (modoVisualizacion.equals("CUADRICULA")) {
            imgPanel.setPreferredSize(new Dimension(260, 280));
        } else { // LISTA
            imgPanel.setPreferredSize(new Dimension(100, 100));
        }
        
        JLabel lblPlaceholder = new JLabel("Cargando...", SwingConstants.CENTER); 
        lblPlaceholder.setFont(new Font("Arial", Font.BOLD, 16));
        lblPlaceholder.setForeground(new Color(80, 80, 80));
        imgPanel.add(lblPlaceholder, BorderLayout.CENTER);
        
        card.add(imgPanel, (modoVisualizacion.equals("CUADRICULA") ? BorderLayout.CENTER : BorderLayout.WEST));
        
        String imageUrl = BASE_IMAGE_URL + p.getId() + ".png";
        cargarImagenDesdeURL(imageUrl, imgPanel, lblPlaceholder);
        
        JPanel info = new JPanel();
        info.setBackground(new Color(45, 45, 45));
        
        JLabel lblNombre = new JLabel(p.getNombre().toUpperCase());
        lblNombre.setFont(new Font("Arial", Font.BOLD, 13));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPrecio = new JLabel("$" + String.format("%.0f", p.getPrecio()));
        lblPrecio.setFont(new Font("Arial", Font.PLAIN, 13));
        lblPrecio.setForeground(Color.WHITE);
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel controlPanel = crearControlPanel(p);

        if (modoVisualizacion.equals("LISTA")) {
            info.setLayout(new BorderLayout()); 

            JPanel textoPanel = new JPanel(new GridLayout(2, 1));
            textoPanel.setBackground(new Color(45, 45, 45));
            textoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            textoPanel.add(lblNombre);
            textoPanel.add(lblPrecio);

            info.add(textoPanel, BorderLayout.CENTER);
            info.add(controlPanel, BorderLayout.EAST);
            
            card.add(info, BorderLayout.CENTER);
            
        } else { // CUADRICULA
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
            
            JPanel textoPanel = new JPanel(); 
            textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
            textoPanel.setBackground(new Color(45, 45, 45));
            textoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            textoPanel.add(lblNombre);
            textoPanel.add(Box.createVerticalStrut(5));
            textoPanel.add(lblPrecio);

            info.add(textoPanel);
            info.add(Box.createVerticalStrut(10));
            info.add(controlPanel);

            card.add(info, BorderLayout.SOUTH);
        }
        
        // --- LÓGICA DEL HOVER EFFECT ---
        Color baseColor = new Color(45, 45, 45);
        Color hoverColor = new Color(65, 65, 65);
        Color baseBorder = new Color(60, 60, 60);
        Color hoverBorder = new Color(255, 180, 0); 

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(hoverColor);
                info.setBackground(hoverColor);
                controlPanel.setBackground(hoverColor);
                card.setBorder(BorderFactory.createLineBorder(hoverBorder, 2)); 
                
                for (Component comp : info.getComponents()) {
                    if (comp instanceof JPanel) {
                        ((JPanel) comp).setBackground(hoverColor);
                    }
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(baseColor);
                info.setBackground(baseColor);
                controlPanel.setBackground(baseColor);
                card.setBorder(BorderFactory.createLineBorder(baseBorder, 1));
                
                for (Component comp : info.getComponents()) {
                    if (comp instanceof JPanel) {
                        ((JPanel) comp).setBackground(baseColor);
                    }
                }
            }
        });

        return card;
    }

    private void manejarPersonalizacion(Producto producto, String opcion) {
        agregarAlCarrito(producto, opcion); 
        
        String detalle = opcion != null && !opcion.isEmpty() 
                         ? "\n   [Añadidos: " + opcion.replace(";", ", ") + "]" 
                         : " (Sin personalización)";

        System.out.println("Agregado al carrito: " + producto.getNombre() + detalle);
        JOptionPane.showMessageDialog(this,
            producto.getNombre() + detalle + " añadido al carrito.",
            "Personalización Aplicada",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cargarImagenDesdeURL(String imagenRuta, JPanel contenedor, JLabel etiquetaPlaceholder) {
        if (imagenRuta == null || imagenRuta.trim().isEmpty()) {
            etiquetaPlaceholder.setText("[IMAGEN NO DISPONIBLE]");
            etiquetaPlaceholder.setFont(new Font("Arial", Font.BOLD, 14)); 
            etiquetaPlaceholder.setForeground(new Color(80, 80, 80));
            return;
        }

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                // Intenta crear ImageIcon desde la URL
                return new ImageIcon(new java.net.URL(imagenRuta));
            }

            @Override
            protected void done() {
                try {
                    ImageIcon originalIcon = get();
                    if (originalIcon != null && originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        Image originalImage = originalIcon.getImage();
                        int width = contenedor.getPreferredSize().width;
                        int height = contenedor.getPreferredSize().height;
                        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        
                        ImageIcon scaledIcon = new ImageIcon(scaledImage);
                        
                        JLabel lblImagenFinal = new JLabel(scaledIcon, SwingConstants.CENTER);
                        contenedor.remove(etiquetaPlaceholder);
                        contenedor.add(lblImagenFinal, BorderLayout.CENTER);
                        
                    } else {
                        // Fallback si la carga de la imagen falla o está incompleta
                        etiquetaPlaceholder.setText("[IMAGEN NO DISPONIBLE]");
                        etiquetaPlaceholder.setFont(new Font("Arial", Font.BOLD, 14));
                        etiquetaPlaceholder.setForeground(new Color(80, 80, 80)); 
                    }
                    
                } catch (Exception e) {
                    // Fallback si ocurre una excepción (ej. URL mal formada)
                    etiquetaPlaceholder.setText("[ERROR DE CARGA]");
                    etiquetaPlaceholder.setFont(new Font("Arial", Font.BOLD, 14));
                    etiquetaPlaceholder.setForeground(new Color(80, 80, 80)); 
                    System.err.println("Error al cargar imagen desde URL: " + imagenRuta + " - " + e.getMessage());
                } finally {
                    contenedor.revalidate();
                    contenedor.repaint();
                }
            }
        }.execute();
    }


    // 🛑 MÉTODOS DE GESTIÓN DE CARRITO 🛑
    
    private void agregarAlCarrito(Producto producto, String personalizacion) {
        int productoId = producto.getId();
        
        carrito.putIfAbsent(productoId, new ArrayList<>());
        
        carrito.get(productoId).add(personalizacion);

        int cantidadActual = carrito.get(productoId).size();
        JLabel lblContador = contadoresProductos.get(productoId);
        if (lblContador != null) {
            lblContador.setText(String.valueOf(cantidadActual));
        }
    }
    
    private void restarDelCarrito(Producto producto) {
        int productoId = producto.getId();
        
        if (carrito.containsKey(productoId)) {
            List<String> personalizaciones = carrito.get(productoId);
            
            if (!personalizaciones.isEmpty()) {
                personalizaciones.remove(personalizaciones.size() - 1);
                
                int cantidadActual = personalizaciones.size();
                JLabel lblContador = contadoresProductos.get(productoId);
                if (lblContador != null) {
                    lblContador.setText(String.valueOf(cantidadActual));
                }

                if (cantidadActual == 0) {
                    carrito.remove(productoId);
                }
            }
        }
    }

    private String generarNumeroComprobante() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "COMP-" + LocalDateTime.now().format(formatter);
    }
    
    private void continuarCompra() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío. Agrega productos para continuar.", "Carrito Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String comprobante = generarNumeroComprobante();
        StringBuilder detalleCompra = new StringBuilder();
        
        System.out.println("\n------------------------------------");
        System.out.println("PROCESANDO PEDIDO: " + comprobante);
        System.out.println("Cliente ID: " + idClienteActual);
        
        for (Map.Entry<Integer, List<String>> entry : carrito.entrySet()) {
            int productoId = entry.getKey();
            List<String> personalizaciones = entry.getValue();
            
            String nombreProductoBase = "Producto ID " + productoId; 

            for (int i = 0; i < personalizaciones.size(); i++) {
                String pers = personalizaciones.get(i);
                
                String itemNumber = personalizaciones.size() > 1 ? " #" + (i + 1) : "";
                
                String detalle = pers.isEmpty() ? " (Sin personalización)" : " | " + pers.replace(";", ", ");
                
                String lineaFinal = "  - " + nombreProductoBase + itemNumber + detalle;
                
                detalleCompra.append(lineaFinal).append("\n");
                System.out.println(lineaFinal);
            }
        }
        System.out.println("------------------------------------\n");
        
        // Muestra el detalle completo
        JOptionPane.showMessageDialog(this, 
            "Pedido de Buffet completado con éxito.\nNúmero de comprobante: " + comprobante + 
            "\n\n--- DETALLES DEL PEDIDO ---\n" + detalleCompra.toString(),
            "Compra Finalizada", 
            JOptionPane.INFORMATION_MESSAGE);
            
        dispose();
    }
}


