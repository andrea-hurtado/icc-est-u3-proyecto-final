package model;

import java.util.*;

public class SearchResult {
    private final List<String> path;        
    private final List<String> visitedOrder; 
    private final List<String> exploredEdges; 
    private final String algorithm;         

    public SearchResult(List<String> path, List<String> visitedOrder, List<String> exploredEdges, String algorithm) {
        this.path = path;
        this.visitedOrder = visitedOrder;
        this.exploredEdges = exploredEdges;
        this.algorithm = algorithm;
    }

    public List<String> getPath() { return path; }
    public List<String> getVisitedOrder() { return visitedOrder; }
    public List<String> getExploredEdges() { return exploredEdges; }
    public String getAlgorithm() { return algorithm; }
    public boolean hasPath() { return path != null && !path.isEmpty(); }
}