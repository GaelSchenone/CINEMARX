package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModificacionSala extends JDialog {

    private M6 mainFrame;
    private int idSala;
    private JComboBox<String> tipoSalaCombo;
    private JTextField numeroSalaField;
    private JTextField butacasField;

    public ModificacionSala(M6 mainFrame, int idSala) {
        super(mainFrame, "Modificación de Sala", true);
        this.mainFrame = mainFrame;
        this.idSala = idSala;
        initComponents();
        cargarDatosSala();
    }

    private void initComponents() {
        setSize(400, 250);
        setLocationRelativeTo(mainFrame);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(M6.BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UIHelpers.createLabel("Tipo de Sala:"), gbc);
        gbc.gridx = 1;
        String[] tipos = {"2D", "3D", "IMAX"};
        tipoSalaCombo = new JComboBox<>(tipos);
        UIHelpers.styleComboBox(tipoSalaCombo);
        formPanel.add(tipoSalaCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(UIHelpers.createLabel("Número de Sala:"), gbc);
        gbc.gridx = 1;
        numeroSalaField = UIHelpers.createTextField();
        numeroSalaField.setEditable(false);
        formPanel.add(numeroSalaField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UIHelpers.createLabel("Cantidad de Butacas:"), gbc);
        gbc.gridx = 1;
        butacasField = UIHelpers.createTextField();
        butacasField.setEditable(false);
        formPanel.add(butacasField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(M6.BACKGROUND_COLOR);
        JButton actualizarBtn = UIHelpers.createButton("Actualizar");
        buttonPanel.add(actualizarBtn);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        actualizarBtn.addActionListener(e -> actualizarSala());
    }

    private void cargarDatosSala() {
        try {
            String query = "SELECT Numero, TipoDeSala, CantButacas FROM Sala WHERE ID_Sala = ?";
            try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
                pstmt.setInt(1, idSala);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    numeroSalaField.setText(String.valueOf(rs.getInt("Numero")));
                    tipoSalaCombo.setSelectedItem(rs.getString("TipoDeSala"));
                    butacasField.setText(String.valueOf(rs.getInt("CantButacas")));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos de la sala: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void actualizarSala() {
        try {
            String tipoSala = (String) tipoSalaCombo.getSelectedItem();

            String query = "UPDATE Sala SET TipoDeSala = ? WHERE ID_Sala = ?";
            try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
                pstmt.setString(1, tipoSala);
                pstmt.setInt(2, idSala);
                pstmt.executeUpdate();
                Logger.log(mainFrame.getConnection(), "Modificación de Sala: ID=" + idSala + ", Número=" + numeroSalaField.getText());
                JOptionPane.showMessageDialog(this, "Sala actualizada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar la sala: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
