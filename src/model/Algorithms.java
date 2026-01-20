
package model;

import java.util.*;

public class Algorithms {

    // BFS recorre camino más corto en número de aristas
    public static SearchResult bfs(Graph g, String start, String end) {
        if (start == null || end == null) return new SearchResult(List.of(), List.of(), "BFS");
        Queue<String> q = new ArrayDeque<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new LinkedHashSet<>();
        q.add(start);
        visited.add(start);

        while (!q.isEmpty()) {
            String u = q.poll();
            if (u.equals(end)) break;
            for (String v : g.getAdjacency().getOrDefault(u, Set.of())) {
                if (!visited.contains(v)) {
                    visited.add(v);
                    parent.put(v, u);
                    q.add(v);
                }
            }
        }

        List<String> path = reconstruct(parent, start, end);
        return new SearchResult(path, new ArrayList<>(visited), "BFS");
    }

    // DFS recorre cualquier camino
    public static SearchResult dfs(Graph g, String start, String end) {
        if (start == null || end == null) return new SearchResult(List.of(), List.of(), "DFS");
        Set<String> visited = new LinkedHashSet<>();
        List<String> path = new ArrayList<>();
        boolean found = dfsRec(g, start, end, visited, path);
        if (!found) path.clear();
        return new SearchResult(path, new ArrayList<>(visited), "DFS");
    }

    private static boolean dfsRec(Graph g, String u, String end, Set<String> visited, List<String> path) {
        visited.add(u);
        path.add(u);
        if (u.equals(end)) return true;
        for (String v : g.getAdjacency().getOrDefault(u, Set.of())) {
            if (!visited.contains(v)) {
                if (dfsRec(g, v, end, visited, path)) return true;
            }
        }
        path.remove(path.size() - 1);
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
        if (cur == null) return List.of(); // si no hay camino
        path.add(start);
        Collections.reverse(path);
        return path;
    }
}
