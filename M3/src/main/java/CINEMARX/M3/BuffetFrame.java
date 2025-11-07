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

public class BuffetFrame extends JFrame {
    private JPanel panelContenido;
    private Map<Integer, Integer> carrito = new HashMap<>();
    private Map<Integer, JLabel> contadoresProductos = new HashMap<>();
    private int idClienteActual = 1; // Cliente por defecto

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

        panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(new Color(45, 45, 45));
        panelContenido.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel lblTitulo = new JLabel("Acompaña tu película con algo para picar:");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panelContenido.add(lblTitulo);

        JScrollPane scroll = new JScrollPane(panelContenido);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        scroll.setBackground(new Color(45, 45, 45));
        scroll.getViewport().setBackground(new Color(45, 45, 45));

        mainPanel.add(scroll, BorderLayout.CENTER);
        add(mainPanel);

        cargarProductosPorCategoria();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(45, 45, 45));
        header.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));

        JButton btnVolver = new JButton("←");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 28));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setBackground(new Color(45, 45, 45));
        btnVolver.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setContentAreaFilled(false);
        btnVolver.addActionListener(e -> dispose());

        JLabel lblLogo = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/02.png"));
            Image logoImg = logoIcon.getImage().getScaledInstance(350, 80, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(logoImg));
        } catch (Exception e) {
            lblLogo.setText("CINEMAR X");
            lblLogo.setFont(new Font("Arial", Font.BOLD, 32));
            lblLogo.setForeground(Color.WHITE);
        }
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 16));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setBackground(new Color(220, 50, 50));
        btnContinuar.setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));
        btnContinuar.setFocusPainted(false);
        btnContinuar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnContinuar.addActionListener(e -> continuarCompra());

        header.add(btnVolver, BorderLayout.WEST);
        header.add(lblLogo, BorderLayout.CENTER);
        header.add(btnContinuar, BorderLayout.EAST);

        return header;
    }

    private void cargarProductosPorCategoria() {
        Map<String, List<Producto>> productosPorCategoria = new HashMap<>();

        try (Connection con = ConexionBD.getConexion()) {
            if (con == null) {
                throw new Exception("No se pudo conectar a la base de datos.");
            }

            String query = "SELECT ID_Prod, Nombre, Precio FROM Producto ORDER BY Nombre";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("ID_Prod"),
                    rs.getString("Nombre"),
                    rs.getDouble("Precio"),
                    determinarCategoria(rs.getString("Nombre")),
                    ""
                );

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

    private String determinarCategoria(String nombreProducto) {
        String nombre = nombreProducto.toLowerCase();
        
        if (nombre.contains("combo") || nombre.contains("pack")) {
            return "Combos";
        } else if (nombre.contains("coca") || nombre.contains("pepsi") || 
                   nombre.contains("sprite") || nombre.contains("agua") || 
                   nombre.contains("jugo") || nombre.contains("gaseosa")) {
            return "Bebidas";
        } else if (nombre.contains("pochoclo") || nombre.contains("nachos") || 
                   nombre.contains("palomitas")) {
            return "Snacks";
        } else if (nombre.contains("chocolate") || nombre.contains("caramelo") || 
                   nombre.contains("gomita") || nombre.contains("dulce")) {
            return "Golosinas";
        }
        
        return "Otros";
    }

    private void mostrarProductosPorCategoria(Map<String, List<Producto>> productosPorCategoria) {
        String[] ordenCategorias = {"Combos", "Bebidas", "Snacks", "Golosinas", "Otros"};

        for (String categoria : ordenCategorias) {
            if (productosPorCategoria.containsKey(categoria)) {
                List<Producto> productos = productosPorCategoria.get(categoria);

                JPanel panelCategoria = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
                panelCategoria.setBackground(new Color(45, 45, 45));
                panelCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);

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

    private JPanel crearTarjetaProducto(Producto p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(260, 400));
        card.setBackground(new Color(45, 45, 45));

        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setBackground(new Color(235, 225, 210));
        imgPanel.setPreferredSize(new Dimension(260, 280));

        String emoji = obtenerEmojiPorCategoria(p.getCategoria());
        JLabel lbl = new JLabel(emoji, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.PLAIN, 80));
        imgPanel.add(lbl, BorderLayout.CENTER);

        card.add(imgPanel, BorderLayout.CENTER);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(new Color(45, 45, 45));
        info.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        JPanel textoPanel = new JPanel();
        textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
        textoPanel.setBackground(new Color(45, 45, 45));
        textoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNombre = new JLabel(p.getNombre().toUpperCase());
        lblNombre.setFont(new Font("Arial", Font.BOLD, 13));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPrecio = new JLabel("$" + String.format("%.0f", p.getPrecio()));
        lblPrecio.setFont(new Font("Arial", Font.PLAIN, 13));
        lblPrecio.setForeground(Color.WHITE);
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);

        textoPanel.add(lblNombre);
        textoPanel.add(Box.createVerticalStrut(5));
        textoPanel.add(lblPrecio);

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

        JLabel lblContador = new JLabel("0");
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
        btnAgregar.addActionListener(e -> agregarAlCarrito(p));

        controlPanel.add(btnRestar);
        controlPanel.add(lblContador);
        controlPanel.add(btnAgregar);

        info.add(textoPanel);
        info.add(Box.createVerticalStrut(10));
        info.add(controlPanel);

        card.add(info, BorderLayout.SOUTH);

        return card;
    }

    private String obtenerEmojiPorCategoria(String categoria) {
        switch (categoria) {
            case "Combos": return "🍿";
            case "Bebidas": return "🥤";
            case "Snacks": return "🍕";
            case "Golosinas": return "🍬";
            default: return "🎬";
        }
    }

    private void agregarAlCarrito(Producto producto) {
        int productoId = producto.getId();
        int cantidadActual = carrito.getOrDefault(productoId, 0);

        carrito.put(productoId, cantidadActual + 1);

        JLabel lblContador = contadoresProductos.get(productoId);
        if (lblContador != null) {
            lblContador.setText(String.valueOf(cantidadActual + 1));
        }

        System.out.println("✓ Agregado: " + producto.getNombre() + " (Cantidad: " + (cantidadActual + 1) + ")");
    }

    private void restarDelCarrito(Producto producto) {
        int productoId = producto.getId();
        int cantidadActual = carrito.getOrDefault(productoId, 0);

        if (cantidadActual > 0) {
            carrito.put(productoId, cantidadActual - 1);

            if (cantidadActual - 1 == 0) {
                carrito.remove(productoId);
            }

            JLabel lblContador = contadoresProductos.get(productoId);
            if (lblContador != null) {
                lblContador.setText(String.valueOf(cantidadActual - 1));
            }

            System.out.println("✓ Restado: " + producto.getNombre() + " (Cantidad: " + (cantidadActual - 1) + ")");
        }
    }

    private void continuarCompra() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No has seleccionado ningún producto",
                "Carrito Vacío",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Connection con = null;
        try {
            con = ConexionBD.getConexion();
            if (con == null) {
                throw new Exception("No se pudo conectar a la base de datos.");
            }

            con.setAutoCommit(false);

            String numComprobante = generarNumeroComprobante();
            String insertComprobante = "INSERT INTO Comprobante (NumComprobante, ID_Cliente, FechaCompra, MetodoPago) VALUES (?, ?, NOW(), ?)";
            PreparedStatement pstmtComprobante = con.prepareStatement(insertComprobante, Statement.RETURN_GENERATED_KEYS);
            pstmtComprobante.setString(1, numComprobante);
            pstmtComprobante.setInt(2, idClienteActual);
            pstmtComprobante.setString(3, "Efectivo");
            pstmtComprobante.executeUpdate();

            ResultSet rsComprobante = pstmtComprobante.getGeneratedKeys();
            int idComprobante = 0;
            if (rsComprobante.next()) {
                idComprobante = rsComprobante.getInt(1);
            }

            String insertComprobanteProducto = "INSERT INTO Comprobante_Producto (ID_Comprobante, ID_Prod, Cantidad) VALUES (?, ?, ?)";
            PreparedStatement pstmtProducto = con.prepareStatement(insertComprobanteProducto);

            double totalCompra = 0.0;
            StringBuilder detalleCompra = new StringBuilder();

            for (Map.Entry<Integer, Integer> item : carrito.entrySet()) {
                int productoId = item.getKey();
                int cantidad = item.getValue();

                String queryProducto = "SELECT Nombre, Precio FROM Producto WHERE ID_Prod = ?";
                PreparedStatement pstmtSelect = con.prepareStatement(queryProducto);
                pstmtSelect.setInt(1, productoId);
                ResultSet rs = pstmtSelect.executeQuery();

                if (rs.next()) {
                    String nombre = rs.getString("Nombre");
                    double precio = rs.getDouble("Precio");
                    double subtotal = precio * cantidad;
                    totalCompra += subtotal;

                    pstmtProducto.setInt(1, idComprobante);
                    pstmtProducto.setInt(2, productoId);
                    pstmtProducto.setInt(3, cantidad);
                    pstmtProducto.executeUpdate();

                    detalleCompra.append(String.format("%dx %s - $%.2f\n", cantidad, nombre, subtotal));
                    System.out.println("✓ Registrado: " + cantidad + "x " + nombre);
                }
            }

            con.commit();

            JOptionPane.showMessageDialog(this,
                "¡Compra realizada con éxito!\n\n" +
                "Comprobante: " + numComprobante + "\n" +
                "----------------------------------------\n" +
                detalleCompra.toString() +
                "----------------------------------------\n" +
                String.format("TOTAL: $%.2f", totalCompra),
                "Compra Exitosa",
                JOptionPane.INFORMATION_MESSAGE);

            carrito.clear();
            panelContenido.removeAll();

            JLabel lblTitulo = new JLabel("Acompaña tu película con algo para picar:");
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
            lblTitulo.setForeground(Color.WHITE);
            lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            panelContenido.add(lblTitulo);

            cargarProductosPorCategoria();

        } catch (Exception e) {
            System.err.println("❌ Error al procesar la compra: " + e.getMessage());
            e.printStackTrace();
            
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            JOptionPane.showMessageDialog(this,
                "Error al procesar la compra: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String generarNumeroComprobante() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "COMP-" + LocalDateTime.now().format(formatter);
    }
}



