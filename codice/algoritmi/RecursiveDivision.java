/**
 * Questo algoritmo parte da uno spazio totalmente aperto (vuoto) 
 * e aggiunge progressivamente muri per partizionare l'area in sotto-regioni sempre più piccole.
 * Ogni volta che traccia un muro divisorio, apre un singolo varco casuale al suo interno 
 * per mantenere il labirinto perfettamente interconnesso ed evitare vicoli ciechi isolati.
*/

package codice.algoritmi;
import codice.*;

import java.util.Stack;
import java.util.Random;

public class RecursiveDivision implements MazeAlgorithm {
    
    /**
     * Rappresenta una sotto-regione rettangolare all'interno del labirinto.
     * Delimita lo spazio geometrico che l'algoritmo deve esaminare e dividere, 
     * tracciato tramite le coordinate dell'angolo superiore sinistro (x, y) e le dimensioni (w, h).
    */
    private static class Region {
        int x, y, w, h;
        Region(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

    private Stack<Region> stack = new Stack<>();    // Coda LIFO
    private Random rand = new Random();

    @Override
    public void init(Cell[][] grid) {
        stack.clear();
        int rows = grid.length;
        int cols = grid[0].length;

        // Abbattimento di tutti i muri
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x].visited = true;
                grid[y][x].isCurrent = false;
                
                grid[y][x].walls[0] = (y == 0);          // Nord
                grid[y][x].walls[1] = (y == rows - 1);   // Sud
                grid[y][x].walls[2] = (x == cols - 1);   // Est
                grid[y][x].walls[3] = (x == 0);          // Ovest
            }
        }
        // Inizializzazione dello stack
        stack.push(new Region(0, 0, cols, rows));
    }

    @Override
    public boolean takeStep(Cell[][] grid) {
        if (stack.isEmpty()) return false;

        Region reg = stack.pop();

        if (reg.w < 2 || reg.h < 2) {    // Regione troppo piccola per essere divisa
            return true;
        }

        boolean horizontal = chooseOrientation(reg.w, reg.h);

        if (horizontal) {
            // Genera un muro orizzontale a una coordinata y all'interno della regione
            int yWall = reg.y + rand.nextInt(reg.h - 1);
            int passageX = reg.x + rand.nextInt(reg.w);

            for (int x = reg.x; x < reg.x + reg.w; x++) {
                if (x != passageX) {
                    grid[yWall][x].walls[1] = true;      // Muro Sud della cella sopra
                    grid[yWall + 1][x].walls[0] = true;  // Muro Nord della cella sotto
                }
            }

            // Inserisce le due sotto-regioni nello stack
            stack.push(new Region(reg.x, reg.y, reg.w, yWall - reg.y + 1));
            stack.push(new Region(reg.x, yWall + 1, reg.w, reg.y + reg.h - (yWall + 1)));
        } else {
            // Genera un muro verticale a una coordinata x all'interno della regione
            int xWall = reg.x + rand.nextInt(reg.w - 1);
            int passageY = reg.y + rand.nextInt(reg.h);

            for (int y = reg.y; y < reg.y + reg.h; y++) {
                if (y != passageY) {
                    grid[y][xWall].walls[2] = true;      // Muro Est della cella a sinistra
                    grid[y][xWall + 1].walls[3] = true;  // Muro Ovest della cella a destra
                }
            }

            // Inserisce le due sotto-regioni nello stack
            stack.push(new Region(reg.x, reg.y, xWall - reg.x + 1, reg.h));
            stack.push(new Region(xWall + 1, reg.y, reg.x + reg.w - (xWall + 1), reg.h));
        }

        return true;
    }

    private boolean chooseOrientation(int width, int height) {
        if (width < height) return true;    // Divisione orizzontale se è più alta che larga
        if (height < width) return false;   // Dividi verticale se è più larga che alta
        return rand.nextBoolean();
    }

    @Override
    public void generateFully(Cell[][] grid) {
        init(grid);
        while (takeStep(grid)) {

        }
    }
}