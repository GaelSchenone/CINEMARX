package CINEMARX.M6;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AltaSala extends JDialog {

    private M6 mainFrame;
    private JComboBox<String> tipoSalaCombo;

    public AltaSala(M6 mainFrame) {
        super(mainFrame, "Alta de Sala", true);
        this.mainFrame = mainFrame;
        initComponents();
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
        JTextField numeroSalaField = UIHelpers.createTextField();
        numeroSalaField.setEditable(false);
        formPanel.add(numeroSalaField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UIHelpers.createLabel("Cantidad de Butacas:"), gbc);
        gbc.gridx = 1;
        JTextField butacasField = UIHelpers.createTextField();
        butacasField.setText("100");
        butacasField.setEditable(false);
        formPanel.add(butacasField, gbc);

        // Obtener el próximo número de sala
        try {
            numeroSalaField.setText(String.valueOf(getNextNumeroSala()));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener el número de sala: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(M6.BACKGROUND_COLOR);
        JButton guardarBtn = UIHelpers.createButton("Guardar");
        buttonPanel.add(guardarBtn);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        guardarBtn.addActionListener(e -> guardarSala());
    }

    private int getNextNumeroSala() throws SQLException {
        String query = "SELECT MAX(Numero) FROM Sala WHERE ID_Cine = 1";
        try (Statement stmt = mainFrame.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        }
        return 1; // Si no hay salas, empezar en 1
    }

    private void guardarSala() {
        try {
            String tipoSala = (String) tipoSalaCombo.getSelectedItem();
            int numeroSala = getNextNumeroSala();

            String query = "INSERT INTO Sala (Numero, CantButacas, TipoDeSala, ID_Cine) VALUES (?, 100, ?, 1)";
            try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, numeroSala);
                pstmt.setString(2, tipoSala);
                pstmt.executeUpdate();

                // Obtener el ID de la sala insertada
                int idSala = -1;
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idSala = generatedKeys.getInt(1);
                }

                Logger.log(mainFrame.getConnection(), "Alta de Sala: ID=" + idSala + ", Número=" + numeroSala);
                JOptionPane.showMessageDialog(this, "Sala creada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al crear la sala: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
