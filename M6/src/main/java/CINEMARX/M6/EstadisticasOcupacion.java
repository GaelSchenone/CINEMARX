package CINEMARX.M6;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    public EstadisticasOcupacion(M6 mainFrame, JPanel contentPanel) {
        this.mainFrame = mainFrame;
        this.contentPanel = contentPanel;
    }

    public void mostrar() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(M6.BACKGROUND_COLOR);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Título principal ---
        JLabel titleLabel = new JLabel("Estadísticas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(M6.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Panel del ComboBox ---
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        comboPanel.setBackground(M6.BACKGROUND_COLOR);
        comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel salaLabel = new JLabel("Seleccione una Sala: ");
        salaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        salaLabel.setForeground(M6.TEXT_COLOR);

        JComboBox<SalaItem> salaComboBox = new JComboBox<>();
        salaComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salaComboBox.setPreferredSize(new Dimension(250, 35));
        salaComboBox.setBackground(Color.WHITE);
        salaComboBox.setForeground(Color.BLACK);

        comboPanel.add(salaLabel);
        comboPanel.add(salaComboBox);

        // --- Subtítulo para la tabla ---
        JLabel datosLabel = new JLabel("Datos Función");
        datosLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        datosLabel.setForeground(M6.TEXT_COLOR);
        datosLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        datosLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // --- Panel de tabla ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(M6.BACKGROUND_COLOR);
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tablePanel.setMaximumSize(new Dimension(700, 200));
        tablePanel.setPreferredSize(new Dimension(700, 200));

        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // --- Subtítulo para el gráfico ---
        JLabel graficoLabel = new JLabel("Estadísticas de Función");
        graficoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        graficoLabel.setForeground(M6.TEXT_COLOR);
        graficoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        graficoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));

        // --- Panel del gráfico ---
        JPanel graficoPanel = new JPanel(new BorderLayout());
        graficoPanel.setBackground(M6.BACKGROUND_COLOR);
        graficoPanel.setPreferredSize(new Dimension(600, 300));
        graficoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Agregar componentes al contenedor ---
        container.add(titleLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(comboPanel);
        container.add(Box.createVerticalStrut(10));
        container.add(datosLabel);
        container.add(tablePanel);
        container.add(graficoLabel);
        container.add(graficoPanel);

        // Cargar salas en el ComboBox
        cargarSalas(salaComboBox);

        // Listener del ComboBox
        salaComboBox.addActionListener(e -> {
            SalaItem salaSeleccionada = (SalaItem) salaComboBox.getSelectedItem();
            if (salaSeleccionada != null) {
                cargarEstadisticasSala(salaSeleccionada.getId(), tableModel, graficoPanel);
            }
        });

        contentPanel.removeAll();
        contentPanel.add(container, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void cargarSalas(JComboBox<SalaItem> comboBox) {
        comboBox.removeAllItems();
        String query = "SELECT ID_Sala, Numero, TipoDeSala, CantButacas FROM Sala ORDER BY ID_Sala";

        try (PreparedStatement pstmt = mainFrame.getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("ID_Sala");
                String tipo = rs.getString("TipoDeSala");
                int butacas = rs.getInt("CantButacas");
                int numero = rs.getInt("Numero");
                comboBox.addItem(new SalaItem(id, numero, tipo, butacas));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error al cargar salas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEstadisticasSala(int idSala, DefaultTableModel tableModel, JPanel graficoPanel) {
        tableModel.setRowCount(0);

        String[] columnNames = {"ID Función", "Película", "Fecha", "Hora", "Butacas Ocup.", "Total Butac."};
        tableModel.setColumnIdentifiers(columnNames);

        String query = "SELECT f.ID_Funcion, p.Titulo, f.FechaFuncion, f.HoraFuncion, " +
                       "s.CantButacas, COUNT(b.ID_Boleto) AS ButacasOcupadas " +
                       "FROM Funcion f " +
                       "INNER JOIN Pelicula p ON f.ID_Pelicula = p.ID_Pelicula " +
                       "INNER JOIN Sala s ON f.ID_Sala = s.ID_Sala " +
                       "LEFT JOIN Boleto b ON f.ID_Funcion = b.ID_Funcion " +
                       "WHERE f.ID_Sala = ? " +
                       "GROUP BY f.ID_Funcion, p.Titulo, f.FechaFuncion, f.HoraFuncion, s.CantButacas " +
                       "ORDER BY f.FechaFuncion DESC, f.HoraFuncion DESC";

        int totalButacas = 0;
        int totalOcupadas = 0;

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

                totalButacas += cantButacas;
                totalOcupadas += butacasOcupadas;
            }

            if (tableModel.getRowCount() == 0) {
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

        // --- Gráfico de torta ---
        graficoPanel.removeAll();

        if (totalButacas > 0) {
            double porcentajeOcupado = (totalOcupadas * 100.0 / totalButacas);
            double porcentajeLibre = 100.0 - porcentajeOcupado;

            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Ocupadas (" + (int) porcentajeOcupado + "%)", totalOcupadas);
            dataset.setValue("Libres (" + (int) porcentajeLibre + "%)", totalButacas - totalOcupadas);

            JFreeChart chart = ChartFactory.createPieChart(
                    "Ocupación Total de la Sala", dataset, true, true, false);

            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setSectionPaint("Ocupadas (" + (int) porcentajeOcupado + "%)", new Color(100, 180, 255));
            plot.setSectionPaint("Libres (" + (int) porcentajeLibre + "%)", new Color(220, 220, 220));
            plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
            plot.setBackgroundPaint(M6.BACKGROUND_COLOR);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(500, 300));
            chartPanel.setBackground(M6.BACKGROUND_COLOR);

            graficoPanel.add(chartPanel, BorderLayout.CENTER);
        }

        graficoPanel.revalidate();
        graficoPanel.repaint();
    }
}
