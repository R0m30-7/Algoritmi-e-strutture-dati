/**
 * Funziona secondo il principio della passeggiata casuale (Random Walk): l'algoritmo 
 * si sposta da una cella all'altra scegliendo a ogni passo una direzione totalmente 
 * a caso tra le 4 disponibili.
 * Se la cella di destinazione non è mai stata visitata prima, viene abbattuto il muro 
 * che le separa e la cella viene inclusa nel labirinto. Se è già stata visitata, l'algoritmo 
 * ci si sposta comunque ma lascia il muro intatto.
*/

package codice.algoritmi;
import codice.*;

import java.util.Random;

public class AldousBroder implements MazeAlgorithm {
    private Random rand = new Random();
    private Cell current;
    private int unvisitedCount;
    private int rows, cols;

    @Override
    public void init(Cell[][] grid) {
        rows = grid.length;
        cols = grid[0].length;
        unvisitedCount = rows * cols;   // Calcola il numero totale di celle da scoprire

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x].visited = false;
                grid[y][x].isCurrent = false;
            }
        }

        // Seleziona una cella iniziale a caso per far partire la camminata e la segna come visitata
        int sx = rand.nextInt(cols);
        int sy = rand.nextInt(rows);
        current = grid[sy][sx];
        current.visited = true;
        unvisitedCount--;
        current.isCurrent = true;
    }

    @Override
    public boolean takeStep(Cell[][] grid) {
        if (unvisitedCount <= 0) {  // Esplorato tutte le celle
            if (current != null) current.isCurrent = false;
            return false;
        }

        current.isCurrent = false;

        // Nord - Sud - Est - Ovest
        int[] dx = {0, 0, 1, -1};
        int[] dy = {-1, 1, 0, 0};
        // Muri da abbattere
        int[] w1 = {0, 1, 2, 3}; 
        int[] w2 = {1, 0, 3, 2}; 

        int dir = rand.nextInt(4);
        int nx = current.x + dx[dir];   // Nuova x
        int ny = current.y + dy[dir];   // Nuova y

        while (nx < 0 || nx >= cols || ny < 0 || ny >= rows) {  // Controllo dei confini
            // Riprova finché non trova una cella accettabile
            dir = rand.nextInt(4);
            nx = current.x + dx[dir];
            ny = current.y + dy[dir];
        }

        Cell next = grid[ny][nx];

        if (!next.visited) {    // Se la cella è nuova
            current.walls[w1[dir]] = false;
            next.walls[w2[dir]] = false;
            next.visited = true;
            unvisitedCount--;
        }

        current = next;
        current.isCurrent = true;
        return true;
    }

    @Override
    public void generateFully(Cell[][] grid) {
        init(grid);
        while (takeStep(grid)) {
            // Esegue fino al completamento
        }
    }
}