package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BajaFuncion {
    
    private M6 mainFrame;
    private JPanel panel;
    
    public BajaFuncion(M6 mainFrame, JPanel panel) {
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

        JLabel infoLabel = UIHelpers.createLabel("Seleccione la función a eliminar:");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(infoLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JComboBox<FuncionItem> funcionCombo = new JComboBox<>();
        UIHelpers.styleComboBox(funcionCombo);
        funcionCombo.setMaximumSize(new Dimension(650, 35));
        funcionCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        funcionCombo.setForeground(Color.BLACK);
        DatabaseHelper.cargarFunciones(mainFrame, funcionCombo);
        formPanel.add(funcionCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton eliminarBtn = UIHelpers.createButton("Eliminar Función");
        eliminarBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(eliminarBtn);

        eliminarBtn.addActionListener(e -> {
            FuncionItem funcion = (FuncionItem) funcionCombo.getSelectedItem();
            if (funcion == null) {
                JOptionPane.showMessageDialog(mainFrame, "Seleccione una función", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(mainFrame, 
                "¿Está seguro de eliminar esta función?\n" + funcion.toString(), 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM Funcion WHERE ID_Funcion = ?";
                    try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
                        pstmt.setInt(1, funcion.getId());
                        pstmt.executeUpdate();
                        Logger.log(mainFrame.getConnection(), "Baja de Función: ID=" + funcion.getId());
                        JOptionPane.showMessageDialog(mainFrame, "Función eliminada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                        // Recargar ComboBox
                        funcionCombo.removeAllItems();
                        DatabaseHelper.cargarFunciones(mainFrame, funcionCombo);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(mainFrame, "Error al eliminar función: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        panel.add(formPanel);
    }
}