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
            background = new ImageIcon(path).getImage();
            setPreferredSize(new Dimension(background.getWidth(null), background.getHeight(null)));
            revalidate();
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

        // Fondo
        if (background != null) {
            g.drawImage(background, 0, 0, this);
        } else {
            g.setColor(new Color(45, 45, 45));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Paleta
        Color edgeColor = new Color(255, 230, 128);
        Color nodeColor = new Color(220, 60, 60);
        Color nodeBorder = new Color(255, 255, 255);
        Color startColor = new Color(60, 200, 60);
        Color endColor = new Color(60, 120, 230);
        Stroke edgeStroke = new BasicStroke(2.0f);
        Stroke routeStroke = new BasicStroke(4.0f);

        // Conjunto de aristas de la ruta (para dibujar con más grosor)
        Set<String> routeEdges = new HashSet<>();
        Set<String> routeNodes = new HashSet<>();
        if (lastResult != null && lastResult.hasPath()) {
            List<String> p = lastResult.getPath();
            routeNodes.addAll(p);
            for (int i = 0; i < p.size() - 1; i++) {
                String a = p.get(i), b = p.get(i + 1);
                routeEdges.add(edgeKey(a, b));
            }
        }

        if (mode == VisualizationMode.EXPLORATION) {
            g.setColor(edgeColor);
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
            if (!routeEdges.isEmpty()) {
                g.setColor(new Color(255, 90, 0));
                g.setStroke(routeStroke);
                drawRouteEdges(g, routeEdges);
            }
            for (Node n : graph.getNodes()) {
                boolean isStart = n.id.equals(graph.getStart());
                boolean isEnd = n.id.equals(graph.getEnd());
                Color fill = isStart ? startColor : isEnd ? endColor : nodeColor;
                drawNode(g, n, fill, nodeBorder);
            }

            if (lastResult != null) {
                java.util.List<String> visited = lastResult.getVisitedOrder();
                if (visited != null && !visited.isEmpty()) {
                    g.setColor(new Color(0, 200, 255, 120)); // azul suave semitransparente
                    for (String id : visited) {
                        Node v = graph.getNode(id);
                        if (v != null) {
                            int rVisited = 4;
                            Shape dot = new Ellipse2D.Double(v.x - rVisited, v.y - rVisited, 2 * rVisited,
                                    2 * rVisited);
                            g.fill(dot);
                        }
                    }
                }
            }

        } else {
            if (!routeEdges.isEmpty()) {
                g.setColor(new Color(255, 90, 0));
                g.setStroke(routeStroke);
                drawRouteEdges(g, routeEdges);
                for (String id : routeNodes) {
                    Node n = graph.getNode(id);
                    if (n != null)
                        drawNode(g, n, nodeColor, nodeBorder);
                }
            }
            if (graph.getStart() != null) {
                Node s = graph.getNode(graph.getStart());
                if (s != null)
                    drawNode(g, s, startColor, nodeBorder);
            }
            if (graph.getEnd() != null) {
                Node e = graph.getNode(graph.getEnd());
                if (e != null)
                    drawNode(g, e, endColor, nodeBorder);
            }
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

    private void drawNode(Graphics2D g, Node n, Color fill, Color border) {
        int r = 6;
        Shape s = new Ellipse2D.Double(n.x - r, n.y - r, 2 * r, 2 * r);
        g.setColor(fill);
        g.fill(s);
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(border);
        g.draw(s);

        g.setFont(getFont().deriveFont(Font.BOLD, 11f));
        g.setColor(new Color(240, 240, 240));
        g.drawString(n.id, n.x + 8, n.y - 8);
    }

    private String edgeKey(String a, String b) {
        return (a.compareTo(b) < 0) ? a + "-" + b : b + "-" + a;
    }
}