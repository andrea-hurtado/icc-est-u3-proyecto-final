
package model;

import java.util.*;

public class Graph {
    private final Map<String, Node> nodes = new LinkedHashMap<>();
    private final Map<String, Set<String>> adj = new LinkedHashMap<>();
    private String startId = null;
    private String endId = null;

    public Collection<Node> getNodes() {
         return nodes.values(); }
    public Map<String, Set<String>> getAdjacency() {
         return adj; }
    public Node getNode(String id) { 
        return nodes.get(id); }
    public boolean contains(String id) { 
        return nodes.containsKey(id); }

    public void addNode(Node n) {
        if (nodes.containsKey(n.id)) 
            return;
        nodes.put(n.id, n);
        adj.put(n.id, new LinkedHashSet<>());
    }

    public void connect(String a, String b) {
        if (!nodes.containsKey(a) || !nodes.containsKey(b) || a.equals(b)) return;
        adj.get(a).add(b);
        adj.get(b).add(a);
    }

    public void clear() {
        nodes.clear();
        adj.clear();
        startId = endId = null;
    }

    public void setStart(String id) { 
        if (nodes.containsKey(id)) startId = id; }
    public void setEnd(String id) {
         if (nodes.containsKey(id)) endId = id; }
    public String getStart() { 
        return startId; }
    public String getEnd() { 
        return endId; }

    public Optional<String> encontrarIdNodoCercano(int x, int y, int radio) {
        String mejorId = null;
        double mejorDist2 = radio * radio;
        for (Node n : nodes.values()) {
            double dx = x - n.x;
            double dy = y - n.y;
            double d2 = dx*dx + dy*dy;
            if (d2 <= mejorDist2) {
                mejorDist2 = d2;
                mejorId = n.id;
            }
        }
        return Optional.ofNullable(mejorId);
    }
}
