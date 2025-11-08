package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Clase auxiliar para crear componentes de UI con estilos consistentes
 */
public class UIHelpers {
    
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(M6.TEXT_COLOR);
        return label;
    }

    public static JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(M6.FIELDTEXT_COLOR);
        textField.setBackground(new Color(50, 50, 50));
        textField.setCaretColor(M6.FIELDTEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return textField;
    }

    public static JTextField createTextFieldWide() {
        JTextField textField = new JTextField(35);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(M6.FIELDTEXT_COLOR);
        textField.setBackground(new Color(50, 50, 50));
        textField.setCaretColor(M6.FIELDTEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return textField;
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(M6.TEXT_COLOR);
        button.setBackground(M6.ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(M6.ACCENT_COLOR.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(M6.ACCENT_COLOR);
            }
        });

        return button;
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(new Color(50, 50, 50));
        comboBox.setForeground(M6.TEXT_COLOR);
        comboBox.setPreferredSize(new Dimension(300, 35));
    }

    /**
     * Genera un nombre de imagen basado en el título de la película
     */
    public static String generarNombreImagen(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return "default.png";
        }

        // Reemplazar espacios por guiones bajos
        String nombreImagen = titulo.trim().replace(" ", "_");

        // Eliminar caracteres especiales problemáticos
        nombreImagen = nombreImagen.replaceAll("[:\\\\/*?\"<>|]", "");

        // Agregar extensión .png
        if (!nombreImagen.toLowerCase().endsWith(".png")) {
            nombreImagen += ".png";
        }

        return nombreImagen;
    }

    public static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(M6.TEXT_COLOR);
        table.setBackground(new Color(40, 40, 40));
        table.setGridColor(new Color(60, 60, 60));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(60, 60, 60));
        table.getTableHeader().setForeground(M6.TEXT_COLOR);
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBackground(M6.BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }
}