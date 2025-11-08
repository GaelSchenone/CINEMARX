package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AltaFuncion {
    
    private M6 mainFrame;
    private JPanel panel;
    
    public AltaFuncion(M6 mainFrame, JPanel panel) {
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
        formPanel.setMaximumSize(new Dimension(700, 400));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hora
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UIHelpers.createLabel("Hora (HH:MM:SS):"), gbc);
        gbc.gridx = 1;
        JTextField horaField = UIHelpers.createTextField();
        formPanel.add(horaField, gbc);

        // Fecha
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(UIHelpers.createLabel("Fecha (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField fechaField = UIHelpers.createTextField();
        formPanel.add(fechaField, gbc);

        // Película
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(UIHelpers.createLabel("Película:"), gbc);
        gbc.gridx = 1;
        JComboBox<PeliculaItem> peliculaCombo = new JComboBox<>();
        UIHelpers.styleComboBox(peliculaCombo);
        DatabaseHelper.cargarPeliculas(mainFrame, peliculaCombo);
        formPanel.add(peliculaCombo, gbc);
        peliculaCombo.setForeground(Color.BLACK);

        // Sala
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(UIHelpers.createLabel("Sala:"), gbc);
        gbc.gridx = 1;
        JComboBox<SalaItem> salaCombo = new JComboBox<>();
        UIHelpers.styleComboBox(salaCombo);
        DatabaseHelper.cargarSalas(mainFrame, salaCombo);
        formPanel.add(salaCombo, gbc);
        salaCombo.setForeground(Color.BLACK);

        // Cartelera
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(UIHelpers.createLabel("Cartelera:"), gbc);
        gbc.gridx = 1;
        JComboBox<CarteleraItem> carteleraCombo = new JComboBox<>();
        UIHelpers.styleComboBox(carteleraCombo);
        DatabaseHelper.cargarCarteleras(mainFrame, carteleraCombo);
        formPanel.add(carteleraCombo, gbc);
        carteleraCombo.setForeground(Color.BLACK);

        // Botón
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JButton guardarBtn = UIHelpers.createButton("Guardar Función");
        formPanel.add(guardarBtn, gbc);

        guardarBtn.addActionListener(e -> {
            try {
                String hora = horaField.getText().trim();
                String fecha = fechaField.getText().trim();
                PeliculaItem pelicula = (PeliculaItem) peliculaCombo.getSelectedItem();
                SalaItem sala = (SalaItem) salaCombo.getSelectedItem();
                CarteleraItem cartelera = (CarteleraItem) carteleraCombo.getSelectedItem();

                if (hora.isEmpty() || fecha.isEmpty() || pelicula == null || sala == null || cartelera == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Por favor complete todos los campos obligatorios", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String query = "INSERT INTO Funcion (HoraFuncion, FechaFuncion, ID_Pelicula, ID_Sala, ID_Cartelera) " +
                              "VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, hora);
                    pstmt.setString(2, fecha);
                    pstmt.setInt(3, pelicula.getId());
                    pstmt.setInt(4, sala.getId());
                    pstmt.setInt(5, cartelera.getId());

                    pstmt.executeUpdate();

                    int idFuncion = -1;
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        idFuncion = generatedKeys.getInt(1);
                    }

                    // Actualizar estado de la película a "En Cartelera"
                    String updateQuery = "UPDATE Pelicula SET Estado = 'En Cartelera' WHERE ID_Pelicula = ?";
                    try (PreparedStatement updateStmt = mainFrame.getConnection().prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, pelicula.getId());
                        updateStmt.executeUpdate();
                    }

                    Logger.log(mainFrame.getConnection(), "Alta de Función: ID=" + idFuncion);
                    JOptionPane.showMessageDialog(mainFrame, "Función creada y película actualizada a 'En Cartelera'", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    // Limpiar campos
                    horaField.setText("");
                    fechaField.setText("");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error al crear función: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        panel.add(formPanel);
    }
}