package CINEMARX.M6;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;

public class EstadisticasOcupacion {
    
    private M6 mainFrame;
    private JPanel contentPanel;
    
    public EstadisticasOcupacion(M6 mainFrame, JPanel contentPanel) {
        this.mainFrame = mainFrame;
        this.contentPanel = contentPanel;
    }
    
    public void mostrar() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(M6.BACKGROUND_COLOR);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Estadísticas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(M6.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Panel para el ComboBox
        JPanel comboPanel = new JPanel();
        comboPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 10));
        comboPanel.setBackground(M6.BACKGROUND_COLOR);
        comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel salaLabel = new JLabel("Seleccione una Sala: ");
        salaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        salaLabel.setForeground(M6.TEXT_COLOR);
        
        JComboBox<SalaItem> salaComboBox = new JComboBox<>();
        salaComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salaComboBox.setPreferredSize(new Dimension(200, 35));
        salaComboBox.setBackground(Color.WHITE);
        salaComboBox.setForeground(Color.BLACK);
        
        comboPanel.add(salaLabel);
        comboPanel.add(salaComboBox);
        
        // Panel para la tabla de estadísticas
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(M6.BACKGROUND_COLOR);
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tablePanel.setMaximumSize(new Dimension(700, 200));
        tablePanel.setPreferredSize(new Dimension(700, 200));
        
        // Crear tabla
        String[] columnNames = {"ID Función", "Película", "Fecha", "Hora", "Butacas Ocupadas", "Total Butacas"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Agregar la primera fila con los nombres de las columnas
        Object[] headerRow = {"ID Función", "Película", "Fecha", "Hora", "Butacas Ocup.", "Total Butac."};
        tableModel.addRow(headerRow);
        
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(M6.TEXT_COLOR);
        table.setBackground(new Color(40, 40, 40));
        table.setGridColor(new Color(60, 60, 60));
        table.setRowHeight(30);
        table.setTableHeader(null);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(M6.BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Cargar salas en el ComboBox
        DatabaseHelper.cargarSalas(mainFrame, salaComboBox);
        
        // Listener para cambios en el ComboBox
        salaComboBox.addActionListener(e -> {
            SalaItem selectedSala = (SalaItem) salaComboBox.getSelectedItem();
            if (selectedSala != null) {
                cargarEstadisticasSala(selectedSala.getId(), tableModel);
            }
        });
        
        // Agregar componentes al contenedor
        container.add(titleLabel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(comboPanel);
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        container.add(tablePanel);
        
        contentPanel.add(container);
        
        // Cargar datos iniciales si hay salas
        if (salaComboBox.getItemCount() > 0) {
            SalaItem firstSala = salaComboBox.getItemAt(0);
            cargarEstadisticasSala(firstSala.getId(), tableModel);
        }
    }
    
    private void cargarEstadisticasSala(int idSala, DefaultTableModel tableModel) {
        tableModel.setRowCount(0);

        // Eliminamos la última columna ("Ocupación %")
        Object[] headerRow = {"ID Función", "Película", "Fecha", "Hora", "Butacas Ocup.", "Total Butac."};
        tableModel.addRow(headerRow);

        String query = "SELECT f.ID_Funcion, p.Titulo, f.FechaFuncion, f.HoraFuncion, " +
                       "s.CantButacas, COUNT(b.ID_Boleto) AS ButacasOcupadas " +
                       "FROM Funcion f " +
                       "INNER JOIN Pelicula p ON f.ID_Pelicula = p.ID_Pelicula " +
                       "INNER JOIN Sala s ON f.ID_Sala = s.ID_Sala " +
                       "LEFT JOIN Boleto b ON f.ID_Funcion = b.ID_Funcion " +
                       "WHERE f.ID_Sala = ? " +
                       "GROUP BY f.ID_Funcion, p.Titulo, f.FechaFuncion, f.HoraFuncion, s.CantButacas " +
                       "ORDER BY f.FechaFuncion DESC, f.HoraFuncion DESC";

        try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query)) {

            pstmt.setInt(1, idSala);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int idFuncion = rs.getInt("ID_Funcion");
                String titulo = rs.getString("Titulo");
                String fecha = rs.getString("FechaFuncion");
                String hora = rs.getString("HoraFuncion");
                int cantButacas = rs.getInt("CantButacas");
                int butacasOcupadas = rs.getInt("ButacasOcupadas");

                Object[] row = {
                    idFuncion,
                    titulo,
                    fecha,
                    hora,
                    butacasOcupadas,
                    cantButacas
                };

                tableModel.addRow(row);
            }

            // Si no hay resultados
            if (tableModel.getRowCount() == 1) {
                Object[] emptyRow = {"---", "No hay funciones para esta sala", "---", "---", "---", "---"};
                tableModel.addRow(emptyRow);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error al cargar estadísticas: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}