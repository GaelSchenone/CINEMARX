package CINEMARX.M4;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseSummaryDialog extends JDialog {
    private int idComprobante;
    private JPanel ticketsPanel;
    private JLabel totalLabel;
    private List<TicketInfo> tickets;
    
    // Clase interna para almacenar información de boletos
    private static class TicketInfo {
        int idBoleto;
        String numeroButaca;
        int idFuncion;
        String tituloMovie;
        String fecha;
        String hora;
        String idioma;
        double precio;
        
        TicketInfo(int idBoleto, String numeroButaca, int idFuncion, String titulo, 
                   String fecha, String hora, String idioma, double precio) {
            this.idBoleto = idBoleto;
            this.numeroButaca = numeroButaca;
            this.idFuncion = idFuncion;
            this.tituloMovie = titulo;
            this.fecha = fecha;
            this.hora = hora;
            this.idioma = idioma;
            this.precio = precio;
        }
    }

    public PurchaseSummaryDialog(Frame owner, int idComprobante) {
        super(owner, true);
        this.idComprobante = idComprobante;
        this.tickets = new ArrayList<>();
        
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(700, 600);
        setLocationRelativeTo(owner);
        
        initComponents();
        loadTicketData();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setOpaque(false);
        setContentPane(mainPanel);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Resumen de tu compra");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Botón cerrar
        JButton closeButton = createStyledButton("✕", new Color(200, 50, 50));
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.addActionListener(e -> dispose());
        headerPanel.add(closeButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel de contenido con scroll
        ticketsPanel = new JPanel();
        ticketsPanel.setLayout(new BoxLayout(ticketsPanel, BoxLayout.Y_AXIS));
        ticketsPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(ticketsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con total
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setForeground(Color.WHITE);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Hacer el diálogo arrastrable
        makeDraggable(mainPanel);
    }
    
    private void loadTicketData() {
        String query = "SELECT b.ID_Boleto, b.NumeroButaca, b.ID_Funcion, " +
                      "p.Titulo, f.FechaFuncion, f.HoraFuncion, f.Idioma, f.Precio " +
                      "FROM Comprobante_Boleto cb " +
                      "INNER JOIN Boleto b ON cb.ID_Boleto = b.ID_Boleto " +
                      "INNER JOIN Funcion f ON b.ID_Funcion = f.ID_Funcion " +
                      "INNER JOIN Pelicula p ON f.ID_Pelicula = p.ID_Pelicula " +
                      "WHERE cb.ID_Comprobante = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, idComprobante);
            ResultSet rs = pstmt.executeQuery();
            
            double total = 0;
            while (rs.next()) {
                TicketInfo ticket = new TicketInfo(
                    rs.getInt("ID_Boleto"),
                    rs.getString("NumeroButaca"),
                    rs.getInt("ID_Funcion"),
                    rs.getString("Titulo"),
                    rs.getString("FechaFuncion"),
                    rs.getString("HoraFuncion"),
                    rs.getString("Idioma"),
                    rs.getDouble("Precio")
                );
                tickets.add(ticket);
                total += ticket.precio;
                addTicketPanel(ticket);
            }
            
            totalLabel.setText(String.format("Total: $%.2f", total));
            
        } catch (SQLException e) {
            e.printStackTrace();
            new CustomDialog((Frame) getOwner(), "Error al cargar los boletos").setVisible(true);
        }
    }
    
    private void addTicketPanel(TicketInfo ticket) {
        JPanel ticketPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(45, 45, 45));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        ticketPanel.setOpaque(false);
        ticketPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        ticketPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        // Info del boleto
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel movieLabel = new JLabel(ticket.tituloMovie);
        movieLabel.setForeground(Color.WHITE);
        movieLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel detailsLabel = new JLabel(String.format("Butaca: %s | Fecha: %s %s | %s", 
            ticket.numeroButaca, ticket.fecha, ticket.hora, ticket.idioma));
        detailsLabel.setForeground(new Color(200, 200, 200));
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel priceLabel = new JLabel(String.format("$%.2f", ticket.precio));
        priceLabel.setForeground(new Color(100, 200, 100));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        infoPanel.add(movieLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(detailsLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        
        // Panel de botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        JButton reprogramButton = createStyledButton("Reprogramar", new Color(70, 130, 180));
        reprogramButton.setPreferredSize(new Dimension(120, 35));
        reprogramButton.addActionListener(e -> showRescheduleDialog(ticket));
        
        JButton cancelButton = createStyledButton("Cancelar", new Color(220, 50, 50));
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> cancelTicket(ticket));
        
        buttonsPanel.add(reprogramButton);
        buttonsPanel.add(cancelButton);
        
        ticketPanel.add(infoPanel, BorderLayout.CENTER);
        ticketPanel.add(buttonsPanel, BorderLayout.EAST);
        
        ticketsPanel.add(ticketPanel);
        ticketsPanel.add(Box.createVerticalStrut(10));
    }
    
    private void showRescheduleDialog(TicketInfo ticket) {
        RescheduleDialog dialog = new RescheduleDialog((Frame) getOwner(), ticket);
        dialog.setVisible(true);
        
        if (dialog.wasSuccessful()) {
            // Recargar datos
            ticketsPanel.removeAll();
            tickets.clear();
            loadTicketData();
            ticketsPanel.revalidate();
            ticketsPanel.repaint();
            
            new CustomDialog((Frame) getOwner(), 
                "Modificacion con exito!<br>Su entrada fue reprogramada correctamente").setVisible(true);
        }
    }
    
    private void cancelTicket(TicketInfo ticket) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cancelar este boleto?",
            "Confirmar cancelación",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        String deleteQuery = "DELETE FROM Boleto WHERE ID_Boleto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            
            pstmt.setInt(1, ticket.idBoleto);
            pstmt.executeUpdate();
            
            // Recargar datos
            ticketsPanel.removeAll();
            tickets.clear();
            loadTicketData();
            ticketsPanel.revalidate();
            ticketsPanel.repaint();
            
            new CustomDialog((Frame) getOwner(), 
                "Cancelacion exitosa!<br>Se le estara debitando el monto completo<br>en su tarjeta en los proximos dias.").setVisible(true);
            
        } catch (SQLException e) {
            e.printStackTrace();
            new CustomDialog((Frame) getOwner(), "Error al cancelar el boleto").setVisible(true);
        }
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                ((AbstractButton) c).setOpaque(false);
            }
            
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton button = (AbstractButton) c;
                
                if (button.getModel().isPressed()) {
                    g2.setColor(bgColor.darker().darker());
                } else if (button.getModel().isRollover()) {
                    g2.setColor(bgColor.darker());
                } else {
                    g2.setColor(button.getBackground());
                }
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                g2.dispose();
                super.paint(g, c);
            }
        });
        
        return button;
    }
    
    private void makeDraggable(JPanel panel) {
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
                
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });
    }
}