package CINEMARX.M1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class VentanaPrincipal extends JFrame {
    private Connection connection;
    private Logear sistemaLogin;
    private UsuarioCliente usuarioActual;

    private JPanel panelLateral;
    private JPanel panelContenido;
    private JLabel lblNombreUsuario;
    
    // Sistema de puntos
    private int puntosUsuario = 17802;
    private List<CanjeRegistro> historialCanjes = new ArrayList<>();
    

    public VentanaPrincipal(Connection connection) {
        this.connection = connection;

        setTitle("CINEMAR");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        this.sistemaLogin = new Logear(connection);

        setVisible(false);
        mostrarLogin();
    }
    
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
            this, 
            "¿Estás seguro de que deseas cerrar sesión?", 
            "Cerrar Sesión", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            usuarioActual = null;
            setVisible(false);
            mostrarLogin();
        }
    }
    private void crearInterfazPrincipal() {
        getContentPane().removeAll();

        panelLateral = new JPanel();
        panelLateral.setPreferredSize(new Dimension(280, getHeight()));
        panelLateral.setBackground(new Color(20, 20, 20));
        panelLateral.setLayout(new BorderLayout());

        JPanel panelUsuario = new JPanel();
        panelUsuario.setBackground(new Color(20, 20, 20));
        panelUsuario.setLayout(new BoxLayout(panelUsuario, BoxLayout.Y_AXIS));
        panelUsuario.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel lblIconoUsuario = new JLabel("USER");
        lblIconoUsuario.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblIconoUsuario.setForeground(new Color(220, 20, 60));
        lblIconoUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNombreUsuario = new JLabel(usuarioActual.getNombreCompleto());
        lblNombreUsuario.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNombreUsuario.setForeground(Color.WHITE);
        lblNombreUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCorreoUsuario = new JLabel(usuarioActual.getCorreo());
        lblCorreoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCorreoUsuario.setForeground(new Color(150, 150, 150));
        lblCorreoUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelUsuario.add(lblIconoUsuario);
        panelUsuario.add(Box.createVerticalStrut(15));
        panelUsuario.add(lblNombreUsuario);
        panelUsuario.add(Box.createVerticalStrut(5));
        panelUsuario.add(lblCorreoUsuario);

        JPanel panelMenu = new JPanel();
        panelMenu.setBackground(new Color(20, 20, 20));
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JLabel lblTituloCuenta = new JLabel("Cuenta");
        lblTituloCuenta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloCuenta.setForeground(new Color(150, 150, 150));
        lblTituloCuenta.setBorder(BorderFactory.createEmptyBorder(0, 25, 10, 0));

        JPanel btnPerfil = crearBotonMenu("●", "Perfil");
        
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(40, 40, 40));
        sep1.setMaximumSize(new Dimension(280, 1));

        JLabel lblTituloPagos = new JLabel("Pagos y recompensas");
        lblTituloPagos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloPagos.setForeground(new Color(150, 150, 150));
        lblTituloPagos.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 0));

        JPanel btnHistorial = crearBotonMenu("●", "Mis compras");
        JPanel btnPuntos = crearBotonMenu("●", "Canjear puntos");
        JPanel btnHistorialCanjes = crearBotonMenu("●", "Historial de canjes");
        
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(40, 40, 40));
        sep2.setMaximumSize(new Dimension(280, 1));

        JPanel btnCerrarSesion = crearBotonMenu("●", "Cerrar sesion");

        panelMenu.add(lblTituloCuenta);
        panelMenu.add(btnPerfil);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(sep1);
        panelMenu.add(lblTituloPagos);
        panelMenu.add(btnHistorial);
        panelMenu.add(Box.createVerticalStrut(10));
        panelMenu.add(btnPuntos);
        panelMenu.add(Box.createVerticalStrut(10));
        panelMenu.add(btnHistorialCanjes);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(sep2);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnCerrarSesion);
        panelMenu.add(Box.createVerticalGlue());

        panelLateral.add(panelUsuario, BorderLayout.NORTH);
        panelLateral.add(panelMenu, BorderLayout.CENTER);

        panelContenido = new JPanel();
        panelContenido.setBackground(new Color(30, 30, 30));
        panelContenido.setLayout(new BorderLayout());

        mostrarBienvenida();

        btnPerfil.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarPerfil();
            }
        });

        btnHistorial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarHistorial();
            }
        });

        btnPuntos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarCanjePuntos();
            }
        });

        btnHistorialCanjes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarHistorialCanjes();
            }
        });

        btnCerrarSesion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cerrarSesion();
            }
        });

        add(panelLateral, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel crearBotonMenu(String icono, String texto) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(20, 20, 20));
        panel.setMaximumSize(new Dimension(280, 50));
        panel.setPreferredSize(new Dimension(280, 50));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblIcono.setForeground(new Color(220, 20, 60));
        lblIcono.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));

        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTexto.setForeground(Color.WHITE);

        panel.add(lblIcono, BorderLayout.WEST);
        panel.add(lblTexto, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(35, 35, 35));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(new Color(20, 20, 20));
            }
        });

        return panel;
    }

    private void mostrarBienvenida() {
        panelContenido.removeAll();
        panelContenido.setLayout(new GridBagLayout());

        JPanel panelCentral = new JPanel();
        panelCentral.setBackground(new Color(30, 30, 30));
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));

        JLabel lblBienvenida = new JLabel("Bienvenido, " + usuarioActual.getNombreCompleto() + "!");
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMensaje = new JLabel("Selecciona una opcion del menu para comenzar");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMensaje.setForeground(new Color(150, 150, 150));
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelCentral.add(lblBienvenida);
        panelCentral.add(Box.createVerticalStrut(20));
        panelCentral.add(lblMensaje);

        panelContenido.add(panelCentral);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarCanjePuntos() {
        panelContenido.removeAll();
        panelContenido.setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBackground(new Color(30, 30, 30));

        // Panel superior con título y puntos
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(new Color(30, 30, 30));
        panelSuperior.setLayout(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(new Color(30, 30, 30));

        JButton btnVolver = new JButton("< Volver");
        btnVolver.setBackground(new Color(40, 40, 40));
        btnVolver.setForeground(new Color(180, 180, 180));
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.setMaximumSize(new Dimension(100, 35));
        btnVolver.addActionListener(e -> mostrarBienvenida());

        JLabel lblTitulo = new JLabel("Canjea tus puntos:");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelIzquierdo.add(btnVolver);
        panelIzquierdo.add(Box.createVerticalStrut(20));
        panelIzquierdo.add(lblTitulo);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelDerecho.setBackground(new Color(30, 30, 30));

        JLabel lblPuntosDisponibles = new JLabel("Tus puntos Cinemax: " + puntosUsuario);
        lblPuntosDisponibles.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPuntosDisponibles.setForeground(Color.WHITE);
        lblPuntosDisponibles.setBackground(new Color(220, 20, 60));
        lblPuntosDisponibles.setOpaque(true);
        lblPuntosDisponibles.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        panelDerecho.add(lblPuntosDisponibles);

        panelSuperior.add(panelIzquierdo, BorderLayout.WEST);
        panelSuperior.add(panelDerecho, BorderLayout.EAST);

        // Panel central con productos
        JPanel panelProductos = new JPanel();
        panelProductos.setLayout(new GridLayout(2, 2, 20, 20));
        panelProductos.setBackground(new Color(30, 30, 30));
        panelProductos.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // Productos disponibles con sus precios
        String[][] productos = {
            {"COMBO PANCHO", "500"},
            {"COMBO CHIPA", "500"},
            {"AMERICAN PSYCHO", "1500"},
            {"TAXI DRIVER", "1500"}
        };

        ButtonGroup grupo = new ButtonGroup();
        JRadioButton[] radios = new JRadioButton[4];

        for (int i = 0; i < productos.length; i++) {
            JPanel panelProducto = crearPanelProducto(productos[i][0], productos[i][1], true);
            radios[i] = (JRadioButton) panelProducto.getComponent(0);
            grupo.add(radios[i]);
            panelProductos.add(panelProducto);
        }

        // Panel inferior con botón de canje
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInferior.setBackground(new Color(30, 30, 30));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JButton btnCanjear = new JButton("CANJEAR PUNTOS");
        btnCanjear.setPreferredSize(new Dimension(250, 50));
        btnCanjear.setBackground(new Color(220, 20, 60));
        btnCanjear.setForeground(Color.WHITE);
        btnCanjear.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCanjear.setFocusPainted(false);
        btnCanjear.setBorderPainted(false);
        btnCanjear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCanjear.addActionListener(e -> {
            int seleccionado = -1;
            for (int i = 0; i < radios.length; i++) {
                if (radios[i].isSelected()) {
                    seleccionado = i;
                    break;
                }
            }

            if (seleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto para canjear.");
                return;
            }

            String productoNombre = productos[seleccionado][0];
            int puntosRequeridos = Integer.parseInt(productos[seleccionado][1]);

            if (puntosUsuario >= puntosRequeridos) {
                puntosUsuario -= puntosRequeridos;
                historialCanjes.add(new CanjeRegistro(productoNombre, puntosRequeridos));
                lblPuntosDisponibles.setText("Tus puntos Cinemax: " + puntosUsuario);
                JOptionPane.showMessageDialog(this, 
                    "¡Canje exitoso!\n" + productoNombre + " - " + puntosRequeridos + " puntos\n" +
                    "Puntos restantes: " + puntosUsuario);
                grupo.clearSelection();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No tienes suficientes puntos.\n" +
                    "Necesitas: " + puntosRequeridos + " puntos\n" +
                    "Tienes: " + puntosUsuario + " puntos");
            }
        });

        panelInferior.add(btnCanjear);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelProductos, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        panelContenido.add(panelPrincipal);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private JPanel crearPanelProducto(String nombre, String puntos, boolean mostrarPuntos) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(45, 45, 45));
        panel.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JRadioButton radio = new JRadioButton();
        radio.setBackground(new Color(45, 45, 45));
        radio.setFocusPainted(false);
        radio.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

        // Panel para la imagen (placeholder con color de fondo)
        JPanel panelImagen = new JPanel();
        panelImagen.setPreferredSize(new Dimension(200, 180));
        panelImagen.setBackground(new Color(60, 60, 60));
        panelImagen.setLayout(new GridBagLayout());
        
        JLabel lblIcono = new JLabel("🍿");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        panelImagen.add(lblIcono);

        // Panel de información
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(new Color(45, 45, 45));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelInfo.add(lblNombre);

        if (mostrarPuntos) {
            panelInfo.add(Box.createVerticalStrut(10));
            JLabel lblPuntos = new JLabel(puntos);
            lblPuntos.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblPuntos.setForeground(new Color(220, 20, 60));
            lblPuntos.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelInfo.add(lblPuntos);
        }

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(new Color(45, 45, 45));
        panelCentral.add(panelImagen, BorderLayout.CENTER);
        panelCentral.add(panelInfo, BorderLayout.SOUTH);

        panel.add(radio, BorderLayout.NORTH);
        panel.add(panelCentral, BorderLayout.CENTER);

        // Hacer que TODO el panel sea clickeable, incluyendo todos los componentes
        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                radio.setSelected(true);
            }
        };
        
        panel.addMouseListener(clickListener);
        panelImagen.addMouseListener(clickListener);
        panelInfo.addMouseListener(clickListener);
        panelCentral.addMouseListener(clickListener);

        return panel;
    }

    private void mostrarHistorialCanjes() {
        panelContenido.removeAll();
        panelContenido.setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBackground(new Color(30, 30, 30));

        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(new Color(30, 30, 30));
        panelSuperior.setLayout(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(new Color(30, 30, 30));

        JButton btnVolver = new JButton("< Volver");
        btnVolver.setBackground(new Color(40, 40, 40));
        btnVolver.setForeground(new Color(180, 180, 180));
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.setMaximumSize(new Dimension(100, 35));
        btnVolver.addActionListener(e -> mostrarBienvenida());

        JLabel lblTitulo = new JLabel("Historial de canjes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Productos canjeados con tus puntos Cinemax.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(180, 180, 180));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelIzquierdo.add(btnVolver);
        panelIzquierdo.add(Box.createVerticalStrut(20));
        panelIzquierdo.add(lblTitulo);
        panelIzquierdo.add(Box.createVerticalStrut(5));
        panelIzquierdo.add(lblSubtitulo);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        panelDerecho.setBackground(new Color(30, 30, 30));

        // Calcular total de puntos gastados
        int totalGastado = 0;
        for (CanjeRegistro canje : historialCanjes) {
            totalGastado += canje.getPuntos();
        }

        JLabel lblPuntosGastados = new JLabel("Gastados: " + totalGastado);
        lblPuntosGastados.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPuntosGastados.setForeground(new Color(255, 140, 0));
        panelDerecho.add(lblPuntosGastados);

        JLabel lblPuntosActuales = new JLabel("Actuales: " + puntosUsuario);
        lblPuntosActuales.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPuntosActuales.setForeground(Color.WHITE);
        lblPuntosActuales.setBackground(new Color(220, 20, 60));
        lblPuntosActuales.setOpaque(true);
        lblPuntosActuales.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        panelDerecho.add(lblPuntosActuales);

        panelSuperior.add(panelIzquierdo, BorderLayout.WEST);
        panelSuperior.add(panelDerecho, BorderLayout.EAST);

        JPanel panelTabla = new JPanel();
        panelTabla.setBackground(new Color(40, 40, 40));
        panelTabla.setLayout(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        if (historialCanjes.isEmpty()) {
            JLabel lblSinCanjes = new JLabel("No has canjeado ningun producto aun.");
            lblSinCanjes.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lblSinCanjes.setForeground(new Color(150, 150, 150));
            lblSinCanjes.setHorizontalAlignment(SwingConstants.CENTER);
            panelTabla.add(lblSinCanjes, BorderLayout.CENTER);
        } else {
            JPanel panelEncabezados = new JPanel(new GridLayout(1, 3));
            panelEncabezados.setBackground(new Color(40, 40, 40));
            panelEncabezados.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

            JLabel lblFecha = new JLabel("Fecha");
            lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblFecha.setForeground(new Color(200, 200, 200));

            JLabel lblProducto = new JLabel("Producto");
            lblProducto.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblProducto.setForeground(new Color(200, 200, 200));

            JLabel lblPuntos = new JLabel("Puntos", SwingConstants.RIGHT);
            lblPuntos.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblPuntos.setForeground(new Color(200, 200, 200));

            panelEncabezados.add(lblFecha);
            panelEncabezados.add(lblProducto);
            panelEncabezados.add(lblPuntos);

            JPanel panelCanjes = new JPanel();
            panelCanjes.setLayout(new BoxLayout(panelCanjes, BoxLayout.Y_AXIS));
            panelCanjes.setBackground(new Color(40, 40, 40));

            for (int i = 0; i < historialCanjes.size(); i++) {
                CanjeRegistro canje = historialCanjes.get(i);
                
                JPanel panelFila = new JPanel(new GridLayout(1, 3));
                panelFila.setBackground(new Color(40, 40, 40));
                panelFila.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
                panelFila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                JLabel fecha = new JLabel(canje.getFecha());
                fecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                fecha.setForeground(Color.WHITE);

                JLabel producto = new JLabel(canje.getProducto());
                producto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                producto.setForeground(Color.WHITE);

                JLabel puntos = new JLabel(String.valueOf(canje.getPuntos()), SwingConstants.RIGHT);
                puntos.setFont(new Font("Segoe UI", Font.BOLD, 13));
                puntos.setForeground(new Color(220, 20, 60));

                panelFila.add(fecha);
                panelFila.add(producto);
                panelFila.add(puntos);

                panelCanjes.add(panelFila);

                if (i < historialCanjes.size() - 1) {
                    JSeparator separador = new JSeparator();
                    separador.setForeground(new Color(60, 60, 60));
                    separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    panelCanjes.add(separador);
                }
            }

            JScrollPane scrollCanjes = new JScrollPane(panelCanjes);
            scrollCanjes.setBorder(null);
            scrollCanjes.setBackground(new Color(40, 40, 40));
            scrollCanjes.getViewport().setBackground(new Color(40, 40, 40));

            panelTabla.add(panelEncabezados, BorderLayout.NORTH);
            panelTabla.add(scrollCanjes, BorderLayout.CENTER);
        }

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);

        panelContenido.add(panelPrincipal, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

// REEMPLAZAR el método mostrarPerfil() en VentanaPrincipal.java

    private void mostrarPerfil() {
        panelContenido.removeAll();
        panelContenido.setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(new Color(30, 30, 30));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JButton btnVolver = new JButton("< Volver");
        btnVolver.setBackground(new Color(40, 40, 40));
        btnVolver.setForeground(new Color(180, 180, 180));
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.setMaximumSize(new Dimension(100, 35));
        btnVolver.addActionListener(e -> mostrarBienvenida());

        JLabel lblTitulo = new JLabel("Ajustes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Gestiona los datos de tu cuenta.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(180, 180, 180));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSeccion1 = new JLabel("Informacion personal");
        lblSeccion1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeccion1.setForeground(Color.WHITE);
        lblSeccion1.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSeccion1.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCampos.setBackground(new Color(30, 30, 30));
        panelCampos.setAlignmentX(Component.LEFT_ALIGNMENT);

        // NOMBRE
        JLabel lblNombreLabel = new JLabel("Nombre");
        lblNombreLabel.setForeground(new Color(180, 180, 180));
        lblNombreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNombreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtNombre = new JTextField(usuarioActual.getNombre());
        txtNombre.setMaximumSize(new Dimension(500, 45));
        txtNombre.setBackground(new Color(50, 50, 50));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setCaretColor(Color.WHITE);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtNombre.setEditable(false);

        panelCampos.add(lblNombreLabel);
        panelCampos.add(Box.createVerticalStrut(5));
        panelCampos.add(txtNombre);
        panelCampos.add(Box.createVerticalStrut(15));

        // APELLIDO
        JLabel lblApellidoLabel = new JLabel("Apellido");
        lblApellidoLabel.setForeground(new Color(180, 180, 180));
        lblApellidoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblApellidoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String apellidoActual = usuarioActual.getApellido() != null ? usuarioActual.getApellido() : "";
        JTextField txtApellido = new JTextField(apellidoActual);
        txtApellido.setMaximumSize(new Dimension(500, 45));
        txtApellido.setBackground(new Color(50, 50, 50));
        txtApellido.setForeground(Color.WHITE);
        txtApellido.setCaretColor(Color.WHITE);
        txtApellido.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtApellido.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtApellido.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtApellido.setEditable(false);

        panelCampos.add(lblApellidoLabel);
        panelCampos.add(Box.createVerticalStrut(5));
        panelCampos.add(txtApellido);
        panelCampos.add(Box.createVerticalStrut(15));

        // DNI
        JLabel lblDNILabel = new JLabel("DNI");
        lblDNILabel.setForeground(new Color(180, 180, 180));
        lblDNILabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDNILabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtDNI = new JTextField(String.valueOf(usuarioActual.getDNI()));
        txtDNI.setMaximumSize(new Dimension(500, 45));
        txtDNI.setBackground(new Color(50, 50, 50));
        txtDNI.setForeground(Color.WHITE);
        txtDNI.setCaretColor(Color.WHITE);
        txtDNI.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDNI.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtDNI.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDNI.setEditable(false);

        panelCampos.add(lblDNILabel);
        panelCampos.add(Box.createVerticalStrut(5));
        panelCampos.add(txtDNI);
        panelCampos.add(Box.createVerticalStrut(15));

        // FECHA DE NACIMIENTO
        JLabel lblFechaNacLabel = new JLabel("Fecha de Nacimiento");
        lblFechaNacLabel.setForeground(new Color(180, 180, 180));
        lblFechaNacLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFechaNacLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        java.sql.Date fechaNac = usuarioActual.getFechaNacimiento();
        String fechaNacStr = "";
        if (fechaNac != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(fechaNac);
            fechaNacStr = String.format("%02d/%02d/%04d", 
                cal.get(java.util.Calendar.DAY_OF_MONTH),
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.YEAR));
        }

        JTextField txtFechaNac = new JTextField(fechaNacStr);
        txtFechaNac.setMaximumSize(new Dimension(500, 45));
        txtFechaNac.setBackground(new Color(50, 50, 50));
        txtFechaNac.setForeground(Color.WHITE);
        txtFechaNac.setCaretColor(Color.WHITE);
        txtFechaNac.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtFechaNac.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtFechaNac.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtFechaNac.setEditable(false);

        panelCampos.add(lblFechaNacLabel);
        panelCampos.add(Box.createVerticalStrut(5));
        panelCampos.add(txtFechaNac);
        panelCampos.add(Box.createVerticalStrut(15));

        JLabel lblSeccion2 = new JLabel("Informacion de la cuenta");
        lblSeccion2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeccion2.setForeground(Color.WHITE);
        lblSeccion2.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSeccion2.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        panelCampos.add(lblSeccion2);

        // CORREO
        JLabel lblCorreoLabel = new JLabel("Direccion de correo electronico");
        lblCorreoLabel.setForeground(new Color(180, 180, 180));
        lblCorreoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCorreoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtCorreo = new JTextField(usuarioActual.getCorreo());
        txtCorreo.setMaximumSize(new Dimension(500, 45));
        txtCorreo.setBackground(new Color(50, 50, 50));
        txtCorreo.setForeground(Color.WHITE);
        txtCorreo.setCaretColor(Color.WHITE);
        txtCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCorreo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtCorreo.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtCorreo.setEditable(false);

        panelCampos.add(lblCorreoLabel);
        panelCampos.add(Box.createVerticalStrut(5));
        panelCampos.add(txtCorreo);
        panelCampos.add(Box.createVerticalStrut(15));

        // CONTRASEÑA
        JLabel lblPassInfoLabel = new JLabel("Contrasena");
        lblPassInfoLabel.setForeground(new Color(180, 180, 180));
        lblPassInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPassInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPassInfo = new JPasswordField(usuarioActual.getContrasena());
        txtPassInfo.setMaximumSize(new Dimension(500, 45));
        txtPassInfo.setBackground(new Color(50, 50, 50));
        txtPassInfo.setForeground(Color.WHITE);
        txtPassInfo.setCaretColor(Color.WHITE);
        txtPassInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtPassInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassInfo.setEditable(false);

        panelCampos.add(lblPassInfoLabel);
        panelCampos.add(Box.createVerticalStrut(5));
        panelCampos.add(txtPassInfo);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        panelBotones.setBackground(new Color(30, 30, 30));
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnEditar = new JButton("Editar Datos");
        btnEditar.setPreferredSize(new Dimension(150, 40));
        btnEditar.setBackground(new Color(220, 20, 60));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEditar.setFocusPainted(false);
        btnEditar.setBorderPainted(false);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setPreferredSize(new Dimension(150, 40));
        btnGuardar.setBackground(new Color(220, 20, 60));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setVisible(false);

        panelBotones.add(btnEditar);
        panelBotones.add(btnGuardar);

        btnEditar.addActionListener(e -> {
            txtNombre.setEditable(true);
            txtApellido.setEditable(true);
            txtDNI.setEditable(true);
            txtFechaNac.setEditable(true);
            txtCorreo.setEditable(true);
            txtPassInfo.setEditable(true);
            txtPassInfo.setEchoChar((char) 0);
            btnEditar.setVisible(false);
            btnGuardar.setVisible(true);
        });

        btnGuardar.addActionListener(e -> {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevoApellido = txtApellido.getText().trim();
            String nuevoDNIStr = txtDNI.getText().trim();
            String nuevaFechaStr = txtFechaNac.getText().trim();
            String nuevoCorreo = txtCorreo.getText().trim();
            String nuevaPass = new String(txtPassInfo.getPassword()).trim();

            // Validar campos
            if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty() || nuevoDNIStr.isEmpty() || 
                nuevaFechaStr.isEmpty() || nuevoCorreo.isEmpty() || nuevaPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }

            // Validar DNI
            int nuevoDNI;
            try {
                nuevoDNI = Integer.parseInt(nuevoDNIStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El DNI debe ser un numero valido.");
                return;
            }

            // Validar y parsear fecha (DD/MM/AAAA)
            java.sql.Date nuevaFechaNac;
            try {
                String[] partes = nuevaFechaStr.split("/");
                if (partes.length != 3) {
                    throw new IllegalArgumentException();
                }
                int dia = Integer.parseInt(partes[0]);
                int mes = Integer.parseInt(partes[1]);
                int anio = Integer.parseInt(partes[2]);

                String fechaSQL = String.format("%04d-%02d-%02d", anio, mes, dia);
                nuevaFechaNac = java.sql.Date.valueOf(fechaSQL);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "La fecha debe tener el formato DD/MM/AAAA.");
                return;
            }

            DatabaseHelper dbHelper = sistemaLogin.getDbHelper();

            if (dbHelper.actualizarDatosCliente(usuarioActual.getCorreo(), nuevoNombre, nuevoApellido,
                                                nuevoCorreo, nuevaPass, nuevoDNI, nuevaFechaNac)) {
                usuarioActual.setNombre(nuevoNombre);
                usuarioActual.setApellido(nuevoApellido);
                usuarioActual.setCorreo(nuevoCorreo);
                usuarioActual.setContrasena(nuevaPass);
                usuarioActual.setDNI(nuevoDNI);
                usuarioActual.setFechaNacimiento(nuevaFechaNac);

                lblNombreUsuario.setText(usuarioActual.getNombreCompleto());

                JOptionPane.showMessageDialog(this, "Datos actualizados correctamente en la base de datos.");
                mostrarPerfil();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar los datos. El DNI podria estar en uso.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelPrincipal.add(btnVolver);
        panelPrincipal.add(Box.createVerticalStrut(20));
        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(Box.createVerticalStrut(5));
        panelPrincipal.add(lblSubtitulo);
        panelPrincipal.add(lblSeccion1);
        panelPrincipal.add(Box.createVerticalStrut(10));
        panelPrincipal.add(panelCampos);
        panelPrincipal.add(panelBotones);

        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panelContenido.add(scrollPane, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarHistorial() {
        panelContenido.removeAll();
        panelContenido.setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBackground(new Color(30, 30, 30));

        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(new Color(30, 30, 30));
        panelSuperior.setLayout(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(new Color(30, 30, 30));

        JButton btnVolver = new JButton("< Volver");
        btnVolver.setBackground(new Color(40, 40, 40));
        btnVolver.setForeground(new Color(180, 180, 180));
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.setMaximumSize(new Dimension(100, 35));
        btnVolver.addActionListener(e -> mostrarBienvenida());

        JLabel lblTitulo = new JLabel("Mis compras");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Consulta el historial de compras realizadas en tu cuenta.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(180, 180, 180));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelIzquierdo.add(btnVolver);
        panelIzquierdo.add(Box.createVerticalStrut(20));
        panelIzquierdo.add(lblTitulo);
        panelIzquierdo.add(Box.createVerticalStrut(5));
        panelIzquierdo.add(lblSubtitulo);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelDerecho.setBackground(new Color(30, 30, 30));

        JLabel lblPuntos = new JLabel("Puntos: " + puntosUsuario);
        lblPuntos.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPuntos.setForeground(new Color(220, 20, 60));
        panelDerecho.add(lblPuntos);

        panelSuperior.add(panelIzquierdo, BorderLayout.CENTER);
        panelSuperior.add(panelDerecho, BorderLayout.EAST);

        JPanel panelTabla = new JPanel();
        panelTabla.setBackground(new Color(40, 40, 40));
        panelTabla.setLayout(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel panelEncabezados = new JPanel(new GridLayout(1, 3));
        panelEncabezados.setBackground(new Color(40, 40, 40));
        panelEncabezados.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lblFecha = new JLabel("Fecha");
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFecha.setForeground(new Color(200, 200, 200));

        JLabel lblDescripcion = new JLabel("Descripcion");
        lblDescripcion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDescripcion.setForeground(new Color(200, 200, 200));

        JLabel lblPrecio = new JLabel("Precio", SwingConstants.RIGHT);
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPrecio.setForeground(new Color(200, 200, 200));

        panelEncabezados.add(lblFecha);
        panelEncabezados.add(lblDescripcion);
        panelEncabezados.add(lblPrecio);

        JPanel panelCompras = new JPanel();
        panelCompras.setLayout(new BoxLayout(panelCompras, BoxLayout.Y_AXIS));
        panelCompras.setBackground(new Color(40, 40, 40));

        String[][] compras = {
            {"27/10/25", "1x Entrada American Psycho / 2D SUB", "$15000"},
            {"15/10/25", "2x Entrada Spider-Man / IMAX 3D", "$18000"},
            {"10/10/25", "1x Combo Grande (Pochoclo + Bebida)", "$6500"},
            {"10/10/25", "1x Chipa", "$2000"},
            {"05/10/25", "1x Entrada Avatar 2 / Sala Premium", "$12000"},
            {"01/10/25", "1x Nachos con queso", "$4500"}
        };

        for (String[] compra : compras) {
            JPanel panelFila = new JPanel(new GridLayout(1, 3));
            panelFila.setBackground(new Color(40, 40, 40));
            panelFila.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
            panelFila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JLabel fecha = new JLabel(compra[0]);
            fecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fecha.setForeground(Color.WHITE);

            JLabel descripcion = new JLabel(compra[1]);
            descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            descripcion.setForeground(Color.WHITE);

            JLabel precio = new JLabel(compra[2], SwingConstants.RIGHT);
            precio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            precio.setForeground(Color.WHITE);

            panelFila.add(fecha);
            panelFila.add(descripcion);
            panelFila.add(precio);

            panelCompras.add(panelFila);

            if (!compra[0].equals("01/10/25")) {
                JSeparator separador = new JSeparator();
                separador.setForeground(new Color(60, 60, 60));
                separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                panelCompras.add(separador);
            }
        }

        JScrollPane scrollCompras = new JScrollPane(panelCompras);
        scrollCompras.setBorder(null);
        scrollCompras.setBackground(new Color(40, 40, 40));
        scrollCompras.getViewport().setBackground(new Color(40, 40, 40));

        panelTabla.add(panelEncabezados, BorderLayout.NORTH);
        panelTabla.add(scrollCompras, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setBackground(new Color(40, 40, 40));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        JLabel lblTotal = new JLabel("TOTAL: $58000");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(Color.WHITE);
        panelInferior.add(lblTotal);

        panelTabla.add(panelInferior, BorderLayout.SOUTH);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);

        panelContenido.add(panelPrincipal, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarLogin() {
        mostrarPantallaLogin();
    }

    private void mostrarPantallaLogin() {
        JDialog dialog = new JDialog((Frame)null, "Iniciar Sesion", true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.getContentPane().setBackground(new Color(50, 50, 50));
        dialog.setLayout(new BorderLayout());

        JPanel panelContenedor = new JPanel(new CardLayout());
        panelContenedor.setBackground(new Color(50, 50, 50));

        JPanel panelLogin = crearPanelLogin(dialog, panelContenedor);
        JPanel panelRegistro = crearPanelRegistro(dialog, panelContenedor);

        panelContenedor.add(panelLogin, "LOGIN");
        panelContenedor.add(panelRegistro, "REGISTRO");

        dialog.add(panelContenedor);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        dialog.setVisible(true);
    }

    private JPanel crearPanelLogin(JDialog dialog, JPanel panelContenedor) {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(new Color(50, 50, 50));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JLabel lblLogo = new JLabel("CINEMAR", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelNegro = new JPanel();
        panelNegro.setBackground(new Color(20, 20, 20));
        panelNegro.setLayout(null);
        panelNegro.setPreferredSize(new Dimension(500, 400));
        panelNegro.setMaximumSize(new Dimension(500, 400));

        JLabel lblTituloLogin = new JLabel("Hola! Que bueno verte por aca.", SwingConstants.CENTER);
        lblTituloLogin.setForeground(Color.WHITE);
        lblTituloLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloLogin.setBounds(0, 40, 500, 30);
        panelNegro.add(lblTituloLogin);

        JLabel lblCorreoLabelLogin = new JLabel("Correo electronico");
        lblCorreoLabelLogin.setForeground(new Color(180, 180, 180));
        lblCorreoLabelLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCorreoLabelLogin.setBounds(100, 100, 300, 15);
        panelNegro.add(lblCorreoLabelLogin);

        JTextField txtCorreoLogin = new JTextField();
        txtCorreoLogin.setBounds(100, 115, 300, 45);
        txtCorreoLogin.setBackground(new Color(35, 35, 35));
        txtCorreoLogin.setForeground(Color.WHITE);
        txtCorreoLogin.setCaretColor(Color.WHITE);
        txtCorreoLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCorreoLogin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        panelNegro.add(txtCorreoLogin);

        JLabel lblPassLabelLogin = new JLabel("Contrasena");
        lblPassLabelLogin.setForeground(new Color(180, 180, 180));
        lblPassLabelLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPassLabelLogin.setBounds(100, 175, 300, 15);
        panelNegro.add(lblPassLabelLogin);

        JPasswordField txtPassLogin = new JPasswordField();
        txtPassLogin.setBounds(100, 190, 300, 45);
        txtPassLogin.setBackground(new Color(35, 35, 35));
        txtPassLogin.setForeground(Color.WHITE);
        txtPassLogin.setCaretColor(Color.WHITE);
        txtPassLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassLogin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        panelNegro.add(txtPassLogin);

        JButton btnIniciar = new JButton("Iniciar Sesion");
        btnIniciar.setBounds(100, 260, 300, 45);
        btnIniciar.setBackground(new Color(220, 20, 60));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIniciar.setFocusPainted(false);
        btnIniciar.setBorderPainted(false);
        btnIniciar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelNegro.add(btnIniciar);

        JButton btnRegistrarse = new JButton("Registrarse");
        btnRegistrarse.setBounds(100, 320, 300, 45);
        btnRegistrarse.setBackground(new Color(60, 60, 60));
        btnRegistrarse.setForeground(Color.WHITE);
        btnRegistrarse.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrarse.setFocusPainted(false);
        btnRegistrarse.setBorderPainted(false);
        btnRegistrarse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelNegro.add(btnRegistrarse);

        panelPrincipal.add(lblLogo);
        panelPrincipal.add(Box.createVerticalStrut(20));
        panelPrincipal.add(panelNegro);

        btnRegistrarse.addActionListener(e -> {
            CardLayout cl = (CardLayout) panelContenedor.getLayout();
            cl.show(panelContenedor, "REGISTRO");
        });

        btnIniciar.addActionListener(e -> {
            String correo = txtCorreoLogin.getText().trim();
            String pass = new String(txtPassLogin.getPassword()).trim();

            if (correo.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete ambos campos.");
                return;
            }

            if (sistemaLogin.iniciarSesion(correo, pass)) {
                usuarioActual = sistemaLogin.getUsuarioActual();
                dialog.dispose();
                crearInterfazPrincipal();
                setVisible(true);
            } else {
                JOptionPane.showMessageDialog(dialog, "Correo o contraseña incorrectos.");
            }
        });

        return panelPrincipal;
    }

    private JPanel crearPanelRegistro(JDialog dialog, JPanel panelContenedor) {
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(new Color(50, 50, 50));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel lblLogo = new JLabel("CINEMAR", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel scrolleable para contener todos los campos
        JPanel panelContenido = new JPanel();
        panelContenido.setBackground(new Color(20, 20, 20));
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel lblTitulo = new JLabel("Registrate:", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Completa todos los campos para crear tu cuenta");
        lblSubtitulo.setForeground(new Color(180, 180, 180));
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelContenido.add(lblTitulo);
        panelContenido.add(Box.createVerticalStrut(5));
        panelContenido.add(lblSubtitulo);
        panelContenido.add(Box.createVerticalStrut(25));

        // NOMBRE
        JLabel lblNombreLabel = new JLabel("Nombre *");
        lblNombreLabel.setForeground(new Color(180, 180, 180));
        lblNombreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNombreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtNombre = new JTextField();
        txtNombre.setMaximumSize(new Dimension(400, 45));
        txtNombre.setBackground(new Color(35, 35, 35));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setCaretColor(Color.WHITE);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelContenido.add(lblNombreLabel);
        panelContenido.add(Box.createVerticalStrut(5));
        panelContenido.add(txtNombre);
        panelContenido.add(Box.createVerticalStrut(15));

        // APELLIDO
        JLabel lblApellidoLabel = new JLabel("Apellido *");
        lblApellidoLabel.setForeground(new Color(180, 180, 180));
        lblApellidoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblApellidoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtApellido = new JTextField();
        txtApellido.setMaximumSize(new Dimension(400, 45));
        txtApellido.setBackground(new Color(35, 35, 35));
        txtApellido.setForeground(Color.WHITE);
        txtApellido.setCaretColor(Color.WHITE);
        txtApellido.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtApellido.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtApellido.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelContenido.add(lblApellidoLabel);
        panelContenido.add(Box.createVerticalStrut(5));
        panelContenido.add(txtApellido);
        panelContenido.add(Box.createVerticalStrut(15));

        // DNI
        JLabel lblDNILabel = new JLabel("DNI (solo numeros) *");
        lblDNILabel.setForeground(new Color(180, 180, 180));
        lblDNILabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDNILabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtDNI = new JTextField();
        txtDNI.setMaximumSize(new Dimension(400, 45));
        txtDNI.setBackground(new Color(35, 35, 35));
        txtDNI.setForeground(Color.WHITE);
        txtDNI.setCaretColor(Color.WHITE);
        txtDNI.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDNI.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtDNI.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelContenido.add(lblDNILabel);
        panelContenido.add(Box.createVerticalStrut(5));
        panelContenido.add(txtDNI);
        panelContenido.add(Box.createVerticalStrut(15));


        // FECHA DE NACIMIENTO
        JLabel lblFechaLabel = new JLabel("Fecha de Nacimiento (DD/MM/AAAA) *");
        lblFechaLabel.setForeground(new Color(180, 180, 180));
        lblFechaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFechaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel contenedor para los campos de fecha
        JPanel panelFechaContainer = new JPanel();
        panelFechaContainer.setLayout(new BoxLayout(panelFechaContainer, BoxLayout.X_AXIS));
        panelFechaContainer.setBackground(new Color(20, 20, 20));
        panelFechaContainer.setMaximumSize(new Dimension(400, 45));
        panelFechaContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Campo DÍA
        JTextField txtDia = new JTextField();
        txtDia.setPreferredSize(new Dimension(70, 45));
        txtDia.setMaximumSize(new Dimension(70, 45));
        txtDia.setBackground(new Color(35, 35, 35));
        txtDia.setForeground(Color.WHITE);
        txtDia.setCaretColor(Color.WHITE);
        txtDia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDia.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtDia.setHorizontalAlignment(JTextField.CENTER);

        // Separador 1
        JLabel lblBarra1 = new JLabel(" / ");
        lblBarra1.setForeground(Color.WHITE);
        lblBarra1.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Campo MES
        JTextField txtMes = new JTextField();
        txtMes.setPreferredSize(new Dimension(70, 45));
        txtMes.setMaximumSize(new Dimension(70, 45));
        txtMes.setBackground(new Color(35, 35, 35));
        txtMes.setForeground(Color.WHITE);
        txtMes.setCaretColor(Color.WHITE);
        txtMes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtMes.setHorizontalAlignment(JTextField.CENTER);

        // Separador 2
        JLabel lblBarra2 = new JLabel(" / ");
        lblBarra2.setForeground(Color.WHITE);
        lblBarra2.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Campo AÑO
        JTextField txtAnio = new JTextField();
        txtAnio.setPreferredSize(new Dimension(100, 45));
        txtAnio.setMaximumSize(new Dimension(100, 45));
        txtAnio.setBackground(new Color(35, 35, 35));
        txtAnio.setForeground(Color.WHITE);
        txtAnio.setCaretColor(Color.WHITE);
        txtAnio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtAnio.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtAnio.setHorizontalAlignment(JTextField.CENTER);

        // Agregar todos los componentes al panel de fecha
        panelFechaContainer.add(txtDia);
        panelFechaContainer.add(lblBarra1);
        panelFechaContainer.add(txtMes);
        panelFechaContainer.add(lblBarra2);
        panelFechaContainer.add(txtAnio);
        panelFechaContainer.add(Box.createHorizontalGlue()); // Para empujar todo a la izquierda

        // Agregar al panel de contenido
        panelContenido.add(lblFechaLabel);
        panelContenido.add(Box.createVerticalStrut(5));
        panelContenido.add(panelFechaContainer);
        panelContenido.add(Box.createVerticalStrut(15));

        // CORREO ELECTRÓNICO
        JLabel lblCorreoLabel = new JLabel("Correo electronico (@gmail.com) *");
        lblCorreoLabel.setForeground(new Color(180, 180, 180));
        lblCorreoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCorreoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtCorreo = new JTextField();
        txtCorreo.setMaximumSize(new Dimension(400, 45));
        txtCorreo.setBackground(new Color(35, 35, 35));
        txtCorreo.setForeground(Color.WHITE);
        txtCorreo.setCaretColor(Color.WHITE);
        txtCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCorreo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtCorreo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelContenido.add(lblCorreoLabel);
        panelContenido.add(Box.createVerticalStrut(5));
        panelContenido.add(txtCorreo);
        panelContenido.add(Box.createVerticalStrut(15));

        // CONTRASEÑA
        JLabel lblPassLabel = new JLabel("Contrasena *");
        lblPassLabel.setForeground(new Color(180, 180, 180));
        lblPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPassLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setMaximumSize(new Dimension(400, 45));
        txtPass.setBackground(new Color(35, 35, 35));
        txtPass.setForeground(Color.WHITE);
        txtPass.setCaretColor(Color.WHITE);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelContenido.add(lblPassLabel);
        panelContenido.add(Box.createVerticalStrut(5));
        panelContenido.add(txtPass);
        panelContenido.add(Box.createVerticalStrut(15));

        // CONFIRMAR CONTRASEÑA
        JLabel lblConfirmarLabel = new JLabel("Confirmar Contrasena *");
        lblConfirmarLabel.setForeground(new Color(180, 180, 180));
        lblConfirmarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblConfirmarLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtConfirmar = new JPasswordField();
        txtConfirmar.setMaximumSize(new Dimension(400, 45));
        txtConfirmar.setBackground(new Color(35, 35, 35));
        txtConfirmar.setForeground(Color.WHITE);
        txtConfirmar.setCaretColor(Color.WHITE);
        txtConfirmar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConfirmar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtConfirmar.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelContenido.add(lblConfirmarLabel);
        panelContenido.add(Box.createVerticalStrut(5));
        panelContenido.add(txtConfirmar);
        panelContenido.add(Box.createVerticalStrut(25));

        // BOTONES
        JButton btnCrear = new JButton("Crear cuenta");
        btnCrear.setMaximumSize(new Dimension(400, 50));
        btnCrear.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCrear.setBackground(new Color(220, 20, 60));
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCrear.setFocusPainted(false);
        btnCrear.setBorderPainted(false);
        btnCrear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnVolverReg = new JButton("Volver");
        btnVolverReg.setMaximumSize(new Dimension(400, 50));
        btnVolverReg.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolverReg.setBackground(new Color(60, 60, 60));
        btnVolverReg.setForeground(Color.WHITE);
        btnVolverReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolverReg.setFocusPainted(false);
        btnVolverReg.setBorderPainted(false);
        btnVolverReg.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelContenido.add(btnCrear);
        panelContenido.add(Box.createVerticalStrut(10));
        panelContenido.add(btnVolverReg);

        // Scroll pane para el contenido
        JScrollPane scrollPane = new JScrollPane(panelContenido);
        scrollPane.setPreferredSize(new Dimension(500, 600));
        scrollPane.setMaximumSize(new Dimension(500, 600));
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(20, 20, 20));
        scrollPane.getViewport().setBackground(new Color(20, 20, 20));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panelPrincipal.add(lblLogo);
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(scrollPane);

        // ACTION LISTENERS
        btnVolverReg.addActionListener(e -> {
            CardLayout cl = (CardLayout) panelContenedor.getLayout();
            cl.show(panelContenedor, "LOGIN");
        });

        btnCrear.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String dniStr = txtDNI.getText().trim();
            String dia = txtDia.getText().trim();
            String mes = txtMes.getText().trim();
            String anio = txtAnio.getText().trim();
            String correo = txtCorreo.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            String confirmar = new String(txtConfirmar.getPassword()).trim();

            // VALIDACIONES
            if (nombre.isEmpty() || apellido.isEmpty() || dniStr.isEmpty() || 
                dia.isEmpty() || mes.isEmpty() || anio.isEmpty() || 
                correo.isEmpty() || pass.isEmpty() || confirmar.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos obligatorios (*).");
                return;
            }

            // Validar DNI
            int DNI;
            try {
                DNI = Integer.parseInt(dniStr);
                if (DNI <= 0) {
                    JOptionPane.showMessageDialog(dialog, "El DNI debe ser un numero positivo.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El DNI debe contener solo numeros.");
                return;
            }

            // Validar fecha
            int diaInt, mesInt, anioInt;
            try {
                diaInt = Integer.parseInt(dia);
                mesInt = Integer.parseInt(mes);
                anioInt = Integer.parseInt(anio);

                if (diaInt < 1 || diaInt > 31) {
                    JOptionPane.showMessageDialog(dialog, "El dia debe estar entre 1 y 31.");
                    return;
                }
                if (mesInt < 1 || mesInt > 12) {
                    JOptionPane.showMessageDialog(dialog, "El mes debe estar entre 1 y 12.");
                    return;
                }
                if (anioInt < 1900 || anioInt > 2024) {
                    JOptionPane.showMessageDialog(dialog, "El ano debe estar entre 1900 y 2024.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "La fecha debe contener solo numeros.");
                return;
            }

            // Crear fecha SQL
            java.sql.Date fechaNacimiento;
            try {
                // Formato: YYYY-MM-DD
                String fechaStr = String.format("%04d-%02d-%02d", anioInt, mesInt, diaInt);
                fechaNacimiento = java.sql.Date.valueOf(fechaStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "La fecha ingresada no es valida.");
                return;
            }

            // Validar contraseñas
            if (!pass.equals(confirmar)) {
                JOptionPane.showMessageDialog(dialog, "Las contrasenas no coinciden.");
                return;
            }

            // Validar correo
            if (!correo.toLowerCase().endsWith("@gmail.com")) {
                JOptionPane.showMessageDialog(dialog, "El correo debe terminar en @gmail.com");
                return;
            }

            // Intentar registrar
            if (sistemaLogin.registrarUsuario(nombre, apellido, correo, pass, DNI, fechaNacimiento)) {
                JOptionPane.showMessageDialog(dialog, "Cuenta creada correctamente!");
                CardLayout cl = (CardLayout) panelContenedor.getLayout();
                cl.show(panelContenedor, "LOGIN");

                // Limpiar campos
                txtNombre.setText("");
                txtApellido.setText("");
                txtDNI.setText("");
                txtDia.setText("");
                txtMes.setText("");
                txtAnio.setText("");
                txtCorreo.setText("");
                txtPass.setText("");
                txtConfirmar.setText("");
            } else {
                JOptionPane.showMessageDialog(dialog, "Error: Este correo o DNI ya esta registrado.");
            }
        });

        return panelPrincipal;
    }

    // Clase interna para registrar los canjes
    private class CanjeRegistro {
        private String producto;
        private int puntos;
        private String fecha;

        public CanjeRegistro(String producto, int puntos) {
            this.producto = producto;
            this.puntos = puntos;
            // Formato de fecha simple (dd/MM/yy)
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yy");
            this.fecha = sdf.format(new java.util.Date());
        }

        public String getProducto() {
            return producto;
        }

        public int getPuntos() {
            return puntos;
        }

        public String getFecha() {
            return fecha;
        }
    }


}