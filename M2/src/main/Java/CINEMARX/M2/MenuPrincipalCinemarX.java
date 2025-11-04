/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CINEMARX.M2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class MenuPrincipalCinemarX extends JFrame {
    
    // Colores del tema Cinemarx
    private static final Color COLOR_FONDO = new Color(18, 18, 18);
    private static final Color COLOR_CARD = new Color(40, 40, 40);
    private static final Color COLOR_ROJO = new Color(229, 9, 20);
    private static final Color COLOR_TEXTO = new Color(230, 230, 230);
    private static final Color COLOR_TEXTO_SECUNDARIO = new Color(160, 160, 160);
    private static final Color COLOR_BORDER = new Color(80, 80, 80);
    
    public MenuPrincipalCinemarX() {
        configurarVentana();
        crearComponentes();
    }
    
    private void configurarVentana() {
        setTitle("CinemarX - Sistema de Gestión de Cine");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
    }
    
    private void crearComponentes() {
        // Panel principal con todo centrado
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(COLOR_FONDO);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0);
        
        // Logo grande
        JPanel panelLogo = crearLogo();
        panelCentral.add(panelLogo, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 30, 0);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Sistema de Gestión de Cine");
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblSubtitulo.setForeground(COLOR_TEXTO_SECUNDARIO);
        panelCentral.add(lblSubtitulo, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        
        // Cards de opciones
        JPanel panelOpciones = crearPanelOpciones();
        panelCentral.add(panelOpciones, gbc);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Footer
        add(crearFooter(), BorderLayout.SOUTH);
    }
    
    private JPanel crearLogo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setBackground(COLOR_FONDO);
        
        JLabel lblCinemar = new JLabel("CINEMAR");
        lblCinemar.setFont(new Font("SansSerif", Font.BOLD, 64));
        lblCinemar.setForeground(COLOR_TEXTO);
        
        JLabel lblX = new JLabel("X");
        lblX.setFont(new Font("SansSerif", Font.BOLD, 64));
        lblX.setForeground(COLOR_ROJO);
        lblX.setBorder(new EmptyBorder(0, 5, 0, 0));
        
        panel.add(lblCinemar);
        panel.add(lblX);
        
        return panel;
    }
    
    private JPanel crearPanelOpciones() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 30, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setPreferredSize(new Dimension(600, 280));
        
        // Card Usuario
        JPanel cardUsuario = crearCard(
            "🎬",
            "MODO USUARIO",
            "Explorar películas en cartelera",
            "Ver horarios y funciones disponibles",
            e -> abrirVistaUsuario()
        );
        
        // Card Administrador
        JPanel cardAdmin = crearCard(
            "⚙️",
            "MODO ADMINISTRADOR",
            "Gestionar películas del sistema",
            "Crear, editar y eliminar contenido",
            e -> abrirVistaAdministrador()
        );
        
        panel.add(cardUsuario);
        panel.add(cardAdmin);
        
        return panel;
    }
    
    private JPanel crearCard(String icono, String titulo, String desc1, String desc2, ActionListener accion) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 2),
            new EmptyBorder(30, 20, 30, 20)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Contenido del card
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(COLOR_CARD);
        
        // Icono
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("SansSerif", Font.PLAIN, 72));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Título
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        // Descripción 1
        JLabel lblDesc1 = new JLabel(desc1);
        lblDesc1.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc1.setForeground(COLOR_TEXTO_SECUNDARIO);
        lblDesc1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Descripción 2
        JLabel lblDesc2 = new JLabel(desc2);
        lblDesc2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc2.setForeground(COLOR_TEXTO_SECUNDARIO);
        lblDesc2.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contenido.add(lblIcono);
        contenido.add(lblTitulo);
        contenido.add(lblDesc1);
        contenido.add(lblDesc2);
        
        card.add(contenido, BorderLayout.CENTER);
        
        // Efecto hover
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_ROJO, 2),
                    new EmptyBorder(30, 20, 30, 20)
                ));
                card.setBackground(new Color(50, 50, 50));
                contenido.setBackground(new Color(50, 50, 50));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDER, 2),
                    new EmptyBorder(30, 20, 30, 20)
                ));
                card.setBackground(COLOR_CARD);
                contenido.setBackground(COLOR_CARD);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                accion.actionPerformed(null);
            }
        });
        
        return card;
    }
    
    private JPanel crearFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(9, 9, 9));
        footer.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        JLabel lblFooter = new JLabel("CinemarX © 2025 - Módulo 2: Cartelera y Películas");
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblFooter.setForeground(COLOR_TEXTO_SECUNDARIO);
        
        footer.add(lblFooter);
        
        return footer;
    }
    
    // Métodos para abrir las ventanas
    
    private void abrirVistaUsuario() {
        try {
            // Abrir el catálogo de películas (vista usuario)
            CatalogoPeliculasFrame ventanaUsuario = new CatalogoPeliculasFrame();
            ventanaUsuario.setVisible(true);
            
            // Opcional: minimizar o cerrar el menú principal
            // this.setVisible(false);
            
        } catch (Exception e) {
            mostrarError("Error al abrir la vista de usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void abrirVistaAdministrador() {
        // Aquí podrías agregar un sistema de login antes de abrir
        // Por ahora abre directo
        
        try {
            // Abrir el panel de administración
            AdminPeliculasPanel ventanaAdmin = new AdminPeliculasPanel();
            ventanaAdmin.setVisible(true);
            
            // Opcional: minimizar o cerrar el menú principal
            // this.setVisible(false);
            
        } catch (Exception e) {
            mostrarError("Error al abrir el panel de administración: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    public static void main(String[] args) {
        // Configurar look and feel consistente
        CinemarXEstilos.configurarLookAndFeel();
        
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipalCinemarX().setVisible(true);
        });
    }
}
