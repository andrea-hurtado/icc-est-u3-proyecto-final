package controller;

import model.*;
import view.MapPanel;
import view.Toolbar;
import view.VisualizationMode;
import java.awt.Cursor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.awt.event.*;
import java.io.File;
import java.util.Optional;

public class MapController {

    private final Graph graph;
    private final MapPanel view;
    private Toolbar.Tool currentTool = Toolbar.Tool.SELECT;
    private String firstNodeToConnect = null;
    private String selectedId = null;
    private int dragOffsetX = 0, dragOffsetY = 0;

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

            @Override
            public void mousePressed(MouseEvent e) {
                if (currentTool != Toolbar.Tool.SELECT)
                    return;
                var near = graph.encontrarIdNodoCercano(e.getX(), e.getY(), 10);
                if (near.isPresent()) {
                    selectedId = near.get();
                    Node n = graph.getNode(selectedId);
                    dragOffsetX = e.getX() - n.x;
                    dragOffsetY = e.getY() - n.y;
                    view.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                } else {
                    selectedId = null;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentTool != Toolbar.Tool.SELECT)
                    return;
                selectedId = null;
                view.setCursor(Cursor.getDefaultCursor());
            }

        });

        view.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentTool != Toolbar.Tool.SELECT || selectedId == null)
                    return;
                Node n = graph.getNode(selectedId);
                if (n != null) {
                    n.x = e.getX() - dragOffsetX;
                    n.y = e.getY() - dragOffsetY;
                    // (opcional) limitar a bounds del panel
                    n.x = Math.max(0, Math.min(n.x, view.getWidth()));
                    n.y = Math.max(0, Math.min(n.y, view.getHeight()));
                    view.setStatusHint("Moviendo " + selectedId + " a (" + n.x + "," + n.y + ")");
                    view.repaint();
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

        selectedId = null;
        view.setCursor(Cursor.getDefaultCursor());

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
        runAndMeasure("BFS", () -> Algorithms.bfs(graph, graph.getStart(), graph.getEnd()));
    }

    public void onRunDFS() {
        runAndMeasure("DFS", () -> Algorithms.dfs(graph, graph.getStart(), graph.getEnd()));
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
        view.setSearchResult(null);
        view.setStatusHint(lr.ok ? ("Cargado: " + file.getPath()) : lr.message);
        view.repaint();
    }

    public void onClear() {
        graph.clear();
        view.setSearchResult(null);
        view.setStatusHint("Grafo limpiado.");
        view.repaint();
    }

    private void runAndMeasure(String name, Supplier<SearchResult> algo) {
        String s = graph.getStart(), t = graph.getEnd();
        if (s == null || t == null) {
            view.setStatusHint("Define Inicio (A) y Destino (B) antes de ejecutar " + name + ".");
            return;
        }
        long t0 = System.nanoTime();
        SearchResult r = algo.get();
        long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);

        view.setSearchResult(r);
        view.setStatusHint((r.hasPath() ? name + " listo (" + r.getPath().size() + " nodos)" : (name + ": no hay ruta"))
                + " — " + ms + " ms");
        logTime(name, ms, s, t, graph.getNodes().size(), countEdges());
    }

    private int countEdges() {
        java.util.Set<String> uniq = new java.util.HashSet<>();
        for (var e : graph.getAdjacency().entrySet()) {
            String a = e.getKey();
            for (String b : e.getValue()) {
                String key = (a.compareTo(b) < 0) ? a + "-" + b : b + "-" + a;
                uniq.add(key);
            }
        }
        return uniq.size();
    }

    private void logTime(String algo, long ms, String s, String t, int nNodes, int nEdges) {
        try {
            java.io.File dir = new java.io.File("data");
            if (!dir.exists())
                dir.mkdirs();
            java.io.File f = new java.io.File(dir, "times.csv");
            boolean newFile = !f.exists();
            try (java.io.FileWriter fw = new java.io.FileWriter(f, true);
                    java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
                if (newFile)
                    pw.println("timestamp,algorithm,start,end,nodes,edges,time_ms");
                String ts = java.time.LocalDateTime.now().toString();
                pw.printf("%s,%s,%s,%s,%d,%d,%d%n", ts, algo, s, t, nNodes, nEdges, ms);
            }
        } catch (Exception ex) {
            view.setStatusHint("No se pudo escribir data/times.csv: " + ex.getMessage());
        }
    }

}