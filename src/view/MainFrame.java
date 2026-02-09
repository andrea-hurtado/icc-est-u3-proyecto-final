package view;

import controller.MapController;
import model.Graph;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame {

    private MapController controller;
    private Toolbar toolbar;

    public MainFrame() {
        super("Visualizador de Algoritmos BFS & DFS | Proyecto Final - Estructura de Datos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Graph model = new Graph();
        MapPanel mapPanel = new MapPanel(model);
        controller = new MapController(model, mapPanel);

        toolbar = new Toolbar(
            controller::onToolChanged,
            controller::onRunBFS,
            controller::onRunDFS,
            controller::onToggleMode,
            this::showTimeStats,
            () -> controller.onSave(new File("data/mapa.cfg")),
            () -> controller.onLoad(new File("data/mapa.cfg")),
            controller::onClear
        );
        
        controller.setToolbar(toolbar);

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(mapPanel), BorderLayout.CENTER);
        
        JPanel helpPanel = createHelpPanel();
        add(helpPanel, BorderLayout.SOUTH);

        File cfg = new File("data/mapa.cfg");
        if (cfg.exists()) {
            controller.onLoad(cfg);
        } else {
            mapPanel.setBackgroundImage("images/mapa.png");
        }

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createHelpPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(240, 240, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel help = new JLabel(
            "💡 Ayuda: 1) Agrega nodos | 2) Conéctalos | 3) Define Inicio (A) y Destino (B) | " +
            "4) Ejecuta BFS o DFS | 5) Usa el botón 'Modo' para alternar visualización"
        );
        help.setFont(help.getFont().deriveFont(Font.PLAIN, 11f));
        help.setForeground(new Color(80, 80, 80));
        panel.add(help);
        
        return panel;
    }
    
    private void showTimeStats() {
        TimeStatsDialog dialog = new TimeStatsDialog(this);
        dialog.setVisible(true);
    }
}