package view;

import model.TimeStats;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TimeStatsDialog extends JDialog {
    
    public TimeStatsDialog(JFrame parent) {
        super(parent, "Estadísticas de Tiempo - BFS vs DFS", true);
        setLayout(new BorderLayout(10, 10));
        
        // Tabla de resultados
        String[] columns = {"Timestamp", "Algoritmo", "Inicio", "Destino", 
                           "Nodos", "Aristas", "Tiempo (ms)", "Ruta", "Long. Ruta"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        List<TimeStats.ExecutionRecord> records = TimeStats.getAllRecords();
        for (TimeStats.ExecutionRecord r : records) {
            model.addRow(new Object[]{
                r.getFormattedTimestamp(),
                r.algorithm,
                r.start,
                r.end,
                r.nodes,
                r.edges,
                r.timeMs,
                r.foundPath ? "Sí" : "No",
                r.foundPath ? r.pathLength : "-"
            });
        }
        
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(60);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(7).setPreferredWidth(60);
        table.getColumnModel().getColumn(8).setPreferredWidth(90);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        
        // Panel de comparación
        JTextArea comparisonArea = new JTextArea(TimeStats.getComparison());
        comparisonArea.setEditable(false);
        comparisonArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        comparisonArea.setBackground(new Color(245, 245, 245));
        comparisonArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Comparación"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("Exportar CSV");
        exportButton.addActionListener(e -> exportToCSV());
        
        JButton clearButton = new JButton("Limpiar Historial");
        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de limpiar todo el historial?", 
                "Confirmar", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                TimeStats.clear();
                dispose();
                JOptionPane.showMessageDialog(this, "Historial limpiado");
            }
        });
        
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(exportButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(closeButton);
        
        // Layout principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(comparisonArea, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar estadísticas como CSV");
        fileChooser.setSelectedFile(new java.io.File("estadisticas_tiempos.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (java.io.PrintWriter pw = new java.io.PrintWriter(file)) {
                pw.println("timestamp,algorithm,start,end,nodes,edges,time_ms,found_path,path_length");
                for (TimeStats.ExecutionRecord r : TimeStats.getAllRecords()) {
                    pw.printf("%s,%s,%s,%s,%d,%d,%d,%s,%d%n",
                        r.getFormattedTimestamp(),
                        r.algorithm,
                        r.start,
                        r.end,
                        r.nodes,
                        r.edges,
                        r.timeMs,
                        r.foundPath,
                        r.foundPath ? r.pathLength : 0
                    );
                }
                JOptionPane.showMessageDialog(this, 
                    "Estadísticas exportadas exitosamente a:\n" + file.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al exportar: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}