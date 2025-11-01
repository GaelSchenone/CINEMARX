/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CINEMARX.M4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.nio.file.Paths;

/**
 *
 * @author gaels
 */
public class PantallaDetallePelicula extends JPanel {
    private DatabaseService dbService;
    private Pelicula pelicula;
    private List<Funcion> funciones;
    private Funcion funcionSeleccionada;
    private JPanel botonesHorarios;

    
    public PantallaDetallePelicula(DatabaseService dbService, Pelicula pelicula) {
        this.dbService = dbService;
        this.pelicula = pelicula;
        this.funciones = dbService.obtenerFuncionesPorPelicula(pelicula.getId());
        
        // Temporary code for testing seat selection
        Funcion dummyFuncion = new Funcion(1, pelicula.getId(), "2025-11-01", "14:00", "ES", "2D", 1);
        mostrarSeleccionButacas(dummyFuncion);
        // End of temporary code
        
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 45));
        
        // Header
        add(crearHeader(), BorderLayout.NORTH);
        
        // Contenido
        JScrollPane scrollPane = crearContenido();
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(45, 45, 45));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE),
            BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        
        String imagePath = java.nio.file.Paths.get(System.getProperty("user.dir"), "RECURSOS", "IMAGENES", "CINEMARX logotipo.png").toString();
        ImageIcon logoIcon = new ImageIcon(imagePath);
        JLabel logo = new JLabel(logoIcon);
        
        JPanel menu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        menu.setBackground(new Color(45, 45, 45));
        
        String[] opciones = {"PELICULAS", "BUFFET", "MEMBRESIA"};
        for (String opcion : opciones) {
            JLabel item = new JLabel(opcion);
            item.setFont(new Font("Arial", Font.PLAIN, 16));
            item.setForeground(Color.WHITE);
            menu.add(item);
        }
        
        JLabel iconoUsuario = new JLabel("👤");
        iconoUsuario.setFont(new Font("Arial", Font.PLAIN, 24));
        iconoUsuario.setForeground(Color.WHITE);
        menu.add(iconoUsuario);
        
        header.add(logo, BorderLayout.WEST);
        header.add(menu, BorderLayout.EAST);
        
        return header;
    }
    
    private JScrollPane crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(40, 0));
        contenido.setBackground(new Color(45, 45, 45));
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        
        // Panel izquierdo (imagen y detalles)
        JPanel panelIzquierdo = crearPanelIzquierdo();
        contenido.add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel derecho (funciones)
        JPanel panelDerecho = crearPanelDerecho();
        contenido.add(panelDerecho, BorderLayout.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(contenido);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        return scrollPane;
    }
    
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(new Color(45, 45, 45));
        
        // Título
        JLabel titulo = new JLabel(pelicula.getTitulo().toUpperCase());
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        
        // Imagen
        JPanel panelImagen = new JPanel();
        panelImagen.setPreferredSize(new Dimension(300, 450));
        panelImagen.setBackground(new Color(80, 80, 80));
        panelImagen.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        
        // Descripción
        JTextArea descripcion = new JTextArea(pelicula.getDescripcion());
        descripcion.setFont(new Font("Arial", Font.PLAIN, 14));
        descripcion.setForeground(new Color(200, 200, 200));
        descripcion.setBackground(new Color(45, 45, 45));
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        descripcion.setEditable(false);
        descripcion.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Información adicional
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 0, 10));
        panelInfo.setBackground(new Color(45, 45, 45));
        
        JPanel lblDuracion = crearLabelInfo("DURACIÓN", pelicula.getDuracion());
        JPanel lblEstreno = crearLabelInfo("FECHA DE ESTRENO", pelicula.getFechaEstreno());
        
        panelInfo.add(lblDuracion);
        panelInfo.add(lblEstreno);
        
        JPanel contenedor = new JPanel(new BorderLayout(0, 20));
        contenedor.setBackground(new Color(45, 45, 45));
        contenedor.add(panelImagen, BorderLayout.NORTH);
        contenedor.add(descripcion, BorderLayout.CENTER);
        contenedor.add(panelInfo, BorderLayout.SOUTH);
        
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(contenedor, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearLabelInfo(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(new Color(45, 45, 45));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.PLAIN, 14));
        lblValor.setForeground(new Color(200, 200, 200));
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel(new BorderLayout(0, 30));
        panel.setBackground(new Color(45, 45, 45));

        // Fechas
        JPanel panelFechas = new JPanel();
        panelFechas.setLayout(new BoxLayout(panelFechas, BoxLayout.Y_AXIS));
        panelFechas.setBackground(new Color(45, 45, 45));

        JLabel lblFechas = new JLabel("Fechas:");
        lblFechas.setFont(new Font("Arial", Font.BOLD, 18));
        lblFechas.setForeground(Color.WHITE);
        lblFechas.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelFechas.add(lblFechas);
        panelFechas.add(Box.createVerticalStrut(15));

        // Botones de fechas
        JPanel botonesFechas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        botonesFechas.setBackground(new Color(45, 45, 45));
        botonesFechas.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Extract unique dates
        java.util.Set<String> uniqueDates = new java.util.TreeSet<>(); // Use TreeSet to have them sorted
        for (Funcion funcion : funciones) {
            uniqueDates.add(funcion.getFecha());
        }

        // Create date buttons
        for (String fecha : uniqueDates) {
            JButton btnFecha = new JButton(fecha);
            btnFecha.setFont(new Font("Arial", Font.PLAIN, 14));
            btnFecha.setForeground(Color.BLACK);
            btnFecha.setBackground(new Color(20, 20, 20));
            btnFecha.setOpaque(true);
            btnFecha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            btnFecha.setFocusPainted(false);
            btnFecha.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnFecha.addActionListener(e -> actualizarHorarios(fecha));
            botonesFechas.add(btnFecha);
        }
        panelFechas.add(botonesFechas);

        // Idioma y Formato
        JPanel panelOpciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelOpciones.setBackground(new Color(45, 45, 45));
        panelOpciones.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cbIdioma = crearComboBox("Idioma");
        JComboBox<String> cbFormato = crearComboBox("Formato");

        panelOpciones.add(cbIdioma);
        panelOpciones.add(cbFormato);
        panelFechas.add(panelOpciones);

        // Horarios
        JPanel panelHorarios = new JPanel();
        panelHorarios.setLayout(new BoxLayout(panelHorarios, BoxLayout.Y_AXIS));
        panelHorarios.setBackground(new Color(45, 45, 45));

        JLabel lblHorarios = new JLabel("Horarios:");
        lblHorarios.setFont(new Font("Arial", Font.BOLD, 18));
        lblHorarios.setForeground(Color.WHITE);
        lblHorarios.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelHorarios.add(lblHorarios);
        panelHorarios.add(Box.createVerticalStrut(15));

        botonesHorarios = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        botonesHorarios.setBackground(new Color(45, 45, 45));
        botonesHorarios.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelHorarios.add(botonesHorarios);

        // Initial load of showtimes
        if (!uniqueDates.isEmpty()) {
            actualizarHorarios(uniqueDates.iterator().next());
        }

        // Botón comprar
        JButton btnComprar = new JButton("COMPRAR ENTRADAS");
        btnComprar.setFont(new Font("Arial", Font.BOLD, 16));
        btnComprar.setForeground(Color.BLACK);
        btnComprar.setBackground(new Color(20, 20, 20));
        btnComprar.setOpaque(true);
        btnComprar.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        btnComprar.setFocusPainted(false);
        btnComprar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComprar.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnComprar.addActionListener(e -> {
            if (funcionSeleccionada != null) {
                mostrarSeleccionButacas(funcionSeleccionada);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un horario",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        panel.add(panelFechas, BorderLayout.NORTH);
        panel.add(panelHorarios, BorderLayout.CENTER);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setBackground(new Color(45, 45, 45));
        panelBoton.add(btnComprar);
        panel.add(panelBoton, BorderLayout.SOUTH);

        return panel;
    }

    private void actualizarHorarios(String fecha) {
        botonesHorarios.removeAll();
        funcionSeleccionada = null; // Reset selection

        for (Funcion funcion : funciones) {
            if (funcion.getFecha().equals(fecha)) {
                JButton btnHorario = new JButton(funcion.getHora());
                btnHorario.setFont(new Font("Arial", Font.PLAIN, 14));
                btnHorario.setForeground(Color.BLACK);
                btnHorario.setBackground(new Color(20, 20, 20));
                btnHorario.setOpaque(true);
                btnHorario.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 1),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
                btnHorario.setFocusPainted(false);
                btnHorario.setCursor(new Cursor(Cursor.HAND_CURSOR));

                btnHorario.addActionListener(e -> {
                    funcionSeleccionada = funcion;
                    // Optionally, you can add some visual feedback for selection
                });

                botonesHorarios.add(btnHorario);
            }
        }

        botonesHorarios.revalidate();
        botonesHorarios.repaint();
    }
    
    private JComboBox<String> crearComboBox(String titulo) {
        String[] opciones = {titulo + " ▼"};
        JComboBox<String> combo = new JComboBox<>(opciones);
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setBackground(new Color(20, 20, 20));
        combo.setForeground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return combo;
    }
    
    private void mostrarSeleccionButacas(Funcion funcion) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.add(new PantallaSeleccionButacas(dbService, pelicula, funcion));
        frame.revalidate();
        frame.repaint();
    }
}