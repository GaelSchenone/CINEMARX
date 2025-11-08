package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ModificacionFuncion {
    
    private M6 mainFrame;
    private JPanel panel;
    
    public ModificacionFuncion(M6 mainFrame, JPanel panel) {
        this.mainFrame = mainFrame;
        this.panel = panel;
    }
    
    public void mostrar() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setMaximumSize(new Dimension(700, 500));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = UIHelpers.createLabel("Seleccione la función a modificar:");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(infoLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JComboBox<FuncionItem> funcionCombo = new JComboBox<>();
        UIHelpers.styleComboBox(funcionCombo);
        funcionCombo.setMaximumSize(new Dimension(650, 35));
        funcionCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        DatabaseHelper.cargarFunciones(mainFrame, funcionCombo);
        formPanel.add(funcionCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        funcionCombo.setForeground(Color.BLACK);

        // Panel de formulario con campos
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(new Color(45, 45, 45));
        fieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(UIHelpers.createLabel("Hora:"), gbc);
        gbc.gridx = 1;
        JTextField horaField = UIHelpers.createTextField();
        fieldsPanel.add(horaField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(UIHelpers.createLabel("Fecha:"), gbc);
        gbc.gridx = 1;
        JTextField fechaField = UIHelpers.createTextField();
        fieldsPanel.add(fechaField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        fieldsPanel.add(UIHelpers.createLabel("Película:"), gbc);
        gbc.gridx = 1;
        JComboBox<PeliculaItem> peliculaCombo = new JComboBox<>();
        UIHelpers.styleComboBox(peliculaCombo);
        DatabaseHelper.cargarPeliculas(mainFrame, peliculaCombo);
        fieldsPanel.add(peliculaCombo, gbc);
        peliculaCombo.setForeground(Color.BLACK);

        gbc.gridx = 0; gbc.gridy = 4;
        fieldsPanel.add(UIHelpers.createLabel("Sala:"), gbc);
        gbc.gridx = 1;
        JComboBox<SalaItem> salaCombo = new JComboBox<>();
        UIHelpers.styleComboBox(salaCombo);
        DatabaseHelper.cargarSalas(mainFrame, salaCombo);
        fieldsPanel.add(salaCombo, gbc);
        salaCombo.setForeground(Color.BLACK);

        gbc.gridx = 0; gbc.gridy = 5;
        fieldsPanel.add(UIHelpers.createLabel("Cartelera:"), gbc);
        gbc.gridx = 1;
        JComboBox<CarteleraItem> carteleraCombo = new JComboBox<>();
        UIHelpers.styleComboBox(carteleraCombo);
        DatabaseHelper.cargarCarteleras(mainFrame, carteleraCombo);
        fieldsPanel.add(carteleraCombo, gbc);
        carteleraCombo.setForeground(Color.BLACK);

        formPanel.add(fieldsPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton actualizarBtn = UIHelpers.createButton("Actualizar Función");
        actualizarBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(actualizarBtn);

        // Listener para cargar datos al seleccionar función
        funcionCombo.addActionListener(e -> {
            FuncionItem funcion = (FuncionItem) funcionCombo.getSelectedItem();
            if (funcion != null) {
                cargarDatosFuncion(funcion.getId(), horaField, fechaField, 
                                  peliculaCombo, salaCombo, carteleraCombo);
            }
        });

        // Cargar datos iniciales
        if (funcionCombo.getItemCount() > 0) {
            FuncionItem firstFuncion = funcionCombo.getItemAt(0);
            cargarDatosFuncion(firstFuncion.getId(), horaField, fechaField, 
                              peliculaCombo, salaCombo, carteleraCombo);
        }

        actualizarBtn.addActionListener(e -> {
            FuncionItem funcion = (FuncionItem) funcionCombo.getSelectedItem();
            if (funcion == null) {
                JOptionPane.showMessageDialog(mainFrame, "Seleccione una función", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Obtener el ID de la película original
                int oldPeliculaId = getOldPeliculaId(funcion.getId());

                String query = "UPDATE Funcion SET HoraFuncion = ?, FechaFuncion = ?, " +
                              "ID_Pelicula = ?, ID_Sala = ?, ID_Cartelera = ? WHERE ID_Funcion = ?";

                try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
                    pstmt.setString(1, horaField.getText().trim());
                    pstmt.setString(2, fechaField.getText().trim());
                    int newPeliculaId = ((PeliculaItem) peliculaCombo.getSelectedItem()).getId();
                    pstmt.setInt(3, newPeliculaId);
                    pstmt.setInt(4, ((SalaItem) salaCombo.getSelectedItem()).getId());
                    pstmt.setInt(5, ((CarteleraItem) carteleraCombo.getSelectedItem()).getId());
                    pstmt.setInt(6, funcion.getId());

                    pstmt.executeUpdate();

                    // Actualizar estado de la nueva película a "En Cartelera"
                    updatePeliculaStatus(newPeliculaId, "En Cartelera");

                    // Verificar si la película anterior debe cambiar su estado
                    if (oldPeliculaId != -1 && oldPeliculaId != newPeliculaId) {
                        if (!DatabaseHelper.isPeliculaInFuncion(mainFrame.getConnection(), oldPeliculaId)) {
                            updatePeliculaStatus(oldPeliculaId, "Proximamente");
                        }
                    }

                    Logger.log(mainFrame.getConnection(), "Modificación de Función: ID=" + funcion.getId());
                    JOptionPane.showMessageDialog(mainFrame, "Función actualizada y estados de películas actualizados", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    // Recargar ComboBox
                    funcionCombo.removeAllItems();
                    DatabaseHelper.cargarFunciones(mainFrame, funcionCombo);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error al actualizar función: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        panel.add(formPanel);
    }
    
    private void cargarDatosFuncion(int idFuncion, JTextField horaField, JTextField fechaField, 
                                    JComboBox<PeliculaItem> peliculaCombo, 
                                    JComboBox<SalaItem> salaCombo, JComboBox<CarteleraItem> carteleraCombo) {
        try {
            String query = "SELECT HoraFuncion, FechaFuncion, ID_Pelicula, ID_Sala, ID_Cartelera " +
                          "FROM Funcion WHERE ID_Funcion = ?";

            try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
                pstmt.setInt(1, idFuncion);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    horaField.setText(rs.getString("HoraFuncion"));
                    fechaField.setText(rs.getString("FechaFuncion"));

                    // Seleccionar película
                    int idPelicula = rs.getInt("ID_Pelicula");
                    for (int i = 0; i < peliculaCombo.getItemCount(); i++) {
                        if (peliculaCombo.getItemAt(i).getId() == idPelicula) {
                            peliculaCombo.setSelectedIndex(i);
                            break;
                        }
                    }

                    // Seleccionar sala
                    int idSala = rs.getInt("ID_Sala");
                    for (int i = 0; i < salaCombo.getItemCount(); i++) {
                        if (salaCombo.getItemAt(i).getId() == idSala) {
                            salaCombo.setSelectedIndex(i);
                            break;
                        }
                    }

                    // Seleccionar cartelera
                    int idCartelera = rs.getInt("ID_Cartelera");
                    for (int i = 0; i < carteleraCombo.getItemCount(); i++) {
                        if (carteleraCombo.getItemAt(i).getId() == idCartelera) {
                            carteleraCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error al cargar datos de función: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private int getOldPeliculaId(int idFuncion) throws SQLException {
        String query = "SELECT ID_Pelicula FROM Funcion WHERE ID_Funcion = ?";
        try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, idFuncion);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID_Pelicula");
            }
        }
        return -1;
    }

    private void updatePeliculaStatus(int idPelicula, String status) throws SQLException {
        String query = "UPDATE Pelicula SET Estado = ? WHERE ID_Pelicula = ?";
        try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, idPelicula);
            pstmt.executeUpdate();
        }
    }
}