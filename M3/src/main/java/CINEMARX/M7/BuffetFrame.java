package CINEMARX.M7;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.awt.image.BufferedImage;

public class BuffetFrame extends JFrame {
    
    // 🎨 COLOR DE FONDO UNIFORME: (43, 43, 43) que equivale a #2b2b2b
    private static final Color COLOR_FONDO = new Color(43, 43, 43);
    
    private JPanel panelContenido;
    private Map<Integer, Integer> carrito = new HashMap<>();
    private Map<Integer, JLabel> contadoresProductos = new HashMap<>();
    private int idClienteActual = 1;
    
    // URL del Logo (Funciona)
    private static final String URL_LOGO = "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=M7%2F02.png";
    
    // ✅ MAPA DE IMÁGENES: SOLO con las 2 URLs de producto que proporcionaste (ID 6 y 7).
    private static final Map<Integer, String> IMAGENES_PRODUCTOS = new HashMap<Integer, String>() {{
        
        // ID 6: Gaseosa Grande
        put(6, "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=M7%2FComida.png");
        
        // ID 7: Nachos con Queso (Usamos la URL con el espacio codificado como %20)
        put(7, "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=M7%2Fcomida%201.png"); 
    }};

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
        mainPanel.setBackground(COLOR_FONDO); 

        JPanel header = createHeader();
        mainPanel.add(header, BorderLayout.NORTH);

        panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(COLOR_FONDO);
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
        scroll.setBackground(COLOR_FONDO);
        scroll.getViewport().setBackground(COLOR_FONDO);

        mainPanel.add(scroll, BorderLayout.CENTER);
        add(mainPanel);

        cargarProductosPorCategoria();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_FONDO); 
        header.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));

        JButton btnVolver = new JButton("←");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 28));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setBackground(COLOR_FONDO); 
        btnVolver.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setContentAreaFilled(false);
        btnVolver.addActionListener(e -> dispose());

        JLabel lblLogo = cargarLogoDesdeURL(URL_LOGO);
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
    
    private JLabel cargarLogoDesdeURL(String urlLogo) {
        JLabel label = new JLabel();
        try {
            URL imgURL = new URL(urlLogo);
            InputStream imageIn = imgURL.openStream();
            BufferedImage originalImage = ImageIO.read(imageIn);
            imageIn.close();
            
            if (originalImage != null) {
                Image logoImg = originalImage.getScaledInstance(350, 80, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(logoImg));
            } else {
                throw new Exception("No se pudo leer la imagen");
            }
        } catch (Exception e) {
            label.setText("CINEMAR X"); 
            label.setFont(new Font("Arial", Font.BOLD, 32));
            label.setForeground(Color.WHITE);
        }
        return label;
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
                int idProd = rs.getInt("ID_Prod");
                
                String urlImagen = IMAGENES_PRODUCTOS.getOrDefault(idProd, "");
                
                // Codificación: Reemplazar espacios por %20 para la URL si es necesario
                String urlCodificada = "";
                if (!urlImagen.isEmpty()) {
                    if (urlImagen.contains("?preview=")) {
                        int index = urlImagen.indexOf("?preview=") + 9;
                        String baseURL = urlImagen.substring(0, index);
                        String paramValue = urlImagen.substring(index);
                        
                        String encodedValue = paramValue.replace(" ", "%20");
                        urlCodificada = baseURL + encodedValue;
                    } else {
                        urlCodificada = urlImagen;
                    }
                }
                
                Producto p = new Producto(
                    idProd,
                    rs.getString("Nombre"),
                    rs.getDouble("Precio"),
                    determinarCategoria(rs.getString("Nombre")),
                    urlCodificada
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
                  nombre.contains("palomitas") || nombre.contains("hot dog")) {
            return "Snacks";
        } else if (nombre.contains("chocolate") || nombre.contains("caramelo") ||  
                  nombre.contains("gomita") || nombre.contains("dulce") || nombre.contains("m&m")) {
            return "Golosinas";
        }
        
        return "Otros";
    }

    private void mostrarProductosPorCategoria(Map<String, List<Producto>> productosPorCategoria) {
        String[] ordenCategorias = {"Combos", "Snacks", "Bebidas", "Golosinas", "Otros"}; 

        for (String categoria : ordenCategorias) {
            if (productosPorCategoria.containsKey(categoria)) {
                
                JLabel lblCategoriaTitulo = new JLabel(categoria.toUpperCase());
                lblCategoriaTitulo.setFont(new Font("Arial", Font.BOLD, 22));
                lblCategoriaTitulo.setForeground(new Color(220, 50, 50)); 
                lblCategoriaTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
                lblCategoriaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
                panelContenido.add(lblCategoriaTitulo);

                List<Producto> productos = productosPorCategoria.get(categoria);

                JPanel panelCategoria = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
                panelCategoria.setBackground(COLOR_FONDO); 
                panelCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                // Corregimos el tamaño máximo para que el FlowLayout funcione correctamente en el ancho.
                panelCategoria.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelCategoria.getPreferredSize().height));


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
        card.setBackground(COLOR_FONDO); 

        // Panel que contiene la imagen y tiene el fondo claro
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setBackground(new Color(235, 225, 210)); 
        imgPanel.setPreferredSize(new Dimension(260, 280));

        JLabel lblImagen = cargarImagenProducto(p.getImagenRuta(), p.getNombre(), p.getCategoria());
        imgPanel.add(lblImagen, BorderLayout.CENTER);

        card.add(imgPanel, BorderLayout.CENTER);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(COLOR_FONDO); 
        info.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        JPanel textoPanel = new JPanel();
        textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
        textoPanel.setBackground(COLOR_FONDO); 
        textoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNombre = new JLabel(p.getNombre().toUpperCase());
        lblNombre.setFont(new Font("Arial", Font.BOLD, 13));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPrecio = new JLabel("$" + String.format("%,.0f", p.getPrecio())); 
        lblPrecio.setFont(new Font("Arial", Font.PLAIN, 13));
        lblPrecio.setForeground(Color.WHITE);
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);

        textoPanel.add(lblNombre);
        textoPanel.add(Box.createVerticalStrut(5));
        textoPanel.add(lblPrecio);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        controlPanel.setBackground(COLOR_FONDO); 
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

        JLabel lblContador = new JLabel(String.valueOf(carrito.getOrDefault(p.getId(), 0))); 
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

    private JLabel cargarImagenProducto(String rutaImagen, String nombreProducto, String categoria) {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        
        try {
            if (rutaImagen != null && !rutaImagen.trim().isEmpty()) { 
                
                URL imgURL = new URL(rutaImagen);
                InputStream imageIn = imgURL.openStream();
                BufferedImage originalImage = ImageIO.read(imageIn);
                imageIn.close();
                
                if (originalImage != null) {
                    
                    // ⭐️ LÍNEA DE DEBUG AÑADIDA PARA VER EL TAMAÑO REAL
                    System.out.println("✅ Imagen leída (" + nombreProducto + "): " + originalImage.getWidth() + "x" + originalImage.getHeight());
                    
                    int originalWidth = originalImage.getWidth();
                    int originalHeight = originalImage.getHeight();
                    
                    int maxWidth = 240;
                    int maxHeight = 240;
                    
                    double widthRatio = (double) maxWidth / originalWidth;
                    double heightRatio = (double) maxHeight / originalHeight;
                    double ratio = Math.min(widthRatio, heightRatio);
                    
                    int newWidth = (int) (originalWidth * ratio);
                    int newHeight = (int) (originalHeight * ratio); 
                    
                    // Solo escalamos si es necesario
                    if (ratio < 1.0) {
                        Image scaledImg = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                        label.setIcon(new ImageIcon(scaledImg));
                    } else {
                        label.setIcon(new ImageIcon(originalImage));
                    }
                    
                } else {
                    // Si ImageIO.read devuelve null, el archivo no es un formato de imagen válido
                    throw new Exception("No se pudo leer la imagen (Formato inválido o datos corruptos)");
                }
            } else {
                throw new Exception("URL vacía o no definida");
            }
            
        } catch (Exception e) {
            String emoji = obtenerEmojiPorCategoria(categoria);
            label.setText(emoji);
            label.setFont(new Font("Arial", Font.PLAIN, 80));
            label.setForeground(new Color(100, 100, 100));
            System.err.println("⚠️ Error cargando imagen para: " + nombreProducto + " - " + e.getMessage()); 
        }
        
        return label;
    }

    private String obtenerEmojiPorCategoria(String categoria) {
        switch (categoria) {
            case "Combos": return "🍿";
            case "Bebidas": return "🥤";
            case "Snacks": return "🌭"; 
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

                    detalleCompra.append(String.format("%dx %s - $%,.2f\n", cantidad, nombre, subtotal));
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
                String.format("TOTAL: $%,.2f", totalCompra),
                "Compra Exitosa",
                JOptionPane.INFORMATION_MESSAGE);

            carrito.clear();
            contadoresProductos.clear(); 
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


