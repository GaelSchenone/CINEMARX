/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package CINEMARX.M2;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

/**
 * Clase utilitaria para mantener consistencia visual en todo el sistema CinemarX
 */
public class CinemarXEstilos {
    
    // Colores del tema Cinemarx (centralizados)
    public static final Color COLOR_FONDO = new Color(24, 24, 24);
    public static final Color COLOR_HEADER = new Color(9, 9, 9);
    public static final Color COLOR_CARD = new Color(40, 40, 40);
    public static final Color COLOR_ROJO = new Color(229, 9, 20);
    public static final Color COLOR_TEXTO = new Color(230, 230, 230);
    public static final Color COLOR_TEXTO_SECUNDARIO = new Color(160, 160, 160);
    public static final Color COLOR_INPUT = new Color(50, 50, 50);
    public static final Color COLOR_BORDER = new Color(80, 80, 80);
    public static final Color COLOR_BOTON = new Color(60, 60, 60);
    public static final Color COLOR_BOTON_HOVER = new Color(80, 80, 80);
    
    /**
     * Configura el Look and Feel del sistema para mantener consistencia
     */
    public static void configurarLookAndFeel() {
        try {
            // Usar el LaF del sistema pero con propiedades personalizadas
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            
            // Personalizar colores globales de componentes
            UIManager.put("ComboBox.background", COLOR_INPUT);
            UIManager.put("ComboBox.foreground", COLOR_TEXTO);
            UIManager.put("ComboBox.selectionBackground", COLOR_BORDER);
            UIManager.put("ComboBox.selectionForeground", COLOR_TEXTO);
            UIManager.put("ComboBox.buttonBackground", COLOR_INPUT);
            
        } catch (Exception e) {
            System.err.println("No se pudo configurar el Look and Feel: " + e.getMessage());
        }
    }
    
    /**
     * Aplica estilo Cinemarx a un JComboBox
     */
    public static void aplicarEstiloComboBox(JComboBox<?> combo) {
        combo.setBackground(COLOR_INPUT);
        combo.setForeground(COLOR_TEXTO);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        combo.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        combo.setFocusable(false);
        
        // Aplicar UI personalizado para controlar completamente el aspecto
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("▼");
                button.setBackground(COLOR_INPUT);
                button.setForeground(COLOR_TEXTO);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setFocusPainted(false);
                return button;
            }
        });
        
        // Personalizar el renderer del popup
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (isSelected) {
                    setBackground(COLOR_BORDER);
                    setForeground(COLOR_TEXTO);
                } else {
                    setBackground(COLOR_INPUT);
                    setForeground(COLOR_TEXTO);
                }
                
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }
    
    /**
     * Aplica estilo Cinemarx a un JTextField
     */
    public static void aplicarEstiloTextField(JTextField textField) {
        textField.setBackground(COLOR_INPUT);
        textField.setForeground(COLOR_TEXTO);
        textField.setCaretColor(COLOR_TEXTO);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }
    
    /**
     * Aplica estilo Cinemarx a un JButton
     */
    public static void aplicarEstiloBoton(JButton boton, Color colorFondo) {
        boton.setBackground(colorFondo);
        boton.setForeground(COLOR_TEXTO);
        boton.setFont(new Font("SansSerif", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            Color original = colorFondo;
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(original);
            }
        });
    }
    
    /**
     * Crea y retorna un panel con el logo de CinemarX
     */
    public static JPanel crearPanelLogo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(COLOR_HEADER);
        
        JLabel lblCinemar = new JLabel("CINEMAR");
        lblCinemar.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblCinemar.setForeground(COLOR_TEXTO);
        
        JLabel lblX = new JLabel("X");
        lblX.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblX.setForeground(COLOR_ROJO);
        lblX.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        
        panel.add(lblCinemar);
        panel.add(lblX);
        
        return panel;
    }
}
