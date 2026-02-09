package view;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Toolbar extends JPanel {

    public enum Tool {
        SELECT, ADD_NODE, CONNECT, SET_START, SET_END, BLOCK
    }

    private Tool currentTool = Tool.SELECT;
    private JButton modeButton;

    public Toolbar(
        Consumer<Tool> onToolChange,
        Runnable onRunBFS,
        Runnable onRunDFS,
        Runnable onToggleMode,
        Runnable onShowStats,
        Runnable onSave,
        Runnable onLoad,
        Runnable onClear
    ) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
        setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

        // === HERRAMIENTAS ===
        addLabel("Herramientas:");
        ButtonGroup group = new ButtonGroup();

        addToolButton("✋ Seleccionar", Tool.SELECT, group, onToolChange, true);
        addToolButton("➕ Agregar Nodo", Tool.ADD_NODE, group, onToolChange, false);
        addToolButton("🔗 Conectar", Tool.CONNECT, group, onToolChange, false);
        addToolButton("🚫 Bloquear", Tool.BLOCK, group, onToolChange, false);
        addToolButton("🟢 Inicio (A)", Tool.SET_START, group, onToolChange, false);
        addToolButton("🔵 Destino (B)", Tool.SET_END, group, onToolChange, false);

        add(createSeparator());

        // === ALGORITMOS ===
        addLabel("Algoritmos:");
        
        JButton bfs = new JButton("▶ BFS");
        bfs.setToolTipText("Búsqueda en Anchura - Camino más corto");
        bfs.setBackground(new Color(60, 200, 60));
        bfs.setForeground(Color.WHITE);
        bfs.setFocusPainted(false);
        bfs.addActionListener(e -> onRunBFS.run());
        add(bfs);

        JButton dfs = new JButton("▶ DFS");
        dfs.setToolTipText("Búsqueda en Profundidad");
        dfs.setBackground(new Color(60, 120, 230));
        dfs.setForeground(Color.WHITE);
        dfs.setFocusPainted(false);
        dfs.addActionListener(e -> onRunDFS.run());
        add(dfs);

        add(createSeparator());

        // === VISUALIZACIÓN ===
        addLabel("Vista:");
        
        modeButton = new JButton("👁 Modo: Exploración");
        modeButton.setToolTipText("Click para cambiar entre Modo Exploración y Modo Ruta Final");
        modeButton.setBackground(new Color(100, 100, 200));
        modeButton.setForeground(Color.WHITE);
        modeButton.setFocusPainted(false);
        modeButton.addActionListener(e -> {
            onToggleMode.run();
            updateModeButtonText();
        });
        add(modeButton);
        
        JButton stats = new JButton("📊 Ver Tiempos");
        stats.setToolTipText("Ver estadísticas de ejecución BFS vs DFS");
        stats.setFocusPainted(false);
        stats.addActionListener(e -> onShowStats.run());
        add(stats);

        add(createSeparator());

        // === ARCHIVO ===
        addLabel("Archivo:");
        
        JButton save = new JButton("💾 Guardar");
        save.setToolTipText("Guardar configuración del grafo");
        save.setFocusPainted(false);
        save.addActionListener(e -> onSave.run());
        add(save);
        
        JButton load = new JButton("📂 Cargar");
        load.setToolTipText("Cargar configuración guardada");
        load.setFocusPainted(false);
        load.addActionListener(e -> onLoad.run());
        add(load);

        JButton clear = new JButton("🗑 Limpiar");
        clear.setToolTipText("Limpiar todo el grafo");
        clear.setForeground(new Color(200, 50, 50));
        clear.setFocusPainted(false);
        clear.addActionListener(e -> onClear.run());
        add(clear);
    }

    private void addLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 10f));
        label.setForeground(new Color(100, 100, 100));
        add(label);
    }

    private JPanel createSeparator() {
        JPanel sep = new JPanel();
        sep.setPreferredSize(new Dimension(2, 30));
        sep.setBackground(new Color(200, 200, 200));
        return sep;
    }

    private void addToolButton(String label, Tool tool, ButtonGroup group,
                               Consumer<Tool> onToolChange, boolean selected) {
        JToggleButton btn = new JToggleButton(label);
        btn.setSelected(selected);
        btn.setFocusPainted(false);
        if (selected) {
            btn.setBackground(new Color(220, 220, 255));
        }
        group.add(btn);
        add(btn);
        btn.addActionListener(e -> {
            currentTool = tool;
            onToolChange.accept(tool);
        });
    }
    
    public void setModeText(String modeText) {
        if (modeButton != null) {
            modeButton.setText("👁 Modo: " + modeText);
        }
    }
    
    private void updateModeButtonText() {
        String currentText = modeButton.getText();
        if (currentText.contains("Exploración")) {
            modeButton.setText("👁 Modo: Ruta Final");
            modeButton.setBackground(new Color(200, 100, 100));
        } else {
            modeButton.setText("👁 Modo: Exploración");
            modeButton.setBackground(new Color(100, 100, 200));
        }
    }

    public Tool getCurrentTool() { return currentTool; }
}