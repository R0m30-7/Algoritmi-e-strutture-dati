package codice.algoritmi;
import codice.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomizedKruskal implements MazeAlgorithm {
    
    private static class Edge {
        Cell c1, c2;
        int wall1, wall2; // Indici dei muri da abbattere

        Edge(Cell c1, Cell c2, int wall1, int wall2) {
            this.c1 = c1;
            this.c2 = c2;
            this.wall1 = wall1;
            this.wall2 = wall2;
        }
    }

    private static class DisjointSet {
        int[] parent;
        int[] rank;

        DisjointSet(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        int find(int i) {
            if (parent[i] == i) {
                return i;
            }
            parent[i] = find(parent[i]); // Compressione dei cammini
            return parent[i];
        }

        boolean union(int i, int j) {
            int rootI = find(i);
            int rootJ = find(j);
            if (rootI != rootJ) {
                if (rank[rootI] < rank[rootJ]) {
                    parent[rootI] = rootJ;
                } else if (rank[rootI] > rank[rootJ]) {
                    parent[rootJ] = rootI;
                } else {
                    parent[rootJ] = rootI;
                    rank[rootI]++;
                }
                return true;
            }
            return false;
        }
    }

    private List<Edge> edges = new ArrayList<>();
    private DisjointSet ds;
    private int rows, cols;
    private int edgeIndex;

    @Override
    public void init(Cell[][] grid) {
        edges.clear();
        rows = grid.length;
        cols = grid[0].length;
        edgeIndex = 0;
        ds = new DisjointSet(rows * cols);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x].visited = true; // In Kruskal le celle sono visivamente attive da subito
                grid[y][x].isCurrent = false;
                
                // Aggiunge archi verso Est e Sud per evitare duplicati
                if (x < cols - 1) {
                    edges.add(new Edge(grid[y][x], grid[y][x + 1], 2, 3));
                }
                if (y < rows - 1) {
                    edges.add(new Edge(grid[y][x], grid[y + 1][x], 1, 0));
                }
            }
        }
        // La randomizzazione di Kruskal sta nel mescolare gli archi all'inizio
        Collections.shuffle(edges);
    }

    @Override
    public boolean takeStep(Cell[][] grid) {
        while (edgeIndex < edges.size()) {
            Edge e = edges.get(edgeIndex++);
            int id1 = e.c1.y * cols + e.c1.x;
            int id2 = e.c2.y * cols + e.c2.x;

            if (ds.union(id1, id2)) {
                e.c1.walls[e.wall1] = false;
                e.c2.walls[e.wall2] = false;
                return true; // Trovato un arco valido, esegue il passo grafico
            }
        }
        return false;
    }

    @Override
    public void generateFully(Cell[][] grid) {
        init(grid);
        while (takeStep(grid)) {
            // Esegue fino al completamento
        }
    }
}