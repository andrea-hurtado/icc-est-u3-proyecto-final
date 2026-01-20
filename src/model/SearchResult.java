
package model;

import java.util.*;

public class SearchResult {
    private final List<String> path;         // ids del camino final
    private final List<String> visitedOrder; // ids en orden de visita opcional 
    private final String algorithm;          // BFS o DFS

    public SearchResult(List<String> path, List<String> visitedOrder, String algorithm) {
        this.path = path;
        this.visitedOrder = visitedOrder;
        this.algorithm = algorithm;
    }

    public List<String> getPath() { return path; }
    public List<String> getVisitedOrder() { return visitedOrder; }
    public String getAlgorithm() { return algorithm; }
    public boolean hasPath() { return path != null && !path.isEmpty(); }
}
