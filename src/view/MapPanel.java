package view;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.*;

public class MapPanel extends JPanel {

    private final Graph graph;
    private Image background;
    private String imagePath;
    private VisualizationMode mode = VisualizationMode.EXPLORATION;
    private SearchResult lastResult = null;
    private String toolHint = "";
    private String statusHint = "";

    public MapPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.DARK_GRAY);
        setOpaque(true);
    }

    public void setBackgroundImage(String path) {
        this.imagePath = path;

        if (path == null) {
            background = null;
        } else {
            try {
                background = javax.imageio.ImageIO.read(new java.io.File(path));
            } catch (Exception e) {
                e.printStackTrace();
                background = null;
            }
        }

        repaint();
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setMode(VisualizationMode mode) {
        this.mode = mode;
        repaint();
    }

    public VisualizationMode getMode() {
        return mode;
    }

    public void setSearchResult(SearchResult r) {
        this.lastResult = r;
        repaint();
    }

    public void setToolHint(String hint) {
        this.toolHint = hint;
        repaint();
    }

    public void setStatusHint(String hint) {
        this.statusHint = hint;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics gBase) {
        super.paintComponent(gBase);
        Graphics2D g = (Graphics2D) gBase.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (background != null) {
            g.drawImage(background, 0, 0, this);
        } else {
            g.setColor(new Color(45, 45, 45));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        Color nodeColor = new Color(220, 60, 60);
        Color nodeBorder = new Color(255, 255, 255);
        Color startColor = new Color(60, 200, 60);
        Color endColor = new Color(60, 120, 230);
        Color blockedColor = new Color(40, 40, 40);
        Color blockedBorder = new Color(180, 0, 0);

        Color exploredEdgeColor = new Color(70, 130, 255, 200);
        Color finalPathColor = new Color(255, 60, 0);

        Stroke edgeStroke = new BasicStroke(2.0f);
        Stroke exploredStroke = new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Stroke routeStroke = new BasicStroke(6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        Set<String> routeEdges = new HashSet<>();
        Set<String> routeNodes = new HashSet<>();
        Set<String> exploredEdges = new HashSet<>();

        if (lastResult != null && lastResult.hasPath()) {
            List<String> p = lastResult.getPath();
            routeNodes.addAll(p);
            for (int i = 0; i < p.size() - 1; i++) {
                String a = p.get(i), b = p.get(i + 1);
                routeEdges.add(edgeKey(a, b));
            }

            if (lastResult.getAlgorithm().equals("DFS") && lastResult.getExploredEdges() != null) {
                exploredEdges.addAll(lastResult.getExploredEdges());
            }
        }

        if (mode == VisualizationMode.EXPLORATION) {
            g.setColor(new Color(255, 230, 128, 100));
            g.setStroke(edgeStroke);
            for (var e : graph.getAdjacency().entrySet()) {
                Node a = graph.getNode(e.getKey());
                for (String vid : e.getValue()) {
                    Node b = graph.getNode(vid);
                    if (a == null || b == null)
                        continue;
                    drawEdge(g, a, b);
                }
            }

            if (!exploredEdges.isEmpty()) {
                g.setStroke(exploredStroke);
                for (String edgeKey : exploredEdges) {
                    if (!routeEdges.contains(edgeKey)) {
                        String[] parts = edgeKey.split("-");
                        if (parts.length == 2) {
                            Node a = graph.getNode(parts[0]);
                            Node b = graph.getNode(parts[1]);
                            if (a != null && b != null) {
                                g.setColor(new Color(70, 130, 255, 80));
                                g.setStroke(new BasicStroke(7.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                                drawEdge(g, a, b);

                                g.setColor(exploredEdgeColor);
                                g.setStroke(exploredStroke);
                                drawEdge(g, a, b);
                            }
                        }
                    }
                }
            }

            if (!routeEdges.isEmpty()) {
                g.setColor(new Color(255, 100, 0, 100));
                g.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                drawRouteEdges(g, routeEdges);

                g.setColor(finalPathColor);
                g.setStroke(routeStroke);
                drawRouteEdges(g, routeEdges);
            }

            for (Node n : graph.getNodes()) {
                boolean isStart = n.id.equals(graph.getStart());
                boolean isEnd = n.id.equals(graph.getEnd());
                boolean isBlocked = n.blocked;

                Color fill;
                Color border;

                if (isBlocked) {
                    fill = blockedColor;
                    border = blockedBorder;
                } else if (isStart) {
                    fill = startColor;
                    border = nodeBorder;
                } else if (isEnd) {
                    fill = endColor;
                    border = nodeBorder;
                } else {
                    fill = nodeColor;
                    border = nodeBorder;
                }

                drawNode(g, n, fill, border, isBlocked);
            }

            if (lastResult != null) {
                java.util.List<String> visited = lastResult.getVisitedOrder();
                if (visited != null && !visited.isEmpty()) {
                    for (String id : visited) {
                        Node v = graph.getNode(id);
                        if (v != null && !v.blocked) {
                            int rVisited = 5;

                            g.setColor(new Color(0, 255, 255, 60));
                            Shape halo = new Ellipse2D.Double(v.x - rVisited - 2, v.y - rVisited - 2,
                                    2 * (rVisited + 2), 2 * (rVisited + 2));
                            g.fill(halo);

                            g.setColor(new Color(0, 220, 255, 180));
                            Shape dot = new Ellipse2D.Double(v.x - rVisited, v.y - rVisited,
                                    2 * rVisited, 2 * rVisited);
                            g.fill(dot);
                        }
                    }
                }
            }

        } else {
            // Modo FINAL_ROUTE: solo mostrar camino final
            if (!routeEdges.isEmpty()) {
                g.setColor(finalPathColor);
                g.setStroke(routeStroke);
                drawRouteEdges(g, routeEdges);
                for (String id : routeNodes) {
                    Node n = graph.getNode(id);
                    if (n != null)
                        drawNode(g, n, nodeColor, nodeBorder, false);
                }
            }
            if (graph.getStart() != null) {
                Node s = graph.getNode(graph.getStart());
                if (s != null)
                    drawNode(g, s, startColor, nodeBorder, false);
            }
            if (graph.getEnd() != null) {
                Node e = graph.getNode(graph.getEnd());
                if (e != null)
                    drawNode(g, e, endColor, nodeBorder, false);
            }
        }

        if (mode == VisualizationMode.EXPLORATION && lastResult != null) {
            drawLegend(g);
        }

        g.setFont(getFont().deriveFont(Font.BOLD, 13f));
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(8, 8, Math.max(180, g.getFontMetrics().stringWidth(toolHint) + 16), 48, 10, 10);
        g.setColor(Color.WHITE);
        g.drawString("Modo: " + mode, 18, 28);
        g.drawString(toolHint, 18, 48);

        if (statusHint != null && !statusHint.isBlank()) {
            int w = Math.min(getWidth() - 16, g.getFontMetrics().stringWidth(statusHint) + 16);
            int x = getWidth() - w - 8;
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRoundRect(x, 8, w, 28, 10, 10);
            g.setColor(Color.WHITE);
            g.drawString(statusHint, x + 8, 28);
        }

        g.dispose();
    }

    private void drawLegend(Graphics2D g) {
        int x = 10;
        int y = getHeight() - 120;
        int w = 220;
        int h = 110;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(x, y, w, h, 10, 10);

        g.setFont(getFont().deriveFont(Font.BOLD, 11f));
        g.setColor(Color.WHITE);
        g.drawString("LEYENDA", x + 10, y + 18);

        g.setFont(getFont().deriveFont(Font.PLAIN, 10f));

        // Caminos explorados (DFS)
        if (lastResult.getAlgorithm().equals("DFS")) {
            g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(70, 130, 255, 200));
            g.drawLine(x + 10, y + 35, x + 35, y + 35);
            g.setColor(Color.WHITE);
            g.drawString("Caminos explorados", x + 40, y + 38);
        }

        // Camino final
        g.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(255, 60, 0));
        g.drawLine(x + 10, y + 55, x + 35, y + 55);
        g.setColor(Color.WHITE);
        g.drawString("Camino final", x + 40, y + 58);

        // Nodos visitados
        g.setColor(new Color(0, 220, 255, 180));
        g.fillOval(x + 15, y + 68, 10, 10);
        g.setColor(Color.WHITE);
        g.drawString("Nodos visitados", x + 40, y + 78);

        // Info adicional
        if (lastResult != null && lastResult.getExploredEdges() != null) {
            g.setFont(getFont().deriveFont(Font.ITALIC, 9f));
            g.setColor(new Color(200, 200, 200));
            g.drawString("Caminos explorados: " + lastResult.getExploredEdges().size(), x + 10, y + 98);
        }
    }

    private void drawEdge(Graphics2D g, Node a, Node b) {
        g.draw(new Line2D.Double(a.x, a.y, b.x, b.y));
    }

    private void drawRouteEdges(Graphics2D g, Set<String> routeEdges) {
        for (var e : graph.getAdjacency().entrySet()) {
            Node a = graph.getNode(e.getKey());
            for (String vid : e.getValue()) {
                Node b = graph.getNode(vid);
                if (a == null || b == null)
                    continue;
                if (routeEdges.contains(edgeKey(a.id, b.id))) {
                    drawEdge(g, a, b);
                }
            }
        }
    }

    private void drawNode(Graphics2D g, Node n, Color fill, Color border, boolean isBlocked) {
        int r = 7;
        Shape s = new Ellipse2D.Double(n.x - r, n.y - r, 2 * r, 2 * r);
        g.setColor(fill);
        g.fill(s);

        g.setStroke(new BasicStroke(isBlocked ? 2.5f : 1.5f));
        g.setColor(border);
        g.draw(s);

        if (isBlocked) {
            g.setColor(new Color(255, 80, 80));
            g.setStroke(new BasicStroke(2.0f));
            int offset = 5;
            g.drawLine(n.x - offset, n.y - offset, n.x + offset, n.y + offset);
            g.drawLine(n.x - offset, n.y + offset, n.x + offset, n.y - offset);
        }

        g.setFont(getFont().deriveFont(Font.BOLD, 11f));
        g.setColor(new Color(240, 240, 240));

        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(n.id, n.x + 9, n.y - 7);
        g.setColor(new Color(240, 240, 240));
        g.drawString(n.id, n.x + 8, n.y - 8);
    }

    private String edgeKey(String a, String b) {
        return (a.compareTo(b) < 0) ? a + "-" + b : b + "-" + a;
    }
}