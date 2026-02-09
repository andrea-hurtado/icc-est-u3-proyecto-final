package model;

import java.util.*;

public class Algorithms {

    // BFS recorre camino más corto en número de aristas, evitando nodos bloqueados
    public static SearchResult bfs(Graph g, String start, String end) {
        if (start == null || end == null) return new SearchResult(List.of(), List.of(), List.of(), "BFS");
        
        Node startNode = g.getNode(start);
        Node endNode = g.getNode(end);
        if (startNode == null || endNode == null || startNode.blocked || endNode.blocked) {
            return new SearchResult(List.of(), List.of(), List.of(), "BFS");
        }
        
        Queue<String> q = new ArrayDeque<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new LinkedHashSet<>();
        q.add(start);
        visited.add(start);

        while (!q.isEmpty()) {
            String u = q.poll();
            if (u.equals(end)) break;
            for (String v : g.getAdjacency().getOrDefault(u, Set.of())) {
                Node vNode = g.getNode(v);
                if (vNode != null && !vNode.blocked && !visited.contains(v)) {
                    visited.add(v);
                    parent.put(v, u);
                    q.add(v);
                }
            }
        }

        List<String> path = reconstruct(parent, start, end);
        return new SearchResult(path, new ArrayList<>(visited), List.of(), "BFS");
    }

    // DFS MEJORADO - Captura todos los caminos explorados de forma muy visible
    public static SearchResult dfs(Graph g, String start, String end) {
        if (start == null || end == null) return new SearchResult(List.of(), List.of(), List.of(), "DFS");
        
        Node startNode = g.getNode(start);
        Node endNode = g.getNode(end);
        if (startNode == null || endNode == null || startNode.blocked || endNode.blocked) {
            return new SearchResult(List.of(), List.of(), List.of(), "DFS");
        }
        
        Set<String> visited = new LinkedHashSet<>();
        List<String> currentPath = new ArrayList<>();
        List<String> finalPath = new ArrayList<>();
        Set<String> allExploredEdges = new LinkedHashSet<>();
        
        System.out.println("\n=== INICIO DFS ===");
        System.out.println("Desde: " + start + " → Hasta: " + end);
        
        dfsRecursive(g, start, end, visited, currentPath, finalPath, allExploredEdges, 0);
        
        System.out.println("\n=== RESULTADO DFS ===");
        System.out.println("Total aristas exploradas: " + allExploredEdges.size());
        System.out.println("Caminos explorados: " + allExploredEdges);
        System.out.println("Camino final: " + finalPath);
        
        return new SearchResult(finalPath, new ArrayList<>(visited), 
                               new ArrayList<>(allExploredEdges), "DFS");
    }

    private static boolean dfsRecursive(Graph g, String current, String end, 
                                       Set<String> visited, List<String> currentPath, 
                                       List<String> finalPath, Set<String> allExploredEdges,
                                       int depth) {
        String indent = "  ".repeat(depth);
        
        visited.add(current);
        currentPath.add(current);
        
        System.out.println(indent + "→ Visitando: " + current + " (profundidad: " + depth + ")");
        
        if (currentPath.size() > 1) {
            String prev = currentPath.get(currentPath.size() - 2);
            String edge = edgeKey(prev, current);
            allExploredEdges.add(edge);
            System.out.println(indent + "  ✓ Arista explorada: " + edge);
        }
        
        if (current.equals(end)) {
            System.out.println(indent + "★ DESTINO ENCONTRADO!");
            finalPath.clear();
            finalPath.addAll(currentPath);
            return true;
        }
        
        List<String> neighbors = new ArrayList<>(g.getAdjacency().getOrDefault(current, Set.of()));
        Collections.sort(neighbors); 
        
        System.out.println(indent + "  Vecinos de " + current + ": " + neighbors);
        
        for (String neighbor : neighbors) {
            Node neighborNode = g.getNode(neighbor);
            
            if (neighborNode != null && !neighborNode.blocked) {
                if (!visited.contains(neighbor)) {
                    System.out.println(indent + "  → Explorando vecino: " + neighbor);
                    
                    if (dfsRecursive(g, neighbor, end, visited, currentPath, 
                                    finalPath, allExploredEdges, depth + 1)) {
                        return true; 
                    }
                } else {
                    System.out.println(indent + "  ✗ " + neighbor + " ya visitado, ignorando");
                }
            } else if (neighborNode != null && neighborNode.blocked) {
                System.out.println(indent + "  ⊗ " + neighbor + " está bloqueado");
            }
        }
        
        System.out.println(indent + "← Backtrack desde: " + current);
        currentPath.remove(currentPath.size() - 1);
        
        return false;
    }

    private static List<String> reconstruct(Map<String, String> parent, String start, String end) {
        if (start.equals(end)) return List.of(start);
        List<String> path = new ArrayList<>();
        String cur = end;
        while (cur != null && !cur.equals(start)) {
            path.add(cur);
            cur = parent.get(cur);
        }
        if (cur == null) return List.of(); 
        path.add(start);
        Collections.reverse(path);
        return path;
    }
    
    private static String edgeKey(String a, String b) {
        return (a.compareTo(b) < 0) ? a + "-" + b : b + "-" + a;
    }
}