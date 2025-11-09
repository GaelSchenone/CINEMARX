package CINEMARX.M6;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

// JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot;

public class EstadisticasOcupacion {

    private M6 mainFrame;
    private JPanel contentPanel;
    private final String[] COLUMN_NAMES = {"ID Función", "Película", "Fecha", "Hora", "Butacas Ocup.", "Total Butac."};


    public EstadisticasOcupacion(M6 mainFrame, JPanel contentPanel) {
        this.mainFrame = mainFrame;
        this.contentPanel = contentPanel;
    }

    public void mostrar() {
        // Panel principal con BoxLayout (vertical)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(M6.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Título principal ---
        JLabel titleLabel = new JLabel("Estadísticas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(M6.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // --- Panel del ComboBox ---
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        comboPanel.setBackground(M6.BACKGROUND_COLOR);
        comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel salaLabel = new JLabel("Seleccione una Sala: ");
        salaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        salaLabel.setForeground(M6.TEXT_COLOR);

        JComboBox<SalaItem> salaComboBox = new JComboBox<>();
        salaComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salaComboBox.setPreferredSize(new Dimension(350, 35));
        salaComboBox.setBackground(M6.BUTTON_COLOR); // Styled
        salaComboBox.setForeground(M6.TEXT_COLOR); // Styled
        salaComboBox.setRenderer(new DefaultListCellRenderer() { // Styled
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(M6.ACCENT_COLOR.darker());
                    setForeground(Color.WHITE);
                } else {
                    setBackground(M6.BUTTON_COLOR);
                    setForeground(M6.TEXT_COLOR);
                }
                return this;
            }
        });
        salaComboBox.setBorder(BorderFactory.createLineBorder(M6.SIDEBAR_COLOR)); // Styled

        comboPanel.add(salaLabel);
        comboPanel.add(salaComboBox);
        mainPanel.add(comboPanel);

        // --- Subtítulo de tabla ---
        JLabel datosLabel = new JLabel("Datos de Función");
        datosLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        datosLabel.setForeground(M6.TEXT_COLOR);
        datosLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(datosLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // --- Tabla ---
        DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabla = new JTable(tableModel);
        tabla.setBackground(M6.SIDEBAR_COLOR); // Styled
        tabla.setForeground(M6.TEXT_COLOR); // Styled
        tabla.setSelectionBackground(M6.ACCENT_COLOR.darker()); // Styled
        tabla.setSelectionForeground(Color.WHITE); // Styled
        tabla.setGridColor(M6.BACKGROUND_COLOR); // Styled
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Styled
        tabla.setRowHeight(25); // Styled

        // Hide Table Header
        tabla.setTableHeader(null);

        JScrollPane tableScrollPane = new JScrollPane(tabla);
        tableScrollPane.setPreferredSize(new Dimension(700, 200));
        tableScrollPane.getViewport().setBackground(M6.BACKGROUND_COLOR); // Styled
        tableScrollPane.setBorder(BorderFactory.createLineBorder(M6.SIDEBAR_COLOR, 1)); // Styled
        mainPanel.add(tableScrollPane);
        mainPanel.add(Box.createVerticalStrut(20));

        // --- Subtítulo de gráfico ---
        JLabel estadisticasLabel = new JLabel("Estadísticas de Función");
        estadisticasLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        estadisticasLabel.setForeground(M6.TEXT_COLOR);
        estadisticasLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(estadisticasLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // --- Panel del gráfico ---
        JPanel graficoPanel = new JPanel(new BorderLayout());
        graficoPanel.setBackground(M6.BACKGROUND_COLOR);
        graficoPanel.setPreferredSize(new Dimension(700, 400));
        mainPanel.add(graficoPanel);

        // --- Scroll principal ---
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // --- Personalización del scrollbar (opcional, estilo oscuro) ---
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(80, 80, 80);
                this.trackColor = new Color(30, 30, 30);
            }
        });
        cargarSalas(salaComboBox);

        // Listener del ComboBox
        salaComboBox.addActionListener(e -> {
            SalaItem salaSeleccionada = (SalaItem) salaComboBox.getSelectedItem();
            if (salaSeleccionada != null) {
                cargarEstadisticasSala(salaSeleccionada.getId(), tableModel);
                graficoPanel.removeAll(); // Limpiar gráfico al cambiar de sala
                graficoPanel.revalidate();
                graficoPanel.repaint();
            }
        });

        // Listener de la tabla para el gráfico
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                int selectedRow = tabla.getSelectedRow();
                // Evitar el header si se hace click
                if (selectedRow == 0 && tableModel.getValueAt(0, 0).equals("ID Función")) {
                    return;
                }
                
                try {
                    int butacasOcupadas = Integer.parseInt(tableModel.getValueAt(selectedRow, 4).toString());
                    int totalButacas = Integer.parseInt(tableModel.getValueAt(selectedRow, 5).toString());
                    String pelicula = tableModel.getValueAt(selectedRow, 1).toString();
                    String fecha = tableModel.getValueAt(selectedRow, 2).toString();

                    crearGraficoOcupacion(graficoPanel, butacasOcupadas, totalButacas, pelicula, fecha);
                } catch (NumberFormatException ex) {
                    // No hacer nada si la fila no contiene números válidos (ej. "No hay funciones")
                }
            }
        });

        // --- Integración en el panel principal de la interfaz ---
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void cargarSalas(JComboBox<SalaItem> comboBox) {
        comboBox.removeAllItems();
        String query = "SELECT s.ID_Sala, s.Numero, s.TipoDeSala, s.CantButacas, c.Nombre AS CineNombre " +
                       "FROM Sala s " +
                       "JOIN Cine c ON s.ID_Cine = c.ID_Cine " +
                       "ORDER BY c.Nombre, s.Numero";

        try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("ID_Sala");
                String tipo = rs.getString("TipoDeSala");
                int butacas = rs.getInt("CantButacas");
                int numero = rs.getInt("Numero");
                String cineNombre = rs.getString("CineNombre");
                comboBox.addItem(new SalaItem(id, numero, tipo, butacas, cineNombre));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error al cargar salas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEstadisticasSala(int idSala, DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        tableModel.addRow(COLUMN_NAMES); // Add column names as the first row
        
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

                Object[] row = {idFuncion, titulo, fecha, hora, butacasOcupadas, cantButacas};
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 1) { // Only the header row exists
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

    private void crearGraficoOcupacion(JPanel graficoPanel, int butacasOcupadas, int totalButacas, String pelicula, String fecha) {
        graficoPanel.removeAll();

        if (totalButacas > 0) {
            double porcentajeOcupado = (butacasOcupadas * 100.0 / totalButacas);
            double porcentajeLibre = 100.0 - porcentajeOcupado;

            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Ocupadas (" + String.format("%.1f", porcentajeOcupado) + "%)", butacasOcupadas);
            dataset.setValue("Libres (" + String.format("%.1f", porcentajeLibre) + "%)", totalButacas - butacasOcupadas);

            JFreeChart chart = ChartFactory.createPieChart(
                    "Ocupación de Función: " + pelicula + " - " + fecha, dataset, true, true, false);

            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setSectionPaint("Ocupadas (" + String.format("%.1f", porcentajeOcupado) + "%)", new Color(100, 180, 255));
            plot.setSectionPaint("Libres (" + String.format("%.1f", porcentajeLibre) + "%)", new Color(220, 220, 220));
            plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
            plot.setBackgroundPaint(M6.BACKGROUND_COLOR);
            chart.getTitle().setPaint(M6.TEXT_COLOR);
            chart.getLegend().setBackgroundPaint(M6.BACKGROUND_COLOR);
            chart.getLegend().setItemPaint(M6.TEXT_COLOR);


            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(500, 300));
            chartPanel.setBackground(M6.BACKGROUND_COLOR);

            graficoPanel.add(chartPanel, BorderLayout.CENTER);
        }

        graficoPanel.revalidate();
        graficoPanel.repaint();
    }
}
