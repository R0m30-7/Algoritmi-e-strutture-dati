/**
 * Questo algoritmo lavora in modo globale: parte con tutte le celle isolate e abbatte
 * e i muri interni in ordine completamente casuale.
 * Utilizza una struttura dati Disjoint Set (Union-Find) per unire progressivamente 
 * i gruppi di celle ed evitare la creazione di cicli (anelli chiusi).
*/

package codice.algoritmi;
import codice.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomizedKruskal implements MazeAlgorithm {
    
    private static class Edge {     // Classe degli archi
        Cell c1, c2;        // Celle adiacenti
        int wall1, wall2;   // Indici dei muri da abbattere

        Edge(Cell c1, Cell c2, int wall1, int wall2) {
            this.c1 = c1;
            this.c2 = c2;
            this.wall1 = wall1;
            this.wall2 = wall2;
        }
    }

    private static class DisjointSet {      // Struttura delle celle
        int[] parent;
        int[] rank;

        /**
         * Crea 'size' gruppi indipendenti. All'inizio, ogni cella è parent 
         * di se stessa e il rango (rank), ovvero l'altezza dell'albero, è pari a 0.
        */
        DisjointSet(int size) {
            parent = new int[size];     // Salva genitore di ogni cella
            rank = new int[size];       // Salva il grado di ogni cella
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        int find(int i) {   // Trova la root di una cella specifica
            if (parent[i] == i) {
                return i;
            }
            parent[i] = find(parent[i]);    // Compressione dei cammini
            return parent[i];
        }

        boolean union(int i, int j) {   // Unione dei gruppi della cella i con cella j
            int rootI = find(i);
            int rootJ = find(j);
            if (rootI != rootJ) {   // Evita la creazione di cicli
                // L'albero più basso viene agganciato sotto l'albero più alto
                if (rank[rootI] < rank[rootJ]) {
                    parent[rootI] = rootJ;
                } else if (rank[rootI] > rank[rootJ]) {
                    parent[rootJ] = rootI;
                } else {
                    parent[rootJ] = rootI;  // Aggancia l'albero j sotto l'albero i
                    rank[rootI]++;
                }
                return true;
            }
            // Ritorna false se le due celle appartengono già allo stesso gruppo
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
        // Inizialmente, ogni cella del labirinto vive nel proprio gruppo isolato
        // Si assegna quindi a ogni cella un ID univoco calcolato convertendo la griglia in un array
        ds = new DisjointSet(rows * cols);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x].visited = true;      // In Kruskal le celle sono tutte scoperte
                grid[y][x].isCurrent = false;   // e non esiste una cella corrente
                
                // Esplora la griglia generando solo i muri verso Est e Sud: 
                // evita i duplicati (ogni muro interno è condiviso da due celle) e salta i confini esterni.
                if (x < cols - 1) {
                    edges.add(new Edge(grid[y][x], grid[y][x + 1], 2, 3));
                }
                if (y < rows - 1) {
                    edges.add(new Edge(grid[y][x], grid[y + 1][x], 1, 0));
                }
            }
        }
        // La randomizzazione di Kruskal sta nel mescolare gli archi all'inizio, in
        // modo che l'algoritmo esaminerà i muri in ordine sparso
        Collections.shuffle(edges);
    }

    @Override
    public boolean takeStep(Cell[][] grid) {
        while (edgeIndex < edges.size()) {
            Edge e = edges.get(edgeIndex++);    // Prende il muro successivo dalla lista mescolata
            int id1 = e.c1.y * cols + e.c1.x;   // ID cella 1
            int id2 = e.c2.y * cols + e.c2.x;   // ID cella 2

            // Se le due celle fossero già collegate in qualche modo (false), l'algoritmo non deve fare niente
            if (ds.union(id1, id2)) {
                e.c1.walls[e.wall1] = false;    // Abbate il muro sulla cella 1
                e.c2.walls[e.wall2] = false;    // Abbate il muro sulla cella 2
                return true;                    // Trovato un arco valido, esegue il passo grafico
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