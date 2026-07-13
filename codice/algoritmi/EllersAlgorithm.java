/**
 * È uno degli algoritmi più efficienti in assoluto dal punto di vista della memoria, 
 * poiché calcola il labirinto riga per riga. Non ha bisogno di conoscere l'intera griglia, 
 * ma memorizza solo le relazioni di connettività della riga corrente tramite un sistema di "set" numerici.
 * Ad ogni riga, l'algoritmo effettua due passaggi: prima unisce casualmente celle adiacenti 
 * di set diversi (abbattendo muri orizzontali), poi crea almeno una connessione verso il basso 
 * per ciascun set (abbattendo muri verticali) per far ereditare il set alla riga successiva. 
 * L'ultima riga viene trattata in modo speciale per connettere forzatamente tutti i set rimasti isolati.
*/

package codice.algoritmi;
import codice.*;

import java.util.*;

public class EllersAlgorithm implements MazeAlgorithm {
    private int[] currentSet;
    private int nextSetId;
    private int currentRow;
    private Random rand = new Random();
    private int rows, cols;

    /**
     * Inizializza lo stato dell'algoritmo preparando la griglia e rendendo le celle visibili.
     * Assegna a ciascuna cella della prima riga un identificativo di "set" univoco.
    */
    @Override
    public void init(Cell[][] grid) {
        rows = grid.length;
        cols = grid[0].length;
        currentSet = new int[cols];
        nextSetId = 1;
        currentRow = 0;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x].visited = true;
                grid[y][x].isCurrent = false;
            }
        }

        // Assegna un set unico ad ogni elemento della prima riga
        for (int x = 0; x < cols; x++) {
            currentSet[x] = nextSetId++;
        }
    }

    @Override
    public boolean takeStep(Cell[][] grid) {
        if (currentRow >= rows) return false;

        // Pulizia dell'evidenziazione grafica della riga precedente
        if (currentRow > 0) {
            for (int x = 0; x < cols; x++) grid[currentRow - 1][x].isCurrent = false;
        }

        // Evidenziazione della riga in fase di elaborazione
        for (int x = 0; x < cols; x++) grid[currentRow][x].isCurrent = true;

        boolean isLastRow = (currentRow == rows - 1);

        // 1. Connessioni orizzontali: Unisce casualmente celle adiacenti con set diversi e ne 
        // unifica i set di appartenenza. Se è l'ultima riga, l'unione è forzata per chiudere il labirinto.
        for (int x = 0; x < cols - 1; x++) {
            if (currentSet[x] != currentSet[x + 1]) {
                boolean join = rand.nextBoolean() || isLastRow;
                if (join) {
                    int oldSet = currentSet[x + 1];
                    int newSet = currentSet[x];
                    
                    // Unifica i set all'interno della riga corrente
                    for (int i = 0; i < cols; i++) {
                        if (currentSet[i] == oldSet) {
                            currentSet[i] = newSet;
                        }
                    }
                    grid[currentRow][x].walls[2] = false;       // Abbatti Est
                    grid[currentRow][x + 1].walls[3] = false;   // Abbatti Ovest
                }
            }
        }

        // 2. Connessioni berticali: garantisce che ogni set esistente nella riga si estenda 
        // verso il basso in almeno un punto casuale, trasmettendo il proprio set alla riga sotto.
        // Inizializza infine le celle senza connessione verticale con nuovi set esclusivi e avanza alla riga successiva.
        if (!isLastRow) {
            int[] nextSet = new int[cols];
            Map<Integer, List<Integer>> setGroups = new HashMap<>();
            
            for (int x = 0; x < cols; x++) {
                setGroups.computeIfAbsent(currentSet[x], k -> new ArrayList<>()).add(x);
            }

            for (Map.Entry<Integer, List<Integer>> entry : setGroups.entrySet()) {
                List<Integer> indices = entry.getValue();
                // Determina quanti passaggi verticali creare per questo specifico set (almeno 1)
                int verticalPaths = 1 + rand.nextInt(indices.size());
                Collections.shuffle(indices);

                for (int i = 0; i < verticalPaths; i++) {
                    int x = indices.get(i);
                    grid[currentRow][x].walls[1] = false;       // Sud riga corrente
                    grid[currentRow + 1][x].walls[0] = false;   // Nord riga successiva
                    nextSet[x] = currentSet[x];                 // Eredità del set
                }
            }

            // Inizializza le celle senza connessione verticale con un nuovo set univoco
            for (int x = 0; x < cols; x++) {
                if (nextSet[x] == 0) {
                    nextSet[x] = nextSetId++;
                }
            }
            currentSet = nextSet;
        }

        currentRow++;
        return true;
    }

    @Override
    public void generateFully(Cell[][] grid) {
        init(grid);
        while (takeStep(grid)) {
            // Avanza riga per riga fino alla fine
        }
        for (int x = 0; x < cols; x++) grid[rows - 1][x].isCurrent = false;
    }
}