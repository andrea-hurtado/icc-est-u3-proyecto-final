package view;

import controller.MapController;
import model.Graph;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Mapa Centro Histórico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Graph model = new Graph();
        MapPanel mapPanel = new MapPanel(model);
        MapController controller = new MapController(model, mapPanel);

        Toolbar toolbar = new Toolbar(
            controller::onToolChanged,
            controller::onRunBFS,
            controller::onRunDFS,
            controller::onToggleMode,
            () -> controller.onSave(new File("data/mapa.cfg")),
            () -> controller.onLoad(new File("data/mapa.cfg")),
            controller::onClear
        );

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(mapPanel), BorderLayout.CENTER);

        File cfg = new File("data/mapa.cfg");
        if (cfg.exists()) controller.onLoad(cfg);
        else mapPanel.setBackgroundImage("images/mapa.png");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}