package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AltaPelicula {
    
    private M6 mainFrame;
    private JPanel panel;
    
    public AltaPelicula(M6 mainFrame, JPanel panel) {
        this.mainFrame = mainFrame;
        this.panel = panel;
    }
    
    public void mostrar() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setMaximumSize(new Dimension(700, 300));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UIHelpers.createLabel("Título:"), gbc);
        gbc.gridx = 1;
        JTextField tituloField = UIHelpers.createTextField();
        formPanel.add(tituloField, gbc);

        // Género
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(UIHelpers.createLabel("Género:"), gbc);
        gbc.gridx = 1;
        JTextField generoField = UIHelpers.createTextField();
        formPanel.add(generoField, gbc);

        // Clasificación
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UIHelpers.createLabel("Clasificación de Edad:"), gbc);
        gbc.gridx = 1;
        String[] clasificaciones = {"ATP", "ATP13", "ATP16", "R18"};
        JComboBox<String> clasificacionCombo = new JComboBox<>(clasificaciones);
        UIHelpers.styleComboBox(clasificacionCombo);
        formPanel.add(clasificacionCombo, gbc);
        clasificacionCombo.setForeground(Color.BLACK);

        // Botón
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton guardarBtn = UIHelpers.createButton("Guardar Película");
        formPanel.add(guardarBtn, gbc);

        guardarBtn.addActionListener(e -> {
            try {
                String titulo = tituloField.getText().trim();
                String genero = generoField.getText().trim();
                String clasificacion = (String) clasificacionCombo.getSelectedItem();

                if (titulo.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "El título es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (clasificacion == null || clasificacion.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "La clasificación de edad es obligatoria", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Generar nombre de imagen automáticamente
                String imagenNombre = UIHelpers.generarNombreImagen(titulo);

                String query = "INSERT INTO Pelicula (Titulo, Genero, ClasificacionEdad, Estado, Imagen) VALUES (?, ?, ?, 'Proximamente', ?)";

                try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
                    pstmt.setString(1, titulo);
                    pstmt.setString(2, genero.isEmpty() ? null : genero);
                    pstmt.setString(3, clasificacion);
                    pstmt.setString(4, imagenNombre);

                    pstmt.executeUpdate();
                    Logger.log(mainFrame.getConnection(), "Alta de Película: Título=" + titulo);
                    JOptionPane.showMessageDialog(mainFrame, "Película creada exitosamente con estado 'Proximamente'", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    // Limpiar campos
                    tituloField.setText("");
                    generoField.setText("");
                    clasificacionCombo.setSelectedIndex(0);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error al crear película: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        panel.add(formPanel);
    }
}