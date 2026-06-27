package codice.algoritmi;
import codice.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WilsonsAlgorithm implements MazeAlgorithm {
    private boolean[][] inMaze;
    private boolean[][] inWalk; // Matrice O(1) per il tracciamento interno del ciclo
    private ArrayList<Cell> currentWalk;
    private Random rand;
    private boolean walking;
    private Cell currentCell;
    private int rows, cols;
    private int cellsInMaze;

    @Override
    public void init(Cell[][] grid) {
        rows = grid.length;
        cols = grid[0].length;
        inMaze = new boolean[rows][cols];
        inWalk = new boolean[rows][cols];
        currentWalk = new ArrayList<>();
        rand = new Random();
        walking = false;
        currentCell = null;

        // Inserisce la prima cella radice nel labirinto permanente
        int startX = rand.nextInt(cols);
        int startY = rand.nextInt(rows);
        inMaze[startY][startX] = true;
        cellsInMaze = 1;
    }

    @Override
    public boolean takeStep(Cell[][] grid) {
        // Se il labirinto è completo, spegniamo l'ultima testina arancione e terminiamo
        if (cellsInMaze >= rows * cols) {
            if (currentCell != null) {
                currentCell.isCurrent = false;
            }
            return false; 
        }

        // Spegniamo la testina arancione sulla cella del passo precedente
        if (currentCell != null) {
            currentCell.isCurrent = false;
        }

        // FASE 1: Inizio di un nuovo cammino stocastico
        if (!walking) {
            currentCell = getRandomUnvisitedCell(grid);
            currentWalk.clear();
            currentWalk.add(currentCell);
            inWalk[currentCell.y][currentCell.x] = true;
            
            // RENDERING: Accendiamo la scia (grigia) e la testa dell'omino (arancione)
            currentCell.visited = true;
            currentCell.isCurrent = true;
            
            walking = true;
            return true;
        }

        // FASE 2: Avanzamento di un singolo passo verso un vicino casuale
        Cell neighbor = getRandomNeighbor(currentCell, grid);

        if (inMaze[neighbor.y][neighbor.x]) {
            // CASO A: Il cammino si connette al labirinto stabile!
            breakWall(currentCell, neighbor); 
            currentWalk.add(neighbor);
            
            // Consolidiamo il ramo: i muri rimangono aperti e lo sfondo torna bianco (default)
            for (int i = 0; i < currentWalk.size() - 1; i++) {
                Cell c = currentWalk.get(i);
                inMaze[c.y][c.x] = true;
                inWalk[c.y][c.x] = false; 
                
                // RENDERING: Ripristino dello sfondo di default come richiesto
                c.visited = false;
                c.isCurrent = false;
                
                cellsInMaze++;
            }
            currentWalk.clear();
            walking = false;
            currentCell = null; 

        } else if (inWalk[neighbor.y][neighbor.x]) {
            // CASO B: Loop-Erasure (L'omino interseca la sua stessa scia)
            // Cancelliamo il cappio, rialziamo i muri e spegniamo i colori in tempo reale
            while (currentWalk.get(currentWalk.size() - 1) != neighbor) {
                Cell removed = currentWalk.remove(currentWalk.size() - 1);
                inWalk[removed.y][removed.x] = false;
                
                // RENDERING: Spegniamo la scia e la testa sulle celle rimosse dal ciclo
                removed.visited = false;
                removed.isCurrent = false;
                
                Cell previous = currentWalk.get(currentWalk.size() - 1);
                repairWall(previous, removed);
            }
            currentCell = neighbor;
            currentCell.isCurrent = true; // La testa arancione si posiziona sul punto di giunzione

        } else {
            // CASO C: Avanzamento standard nello spazio vuoto
            breakWall(currentCell, neighbor); 
            currentWalk.add(neighbor);
            inWalk[neighbor.y][neighbor.x] = true;
            
            // RENDERING: Coloriamo la nuova cella del cammino e spostiamo la testa arancione
            neighbor.visited = true;
            neighbor.isCurrent = true;
            
            currentCell = neighbor;
        }

        return cellsInMaze < rows * cols;
    }

    @Override
    public void generateFully(Cell[][] grid) {
        init(grid);
        while (takeStep(grid)) {
            // Sfrutta la medesima logica strutturale a velocità nativa per i benchmark
        }
    }

    private Cell getRandomUnvisitedCell(Cell[][] grid) {
        ArrayList<Cell> unvisited = new ArrayList<>();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (!inMaze[y][x]) unvisited.add(grid[y][x]);
            }
        }
        return unvisited.get(rand.nextInt(unvisited.size()));
    }

    private Cell getRandomNeighbor(Cell cell, Cell[][] grid) {
        ArrayList<Cell> neighbors = new ArrayList<>();
        if (cell.y > 0) neighbors.add(grid[cell.y - 1][cell.x]);
        if (cell.y < rows - 1) neighbors.add(grid[cell.y + 1][cell.x]);
        if (cell.x < cols - 1) neighbors.add(grid[cell.y][cell.x + 1]);
        if (cell.x > 0) neighbors.add(grid[cell.y][cell.x - 1]);
        return neighbors.get(rand.nextInt(neighbors.size()));
    }

    private void breakWall(Cell c1, Cell c2) {
        if (c1.x == c2.x) {
            if (c1.y > c2.y) { c1.walls[0] = false; c2.walls[1] = false; }
            else { c1.walls[1] = false; c2.walls[0] = false; }
        } else {
            if (c1.x > c2.x) { c1.walls[3] = false; c2.walls[2] = false; }
            else { c1.walls[2] = false; c2.walls[3] = false; }
        }
    }

    private void repairWall(Cell c1, Cell c2) {
        if (c1.x == c2.x) {
            if (c1.y > c2.y) { c1.walls[0] = true; c2.walls[1] = true; }
            else { c1.walls[1] = true; c2.walls[0] = true; }
        } else {
            if (c1.x > c2.x) { c1.walls[3] = true; c2.walls[2] = true; }
            else { c1.walls[2] = true; c2.walls[3] = true; }
        }
    }
}