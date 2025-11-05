/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CINEMARX.M2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoPeliculasFrame extends JFrame {
    
    // Colores del tema Cinemarx
    private static final Color COLOR_FONDO = new Color(24, 24, 24);
    private static final Color COLOR_HEADER = new Color(9, 9, 9);
    private static final Color COLOR_CARD = new Color(40, 40, 40);
    private static final Color COLOR_ROJO = new Color(229, 9, 20);
    private static final Color COLOR_TEXTO = new Color(230, 230, 230);
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(160, 160, 160);
    private static final Color COLOR_INPUT = new Color(50, 50, 50);
    private static final Color COLOR_BORDER = new Color(80, 80, 80);
    
    // Componentes principales
    private JPanel panelPeliculas;
    private JTextField txtBusqueda;
    private JComboBox<String> cmbGenero, cmbClasificacion;
    private String tabActiva = "cartelera"; // cartelera, proximamente, todas
    
    // --- CAMBIO ---
    // Ya no usamos 'peliculasEjemplo' ni 'PeliculaData'
    private PeliculaDAO peliculaDAO; // Objeto para acceder a la BD
    private List<Pelicula> listaCompletaPeliculas; // Lista para guardar todas las películas de la BD
    
    public CatalogoPeliculasFrame() {
        inicializarDatos();
        configurarVentana();
        crearComponentes();
    }
    
    private void inicializarDatos() {
        // --- CAMBIO ---
        // 1. Instanciar el DAO
        this.peliculaDAO = new PeliculaDAO();
        
        // 2. Traer TODAS las películas de la BD y guardarlas en la lista
        //    Tu PeliculaDAO usa M2.java para conectarse automáticamente.
        this.listaCompletaPeliculas = peliculaDAO.obtenerTodas(); 
        
        // Imprimir en consola para verificar que funciona
        System.out.println("Catalogo: Se cargaron " + this.listaCompletaPeliculas.size() + " películas desde la BD.");
        
        // Ya no necesitamos los datos de ejemplo
    }
    
    private void configurarVentana() {
        setTitle("CinemarX - Catálogo de Películas");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
    }
    
    private void crearComponentes() {
        add(crearHeader(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
    }
    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER);
        header.setBorder(new EmptyBorder(15, 30, 15, 30));
        
        // Logo
        JPanel panelLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelLogo.setBackground(COLOR_HEADER);
        
        JLabel lblCinemar = new JLabel("CINEMAR");
        lblCinemar.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblCinemar.setForeground(COLOR_TEXTO);
        
        JLabel lblX = new JLabel("X");
        lblX.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblX.setForeground(COLOR_ROJO);
        lblX.setBorder(new EmptyBorder(0, 2, 0, 0));
        
        panelLogo.add(lblCinemar);
        panelLogo.add(lblX);
        
        // Menú derecho
        JPanel panelMenu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        panelMenu.setBackground(COLOR_HEADER);
        
        String[] opciones = {"PELÍCULAS", "BUFFET", "MEMBRESÍA"};
        for (String opcion : opciones) {
            JLabel lblMenu = new JLabel(opcion);
            lblMenu.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblMenu.setForeground(COLOR_TEXTO_SECUNDARIO);
            lblMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblMenu.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    lblMenu.setForeground(COLOR_ROJO);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    lblMenu.setForeground(COLOR_TEXTO_SECUNDARIO);
                }
            });
            panelMenu.add(lblMenu);
        }
        
        // Icono usuario
        JLabel iconoUsuario = new JLabel("👤");
        iconoUsuario.setFont(new Font("SansSerif", Font.PLAIN, 24));
        iconoUsuario.setOpaque(true);
        iconoUsuario.setBackground(Color.WHITE);
        iconoUsuario.setPreferredSize(new Dimension(40, 40));
        iconoUsuario.setHorizontalAlignment(SwingConstants.CENTER);
        panelMenu.add(iconoUsuario);
        
        header.add(panelLogo, BorderLayout.WEST);
        header.add(panelMenu, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        
        panel.add(crearPanelTabs(), BorderLayout.NORTH);
        
        // Panel con scroll para las películas
        JScrollPane scrollPane = new JScrollPane(crearPanelContenido());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelTabs() {
        JPanel panelTabs = new JPanel(new BorderLayout());
        panelTabs.setBackground(COLOR_HEADER);
        panelTabs.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabs.setBackground(COLOR_HEADER);
        tabs.setBorder(new EmptyBorder(0, 30, 0, 0));
        
        String[] nombreTabs = {"EN CARTELERA", "PRÓXIMAMENTE", "TODAS"};
        String[] valorTabs = {"cartelera", "proximamente", "todas"};
        
        for (int i = 0; i < nombreTabs.length; i++) {
            final String valor = valorTabs[i];
            JPanel tab = crearTab(nombreTabs[i], valor);
            tabs.add(tab);
        }
        
        panelTabs.add(tabs, BorderLayout.CENTER);
        return panelTabs;
    }
    
    private JPanel crearTab(String nombre, String valor) {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBackground(COLOR_HEADER);
        tab.setBorder(new EmptyBorder(15, 25, 15, 25));
        tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JLabel lblTab = new JLabel(nombre);
        lblTab.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTab.setForeground(tabActiva.equals(valor) ? COLOR_TEXTO : COLOR_TEXTO_SECUNDARIO);
        
        tab.add(lblTab, BorderLayout.CENTER);
        
        // Barra inferior roja si está activa
        if (tabActiva.equals(valor)) {
            JPanel barra = new JPanel();
            barra.setBackground(COLOR_ROJO);
            barra.setPreferredSize(new Dimension(0, 3));
            tab.add(barra, BorderLayout.SOUTH);
        }
        
        tab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabActiva = valor;
                actualizarVista();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!tabActiva.equals(valor)) {
                    lblTab.setForeground(COLOR_TEXTO);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!tabActiva.equals(valor)) {
                    lblTab.setForeground(COLOR_TEXTO_SECUNDARIO);
                }
            }
        });
        
        return tab;
    }
    
    private JPanel crearPanelContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(COLOR_FONDO);
        contenido.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        contenido.add(crearPanelFiltros(), BorderLayout.NORTH);
        
        // Panel para las películas
        panelPeliculas = new JPanel(new GridLayout(0, 4, 20, 20));
        panelPeliculas.setBackground(COLOR_FONDO);
        panelPeliculas.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        cargarPeliculas();
        
        contenido.add(panelPeliculas, BorderLayout.CENTER);
        
        return contenido;
    }
    
    private JPanel crearPanelFiltros() {
        JPanel filtros = new JPanel(new GridLayout(1, 3, 15, 0));
        filtros.setBackground(COLOR_FONDO);
        filtros.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        // Campo de búsqueda
        txtBusqueda = new JTextField();
        txtBusqueda.setBackground(COLOR_INPUT);
        txtBusqueda.setForeground(COLOR_TEXTO);
        txtBusqueda.setCaretColor(COLOR_TEXTO);
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER),
            new EmptyBorder(8, 12, 8, 12)
        ));
        txtBusqueda.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtBusqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                cargarPeliculas();
            }
        });
        
        JPanel panelBusqueda = new JPanel(new BorderLayout());
        panelBusqueda.setBackground(COLOR_FONDO);
        JLabel placeholder = new JLabel("🔍");
        placeholder.setBorder(new EmptyBorder(0, 10, 0, 0));
        panelBusqueda.add(placeholder, BorderLayout.WEST);
        panelBusqueda.add(txtBusqueda, BorderLayout.CENTER);
        
        // ComboBox Género
        String[] generos = {"Todos los géneros", "Acción", "Terror", "Drama", "Ciencia Ficción", "Animación", "Romance"};
        cmbGenero = crearComboBox(generos);
        
        // ComboBox Clasificación
        String[] clasificaciones = {"Todas las edades", "ATP", "+13", "+16", "+18"};
        cmbClasificacion = crearComboBox(clasificaciones);
        
        filtros.add(panelBusqueda);
        filtros.add(cmbGenero);
        filtros.add(cmbClasificacion);
        
        return filtros;
    }
    
    private JComboBox<String> crearComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        CinemarXEstilos.aplicarEstiloComboBox(combo);
        combo.addActionListener(e -> cargarPeliculas());
        return combo;
    }
    
    private void cargarPeliculas() {
        panelPeliculas.removeAll();
        
        String busqueda = txtBusqueda.getText().toLowerCase();
        String genero = (String) cmbGenero.getSelectedItem();
        String clasificacion = (String) cmbClasificacion.getSelectedItem();
        
        // --- CAMBIO ---
        // Ahora la lista es de tipo 'Pelicula'
        List<Pelicula> peliculasFiltradas = filtrarPeliculas(busqueda, genero, clasificacion);
        
        // --- CAMBIO ---
        // El bucle ahora usa el objeto 'Pelicula'
        for (Pelicula peli : peliculasFiltradas) {
            panelPeliculas.add(crearCardPelicula(peli));
        }
        
        panelPeliculas.revalidate();
        panelPeliculas.repaint();
    }
    
    // --- CAMBIO ---
    // El método ahora retorna una lista de 'Pelicula'
    private List<Pelicula> filtrarPeliculas(String busqueda, String genero, String clasificacion) {
        // --- CAMBIO ---
        // La lista 'filtradas' es de tipo 'Pelicula'
        List<Pelicula> filtradas = new ArrayList<>();
        
        // --- CAMBIO ---
        // Iteramos sobre la lista real 'listaCompletaPeliculas'
        for (Pelicula peli : listaCompletaPeliculas) {
            
            // --- CAMBIO ---
            // Usamos los getters del objeto 'Pelicula'
            boolean cumpleBusqueda = peli.getTitulo().toLowerCase().contains(busqueda);
            boolean cumpleGenero = genero.equals("Todos los géneros") || peli.getGenero().equals(genero);
            boolean cumpleClasificacion = clasificacion.equals("Todas las edades") || peli.getClasificacionEdad().equals(clasificacion);
            boolean cumpleTab = false;
            
            String estadoPeli = peli.getEstado(); // Usamos el getter
            
            if (tabActiva.equals("cartelera")) {
                cumpleTab = "En Cartelera".equalsIgnoreCase(estadoPeli);
            } else if (tabActiva.equals("proximamente")) {
                cumpleTab = "Próximamente".equalsIgnoreCase(estadoPeli);
            } else {
                cumpleTab = true;
            }
            
            if (cumpleBusqueda && cumpleGenero && cumpleClasificacion && cumpleTab) {
                filtradas.add(peli);
            }
        }
        
        return filtradas;
    }
    
    // --- CAMBIO ---
    // El método ahora recibe un objeto 'Pelicula'
    private JPanel crearCardPelicula(Pelicula peli) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(200, 350));
        
        // Poster (simulado con color y letra inicial)
        JPanel poster = new JPanel(new GridBagLayout());
        poster.setBackground(new Color(60, 60, 60));
        poster.setPreferredSize(new Dimension(200, 280));
        
        // --- CAMBIO --- Usamos getTitulo()
        JLabel lblInicial = new JLabel(String.valueOf(peli.getTitulo().charAt(0)));
        lblInicial.setFont(new Font("SansSerif", Font.BOLD, 72));
        lblInicial.setForeground(COLOR_TEXTO_SECUNDARIO);
        poster.add(lblInicial);
        
        // Badge de estado
        // --- CAMBIO --- Usamos los métodos helper de la clase Pelicula
        JLabel badge = new JLabel(peli.estaEnCartelera() ? "EN CARTELERA" : "PRÓXIMAMENTE");
        badge.setFont(new Font("SansSerif", Font.BOLD, 9));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(peli.estaEnCartelera() ? COLOR_ROJO : new Color(234, 179, 8));
        badge.setBorder(new EmptyBorder(4, 8, 4, 8));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        topPanel.add(badge);
        
        JPanel posterWrapper = new JPanel(new BorderLayout());
        posterWrapper.add(poster, BorderLayout.CENTER);
        posterWrapper.add(topPanel, BorderLayout.NORTH);
        
        // Info
        JPanel info = new JPanel(new BorderLayout());
        info.setBackground(COLOR_CARD);
        info.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // --- CAMBIO --- Usamos getTitulo()
        JLabel lblTitulo = new JLabel("<html>" + peli.getTitulo() + "</html>");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_TEXTO);
        
        // --- CAMBIO ---
        // El "formato" (2D/3D) no está en la tabla Pelicula,
        // así que mostramos solo la clasificación.
        JLabel lblFormato = new JLabel(peli.getClasificacionEdad());
        lblFormato.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblFormato.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        info.add(lblTitulo, BorderLayout.NORTH);
        info.add(lblFormato, BorderLayout.SOUTH);
        
        card.add(posterWrapper, BorderLayout.CENTER);
        card.add(info, BorderLayout.SOUTH);
        
        // Efecto hover
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(COLOR_ROJO, 2));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                // --- CAMBIO --- Usamos getters en el mensaje
                JOptionPane.showMessageDialog(card, 
                    "Película: " + peli.getTitulo() + "\n" +
                    "Género: " + peli.getGenero() + "\n" +
                    "Clasificación: " + peli.getClasificacionEdad(),
                    "Detalle de Película",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
        return card;
    }
    
    private void actualizarVista() {
        // Recrear todo el panel central
        getContentPane().removeAll();
        add(crearHeader(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    // --- CAMBIO ---
    // Se eliminó la clase interna 'PeliculaData' 
    // porque ahora usamos la clase 'Pelicula' real.
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CatalogoPeliculasFrame().setVisible(true);
        });
    }
}
