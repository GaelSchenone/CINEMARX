package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.awt.image.BufferedImage;

/**
 * Clase para mostrar el catálogo de películas
 */
public class Peliculas {
    private M6 mainFrame;
    private JPanel contentPanel;
    
    public Peliculas(M6 mainFrame, JPanel contentPanel) {
        this.mainFrame = mainFrame;
        this.contentPanel = contentPanel;
    }
    
    public void mostrar() {
        contentPanel.setLayout(new BorderLayout(10, 10));
        
        // Panel principal con scroll
        JPanel peliculasPanel = new JPanel();
        peliculasPanel.setBackground(M6.BACKGROUND_COLOR);
        peliculasPanel.setLayout(new GridBagLayout());
        
        // Configurar scroll
        JScrollPane scrollPane = new JScrollPane(peliculasPanel);
        scrollPane.setBackground(M6.BACKGROUND_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Cargar películas desde la base de datos
        cargarPeliculas(peliculasPanel);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void cargarPeliculas(JPanel peliculasPanel) {
        try {
            String query = "SELECT ID_Pelicula, Titulo, Imagen FROM Pelicula ORDER BY Titulo";
            
            try (Statement stmt = mainFrame.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10);
                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.NONE;
                
                int col = 0;
                int row = 0;
                
                while (rs.next()) {
                    int idPelicula = rs.getInt("ID_Pelicula");
                    String titulo = rs.getString("Titulo");
                    String nombreImagen = rs.getString("Imagen");
                    
                    // Crear panel para cada película
                    JPanel peliculaCard = crearPeliculaCard(idPelicula, titulo, nombreImagen);
                    
                    gbc.gridx = col;
                    gbc.gridy = row;
                    peliculasPanel.add(peliculaCard, gbc);
                    
                    col++;
                    if (col >= 4) {
                        col = 0;
                        row++;
                    }
                }
                
                // Agregar panel vacío al final para empujar todo hacia arriba
                gbc.gridx = 0;
                gbc.gridy = row + 1;
                gbc.gridwidth = 4;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.BOTH;
                peliculasPanel.add(Box.createVerticalGlue(), gbc);
                
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error al cargar las películas: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private JPanel crearPeliculaCard(int idPelicula, String titulo, String nombreImagen) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(M6.SIDEBAR_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(200, 360));
        card.setMaximumSize(new Dimension(200, 360));
        
        // Contenedor para la imagen con GridBagLayout para centrar
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setBackground(M6.SIDEBAR_COLOR);
        imagePanel.setPreferredSize(new Dimension(180, 260));
        imagePanel.setMaximumSize(new Dimension(180, 260));
        
        // Cargar imagen
        JLabel imageLabel = cargarImagenPelicula(nombreImagen, titulo);
        imagePanel.add(imageLabel);
        
        // Título de la película - centrado
        JLabel tituloLabel = new JLabel("<html><div style='text-align: left; width: 180px;'>" + titulo + "</div></html>");
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tituloLabel.setForeground(M6.TEXT_COLOR);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Botón de reportes
        JButton reportesButton = new JButton("Reportes");
        reportesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        reportesButton.addActionListener(e -> {
            try {
                ReportePelicula reporte = new ReportePelicula(mainFrame, mainFrame.getConnection(), idPelicula);
                reporte.setVisible(true);
            } catch (SQLException ex) {
                System.getLogger(Peliculas.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });

        card.add(imagePanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(tituloLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(reportesButton);
        
        return card;
    }
    
    private JLabel cargarImagenPelicula(String nombreImagen, String titulo) {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        
        try {
            // Usar directamente el nombre de la columna Imagen
            String imagenFinal = (nombreImagen != null && !nombreImagen.trim().isEmpty()) 
                                ? nombreImagen.trim() 
                                : "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=botones%2FM6%2F"+"default.png";
            
            // URL de la imagen
            String imageUrl = imagenFinal;
            
            URL imgURL = new URL(imageUrl);
            InputStream imageIn = imgURL.openStream();
            BufferedImage originalImage = ImageIO.read(imageIn);
            imageIn.close();
            
            if (originalImage != null) {
                // Escalar imagen manteniendo aspecto
                int originalWidth = originalImage.getWidth();
                int originalHeight = originalImage.getHeight();
                
                int maxWidth = 180;
                int maxHeight = 260;
                
                double widthRatio = (double) maxWidth / originalWidth;
                double heightRatio = (double) maxHeight / originalHeight;
                double ratio = Math.min(widthRatio, heightRatio);
                
                int newWidth = (int) (originalWidth * ratio);
                int newHeight = (int) (originalHeight * ratio);
                
                Image scaledImg = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImg));
            } else {
                throw new Exception("No se pudo cargar la imagen");
            }
            
        } catch (Exception e) {
            // En caso de error, mostrar texto alternativo
            label.setText("<html><div style='text-align: center; width: 180px;'>Imagen no disponible</div></html>");
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(M6.FIELDTEXT_COLOR);
            System.err.println("Error cargando imagen para: " + titulo + " - " + e.getMessage());
        }
        
        return label;
    }
}