package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BajaPelicula {
    
    private M6 mainFrame;
    private JPanel panel;
    
    public BajaPelicula(M6 mainFrame, JPanel panel) {
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
        formPanel.setMaximumSize(new Dimension(700, 200));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = UIHelpers.createLabel("Seleccione la película a eliminar:");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(infoLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JComboBox<PeliculaItem> peliculaCombo = new JComboBox<>();
        UIHelpers.styleComboBox(peliculaCombo);
        peliculaCombo.setMaximumSize(new Dimension(650, 35));
        peliculaCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        DatabaseHelper.cargarPeliculas(mainFrame, peliculaCombo);
        formPanel.add(peliculaCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton eliminarBtn = UIHelpers.createButton("Eliminar Película");
        eliminarBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(eliminarBtn);

        eliminarBtn.addActionListener(e -> {
            PeliculaItem pelicula = (PeliculaItem) peliculaCombo.getSelectedItem();
            if (pelicula == null) {
                JOptionPane.showMessageDialog(mainFrame, "Seleccione una película", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(mainFrame, 
                "¿Está seguro de eliminar esta película?\n" + pelicula.toString(), 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM Pelicula WHERE ID_Pelicula = ?";
                    try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
                        pstmt.setInt(1, pelicula.getId());
                        pstmt.executeUpdate();
                        Logger.log(mainFrame.getConnection(), "Baja de Película: ID=" + pelicula.getId() + ", Título=" + pelicula.toString());
                        JOptionPane.showMessageDialog(mainFrame, "Película eliminada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                        // Recargar ComboBox
                        peliculaCombo.removeAllItems();
                        DatabaseHelper.cargarPeliculas(mainFrame, peliculaCombo);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(mainFrame, "Error al eliminar película: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        panel.add(formPanel);
    }
}