package view;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Toolbar extends JPanel {

    public enum Tool {
        SELECT, ADD_NODE, CONNECT, SET_START, SET_END
    }

    private Tool currentTool = Tool.SELECT;

    public Toolbar(
        Consumer<Tool> onToolChange,
        Runnable onRunBFS,
        Runnable onRunDFS,
        Runnable onToggleMode,
        Runnable onSave,
        Runnable onLoad,
        Runnable onClear
    ) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

        ButtonGroup group = new ButtonGroup();

        addToolButton("Seleccionar", Tool.SELECT, group, onToolChange, true);
        addToolButton("Agregar Nodo", Tool.ADD_NODE, group, onToolChange, false);
        addToolButton("Conectar", Tool.CONNECT, group, onToolChange, false);
        addToolButton("Inicio (A)", Tool.SET_START, group, onToolChange, false);
        addToolButton("Destino (B)", Tool.SET_END, group, onToolChange, false);

        add(Box.createHorizontalStrut(12));
        JButton bfs = new JButton("BFS");
        bfs.addActionListener(e -> onRunBFS.run());
        add(bfs);

        JButton dfs = new JButton("DFS");
        dfs.addActionListener(e -> onRunDFS.run());
        add(dfs);

        add(Box.createHorizontalStrut(12));
        JButton mode = new JButton("Alternar Vista");
        mode.setToolTipText("Exploración / Ruta final");
        mode.addActionListener(e -> onToggleMode.run());
        add(mode);

        add(Box.createHorizontalStrut(12));
        JButton save = new JButton("Guardar");
        save.addActionListener(e -> onSave.run());
        add(save);
        JButton load = new JButton("Cargar");
        load.addActionListener(e -> onLoad.run());
        add(load);

        add(Box.createHorizontalStrut(12));
        JButton clear = new JButton("Limpiar");
        clear.addActionListener(e -> onClear.run());
        add(clear);
    }

    private void addToolButton(String label, Tool tool, ButtonGroup group,
                               Consumer<Tool> onToolChange, boolean selected) {
        JToggleButton btn = new JToggleButton(label);
        btn.setSelected(selected);
        group.add(btn);
        add(btn);
        btn.addActionListener(e -> {
            currentTool = tool;
            onToolChange.accept(tool);
        });
    }

    public Tool getCurrentTool() { return currentTool; }
}