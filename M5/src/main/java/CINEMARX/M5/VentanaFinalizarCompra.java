package CINEMARX.M5;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Ventana para finalizar la compra y seleccionar método de pago
 * Versión mejorada con ComboBox de empresas de tarjetas y sistema de puntos VIP
 * SIN envío de emails
 */
public class VentanaFinalizarCompra extends JFrame {
    
    // Colores de CinemarX
    private static final Color COLOR_FONDO = new Color(45, 45, 45);
    private static final Color COLOR_FONDO_CLARO = new Color(55, 55, 55);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_BOTON = new Color(239, 68, 68);
    private static final Color COLOR_BORDE = new Color(70, 70, 70);
    
    // Componentes
    private JCheckBox checkNuevaTarjeta;
    private JPanel panelFormularioNuevaTarjeta;
    private JLabel lblResumenCompra;
    private JLabel lblTotal;
    private JButton btnRealizarPedido;
    private MetodoPago metodoSeleccionado;
    private JPanel panelContainer;
    private JLabel lblMetodoSeleccionado;
    private JPopupMenu popupMetodos;
    
    // Campos del formulario
    private JTextField txtNumeroTarjeta;
    private JTextField txtNombreTarjeta;
    private JComboBox<String> comboEmpresaTarjeta;
    private JTextField txtCaducidad;
    private JTextField txtCVV;
    private JCheckBox checkGuardarSi;
    private JComboBox<String> comboTipoTarjeta;
    
    // Datos
    private Cliente clienteActual;
    private ResumenCompra compraActual;
    
    /**
     * Constructor - Inicializa la ventana
     */
    public VentanaFinalizarCompra() {
        if (!inicializarDatos()) {
            mostrarErrorInicializacion();
            return;
        }
        inicializarComponentes();
    }
    
    /**
     * Muestra un diálogo de error cuando falla la inicialización
     */
    private void mostrarErrorInicializacion() {
        JOptionPane.showMessageDialog(this,
            "No se pudo inicializar la aplicación.\n\n" +
            "Verifique que:\n" +
            "1. El servidor de base de datos esté activo\n" +
            "2. La base de datos 'Cinemarx' exista\n" +
            "3. Haya datos de prueba cargados\n" +
            "4. Las credenciales de conexión sean correctas",
            "Error de Inicialización",
            JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Inicializa los datos del cliente y compra
     * @return false si hay algún error crítico
     */
    private boolean inicializarDatos() {
        try {
            System.out.println("📋 Inicializando datos de la aplicación...");
            
            Connection conn = ConexionBD.obtenerConexion();
            if (conn == null) {
                System.err.println("❌ ERROR: No se pudo conectar a la base de datos");
                return false;
            }
            
            if (!verificarExistenClientes(conn)) {
                System.err.println("❌ ERROR: No hay clientes en la base de datos");
                JOptionPane.showMessageDialog(this,
                    "No hay clientes en la base de datos.\n" +
                    "Por favor ejecute el script de datos de prueba.",
                    "Base de datos vacía",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            if (!verificarExistenFunciones(conn)) {
                System.err.println("❌ ERROR: No hay funciones disponibles en la base de datos");
                JOptionPane.showMessageDialog(this,
                    "No hay funciones disponibles en la base de datos.\n" +
                    "Por favor ejecute el script de datos de prueba.",
                    "Base de datos vacía",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            clienteActual = Cliente.obtenerClienteAleatorio();
            
            if (clienteActual == null) {
                System.err.println("❌ ERROR: No se pudo obtener un cliente");
                return false;
            }
            
            clienteActual.cargarMetodosPago();
            
            System.out.println("\n✓ Cliente cargado: " + clienteActual.getNombre() + " " + 
                             clienteActual.getApellido());
            System.out.println("✓ ID Cliente: " + clienteActual.getIdCliente());
            System.out.println("✓ Métodos de pago: " + clienteActual.getMetodosPago().size());
            
            compraActual = new ResumenCompra("Entrada American Psycho / 2D SUB", 1, 15000);
            System.out.println("✓ Compra creada: " + compraActual.getDescripcion() + 
                             " - $" + compraActual.getPrecio() + "\n");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR en inicializarDatos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Verifica si existen clientes en la base de datos
     */
    private boolean verificarExistenClientes(Connection conn) {
        String query = "SELECT COUNT(*) as total FROM Cliente";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("   Clientes encontrados: " + total);
                return total > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar clientes: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Verifica si existen funciones disponibles en la base de datos
     */
    private boolean verificarExistenFunciones(Connection conn) {
        String query = "SELECT COUNT(*) as total FROM Funcion WHERE Estado = 'Disponible'";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("   Funciones disponibles: " + total);
                return total > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar funciones: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Inicializa todos los componentes de la interfaz
     */
    private void inicializarComponentes() {
        setTitle("Finalizar Compra - CinemarX");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());
        
        add(crearPanelHeader(), BorderLayout.NORTH);
        add(crearPanelContenido(), BorderLayout.CENTER);
    }
    
    /**
     * Crea el panel del header con el logo y menú
     */
    private JPanel crearPanelHeader() {
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(COLOR_FONDO);
        panelHeader.setBorder(new EmptyBorder(20, 50, 20, 50));
        panelHeader.setPreferredSize(new Dimension(0, 100));

        JButton btnLogo = new JButton();
        ImageIcon logoIcon = ImageLoader.cargarImagenDesdeURL(ImageLoader.URL_LOGO, 250, 45);

        if (logoIcon != null) { 
            btnLogo.setIcon(logoIcon);
        } else {
            btnLogo.setText("CINEMARX");
            btnLogo.setFont(new Font("Arial", Font.BOLD, 32));
            btnLogo.setForeground(COLOR_TEXTO);
        }

        btnLogo.setBackground(COLOR_FONDO);
        btnLogo.setBorderPainted(false);
        btnLogo.setFocusPainted(false);
        btnLogo.setContentAreaFilled(false);
        btnLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogo.addActionListener(e -> {
            System.out.println("Logo clickeado - Ir a página principal");
        });

        JPanel panelLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelLogo.setBackground(COLOR_FONDO);
        panelLogo.add(btnLogo);

        JPanel panelMenu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 35, 0));
        panelMenu.setBackground(COLOR_FONDO);

        String[] opciones = {"PELICULAS", "BUFFET", "MEMBRESIA"};
        for (String opcion : opciones) {
            JButton btnOpcion = new JButton(opcion);
            btnOpcion.setFont(new Font("Arial", Font.PLAIN, 15));
            btnOpcion.setForeground(COLOR_TEXTO);

            if (opcion.equals("PELICULAS")) {
                btnOpcion.setFont(new Font("Arial", Font.BOLD, 15));
            }

            btnOpcion.setBackground(COLOR_FONDO);
            btnOpcion.setBorderPainted(false);
            btnOpcion.setFocusPainted(false);
            btnOpcion.setContentAreaFilled(false);
            btnOpcion.setCursor(new Cursor(Cursor.HAND_CURSOR));

            final String opcionFinal = opcion;
            btnOpcion.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btnOpcion.setText("<html><u>" + opcionFinal + "</u></html>");
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btnOpcion.setText(opcionFinal);
                }
            });

            btnOpcion.addActionListener(e -> {
                System.out.println("Sección clickeada: " + opcionFinal);
            });

            panelMenu.add(btnOpcion);
        }

        JButton btnUsuario = new JButton();
        ImageIcon userIcon = ImageLoader.cargarImagenDesdeURL(ImageLoader.URL_USER, 32, 32);

        if (userIcon != null) {
            btnUsuario.setIcon(userIcon);
        } else {
            btnUsuario.setText("👤");
            btnUsuario.setFont(new Font("Arial", Font.PLAIN, 26));
        }

        btnUsuario.setBackground(COLOR_FONDO);
        btnUsuario.setBorderPainted(false);
        btnUsuario.setFocusPainted(false);
        btnUsuario.setContentAreaFilled(false);
        btnUsuario.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnUsuario.setBorder(BorderFactory.createLineBorder(COLOR_TEXTO, 2));
                btnUsuario.setBorderPainted(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnUsuario.setBorderPainted(false);
            }
        });

        btnUsuario.addActionListener(e -> {
            System.out.println("Perfil de usuario: " + clienteActual.getNombre());
        });

        panelMenu.add(btnUsuario);

        panelHeader.add(panelLogo, BorderLayout.WEST);
        panelHeader.add(panelMenu, BorderLayout.EAST);

        return panelHeader;
    }
    
    /**
     * Crea el panel de contenido principal
     */
    private JPanel crearPanelContenido() {
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setBackground(COLOR_FONDO);
        panelContenido.setBorder(new EmptyBorder(5, 50, 40, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.50;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JPanel panelMetodos = crearPanelMetodosPago();
        panelMetodos.setPreferredSize(new Dimension(480, 600));
        panelMetodos.setMaximumSize(new Dimension(480, 600));
        panelContenido.add(panelMetodos, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panelContenido.add(Box.createHorizontalStrut(0), gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0.42;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        JPanel panelResumen = crearPanelResumen();
        panelResumen.setPreferredSize(new Dimension(500, 345));
        panelContenido.add(panelResumen, gbc);
        
        return panelContenido;
    }
    
    /**
     * Crea el panel de métodos de pago
     */
    private JPanel crearPanelMetodosPago() {
        panelContainer = new JPanel(new BorderLayout());
        panelContainer.setBackground(COLOR_FONDO);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(0, 0, 0, 35));
        
        JLabel lblTitulo = new JLabel("Finalizar Compra");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel lblTusMetodos = new JLabel("Tus métodos de pago");
        lblTusMetodos.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTusMetodos.setForeground(COLOR_TEXTO);
        lblTusMetodos.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTusMetodos);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        JPanel panelListaDesplegable = crearPanelListaDesplegable();
        panel.add(panelListaDesplegable);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel lblOtrosMetodos = new JLabel("Otros métodos de pago");
        lblOtrosMetodos.setFont(new Font("Arial", Font.PLAIN, 14));
        lblOtrosMetodos.setForeground(COLOR_TEXTO);
        lblOtrosMetodos.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblOtrosMetodos);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        JPanel panelNuevaTarjeta = crearPanelNuevaTarjeta();
        panel.add(panelNuevaTarjeta);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        panelFormularioNuevaTarjeta = crearPanelFormularioTarjeta();
        panelFormularioNuevaTarjeta.setVisible(false);
        panelFormularioNuevaTarjeta.setMaximumSize(new Dimension(400, 0));
        panelFormularioNuevaTarjeta.setPreferredSize(new Dimension(400, 0));
        panelFormularioNuevaTarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(panelFormularioNuevaTarjeta);
        
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(COLOR_FONDO);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(515, 600));
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(10, 0));
        verticalScrollBar.setBackground(COLOR_FONDO);
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = COLOR_BORDE;
                this.trackColor = COLOR_FONDO;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        
        panelContainer.add(scrollPane, BorderLayout.CENTER);
        
        return panelContainer;
    }
    
    /**
     * Crea el panel de lista desplegable de métodos de pago
     */
    private JPanel crearPanelListaDesplegable() {
        JPanel panelListaDesplegable = new JPanel(new BorderLayout());
        panelListaDesplegable.setMaximumSize(new Dimension(400, 43));
        panelListaDesplegable.setPreferredSize(new Dimension(400, 43));
        panelListaDesplegable.setBackground(COLOR_FONDO_CLARO);
        panelListaDesplegable.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        panelListaDesplegable.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblMetodoSeleccionado = new JLabel("   Seleccionar método de pago");
        lblMetodoSeleccionado.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMetodoSeleccionado.setForeground(new Color(150, 150, 150));
        lblMetodoSeleccionado.setOpaque(true);
        lblMetodoSeleccionado.setBackground(COLOR_FONDO_CLARO);
        
        JLabel lblFlecha = new JLabel("▼  ");
        lblFlecha.setFont(new Font("Arial", Font.PLAIN, 10));
        lblFlecha.setForeground(COLOR_TEXTO);
        lblFlecha.setOpaque(true);
        lblFlecha.setBackground(COLOR_FONDO_CLARO);
        
        panelListaDesplegable.add(lblMetodoSeleccionado, BorderLayout.CENTER);
        panelListaDesplegable.add(lblFlecha, BorderLayout.EAST);
        
        popupMetodos = new JPopupMenu();
        popupMetodos.setBackground(COLOR_FONDO_CLARO);
        popupMetodos.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        
        cargarMetodosPagoEnPopup();
        
        panelListaDesplegable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                popupMetodos.show(panelListaDesplegable, 0, panelListaDesplegable.getHeight());
            }
        });
        
        panelListaDesplegable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return panelListaDesplegable;
    }
    
    /**
     * Carga los métodos de pago en el popup menu
     */
    private void cargarMetodosPagoEnPopup() {
        popupMetodos.removeAll();
        
        ArrayList<MetodoPago> metodosPago = clienteActual.getMetodosPago();
        
        if (metodosPago.isEmpty()) {
            JMenuItem item = new JMenuItem("   No hay métodos de pago guardados");
            item.setFont(new Font("Arial", Font.ITALIC, 12));
            item.setForeground(new Color(150, 150, 150));
            item.setBackground(COLOR_FONDO_CLARO);
            item.setEnabled(false);
            popupMetodos.add(item);
        } else {
            for (MetodoPago metodo : metodosPago) {
                JMenuItem item = new JMenuItem("   " + metodo.toString());
                item.setFont(new Font("Arial", Font.PLAIN, 12));
                item.setForeground(COLOR_TEXTO);
                item.setBackground(COLOR_FONDO_CLARO);
                item.setOpaque(true);
                item.setBorder(new EmptyBorder(10, 10, 10, 10));
                
                item.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        item.setBackground(COLOR_FONDO_CLARO.brighter());
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        item.setBackground(COLOR_FONDO_CLARO);
                    }
                });
                
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        lblMetodoSeleccionado.setText("   " + metodo.toString());
                        lblMetodoSeleccionado.setForeground(COLOR_TEXTO);
                        metodoSeleccionado = metodo;
                        
                        checkNuevaTarjeta.setSelected(false);
                        panelFormularioNuevaTarjeta.setVisible(false);
                        panelFormularioNuevaTarjeta.setMaximumSize(new Dimension(400, 0));
                        panelFormularioNuevaTarjeta.setPreferredSize(new Dimension(400, 0));
                        limpiarFormulario();
                        panelContainer.revalidate();
                        panelContainer.repaint();
                    }
                });
                
                popupMetodos.add(item);
            }
        }
    }
    
    /**
     * Crea el panel de nueva tarjeta
     */
    private JPanel crearPanelNuevaTarjeta() {
        JPanel panelNuevaTarjeta = new JPanel(new FlowLayout(FlowLayout.LEFT, 13, 13));
        panelNuevaTarjeta.setBackground(COLOR_FONDO_CLARO);
        panelNuevaTarjeta.setMaximumSize(new Dimension(400, 51));
        panelNuevaTarjeta.setPreferredSize(new Dimension(400, 51));
        panelNuevaTarjeta.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1, true));
        panelNuevaTarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        checkNuevaTarjeta = new JCheckBox();
        checkNuevaTarjeta.setBackground(COLOR_FONDO_CLARO);
        checkNuevaTarjeta.setForeground(COLOR_TEXTO);
        checkNuevaTarjeta.setFocusPainted(false);

        JLabel lblIconoTarjeta = new JLabel();
        ImageIcon cardIcon = ImageLoader.cargarImagenDesdeURL(ImageLoader.URL_CARD, 24, 24);

        if (cardIcon != null) {
            lblIconoTarjeta.setIcon(cardIcon);
        } else {
            lblIconoTarjeta.setText("💳");
            lblIconoTarjeta.setFont(new Font("Arial", Font.PLAIN, 20));
        }

        JLabel lblNuevaTarjeta = new JLabel("Tarjeta de crédito o débito");
        lblNuevaTarjeta.setFont(new Font("Arial", Font.PLAIN, 12));
        lblNuevaTarjeta.setForeground(COLOR_TEXTO);

        panelNuevaTarjeta.add(checkNuevaTarjeta);
        panelNuevaTarjeta.add(lblIconoTarjeta);
        panelNuevaTarjeta.add(lblNuevaTarjeta);
        
        checkNuevaTarjeta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean mostrar = checkNuevaTarjeta.isSelected();
                
                if (mostrar) {
                    lblMetodoSeleccionado.setText("   Seleccionar método de pago");
                    lblMetodoSeleccionado.setForeground(new Color(150, 150, 150));
                    metodoSeleccionado = null;
                    
                    panelFormularioNuevaTarjeta.setVisible(true);
                    panelFormularioNuevaTarjeta.setMaximumSize(new Dimension(400, 480));
                    panelFormularioNuevaTarjeta.setPreferredSize(new Dimension(400, 480));
                } else {
                    panelFormularioNuevaTarjeta.setVisible(false);
                    panelFormularioNuevaTarjeta.setMaximumSize(new Dimension(400, 0));
                    panelFormularioNuevaTarjeta.setPreferredSize(new Dimension(400, 0));
                    limpiarFormulario();
                }
                
                panelContainer.revalidate();
                panelContainer.repaint();
            }
        });
        
        return panelNuevaTarjeta;
    }
    
    /**
     * Crea el panel del formulario de nueva tarjeta
     */
    private JPanel crearPanelFormularioTarjeta() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO_CLARO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
            new EmptyBorder(17, 17, 17, 17)
        ));
        panel.setMaximumSize(new Dimension(400, 480));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        agregarCampoFormulario(panel, "Número de la tarjeta*", 
            txtNumeroTarjeta = crearCampoTexto());
        
        agregarCampoFormulario(panel, "Nombre en la tarjeta*", 
            txtNombreTarjeta = crearCampoTexto());
        
        JLabel lblEmpresa = new JLabel("Empresa de tarjeta*");
        lblEmpresa.setFont(new Font("Arial", Font.PLAIN, 10));
        lblEmpresa.setForeground(new Color(180, 180, 180));
        lblEmpresa.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblEmpresa);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        
        String[] empresas = {"Mastercard", "Visa", "Patagonia365", "American Express"};
        comboEmpresaTarjeta = new JComboBox<>(empresas);
        comboEmpresaTarjeta.setFont(new Font("Arial", Font.PLAIN, 13));
        comboEmpresaTarjeta.setBackground(Color.WHITE);
        comboEmpresaTarjeta.setForeground(Color.BLACK);
        comboEmpresaTarjeta.setMaximumSize(new Dimension(366, 36));
        comboEmpresaTarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(comboEmpresaTarjeta);
        panel.add(Box.createRigidArea(new Dimension(0, 13)));
        
        JLabel lblTipo = new JLabel("Tipo de tarjeta*");
        lblTipo.setFont(new Font("Arial", Font.PLAIN, 10));
        lblTipo.setForeground(new Color(180, 180, 180));
        lblTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTipo);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        
        String[] tipos = {"Credito", "Debito"};
        comboTipoTarjeta = new JComboBox<>(tipos);
        comboTipoTarjeta.setFont(new Font("Arial", Font.PLAIN, 13));
        comboTipoTarjeta.setBackground(Color.WHITE);
        comboTipoTarjeta.setForeground(Color.BLACK);
        comboTipoTarjeta.setMaximumSize(new Dimension(366, 36));
        comboTipoTarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(comboTipoTarjeta);
        panel.add(Box.createRigidArea(new Dimension(0, 13)));
        
        agregarCampoFormulario(panel, "Caducidad (MM/AAAA)*", 
            txtCaducidad = crearCampoTexto());
        
        agregarCampoFormulario(panel, "CVV*", 
            txtCVV = crearCampoTexto());
        
        JPanel panelGuardarContainer = new JPanel();
        panelGuardarContainer.setLayout(new BoxLayout(panelGuardarContainer, BoxLayout.Y_AXIS));
        panelGuardarContainer.setBackground(COLOR_FONDO_CLARO);
        panelGuardarContainer.setMaximumSize(new Dimension(366, 80));
        panelGuardarContainer.setPreferredSize(new Dimension(366, 80));
        panelGuardarContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblGuardar = new JLabel("¿Quieres guardar este método de pago para futuras compras?");
        lblGuardar.setFont(new Font("Arial", Font.PLAIN, 10));
        lblGuardar.setForeground(COLOR_TEXTO);
        lblGuardar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelGuardarContainer.add(lblGuardar);
        panelGuardarContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        
        JPanel panelOpciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelOpciones.setBackground(COLOR_FONDO_CLARO);
        panelOpciones.setMaximumSize(new Dimension(366, 30));
        panelOpciones.setPreferredSize(new Dimension(366, 30));
        panelOpciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        checkGuardarSi = new JCheckBox("SI");
        checkGuardarSi.setBackground(COLOR_FONDO_CLARO);
        checkGuardarSi.setForeground(COLOR_TEXTO);
        checkGuardarSi.setFont(new Font("Arial", Font.PLAIN, 12));
        checkGuardarSi.setFocusPainted(false);
        
        panelOpciones.add(checkGuardarSi);
        panelGuardarContainer.add(panelOpciones);
        panel.add(panelGuardarContainer);
        
        return panel;
    }
    
    /**
     * Crea un campo de texto estándar para el formulario
     */
    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setForeground(Color.BLACK);
        campo.setBackground(Color.WHITE);
        campo.setCaretColor(Color.BLACK);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        campo.setMaximumSize(new Dimension(366, 36));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return campo;
    }
    
    /**
     * Agrega un campo de formulario con su etiqueta
     */
    private void agregarCampoFormulario(JPanel panel, String etiqueta, JTextField campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Arial", Font.PLAIN, 10));
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        panel.add(campo);
        panel.add(Box.createRigidArea(new Dimension(0, 13)));
    }
    
    /**
     * Limpia el formulario de nueva tarjeta
     */
    private void limpiarFormulario() {
        txtNumeroTarjeta.setText("");
        txtNombreTarjeta.setText("");
        txtCaducidad.setText("");
        txtCVV.setText("");
        checkGuardarSi.setSelected(false);
        comboTipoTarjeta.setSelectedIndex(0);
        comboEmpresaTarjeta.setSelectedIndex(0);
    }
    
    /**
     * Crea el panel del resumen de compra
     */
    private JPanel crearPanelResumen() {
        JPanel panelWrapper = new JPanel();
        panelWrapper.setLayout(new BoxLayout(panelWrapper, BoxLayout.Y_AXIS));
        panelWrapper.setBackground(COLOR_FONDO);
        panelWrapper.setBorder(new EmptyBorder(0, 60, 0, 0));
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);
        
        JLabel lblTitulo = new JLabel("Resumen de tu compra");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 29)));
        
        JPanel panelLinea = new JPanel(new BorderLayout(9, 0));
        panelLinea.setBackground(COLOR_FONDO);
        panelLinea.setMaximumSize(new Dimension(440, 29));
        panelLinea.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblResumenCompra = new JLabel(compraActual.getLineaCompra());
        lblResumenCompra.setFont(new Font("Arial", Font.PLAIN, 14));
        lblResumenCompra.setForeground(COLOR_TEXTO);
        
        JLabel lblPrecio = new JLabel(formatearPrecio(compraActual.getPrecio()));
        lblPrecio.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPrecio.setForeground(COLOR_TEXTO);
        lblPrecio.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panelLinea.add(lblResumenCompra, BorderLayout.WEST);
        panelLinea.add(lblPrecio, BorderLayout.EAST);
        
        panel.add(panelLinea);
        panel.add(Box.createRigidArea(new Dimension(0, 29)));
        
        JSeparator separador = new JSeparator();
        separador.setForeground(COLOR_BORDE);
        separador.setBackground(COLOR_BORDE);
        separador.setMaximumSize(new Dimension(440, 1));
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(separador);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel panelTotal = new JPanel(new BorderLayout(9, 0));
        panelTotal.setBackground(COLOR_FONDO);
        panelTotal.setMaximumSize(new Dimension(440, 29));
        panelTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblTotalTexto = new JLabel("Total");
        lblTotalTexto.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalTexto.setForeground(COLOR_TEXTO);
        
        lblTotal = new JLabel(formatearPrecio(compraActual.getSubtotal()));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(COLOR_TEXTO);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panelTotal.add(lblTotalTexto, BorderLayout.WEST);
        panelTotal.add(lblTotal, BorderLayout.EAST);
        
        panel.add(panelTotal);
        panel.add(Box.createRigidArea(new Dimension(0, 49)));
        
        btnRealizarPedido = new JButton("Realizar pedido");
        btnRealizarPedido.setFont(new Font("Arial", Font.BOLD, 15));
        btnRealizarPedido.setForeground(COLOR_FONDO);
        btnRealizarPedido.setBackground(COLOR_BOTON);
        btnRealizarPedido.setFocusPainted(false);
        btnRealizarPedido.setBorderPainted(false);
        btnRealizarPedido.setMaximumSize(new Dimension(440, 49));
        btnRealizarPedido.setPreferredSize(new Dimension(440, 49));
        btnRealizarPedido.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRealizarPedido.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRealizarPedido.setOpaque(true);
        
        btnRealizarPedido.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnRealizarPedido.setBackground(new Color(220, 60, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnRealizarPedido.setBackground(COLOR_BOTON);
            }
        });
        
        btnRealizarPedido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarPedido();
            }
        });
        
        panel.add(btnRealizarPedido);
        
        panelWrapper.add(panel);
        
        return panelWrapper;
    }
    
    /**
     * Procesa el pedido validando el método de pago seleccionado
     */
    private void realizarPedido() {
        System.out.println("\n🛒 Procesando pedido...");
        
        if (checkNuevaTarjeta.isSelected()) {
            if (!validarFormularioNuevaTarjeta()) {
                return;
            }
            
            String numeroTarjeta = txtNumeroTarjeta.getText().trim().replaceAll("\\s+", "");
            
            String numTarjetaGuardada = null;
            if (checkGuardarSi.isSelected()) {
                if (guardarNuevoMetodoPago(numeroTarjeta)) {
                    numTarjetaGuardada = numeroTarjeta;
                    cargarMetodosPagoEnPopup();
                }
            }
            
            if (guardarComprobante(numTarjetaGuardada != null ? numTarjetaGuardada : numeroTarjeta)) {
                mostrarMensajeExito(numeroTarjeta);
                resetearFormulario();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al registrar la compra en la base de datos.\n" +
                    "Por favor intente nuevamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } else {
            if (metodoSeleccionado != null) {
                if (guardarComprobante(metodoSeleccionado.getNumeroCompleto())) {
                    JOptionPane.showMessageDialog(this,
                        "✓ Pedido realizado exitosamente\n\n" +
                        "Método de pago: " + metodoSeleccionado.getDescripcionCompleta() + "\n" +
                        "Total: " + formatearPrecio(compraActual.getSubtotal()),
                        "Pedido exitoso",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error al registrar la compra en la base de datos.\n" +
                        "Por favor intente nuevamente.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un método de pago",
                    "Método de pago no seleccionado",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    /**
     * Valida el formulario de nueva tarjeta
     */
    private boolean validarFormularioNuevaTarjeta() {
        if (txtNumeroTarjeta.getText().trim().isEmpty() ||
            txtNombreTarjeta.getText().trim().isEmpty() ||
            txtCaducidad.getText().trim().isEmpty() ||
            txtCVV.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Por favor complete todos los campos de la tarjeta",
                "Campos incompletos",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        String numeroTarjeta = txtNumeroTarjeta.getText().trim().replaceAll("\\s+", "");
        if (!numeroTarjeta.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(this,
                "El número de tarjeta debe tener 16 dígitos",
                "Formato inválido",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        String caducidad = txtCaducidad.getText().trim();
        if (!caducidad.matches("\\d{2}/\\d{4}")) {
            JOptionPane.showMessageDialog(this,
                "La caducidad debe tener el formato MM/AAAA",
                "Formato inválido",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        String cvv = txtCVV.getText().trim();
        if (!cvv.matches("\\d{3,4}")) {
            JOptionPane.showMessageDialog(this,
                "El CVV debe tener 3 o 4 dígitos",
                "Formato inválido",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Guarda un nuevo método de pago para el cliente
     */
    private boolean guardarNuevoMetodoPago(String numeroTarjeta) {
        String empresaSeleccionada = (String) comboEmpresaTarjeta.getSelectedItem();
        
        MetodoPago nuevoMetodo = new MetodoPago(empresaSeleccionada, 
            (String) comboTipoTarjeta.getSelectedItem(), 
            numeroTarjeta.substring(numeroTarjeta.length() - 4), 
            "");
        nuevoMetodo.setNumeroCompleto(numeroTarjeta);
        nuevoMetodo.setNombreTitular(txtNombreTarjeta.getText().trim());
        nuevoMetodo.setFechaCaducidad(txtCaducidad.getText().trim());
        
        return clienteActual.agregarMetodoPago(nuevoMetodo);
    }
    
    /**
     * Muestra mensaje de éxito después de realizar el pedido
     */
    private void mostrarMensajeExito(String numeroTarjeta) {
        String mensaje = "✓ Pedido realizado exitosamente\n";
        if (checkGuardarSi.isSelected()) {
            mensaje += "✓ Método de pago guardado\n";
        }
        mensaje += "\nEmpresa: " + comboEmpresaTarjeta.getSelectedItem() + "\n";
        mensaje += "Titular: " + txtNombreTarjeta.getText() + "\n";
        mensaje += "Tarjeta: ****" + numeroTarjeta.substring(numeroTarjeta.length() - 4) + "\n";
        mensaje += "Total: " + formatearPrecio(compraActual.getSubtotal());
        
        JOptionPane.showMessageDialog(this, mensaje, "Pedido exitoso", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Resetea el formulario después de completar la compra
     */
    private void resetearFormulario() {
        checkNuevaTarjeta.setSelected(false);
        panelFormularioNuevaTarjeta.setVisible(false);
        panelFormularioNuevaTarjeta.setMaximumSize(new Dimension(400, 0));
        panelFormularioNuevaTarjeta.setPreferredSize(new Dimension(400, 0));
        limpiarFormulario();
        panelContainer.revalidate();
        panelContainer.repaint();
    }
    
    /**
     * Guarda el comprobante completo en la base de datos
     * Suma puntos VIP al cliente si tiene membresía VIP
     * SIN envío de email
     */
    private boolean guardarComprobante(String numeroTarjeta) {
        Connection conn = ConexionBD.obtenerConexion();
        
        try {
            conn.setAutoCommit(false);
            
            System.out.println("\n========================================");
            System.out.println("  PROCESANDO COMPRA COMPLETA");
            System.out.println("========================================");
            
            String queryComprobante = "INSERT INTO Comprobante (NumComprobante, ID_Cliente, FechaCompra, MetodoPago) " +
                                     "VALUES (?, ?, NOW(), ?)";
            
            PreparedStatement stmtComprobante = conn.prepareStatement(queryComprobante, 
                PreparedStatement.RETURN_GENERATED_KEYS);
            
            String numComprobante = generarNumeroComprobante();
            String metodoPago = determinarMetodoPago(numeroTarjeta);
            
            stmtComprobante.setString(1, numComprobante);
            stmtComprobante.setInt(2, clienteActual.getIdCliente());
            stmtComprobante.setString(3, metodoPago);
            
            int rowsComprobante = stmtComprobante.executeUpdate();
            
            if (rowsComprobante == 0) {
                conn.rollback();
                stmtComprobante.close();
                return false;
            }
            
            ResultSet rsComprobante = stmtComprobante.getGeneratedKeys();
            int idComprobante = -1;
            if (rsComprobante.next()) {
                idComprobante = rsComprobante.getInt(1);
            }
            rsComprobante.close();
            stmtComprobante.close();
            
            System.out.println("✓ Paso 1/4: Comprobante creado (ID: " + idComprobante + ")");
            
            int idFuncion = obtenerFuncionAleatoria(conn);
            if (idFuncion == -1) {
                System.err.println("❌ Error: No hay funciones disponibles");
                conn.rollback();
                JOptionPane.showMessageDialog(this,
                    "No hay funciones disponibles en este momento.\n" +
                    "Por favor verifique que existan funciones en la base de datos.",
                    "Error - Sin funciones",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            String queryBoleto = "INSERT INTO Boleto (NumeroButaca, ID_Funcion, ID_Cliente) " +
                                "VALUES (?, ?, ?)";
            
            PreparedStatement stmtBoleto = conn.prepareStatement(queryBoleto, 
                PreparedStatement.RETURN_GENERATED_KEYS);
            
            ArrayList<Integer> idsBoletos = new ArrayList<>();
            
            for (int i = 0; i < compraActual.getCantidad(); i++) {
                String numeroButaca = generarNumeroButaca(i + 1);
                
                stmtBoleto.setString(1, numeroButaca);
                stmtBoleto.setInt(2, idFuncion);
                stmtBoleto.setInt(3, clienteActual.getIdCliente());
                
                int rowsBoleto = stmtBoleto.executeUpdate();
                
                if (rowsBoleto > 0) {
                    ResultSet rsBoleto = stmtBoleto.getGeneratedKeys();
                    if (rsBoleto.next()) {
                        idsBoletos.add(rsBoleto.getInt(1));
                    }
                    rsBoleto.close();
                }
            }
            
            stmtBoleto.close();
            
            if (idsBoletos.size() != compraActual.getCantidad()) {
                System.err.println("❌ Error: No se crearon todos los boletos");
                conn.rollback();
                return false;
            }
            
            System.out.println("✓ Paso 2/4: " + idsBoletos.size() + " boleto(s) creado(s)");
            
            String queryComprobanteBoleto = "INSERT INTO Comprobante_Boleto (ID_Comprobante, ID_Boleto, Cantidad) " +
                                           "VALUES (?, ?, 1)";
            
            PreparedStatement stmtCB = conn.prepareStatement(queryComprobanteBoleto);
            
            int totalVinculados = 0;
            for (Integer idBoleto : idsBoletos) {
                stmtCB.setInt(1, idComprobante);
                stmtCB.setInt(2, idBoleto);
                totalVinculados += stmtCB.executeUpdate();
            }
            
            stmtCB.close();
            
            if (totalVinculados != idsBoletos.size()) {
                System.err.println("❌ Error: No se vincularon todos los boletos");
                conn.rollback();
                return false;
            }
            
            System.out.println("✓ Paso 3/4: " + totalVinculados + " boleto(s) vinculado(s) al comprobante");
            
            // SISTEMA DE PUNTOS VIP
            String queryVerificarVIP = "SELECT Membresia, Puntos FROM Cliente WHERE ID_Cliente = ?";
            PreparedStatement stmtVerificarVIP = conn.prepareStatement(queryVerificarVIP);
            stmtVerificarVIP.setInt(1, clienteActual.getIdCliente());
            ResultSet rsVIP = stmtVerificarVIP.executeQuery();
            
            if (rsVIP.next()) {
                String membresia = rsVIP.getString("Membresia");
                int puntosActuales = rsVIP.getInt("Puntos");
                
                if (membresia != null && membresia.equalsIgnoreCase("VIP")) {
                    double totalGastado = compraActual.getSubtotal();
                    int puntosASumar = (int) Math.round(totalGastado * 0.01);
                    int puntosNuevos = puntosActuales + puntosASumar;
                    
                    String queryActualizarPuntos = "UPDATE Cliente SET Puntos = ? WHERE ID_Cliente = ?";
                    PreparedStatement stmtActualizarPuntos = conn.prepareStatement(queryActualizarPuntos);
                    stmtActualizarPuntos.setInt(1, puntosNuevos);
                    stmtActualizarPuntos.setInt(2, clienteActual.getIdCliente());
                    
                    int rowsActualizados = stmtActualizarPuntos.executeUpdate();
                    stmtActualizarPuntos.close();
                    
                    if (rowsActualizados > 0) {
                        System.out.println("✓ Paso 4/4: Puntos VIP actualizados");
                        System.out.println("  → Cliente: " + clienteActual.getNombre() + " " + clienteActual.getApellido());
                        System.out.println("  → Membresía: VIP");
                        System.out.println("  → Total gastado: $" + String.format("%.2f", totalGastado));
                        System.out.println("  → Puntos ganados: " + puntosASumar);
                        System.out.println("  → Puntos anteriores: " + puntosActuales);
                        System.out.println("  → Puntos nuevos: " + puntosNuevos);
                    } else {
                        System.err.println("❌ Error: No se pudieron actualizar los puntos");
                        conn.rollback();
                        rsVIP.close();
                        stmtVerificarVIP.close();
                        return false;
                    }
                } else {
                    System.out.println("✓ Paso 4/4: Cliente sin membresía VIP - No se suman puntos");
                    System.out.println("  → Membresía actual: " + (membresia != null ? membresia : "NO VIP"));
                }
            }
            
            rsVIP.close();
            stmtVerificarVIP.close();
            
            conn.commit();
            conn.setAutoCommit(true);
            
            mostrarResumenCompra(idComprobante, numComprobante, idsBoletos, idFuncion, numeroTarjeta);
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar comprobante completo");
            e.printStackTrace();
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        return false;
    }
    
    /**
     * Determina el método de pago basándose en el número de tarjeta
     */
    private String determinarMetodoPago(String numeroTarjeta) {
        if (numeroTarjeta == null || numeroTarjeta.isEmpty()) {
            return "Tarjeta de crédito/débito";
        }
        
        String ultimos4 = numeroTarjeta.substring(numeroTarjeta.length() - 4);
        
        if (checkNuevaTarjeta.isSelected()) {
            String empresa = (String) comboEmpresaTarjeta.getSelectedItem();
            return empresa + " ****" + ultimos4;
        }
        
        if (numeroTarjeta.startsWith("4")) {
            return "Visa ****" + ultimos4;
        } else if (numeroTarjeta.startsWith("5")) {
            return "Mastercard ****" + ultimos4;
        } else if (numeroTarjeta.startsWith("3")) {
            return "American Express ****" + ultimos4;
        }
        
        return "Tarjeta ****" + ultimos4;
    }
    
    /**
     * Genera un número de butaca aleatorio
     */
    private String generarNumeroButaca(int index) {
        char fila = (char) ('A' + (int) (Math.random() * 10));
        int numero = (int) (Math.random() * 20) + 1;
        return fila + String.valueOf(numero);
    }
    
    /**
     * Obtiene una función aleatoria disponible de la base de datos
     */
    private int obtenerFuncionAleatoria(Connection conn) {
        String query = "SELECT f.ID_Funcion, p.Titulo, f.FechaFuncion, f.HoraFuncion, s.Numero AS NumeroSala " +
                      "FROM Funcion f " +
                      "INNER JOIN Pelicula p ON f.ID_Pelicula = p.ID_Pelicula " +
                      "INNER JOIN Sala s ON f.ID_Sala = s.ID_Sala " +
                      "WHERE f.Estado = 'Disponible' " +
                      "ORDER BY RAND() LIMIT 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int idFuncion = rs.getInt("ID_Funcion");
                System.out.println("  → Función: " + rs.getString("Titulo"));
                System.out.println("  → Fecha: " + rs.getDate("FechaFuncion"));
                System.out.println("  → Hora: " + rs.getTime("HoraFuncion"));
                System.out.println("  → Sala: " + rs.getInt("NumeroSala"));
                return idFuncion;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener función");
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Muestra un resumen detallado de la compra realizada
     */
    private void mostrarResumenCompra(int idComprobante, String numComprobante, 
                                      ArrayList<Integer> idsBoletos, int idFuncion, 
                                      String numeroTarjeta) {
        System.out.println("\n========================================");
        System.out.println("  ✓ COMPRA REALIZADA EXITOSAMENTE");
        System.out.println("========================================");
        System.out.println("DATOS DEL COMPROBANTE");
        System.out.println("----------------------------------------");
        System.out.println("ID Comprobante: " + idComprobante);
        System.out.println("Num Comprobante: " + numComprobante);
        System.out.println("Cliente: " + clienteActual.getNombre() + " " + clienteActual.getApellido());
        System.out.println("DNI: " + clienteActual.getDni());
        System.out.println("Cantidad: " + compraActual.getCantidad() + " boleto(s)");
        System.out.println("Total: $" + String.format("%.2f", compraActual.getSubtotal()));
        System.out.println("----------------------------------------");
        System.out.println("BOLETOS GENERADOS");
        System.out.println("----------------------------------------");
        for (int i = 0; i < idsBoletos.size(); i++) {
            System.out.println("Boleto " + (i + 1) + " - ID: " + idsBoletos.get(i));
        }
        System.out.println("Función ID: " + idFuncion);
        System.out.println("Descripción: " + compraActual.getDescripcion());
        System.out.println("----------------------------------------");
        System.out.println("MÉTODO DE PAGO");
        System.out.println("----------------------------------------");
        if (numeroTarjeta != null && numeroTarjeta.length() >= 4) {
            System.out.println("Tarjeta: ****" + numeroTarjeta.substring(numeroTarjeta.length() - 4));
        } else {
            System.out.println("Tarjeta: Nueva (no guardada)");
        }
        System.out.println("========================================\n");
    }
    
    /**
     * Genera un número único de comprobante
     */
    private String generarNumeroComprobante() {
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formato = 
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        
        int random = (int) (Math.random() * 9000) + 1000;
        
        return "COMP-" + ahora.format(formato) + "-" + random;
    }
    
    /**
     * Formatea un precio en formato de moneda
     */
    private String formatearPrecio(double precio) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        return formato.format(precio);
    }
}