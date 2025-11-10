package CINEMARX.M5;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class ImageLoader {
    
    public static final String URL_LOGO = "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=M5%2FCINEMARX%20logotipo.png";
    public static final String URL_USER = "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=M5%2Fuser.png";
    public static final String URL_CARD = "https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=M5%2Fcreditcard.png";
    
    public static ImageIcon cargarImagenDesdeURL(String urlString, int width, int height) {
        try {
            URL url = new URL(urlString);
            BufferedImage imagen = ImageIO.read(url);
            
            if (imagen != null) {
                Image imagenEscalada = imagen.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(imagenEscalada);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen desde URL: " + urlString);
            e.printStackTrace();
        }
        return null;
    }
    
    public static ImageIcon cargarImagenDesdeURL(String urlString) {
        try {
            URL url = new URL(urlString);
            BufferedImage imagen = ImageIO.read(url);
            
            if (imagen != null) {
                return new ImageIcon(imagen);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen desde URL: " + urlString);
            e.printStackTrace();
        }
        return null;
    }
}