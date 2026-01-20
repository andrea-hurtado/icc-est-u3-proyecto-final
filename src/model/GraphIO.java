
package model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GraphIO {

    public static class LoadResult {
        public final String imagePath;
        public final boolean ok;
        public final String message;
        public LoadResult(String imagePath, boolean ok, String message) {
            this.imagePath = imagePath; this.ok = ok; this.message = message;
        }
    }

    public static LoadResult load(File file, Graph g) {
        g.clear();
        String imagePath = null;
        boolean inNodes = false, inEdges = false;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.strip();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("IMAGE")) {
                    imagePath = line.substring("IMAGE".length()).trim();
                    inNodes = inEdges = false;
                } else if (line.equals("NODES")) {
                    inNodes = true; inEdges = false;
                } else if (line.equals("EDGES")) {
                    inNodes = false; inEdges = true;
                } else if (line.startsWith("START")) {
                    String[] p = line.split("\\s+");
                    if (p.length >= 2) g.setStart(p[1]);
                } else if (line.startsWith("END")) {
                    String[] p = line.split("\\s+");
                    if (p.length >= 2) g.setEnd(p[1]);
                } else if (inNodes) {
                    String[] p = line.split("\\s+");
                    if (p.length >= 3) {
                        String id = p[0];
                        int x = Integer.parseInt(p[1]);
                        int y = Integer.parseInt(p[2]);
                        g.addNode(new Node(id, x, y));
                    }
                } else if (inEdges) {
                    String[] p = line.split("\\s+");
                    if (p.length >= 2) g.connect(p[0], p[1]);
                }
            }
            return new LoadResult(imagePath, true, "OK");
        } catch (Exception ex) {
            return new LoadResult(imagePath, false, "Error al cargar: " + ex.getMessage());
        }
    }

    public static boolean save(File file, Graph g, String imagePath) {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {

            if (imagePath != null && !imagePath.isBlank()) {
                pw.println("IMAGE " + imagePath);
            }
            pw.println("NODES");
            for (Node n : g.getNodes()) {
                pw.printf("%s %d %d%n", n.id, n.x, n.y);
            }
            pw.println("EDGES");
            for (var e : g.getAdjacency().entrySet()) {
                String a = e.getKey();
                for (String b : e.getValue()) {
                    if (a.compareTo(b) < 0) {
                        pw.printf("%s %s%n", a, b);
                    }
                }
            }
            if (g.getStart() != null) pw.println("START " + g.getStart());
            if (g.getEnd() != null) pw.println("END " + g.getEnd());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
