package controller;

import model.*;
import view.MapPanel;
import view.Toolbar;
import view.VisualizationMode;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.Optional;

public class MapController {

    private final Graph graph;
    private final MapPanel view;
    private Toolbar.Tool currentTool = Toolbar.Tool.SELECT;
    private String firstNodeToConnect = null;

    public MapController(Graph graph, MapPanel view) {
        this.graph = graph;
        this.view = view;
        attachMouseHandlers();
        updateToolHint();
    }

    // Interacciones de mouse 
    private void attachMouseHandlers() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (currentTool) {
                    case ADD_NODE -> handleAddNode(e.getX(), e.getY());
                    case CONNECT -> handleConnect(e.getX(), e.getY());
                    case SET_START -> handleSetStart(e.getX(), e.getY());
                    case SET_END -> handleSetEnd(e.getX(), e.getY());
                    case SELECT -> {
                        }
                }
            }
        });
    }

    private void handleAddNode(int x, int y) {
        String id = nextNodeId();
        graph.addNode(new Node(id, x, y));
        view.setStatusHint("Nodo " + id + " agregado en (" + x + "," + y + ")");
        view.repaint();
    }

    private void handleConnect(int x, int y) {
        Optional<String> near = graph.encontrarIdNodoCercano(x, y, 10);
        if (near.isEmpty()) {
            view.setStatusHint("Haz click cerca de un nodo para conectar.");
            return;
        }
        String id = near.get();
        if (firstNodeToConnect == null) {
            firstNodeToConnect = id;
            view.setStatusHint("Selecciona el segundo nodo para conectar con " + id);
        } else {
            if (!firstNodeToConnect.equals(id)) {
                graph.connect(firstNodeToConnect, id);
                view.setStatusHint("Conectados: " + firstNodeToConnect + " - " + id);
            }
            firstNodeToConnect = null;
            view.repaint();
        }
    }

    private void handleSetStart(int x, int y) {
        Optional<String> near = graph.encontrarIdNodoCercano(x, y, 10);
        if (near.isPresent()) {
            graph.setStart(near.get());
            view.setStatusHint("Inicio (A): " + near.get());
            view.repaint();
        } else {
            view.setStatusHint("Haz click cerca de un nodo para fijar Inicio (A).");
        }
    }

    private void handleSetEnd(int x, int y) {
        Optional<String> near = graph.encontrarIdNodoCercano(x, y, 10);
        if (near.isPresent()) {
            graph.setEnd(near.get());
            view.setStatusHint("Destino (B): " + near.get());
            view.repaint();
        } else {
            view.setStatusHint("Haz click cerca de un nodo para fijar Destino (B).");
        }
    }

    private String nextNodeId() {
        int i = graph.getNodes().size() + 1;
        String id;
        do {
            id = "N" + i++;
        } while (graph.contains(id));
        return id;
    }

    public void onToolChanged(Toolbar.Tool tool) {
        this.currentTool = tool;
        if (tool != Toolbar.Tool.CONNECT)
            firstNodeToConnect = null;
        updateToolHint();
    }

    private void updateToolHint() {
        String hint = switch (currentTool) {
            case SELECT -> "Seleccionar (mueve nodos)";
            case ADD_NODE -> "Agregar nuevo nodo";
            case CONNECT -> "Pulse nodo 1 y luego nodo 2 para conectar";
            case SET_START -> "Elija un nodo para Inicio (A)";
            case SET_END -> "Elija un nodo para Destino (B)";
        };
        view.setToolHint(hint);
    }

    public void onRunBFS() {
        String s = graph.getStart(), t = graph.getEnd();
        if (s == null || t == null) {
            view.setStatusHint("Define Inicio (A) y Destino (B) antes de ejecutar BFS.");
            return;
        }
        SearchResult r = Algorithms.bfs(graph, s, t);
        view.setSearchResult(r);
        view.setStatusHint(r.hasPath() ? "BFS listo (" + r.getPath().size() + " nodos)" : "BFS: no hay ruta.");
    }

    public void onRunDFS() {
        String s = graph.getStart(), t = graph.getEnd();
        if (s == null || t == null) {
            view.setStatusHint("Define Inicio (A) y Destino (B) antes de ejecutar DFS.");
            return;
        }
        SearchResult r = Algorithms.dfs(graph, s, t);
        view.setSearchResult(r);
        view.setStatusHint(r.hasPath() ? "DFS listo (" + r.getPath().size() + " nodos)" : "DFS: no hay ruta.");
    }

    public void onToggleMode() {
        VisualizationMode m = (view.getMode() == VisualizationMode.EXPLORATION)
                ? VisualizationMode.FINAL_ROUTE
                : VisualizationMode.EXPLORATION;
        view.setMode(m);
        view.setStatusHint("Vista: " + m);
    }

    public void onSave(File file) {
        boolean ok = GraphIO.save(file, graph, view.getImagePath());
        view.setStatusHint(ok ? "Guardado en " + file.getPath() : "Error al guardar.");
    }

    public void onLoad(File file) {
        GraphIO.LoadResult lr = GraphIO.load(file, graph);
        if (lr.imagePath != null)
            view.setBackgroundImage(lr.imagePath);
        //view.setStatusHint(lr.ok ? ("Cargado desde " + file.getPath()) : lr.message);
        view.setSearchResult(null);
        view.repaint();
    }

    public void onClear() {
        graph.clear();
        view.setSearchResult(null);
        view.setStatusHint("Grafo limpiado.");
        view.repaint();
    }
}