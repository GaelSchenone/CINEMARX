package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class ABM {
    
    private M6 mainFrame;
    private JPanel contentPanel;
    
    public ABM(M6 mainFrame, JPanel contentPanel) {
        this.mainFrame = mainFrame;
        this.contentPanel = contentPanel;
    }
    
    public void mostrar() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(M6.BACKGROUND_COLOR);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título principal
        JLabel titleLabel = new JLabel("ABM - Alta, Baja, Modificación");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(M6.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel de selección
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        selectionPanel.setBackground(M6.BACKGROUND_COLOR);
        selectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel operacionLabel = new JLabel("Operación:");
        operacionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        operacionLabel.setForeground(M6.TEXT_COLOR);

        String[] operaciones = {"Alta", "Baja", "Modificación"};
        JComboBox<String> operacionComboBox = new JComboBox<>(operaciones);
        operacionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        operacionComboBox.setPreferredSize(new Dimension(150, 35));
        operacionComboBox.setBackground(new Color(50, 50, 50));
        operacionComboBox.setForeground(Color.BLACK);

        JLabel entidadLabel = new JLabel("Entidad:");
        entidadLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        entidadLabel.setForeground(M6.TEXT_COLOR);

        String[] entidades = {"Función", "Película"};
        JComboBox<String> entidadComboBox = new JComboBox<>(entidades);
        entidadComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        entidadComboBox.setPreferredSize(new Dimension(150, 35));
        entidadComboBox.setBackground(new Color(50, 50, 50));
        entidadComboBox.setForeground(Color.BLACK);

        selectionPanel.add(operacionLabel);
        selectionPanel.add(operacionComboBox);
        selectionPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        selectionPanel.add(entidadLabel);
        selectionPanel.add(entidadComboBox);

        // Panel de contenido dinámico
        JPanel dynamicPanel = new JPanel();
        dynamicPanel.setLayout(new BoxLayout(dynamicPanel, BoxLayout.Y_AXIS));
        dynamicPanel.setBackground(M6.BACKGROUND_COLOR);
        dynamicPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Listener para cambios en los ComboBox
        ActionListener updateListener = e -> {
            String operacion = (String) operacionComboBox.getSelectedItem();
            String entidad = (String) entidadComboBox.getSelectedItem();
            actualizarPanelABM(dynamicPanel, operacion, entidad);
        };

        operacionComboBox.addActionListener(updateListener);
        entidadComboBox.addActionListener(updateListener);

        // Agregar componentes al contenedor principal
        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(selectionPanel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(dynamicPanel);

        contentPanel.add(container);

        // Cargar contenido inicial
        actualizarPanelABM(dynamicPanel, "Alta", "Función");
    }

    private void actualizarPanelABM(JPanel dynamicPanel, String operacion, String entidad) {
        dynamicPanel.removeAll();

        if (operacion.equals("Alta")) {
            if (entidad.equals("Función")) {
                new AltaFuncion(mainFrame, dynamicPanel).mostrar();
            } else {
                new AltaPelicula(mainFrame, dynamicPanel).mostrar();
            }
        } else if (operacion.equals("Baja")) {
            if (entidad.equals("Función")) {
                new BajaFuncion(mainFrame, dynamicPanel).mostrar();
            } else {
                new BajaPelicula(mainFrame, dynamicPanel).mostrar();
            }
        } else if (operacion.equals("Modificación")) {
            if (entidad.equals("Función")) {
                new ModificacionFuncion(mainFrame, dynamicPanel).mostrar();
            } else {
                new ModificacionPelicula(mainFrame, dynamicPanel).mostrar();
            }
        }

        dynamicPanel.revalidate();
        dynamicPanel.repaint();
    }
}