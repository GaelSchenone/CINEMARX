
package CINEMARX.M4;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;

public class CustomDialog extends JDialog {

    public CustomDialog(Frame owner, String message) {
        super(owner, true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(400, 200);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);
        setContentPane(panel);

        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(messageLabel, BorderLayout.CENTER);

        JButton okButton = new JButton("ENTENDIDO");
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setForeground(Color.WHITE);
        okButton.setBackground(new Color(220, 50, 50));
        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);
        okButton.setContentAreaFilled(false); // Ensure custom painting is visible
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                okButton.setBackground(new Color(200, 40, 40)); // Darker red on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                okButton.setBackground(new Color(220, 50, 50)); // Original red
            }
        });
        okButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                AbstractButton button = (AbstractButton) c;
                button.setOpaque(false);
            }

            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton button = (AbstractButton) c;
                ButtonModel model = button.getModel();

                if (model.isPressed()) {
                    g2.setColor(new Color(180, 30, 30)); // Even darker when pressed
                } else if (model.isRollover()) {
                    g2.setColor(new Color(200, 40, 40)); // Darker red on hover
                } else {
                    g2.setColor(button.getBackground());
                }
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15); // Rounded corners

                g2.dispose();
                super.paint(g, c);
            }
        });
        okButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Make dialog draggable
        Point initialClick = new Point();
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick.setLocation(e.getPoint());
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });
    }
}
