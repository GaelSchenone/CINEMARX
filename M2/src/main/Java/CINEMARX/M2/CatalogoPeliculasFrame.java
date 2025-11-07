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

// Imports de Modelo y DAO
import CINEMARX.M2.Pelicula;
import CINEMARX.M2.PeliculaDAO;

// Imports para imágenes
import javax.imageio.ImageIO;
import java.net.URL;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.IOException;

// Imports para CardLayout, Sugerencias y Eventos
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
// --- IMPORTANTE: Importar tu clase de Estilos ---
import CINEMARX.M2.CinemarXEstilos;


public class CatalogoPeliculasFrame extends JFrame {
    
    // Componentes de Búsqueda
    private JTextField txtBusqueda;
    private JButton btnLimpiarBusqueda;
    private JPopupMenu sugerenciasPopup;
    private boolean actualizandoPorSugerencia = false;
    
    // Componentes de Layout
    private CardLayout cardLayout;
    private JPanel panelContenedor; // Panel principal con CardLayout
    private JPanel panelPeliculasSecciones; // Panel vertical para las filas
    private JPanel panelResultadosBusqueda; // Panel grid para resultados
    
    // Componentes para "SPOTLIGHT"
    private JPanel panelSpotlight;
    private JLabel lblSpotlightImagen; // Para el póster
    private JLabel lblSpotlightTitulo; // Para el título grande
    private JTextArea txtSpotlightSinopsis; // Para la sinopsis
    private Pelicula peliculaDestacada; // Para guardar la película

    // Conexión a la BD
    private PeliculaDAO peliculaDAO; 
    
    
    public CatalogoPeliculasFrame() {
        inicializarDatos();
        configurarVentana();
        crearComponentes(); // Esto crea la UI base
        
        // Cargamos las secciones después de que la ventana es visible
        SwingUtilities.invokeLater(() -> {
            cargarSpotlight(); // Cargar la película destacada
            cargarSecciones(); // Cargar las filas
        });
    }
    
    private void inicializarDatos() {
        this.peliculaDAO = new PeliculaDAO();
        this.sugerenciasPopup = new JPopupMenu();
        // USANDO CINEMARXESTILOS:
        this.sugerenciasPopup.setBackground(CinemarXEstilos.COLOR_CARD);
        this.sugerenciasPopup.setBorder(BorderFactory.createLineBorder(CinemarXEstilos.COLOR_BORDER));
    }
    
    private void configurarVentana() {
        setTitle("CinemarX - Catálogo de Películas");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        // USANDO CINEMARXESTILOS:
        getContentPane().setBackground(CinemarXEstilos.COLOR_FONDO);
    }
    
    private void crearComponentes() {
        add(crearHeader(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
    }

    private JPanel crearHeader() {
        // USANDO CINEMARXESTILOS:
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CinemarXEstilos.COLOR_HEADER);
        header.setBorder(new EmptyBorder(15, 30, 15, 30));
        
        JPanel panelLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelLogo.setBackground(CinemarXEstilos.COLOR_HEADER);
        
        JLabel lblCinemar = new JLabel("CINEMAR");
        lblCinemar.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblCinemar.setForeground(CinemarXEstilos.COLOR_TEXTO);
        
        JLabel lblX = new JLabel("X");
        lblX.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblX.setForeground(CinemarXEstilos.COLOR_ROJO);
        lblX.setBorder(new EmptyBorder(0, 2, 0, 0));
        
        panelLogo.add(lblCinemar);
        panelLogo.add(lblX);
        
        JPanel panelMenu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        panelMenu.setBackground(CinemarXEstilos.COLOR_HEADER);
        
        String[] opciones = {"PELÍCULAS", "BUFFET", "MEMBRESÍA"};
        for (String opcion : opciones) {
            JLabel lblMenu = new JLabel(opcion);
            lblMenu.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblMenu.setForeground(CinemarXEstilos.COLOR_TEXTO_SECUNDARIO);
            lblMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblMenu.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { lblMenu.setForeground(CinemarXEstilos.COLOR_ROJO); }
                @Override public void mouseExited(MouseEvent e) { lblMenu.setForeground(CinemarXEstilos.COLOR_TEXTO_SECUNDARIO); }
            });
            panelMenu.add(lblMenu);
        }
        
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
        // USANDO CINEMARXESTILOS:
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(CinemarXEstilos.COLOR_FONDO);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 1. Panel de Búsqueda (Arriba)
        panel.add(crearPanelBusqueda(), BorderLayout.NORTH);
        
        // 2. Panel de Contenido Principal (Centro)
        JPanel panelContenidoPrincipal = new JPanel(new BorderLayout(0, 15));
        panelContenidoPrincipal.setBackground(CinemarXEstilos.COLOR_FONDO);

        // 2a. Añadir el nuevo Panel Spotlight
        panelContenidoPrincipal.add(crearPanelSpotlight(), BorderLayout.NORTH);
        
        // 2b. Añadir el CardLayout (como estaba antes)
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        panelContenedor.setBackground(CinemarXEstilos.COLOR_FONDO);

        // Panel 1: "SECCIONES"
        panelPeliculasSecciones = new JPanel();
        panelPeliculasSecciones.setLayout(new BoxLayout(panelPeliculasSecciones, BoxLayout.Y_AXIS));
        panelPeliculasSecciones.setBackground(CinemarXEstilos.COLOR_FONDO);
        
        JScrollPane scrollSecciones = new JScrollPane(panelPeliculasSecciones);
        scrollSecciones.setBorder(null);
        scrollSecciones.getVerticalScrollBar().setUnitIncrement(16);
        scrollSecciones.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Panel 2: "BUSQUEDA"
        panelResultadosBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        panelResultadosBusqueda.setBackground(CinemarXEstilos.COLOR_FONDO);
        
        JScrollPane scrollBusqueda = new JScrollPane(panelResultadosBusqueda);
        scrollBusqueda.setBorder(null);
        scrollBusqueda.getVerticalScrollBar().setUnitIncrement(16);

        // Añadir vistas al CardLayout
        panelContenedor.add(scrollSecciones, "SECCIONES");
        panelContenedor.add(scrollBusqueda, "BUSQUEDA");
        
        // Añadir el CardLayout al Contenido Principal
        panelContenidoPrincipal.add(panelContenedor, BorderLayout.CENTER);
        
        // Añadir el Contenido Principal al panel central
        panel.add(panelContenidoPrincipal, BorderLayout.CENTER);
        
        // Mostrar "SECCIONES" por defecto
        cardLayout.show(panelContenedor, "SECCIONES");
        
        return panel;
    }

    private JPanel crearPanelBusqueda() {
        // USANDO CINEMARXESTILOS:
        JPanel panelBusqueda = new JPanel(new BorderLayout(10, 0));
        panelBusqueda.setBackground(CinemarXEstilos.COLOR_FONDO);
        
        txtBusqueda = new JTextField("🔍 Buscar película...");
        // Esto estaba bien, llama al método de tu clase
        CinemarXEstilos.aplicarEstiloTextField(txtBusqueda);
        
        txtBusqueda.setForeground(CinemarXEstilos.COLOR_TEXTO_SECUNDARIO);
        txtBusqueda.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtBusqueda.getText().equals("🔍 Buscar película...")) {
                    txtBusqueda.setText("");
                    txtBusqueda.setForeground(CinemarXEstilos.COLOR_TEXTO);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtBusqueda.getText().isEmpty()) {
                    txtBusqueda.setText("🔍 Buscar película...");
                    txtBusqueda.setForeground(CinemarXEstilos.COLOR_TEXTO_SECUNDARIO);
                }
            }
        });

        txtBusqueda.addActionListener(e -> {
            sugerenciasPopup.setVisible(false);
            ejecutarBusqueda(txtBusqueda.getText());
        });
        
        txtBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { manejarSugerencias(); }
            @Override public void removeUpdate(DocumentEvent e) { manejarSugerencias(); }
            @Override public void changedUpdate(DocumentEvent e) { manejarSugerencias(); }
        });
        
        // Botón Limpiar ("X")
        btnLimpiarBusqueda = new JButton("X");
        // USANDO CINEMARXESTILOS:
        CinemarXEstilos.aplicarEstiloBoton(btnLimpiarBusqueda, CinemarXEstilos.COLOR_ROJO);
        btnLimpiarBusqueda.setPreferredSize(new Dimension(45, 45));
        btnLimpiarBusqueda.setVisible(false); // Oculto por defecto
        btnLimpiarBusqueda.addActionListener(e -> limpiarBusqueda());
        
        panelBusqueda.add(txtBusqueda, BorderLayout.CENTER);
        panelBusqueda.add(btnLimpiarBusqueda, BorderLayout.EAST);
        
        return panelBusqueda;
    }
    
    // ==========================================
    // MÉTODOS "SPOTLIGHT" (CORREGIDOS)
    // ==========================================
    
    private JPanel crearPanelSpotlight() {
        // USANDO CINEMARXESTILOS:
        panelSpotlight = new JPanel(new BorderLayout(15, 0));
        panelSpotlight.setBackground(CinemarXEstilos.COLOR_CARD); // Fondo
        panelSpotlight.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CinemarXEstilos.COLOR_BORDER), // Borde
            new EmptyBorder(15, 15, 15, 15)
        ));
        panelSpotlight.setPreferredSize(new Dimension(0, 250)); // Altura fija
        
        // 1. Panel de Imagen (Oeste)
        lblSpotlightImagen = new JLabel();
        lblSpotlightImagen.setPreferredSize(new Dimension(150, 220));
        lblSpotlightImagen.setBackground(new Color(60, 60, 60)); // Color específico está bien
        lblSpotlightImagen.setOpaque(true);
        lblSpotlightImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblSpotlightImagen.setForeground(CinemarXEstilos.COLOR_TEXTO_SECUNDARIO); // Texto
        lblSpotlightImagen.setText("Cargando...");
        panelSpotlight.add(lblSpotlightImagen, BorderLayout.WEST);
        
        // 2. Panel de Información (Centro)
        JPanel panelInfo = new JPanel(new BorderLayout(0, 10));
        panelInfo.setOpaque(false);
        
        // 2a. Título
        lblSpotlightTitulo = new JLabel("Cargando película destacada...");
        lblSpotlightTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblSpotlightTitulo.setForeground(CinemarXEstilos.COLOR_TEXTO); // Texto
        panelInfo.add(lblSpotlightTitulo, BorderLayout.NORTH);
        
        // 2b. Sinopsis
        txtSpotlightSinopsis = new JTextArea("Cargando sinopsis...");
        txtSpotlightSinopsis.setEditable(false);
        txtSpotlightSinopsis.setLineWrap(true);
        txtSpotlightSinopsis.setWrapStyleWord(true);
        txtSpotlightSinopsis.setOpaque(false);
        txtSpotlightSinopsis.setForeground(CinemarXEstilos.COLOR_TEXTO_SECUNDARIO); // Texto
        txtSpotlightSinopsis.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSpotlightSinopsis.setFocusable(false);
        
        JScrollPane scrollSinopsis = new JScrollPane(txtSpotlightSinopsis);
        scrollSinopsis.setBorder(null);
        scrollSinopsis.setOpaque(false);
        scrollSinopsis.getViewport().setOpaque(false);
        
        panelInfo.add(scrollSinopsis, BorderLayout.CENTER);
        
        // Botón de Refrescar
        JButton btnRefresh = new JButton("🔄");
        btnRefresh.setFont(new Font("SansSerif", Font.PLAIN, 20));
        // USANDO CINEMARXESTILOS:
        CinemarXEstilos.aplicarEstiloBoton(btnRefresh, CinemarXEstilos.COLOR_BOTON);
        btnRefresh.setPreferredSize(new Dimension(50, 50));
        btnRefresh.setToolTipText("Mostrar otra película aleatoria");
        btnRefresh.addActionListener(e -> cargarSpotlight()); // Vuelve a cargar
        
        panelSpotlight.add(panelInfo, BorderLayout.CENTER);
        panelSpotlight.add(btnRefresh, BorderLayout.EAST);
        
        return panelSpotlight;
    }
    
    private void cargarSpotlight() {
        // (Este método no usa colores)
        lblSpotlightTitulo.setText("Buscando película...");
        txtSpotlightSinopsis.setText("");
        lblSpotlightImagen.setIcon(null);
        lblSpotlightImagen.setText("Cargando...");
        
        SwingWorker<Pelicula, Void> worker = new SwingWorker<Pelicula, Void>() {
            @Override
            protected Pelicula doInBackground() throws Exception {
                return peliculaDAO.obtenerPeliculaAleatoriaEnCartelera();
            }
            
            @Override
            protected void done() {
                try {
                    peliculaDestacada = get();
                    if (peliculaDestacada != null) {
                        lblSpotlightTitulo.setText(peliculaDestacada.getTitulo());
                        txtSpotlightSinopsis.setText(peliculaDestacada.getSinopsis());
                        cargarImagenSpotlight(peliculaDestacada.getImagen());
                    } else {
                        lblSpotlightTitulo.setText("No hay películas destacadas");
                        txtSpotlightSinopsis.setText("Prueba de nuevo más tarde.");
                        lblSpotlightImagen.setText("N/A");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void cargarImagenSpotlight(String urlImagen) {
        // (Este método no usa colores)
        final int POSTER_WIDTH = 150;
        final int POSTER_HEIGHT = 220;
        
        if (urlImagen != null && !urlImagen.trim().isEmpty()) {
            SwingWorker<ImageIcon, Void> workerImg = new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    URL url = new URL(urlImagen);
                    Image image = ImageIO.read(url);
                    if (image != null) {
                        Image scaledImage = image.getScaledInstance(POSTER_WIDTH, POSTER_HEIGHT, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        ImageIcon imageIcon = get();
                        if (imageIcon != null) {
                            lblSpotlightImagen.setIcon(imageIcon);
                            lblSpotlightImagen.setText(null);
                        } else {
                            lblSpotlightImagen.setText("Sin Imagen");
                        }
                    } catch (Exception e) {
                        lblSpotlightImagen.setText("Error Img");
                    }
                }
            };
            workerImg.execute();
        } else {
            lblSpotlightImagen.setIcon(null);
            lblSpotlightImagen.setText("Sin Imagen");
        }
    }

    
    // ==========================================
    // LÓGICA DE BÚSQUEDA
    // ==========================================
     
    private void manejarSugerencias() {
        if (actualizandoPorSugerencia) return;
        String termino = txtBusqueda.getText();
        if (termino.isEmpty() || termino.equals("🔍 Buscar película...") || termino.length() < 2) {
            sugerenciasPopup.setVisible(false);
            return;
        }
        
        SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                return peliculaDAO.obtenerTitulosQueCoinciden(termino, 5);
            }
            @Override
            protected void done() {
                try {
                    List<String> titulos = get();
                    sugerenciasPopup.removeAll();
                    if (titulos.isEmpty()) {
                        sugerenciasPopup.setVisible(false);
                        return;
                    }
                    for (String titulo : titulos) {
                        JMenuItem item = new JMenuItem(titulo);
                        // USANDO CINEMARXESTILOS:
                        item.setBackground(CinemarXEstilos.COLOR_CARD);
                        item.setForeground(CinemarXEstilos.COLOR_TEXTO);
                        item.setFont(new Font("SansSerif", Font.PLAIN, 14));
                        item.setBorder(new EmptyBorder(8, 10, 8, 10));
                        item.addActionListener(e -> {
                            actualizandoPorSugerencia = true;
                            txtBusqueda.setText(titulo);
                            sugerenciasPopup.setVisible(false);
                            ejecutarBusqueda(titulo);
                            actualizandoPorSugerencia = false;
                        });
                        sugerenciasPopup.add(item);
                    }
                    sugerenciasPopup.show(txtBusqueda, 0, txtBusqueda.getHeight());
                    txtBusqueda.requestFocusInWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void ejecutarBusqueda(String termino) {
        if (termino.isEmpty() || termino.equals("🔍 Buscar película...")) {
            limpiarBusqueda();
            return;
        }
        List<Pelicula> resultados = peliculaDAO.buscarPorTitulo(termino);
        panelResultadosBusqueda.removeAll();
        if (resultados.isEmpty()) {
            JLabel noResultados = new JLabel("No se encontraron resultados para '" + termino + "'");
            noResultados.setFont(new Font("SansSerif", Font.BOLD, 18));
            // USANDO CINEMARXESTILOS:
            noResultados.setForeground(CinemarXEstilos.COLOR_TEXTO_SECUNDARIO);
            panelResultadosBusqueda.add(noResultados);
        } else {
            for (Pelicula peli : resultados) {
                panelResultadosBusqueda.add(crearCardPelicula(peli));
            }
        }
        panelResultadosBusqueda.revalidate();
        panelResultadosBusqueda.repaint();
        btnLimpiarBusqueda.setVisible(true);
        cardLayout.show(panelContenedor, "BUSQUEDA");
    }
    
    private void limpiarBusqueda() {
        txtBusqueda.setText("🔍 Buscar película...");
        // USANDO CINEMARXESTILOS:
        txtBusqueda.setForeground(CinemarXEstilos.COLOR_TEXTO_SECUNDARIO);
        btnLimpiarBusqueda.setVisible(false);
        sugerenciasPopup.setVisible(false);
        cardLayout.show(panelContenedor, "SECCIONES");
    }

    
    // ==========================================
    // LÓGICA DE SECCIONES
    // ==========================================
    
    private void cargarSecciones() {
        // (Sin cambios, ya estaba bien)
        panelPeliculasSecciones.removeAll();
        panelPeliculasSecciones.add(Box.createRigidArea(new Dimension(0, 10)));
        int LIMITE_POR_FILA = 10;
        
        List<Pelicula> masTaquilleras = peliculaDAO.obtenerPeliculasMasTaquilleras(LIMITE_POR_FILA);
        if (!masTaquilleras.isEmpty()) {
            panelPeliculasSecciones.add(crearFilaSeccion("🏆 Películas más taquilleras", masTaquilleras));
            panelPeliculasSecciones.add(Box.createRigidArea(new Dimension(0, 30)));
        }

        List<Pelicula> enCartelera = peliculaDAO.obtenerPeliculasConFiltro(null, "En Cartelera", LIMITE_POR_FILA);
        if (!enCartelera.isEmpty()) {
            panelPeliculasSecciones.add(crearFilaSeccion("🎬 En Cartelera", enCartelera));
            panelPeliculasSecciones.add(Box.createRigidArea(new Dimension(0, 30)));
        }
        
        List<Pelicula> proximamente = peliculaDAO.obtenerPeliculasConFiltro(null, "Próximamente", LIMITE_POR_FILA);
        if (!proximamente.isEmpty()) {
            panelPeliculasSecciones.add(crearFilaSeccion("🍿 Próximamente", proximamente));
            panelPeliculasSecciones.add(Box.createRigidArea(new Dimension(0, 30)));
        }
        
        List<Pelicula> accion = peliculaDAO.obtenerPeliculasConFiltro("Acción", "En Cartelera", LIMITE_POR_FILA);
        if (!accion.isEmpty()) {
            panelPeliculasSecciones.add(crearFilaSeccion("💥 Acción", accion));
            panelPeliculasSecciones.add(Box.createRigidArea(new Dimension(0, 30)));
        }
        
        List<Pelicula> terror = peliculaDAO.obtenerPeliculasConFiltro("Terror", "En Cartelera", LIMITE_POR_FILA);
        if (!terror.isEmpty()) {
            panelPeliculasSecciones.add(crearFilaSeccion("👻 Terror", terror));
            panelPeliculasSecciones.add(Box.createRigidArea(new Dimension(0, 30)));
        }
        
        List<Pelicula> comedia = peliculaDAO.obtenerPeliculasConFiltro("Comedia", "En Cartelera", LIMITE_POR_FILA);
        if (!comedia.isEmpty()) {
            panelPeliculasSecciones.add(crearFilaSeccion("😂 Comedia", comedia));
            panelPeliculasSecciones.add(Box.createRigidArea(new Dimension(0, 30)));
        }
        
        panelPeliculasSecciones.revalidate();
        panelPeliculasSecciones.repaint();
    }
    
    private JPanel crearFilaSeccion(String titulo, List<Pelicula> peliculas) {
        // USANDO CINEMARXESTILOS:
        JPanel panelFila = new JPanel(new BorderLayout(0, 10));
        panelFila.setBackground(CinemarXEstilos.COLOR_FONDO);
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitulo.setForeground(CinemarXEstilos.COLOR_TEXTO);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 5, 0));
        panelFila.add(lblTitulo, BorderLayout.NORTH);
        
        JPanel panelTarjetas = new JPanel();
        panelTarjetas.setLayout(new BoxLayout(panelTarjetas, BoxLayout.X_AXIS));
        panelTarjetas.setBackground(CinemarXEstilos.COLOR_FONDO);
        
        for (Pelicula peli : peliculas) {
            panelTarjetas.add(crearCardPelicula(peli));
            panelTarjetas.add(Box.createRigidArea(new Dimension(15, 0)));
        }
        
        JScrollPane scrollTarjetas = new JScrollPane(panelTarjetas);
        scrollTarjetas.setBorder(null);
        scrollTarjetas.setBackground(CinemarXEstilos.COLOR_FONDO);
        scrollTarjetas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollTarjetas.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollTarjetas.getHorizontalScrollBar().setUnitIncrement(16);
        
        scrollTarjetas.setMinimumSize(new Dimension(500, 370));
        scrollTarjetas.setPreferredSize(new Dimension(1100, 370));
        scrollTarjetas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 370));
        
        panelFila.add(scrollTarjetas, BorderLayout.CENTER);
        
        return panelFila;
    }
    
    
    private JPanel crearCardPelicula(Pelicula peli) {
        // USANDO CINEMARXESTILOS:
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CinemarXEstilos.COLOR_CARD);
        card.setBorder(BorderFactory.createLineBorder(CinemarXEstilos.COLOR_BORDER));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        card.setPreferredSize(new Dimension(200, 350));
        card.setMinimumSize(new Dimension(200, 350));
        card.setMaximumSize(new Dimension(200, 350));
        
        final int POSTER_WIDTH = 200;
        final int POSTER_HEIGHT = 280;
        
        JLabel lblPoster = new JLabel();
        lblPoster.setPreferredSize(new Dimension(POSTER_WIDTH, POSTER_HEIGHT));
        lblPoster.setBackground(new Color(60, 60, 60)); // Color específico está bien
        lblPoster.setOpaque(true);
        lblPoster.setHorizontalAlignment(SwingConstants.CENTER);
        lblPoster.setForeground(CinemarXEstilos.COLOR_TEXTO_SECUNDARIO);
        lblPoster.setText("Cargando...");
        
        String urlImagen = peli.getImagen();
        String tituloPeli = peli.getTitulo();

        if (urlImagen != null && !urlImagen.trim().isEmpty()) {
            SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    try {
                        URL url = new URL(urlImagen);
                        Image image = ImageIO.read(url);
                        if (image != null) {
                            Image scaledImage = image.getScaledInstance(POSTER_WIDTH, POSTER_HEIGHT, Image.SCALE_SMOOTH);
                            return new ImageIcon(scaledImage);
                        }
                    } catch (IOException e) {
                        System.err.println("Error al cargar imagen: " + urlImagen + " | " + e.getMessage());
                    }
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        ImageIcon imageIcon = get();
                        if (imageIcon != null) {
                            lblPoster.setIcon(imageIcon);
                            lblPoster.setText(null);
                        } else {
                            lblPoster.setText(String.valueOf(tituloPeli.charAt(0)));
                            lblPoster.setFont(new Font("SansSerif", Font.BOLD, 72));
                        }
                    } catch (Exception e) {
                        lblPoster.setText(String.valueOf(tituloPeli.charAt(0)));
                        lblPoster.setFont(new Font("SansSerif", Font.BOLD, 72));
                    }
                }
            };
            worker.execute();
        } else {
            lblPoster.setText(String.valueOf(tituloPeli.charAt(0)));
            lblPoster.setFont(new Font("SansSerif", Font.BOLD, 72));
        }
        
        JLabel badge = new JLabel(peli.estaEnCartelera() ? "EN CARTELERA" : "PRÓXIMAMENTE");
        badge.setFont(new Font("SansSerif", Font.BOLD, 9));
        badge.setForeground(Color.WHITE); // Color específico está bien
        badge.setOpaque(true);
        // USANDO CINEMARXESTILOS:
        badge.setBackground(peli.estaEnCartelera() ? CinemarXEstilos.COLOR_ROJO : new Color(234, 179, 8)); // Color específico para "Próximamente"
        badge.setBorder(new EmptyBorder(4, 8, 4, 8));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        topPanel.add(badge);
        
        JPanel posterWrapper = new JPanel(new BorderLayout());
        posterWrapper.add(lblPoster, BorderLayout.CENTER);
        posterWrapper.add(topPanel, BorderLayout.NORTH);
        
        JPanel info = new JPanel(new BorderLayout());
        // USANDO CINEMARXESTILOS:
        info.setBackground(CinemarXEstilos.COLOR_CARD);
        info.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("<html>" + peli.getTitulo() + "</html>");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitulo.setForeground(CinemarXEstilos.COLOR_TEXTO);
        
        JLabel lblFormato = new JLabel(peli.getClasificacionEdad());
        lblFormato.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblFormato.setForeground(CinemarXEstilos.COLOR_TEXTO_SECUNDARIO);
        
        info.add(lblTitulo, BorderLayout.NORTH);
        info.add(lblFormato, BorderLayout.SOUTH);
        
        card.add(posterWrapper, BorderLayout.CENTER);
        card.add(info, BorderLayout.SOUTH);
        
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBorder(BorderFactory.createLineBorder(CinemarXEstilos.COLOR_ROJO, 2)); }
            @Override public void mouseExited(MouseEvent e) { card.setBorder(BorderFactory.createLineBorder(CinemarXEstilos.COLOR_BORDER)); }
            @Override
            public void mouseClicked(MouseEvent e) {
                // Aquí es donde tu compañero pondría su código
                JOptionPane.showMessageDialog(card, 
                    "CLIC: Abriendo módulo de detalle para:\n" + peli.getTitulo(),
                    "Acción Principal",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
        return card;
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CatalogoPeliculasFrame().setVisible(true);
        });
    }
}