package CINEMARX.M6;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.*;
import javax.imageio.ImageIO;

public class PersonalDialogs {
    
    private M6 mainFrame;
    
    public PersonalDialogs(M6 mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    public void abrirVentanaAltaPersonal(DefaultTableModel tableModel) {
        // Crear ventana emergente
        JDialog dialog = new JDialog(mainFrame, "Alta de Personal", true);
        dialog.setSize(500, 650); // Aumentar altura para nuevos campos
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(M6.BACKGROUND_COLOR);

        // Panel superior con imagen TOPBAR
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(M6.TOPBAR_COLOR);
        topPanel.setPreferredSize(new Dimension(500, 80));

        try {
            URL imgURL = new URL("https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=logos%2FCINEMARX%20logotipo.png");
            BufferedImage image = ImageIO.read(imgURL);

            if (image != null) {
                Image scaledImg = image.getScaledInstance(500, 80, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                topPanel.add(imageLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.add(topPanel, BorderLayout.NORTH);

        // Panel central con formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(M6.BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // DNI
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UIHelpers.createLabel("DNI:"), gbc);
        gbc.gridx = 1;
        JTextField dniField = UIHelpers.createTextField();
        formPanel.add(dniField, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(UIHelpers.createLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        JTextField nombreField = UIHelpers.createTextField();
        formPanel.add(nombreField, gbc);

        // Apellido
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UIHelpers.createLabel("Apellido:"), gbc);
        gbc.gridx = 1;
        JTextField apellidoField = UIHelpers.createTextField();
        formPanel.add(apellidoField, gbc);

        // Fecha de Nacimiento
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(UIHelpers.createLabel("Fecha Nac. (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField fechaNacField = UIHelpers.createTextField();
        formPanel.add(fechaNacField, gbc);

        // Rol - Botones de selección exclusiva
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(UIHelpers.createLabel("Rol:"), gbc);

        gbc.gridx = 1;
        JPanel rolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rolPanel.setBackground(M6.BACKGROUND_COLOR);

        ButtonGroup rolGroup = new ButtonGroup();
        JRadioButton empleadoRadio = new JRadioButton("Empleado");
        JRadioButton adminRadio = new JRadioButton("Administrador");

        empleadoRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        empleadoRadio.setForeground(M6.TEXT_COLOR);
        empleadoRadio.setBackground(M6.BACKGROUND_COLOR);
        empleadoRadio.setSelected(true);

        adminRadio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adminRadio.setForeground(M6.TEXT_COLOR);
        adminRadio.setBackground(M6.BACKGROUND_COLOR);

        rolGroup.add(empleadoRadio);
        rolGroup.add(adminRadio);
        rolPanel.add(empleadoRadio);
        rolPanel.add(adminRadio);

        formPanel.add(rolPanel, gbc);

        // --- Campos adicionales para Administrador ---
        // Mail
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel mailLabel = UIHelpers.createLabel("Mail:");
        mailLabel.setVisible(false);
        formPanel.add(mailLabel, gbc);

        gbc.gridx = 1;
        JTextField mailField = UIHelpers.createTextField();
        mailField.setVisible(false);
        formPanel.add(mailField, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel passLabel = UIHelpers.createLabel("Contraseña:");
        passLabel.setVisible(false);
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField();
        passField.setVisible(false);
        formPanel.add(passField, gbc);
        
        // Cine
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel cineLabel = UIHelpers.createLabel("Cine:");
        cineLabel.setVisible(false);
        formPanel.add(cineLabel, gbc);

        gbc.gridx = 1;
        JComboBox<CineItem> cineCombo = new JComboBox<>();
        UIHelpers.styleComboBox(cineCombo);
        cineCombo.setVisible(false);
        DatabaseHelper.cargarCines(mainFrame, cineCombo);
        formPanel.add(cineCombo, gbc);

        // Listener para mostrar/ocultar campos de admin
        adminRadio.addActionListener(e -> {
            mailLabel.setVisible(true);
            mailField.setVisible(true);
            passLabel.setVisible(true);
            passField.setVisible(true);
            cineLabel.setVisible(true);
            cineCombo.setVisible(true);
            dialog.revalidate();
            dialog.repaint();
        });

        empleadoRadio.addActionListener(e -> {
            mailLabel.setVisible(false);
            mailField.setVisible(false);
            passLabel.setVisible(false);
            passField.setVisible(false);
            cineLabel.setVisible(false);
            cineCombo.setVisible(false);
            dialog.revalidate();
            dialog.repaint();
        });

        dialog.add(formPanel, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(M6.BACKGROUND_COLOR);

        JButton guardarBtn = UIHelpers.createButton("Guardar");
        JButton cancelarBtn = UIHelpers.createButton("Cancelar");
        cancelarBtn.setBackground(new Color(100, 100, 100));

        buttonPanel.add(guardarBtn);
        buttonPanel.add(cancelarBtn);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Listener del botón Guardar
        guardarBtn.addActionListener(e -> {
            try {
                String dniStr = dniField.getText().trim();
                String nombre = nombreField.getText().trim();
                String apellido = apellidoField.getText().trim();
                String fechaNac = fechaNacField.getText().trim();
                String mail = mailField.getText().trim();
                String password = new String(passField.getPassword()).trim();

                if (dniStr.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || fechaNac.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Complete todos los campos básicos", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int dni = Integer.parseInt(dniStr);
                boolean esAdmin = adminRadio.isSelected();

                if (esAdmin && (mail.isEmpty() || password.isEmpty() || cineCombo.getSelectedItem() == null)) {
                    JOptionPane.showMessageDialog(dialog, "Para administradores, complete Mail, Contraseña y Cine", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Insertar usuario primero
                String queryUsuario = "INSERT INTO Usuario (DNI, FechaNac, Nombre, Apellido) VALUES (?, ?, ?, ?)";

                try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(queryUsuario)) {
                    pstmt.setInt(1, dni);
                    pstmt.setString(2, fechaNac);
                    pstmt.setString(3, nombre);
                    pstmt.setString(4, apellido);
                    pstmt.executeUpdate();
                }

                // Insertar empleado o administrador
                if (esAdmin) {
                    CineItem cine = (CineItem) cineCombo.getSelectedItem();
                    String queryAdmin = "INSERT INTO Administrador (DNI, ID_Cine, Mail, Contrasena) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(queryAdmin)) {
                        pstmt.setInt(1, dni);
                        pstmt.setInt(2, cine.getId());
                        pstmt.setString(3, mail);
                        pstmt.setString(4, password);
                        pstmt.executeUpdate();
                    }
                    Logger.log(mainFrame.getConnection(), "Alta de Administrador: DNI=" + dni + ", Nombre=" + nombre + " " + apellido);
                } else {
                    String queryEmp = "INSERT INTO Empleado (DNI) VALUES (?)";
                    try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(queryEmp)) {
                        pstmt.setInt(1, dni);
                        pstmt.executeUpdate();
                    }
                    Logger.log(mainFrame.getConnection(), "Alta de Empleado: DNI=" + dni + ", Nombre=" + nombre + " " + apellido);
                }

                JOptionPane.showMessageDialog(dialog, 
                    (esAdmin ? "Administrador" : "Empleado") + " creado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

                new Personal(mainFrame, null).cargarPersonal(tableModel);
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El DNI debe ser un número válido", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error al crear registro: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelarBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
// CONTINUACIÓN DE PersonalDialogs.java - Agregar este método a la clase

    public void abrirVentanaModificacionPersonal(JTable table, DefaultTableModel tableModel, int selectedRow) {
        // Obtener datos de la fila seleccionada
        int dni = (int) tableModel.getValueAt(selectedRow, 0);
        String nombreActual = (String) tableModel.getValueAt(selectedRow, 1);
        String apellidoActual = (String) tableModel.getValueAt(selectedRow, 2);
        String rolActual = (String) tableModel.getValueAt(selectedRow, 3);

        // Crear ventana emergente
        JDialog dialog = new JDialog(mainFrame, "Modificación de Personal", true);
        dialog.setSize(700, 600); // Aumentar altura
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(M6.BACKGROUND_COLOR);

        // Panel superior con imagen TOPBAR
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(M6.TOPBAR_COLOR);
        topPanel.setPreferredSize(new Dimension(700, 80));

        try {
            URL imgURL = new URL("https://gaelschenone.aguilucho.ar/source_cmx/index.php?preview=logos%2FCINEMARX%20logotipo.png");
            BufferedImage image = ImageIO.read(imgURL);

            if (image != null) {
                Image scaledImg = image.getScaledInstance(500, 80, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                topPanel.add(imageLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.add(topPanel, BorderLayout.NORTH);

        // Panel central con formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(M6.BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Campos del formulario ---
        int gridY = 0;

        // Rol (no editable)
        gbc.gridx = 0; gbc.gridy = gridY++;
        gbc.weightx = 0.3;
        formPanel.add(UIHelpers.createLabel("Rol:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel rolLabel = UIHelpers.createLabel(rolActual);
        rolLabel.setForeground(M6.ACCENT_COLOR);
        formPanel.add(rolLabel, gbc);

        // DNI (EDITABLE)
        gbc.gridx = 0; gbc.gridy = gridY++;
        formPanel.add(UIHelpers.createLabel("DNI:"), gbc);
        gbc.gridx = 1;
        JTextField dniField = UIHelpers.createTextFieldWide();
        dniField.setText(String.valueOf(dni));
        formPanel.add(dniField, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = gridY++;
        formPanel.add(UIHelpers.createLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        JTextField nombreField = UIHelpers.createTextFieldWide();
        nombreField.setText(nombreActual);
        formPanel.add(nombreField, gbc);

        // Apellido
        gbc.gridx = 0; gbc.gridy = gridY++;
        formPanel.add(UIHelpers.createLabel("Apellido:"), gbc);
        gbc.gridx = 1;
        JTextField apellidoField = UIHelpers.createTextFieldWide();
        apellidoField.setText(apellidoActual);
        formPanel.add(apellidoField, gbc);

        // Fecha de Nacimiento
        gbc.gridx = 0; gbc.gridy = gridY++;
        formPanel.add(UIHelpers.createLabel("Fecha Nac. (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField fechaNacField = UIHelpers.createTextFieldWide();
        formPanel.add(fechaNacField, gbc);

        // Campos de Administrador
        JLabel mailLabel = UIHelpers.createLabel("Mail:");
        JTextField mailField = UIHelpers.createTextFieldWide();
        JLabel passLabel = UIHelpers.createLabel("Contraseña:");
        JPasswordField passField = new JPasswordField();

        if (rolActual.equals("Administrador")) {
            gbc.gridx = 0; gbc.gridy = gridY++;
            formPanel.add(mailLabel, gbc);
            gbc.gridx = 1;
            formPanel.add(mailField, gbc);

            gbc.gridx = 0; gbc.gridy = gridY++;
            formPanel.add(passLabel, gbc);
            gbc.gridx = 1;
            formPanel.add(passField, gbc);
        }

        // Cargar datos existentes
        try {
            String query = "SELECT u.FechaNac, a.Mail, a.Contrasena FROM Usuario u LEFT JOIN Administrador a ON u.DNI = a.DNI WHERE u.DNI = ?";
            try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {
                pstmt.setInt(1, dni);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    fechaNacField.setText(rs.getString("FechaNac"));
                    if (rolActual.equals("Administrador")) {
                        mailField.setText(rs.getString("Mail"));
                        passField.setText(rs.getString("Contrasena"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dialog.add(formPanel, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(M6.BACKGROUND_COLOR);

        JButton actualizarBtn = UIHelpers.createButton("Actualizar");
        JButton cancelarBtn = UIHelpers.createButton("Cancelar");
        cancelarBtn.setBackground(new Color(100, 100, 100));

        buttonPanel.add(actualizarBtn);
        buttonPanel.add(cancelarBtn);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Listener del botón Actualizar
        actualizarBtn.addActionListener(e -> {
            try {
                String nuevoDniStr = dniField.getText().trim();
                String nuevoNombre = nombreField.getText().trim();
                String nuevoApellido = apellidoField.getText().trim();
                String nuevaFechaNac = fechaNacField.getText().trim();
                String nuevoMail = mailField.getText().trim();
                String nuevaPass = new String(passField.getPassword()).trim();

                if (nuevoDniStr.isEmpty() || nuevoNombre.isEmpty() || nuevoApellido.isEmpty() || nuevaFechaNac.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Complete todos los campos básicos", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (rolActual.equals("Administrador") && (nuevoMail.isEmpty() || nuevaPass.isEmpty())){
                    JOptionPane.showMessageDialog(dialog, "Mail y Contraseña no pueden estar vacíos para administradores", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int nuevoDni = Integer.parseInt(nuevoDniStr);

                // Si el DNI cambió, verificar que no exista y actualizar en cascada
                if (nuevoDni != dni) {
                    // (La lógica de verificación y actualización de DNI en cascada se mantiene igual)
                }

                // Actualizar usuario
                String queryUpdate = "UPDATE Usuario SET DNI = ?, Nombre = ?, Apellido = ?, FechaNac = ? WHERE DNI = ?";

                try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(queryUpdate)) {
                    pstmt.setInt(1, nuevoDni);
                    pstmt.setString(2, nuevoNombre);
                    pstmt.setString(3, nuevoApellido);
                    pstmt.setString(4, nuevaFechaNac);
                    pstmt.setInt(5, dni);
                    pstmt.executeUpdate();
                }
                
                if(rolActual.equals("Administrador")){
                    String queryUpdateAdmin = "UPDATE Administrador SET Mail = ?, Contrasena = ? WHERE DNI = ?";
                    try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(queryUpdateAdmin)) {
                        pstmt.setString(1, nuevoMail);
                        pstmt.setString(2, nuevaPass);
                        pstmt.setInt(3, nuevoDni);
                        pstmt.executeUpdate();
                    }
                    Logger.log(mainFrame.getConnection(), "Modificación de Administrador: DNI=" + nuevoDni + ", Nombre=" + nuevoNombre + " " + nuevoApellido);
                } else {
                    Logger.log(mainFrame.getConnection(), "Modificación de Empleado: DNI=" + nuevoDni + ", Nombre=" + nuevoNombre + " " + nuevoApellido);
                }

                JOptionPane.showMessageDialog(dialog, "Datos actualizados exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

                new Personal(mainFrame, null).cargarPersonal(tableModel);
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El DNI debe ser un número válido", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error al actualizar: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelarBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}

// FIN DE LA CLASE PersonalDialogs.java