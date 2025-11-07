package CINEMARX.M4;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel que muestra placeholders geometricos mientras carga el contenido.
 */
public class LoadingPanel extends JPanel {

    public LoadingPanel() {
        setBackground(new Color(0x2B2B2B));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0x3C3C3C));

            int width = getWidth();
            int height = getHeight();
            int cornerRadius = 20;

            // Dibuja algunas formas redondeadas como placeholders
            g2d.fill(new RoundRectangle2D.Float(50, 50, 200, 300, cornerRadius, cornerRadius));
            g2d.fill(new RoundRectangle2D.Float(300, 50, 200, 300, cornerRadius, cornerRadius));
            g2d.fill(new RoundRectangle2D.Float(550, 50, 200, 300, cornerRadius, cornerRadius));
            g2d.fill(new RoundRectangle2D.Float(800, 50, 200, 300, cornerRadius, cornerRadius));
            
            g2d.fill(new RoundRectangle2D.Float(50, 400, width - 100, 50, cornerRadius, cornerRadius));
            g2d.fill(new RoundRectangle2D.Float(50, 480, width - 100, 200, cornerRadius, cornerRadius));

        } finally {
            g2d.dispose();
        }
    }
}