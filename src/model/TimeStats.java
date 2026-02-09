package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimeStats {
    
    public static class ExecutionRecord {
        public final LocalDateTime timestamp;
        public final String algorithm;
        public final String start;
        public final String end;
        public final int nodes;
        public final int edges;
        public final long timeMs;
        public final boolean foundPath;
        public final int pathLength;
        
        public ExecutionRecord(String algorithm, String start, String end, int nodes, 
                              int edges, long timeMs, boolean foundPath, int pathLength) {
            this.timestamp = LocalDateTime.now();
            this.algorithm = algorithm;
            this.start = start;
            this.end = end;
            this.nodes = nodes;
            this.edges = edges;
            this.timeMs = timeMs;
            this.foundPath = foundPath;
            this.pathLength = pathLength;
        }
        
        public String getFormattedTimestamp() {
            return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
    
    private static final List<ExecutionRecord> records = new ArrayList<>();
    
    public static void addRecord(ExecutionRecord record) {
        records.add(record);
    }
    
    public static List<ExecutionRecord> getAllRecords() {
        return new ArrayList<>(records);
    }
    
    public static List<ExecutionRecord> getRecentRecords(int count) {
        int size = records.size();
        if (size <= count) return new ArrayList<>(records);
        return new ArrayList<>(records.subList(size - count, size));
    }
    
    public static void clear() {
        records.clear();
    }
    
    public static String getComparison() {
        if (records.size() < 2) return "Ejecuta ambos algoritmos para comparar";
        
        ExecutionRecord lastBFS = null;
        ExecutionRecord lastDFS = null;
        
        for (int i = records.size() - 1; i >= 0; i--) {
            ExecutionRecord r = records.get(i);
            if (lastBFS == null && r.algorithm.equals("BFS")) lastBFS = r;
            if (lastDFS == null && r.algorithm.equals("DFS")) lastDFS = r;
            if (lastBFS != null && lastDFS != null) break;
        }
        
        if (lastBFS == null || lastDFS == null) {
            return "Ejecuta ambos algoritmos para comparar";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Última Comparación BFS vs DFS:\n");
        sb.append(String.format("BFS: %d ms | Ruta: %d nodos\n", 
            lastBFS.timeMs, lastBFS.pathLength));
        sb.append(String.format("DFS: %d ms | Ruta: %d nodos\n", 
            lastDFS.timeMs, lastDFS.pathLength));
        
        if (lastBFS.timeMs < lastDFS.timeMs) {
            sb.append("→ BFS fue más rápido\n");
        } else if (lastDFS.timeMs < lastBFS.timeMs) {
            sb.append("→ DFS fue más rápido\n");
        } else {
            sb.append("→ Ambos tardaron lo mismo\n");
        }
        
        return sb.toString();
    }
}