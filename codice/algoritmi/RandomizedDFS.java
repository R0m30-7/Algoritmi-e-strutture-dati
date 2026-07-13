package codice.algoritmi;
import codice.*;

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizedDFS implements MazeAlgorithm {
    private Stack<Cell> stack = new Stack<>();  // Si usa la pila per la proprietà LIFO
    private Random rand = new Random();
    private Cell current;

    @Override
    public void init(Cell[][] grid) {
        stack.clear();
        // Inizializziamo tutte le celle come non visitate
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j].visited = false;
                grid[i][j].isCurrent = false;
            }
        }
        // Aggiunge la cella iniziale allo stack e la segna come visitata
        current = grid[0][0];
        current.visited = true;
        stack.push(current);
    }

    @Override
    public boolean takeStep(Cell[][] grid) {
        if (stack.isEmpty()) {  // Interrompe la visualizzazione della cella corrente nell'animazione
            if (current != null) current.isCurrent = false;
            return false;
        }

        current.isCurrent = false;
        current = stack.peek();
        current.isCurrent = true;

        // Salvo le celle vicine non ancora visitate nella lista
        List<Cell> neighbors = getUnvisitedNeighbors(current, grid);

        if (!neighbors.isEmpty()) {
            // Scelta un vicino casualmente non ancora visitato
            Cell next = neighbors.get(rand.nextInt(neighbors.size()));
            removeWalls(current, next); // Rimozione dei muri tra la cella corrente e quella scelta
            next.visited = true;
            stack.push(next);           // Aggiungiamo la nuova cella in cima allo stack
        } else {
            stack.pop();                // Vicolo cieco: torniamo indietro
        }
        return true;
    }

    @Override
    public void generateFully(Cell[][] grid) {  // Generazione istantanea
        init(grid);
        while (takeStep(grid)) {
            // Il ciclo gira finché ci sono ancora celle nello stack
        }
        if (current != null) current.isCurrent = false;
    }

    private List<Cell> getUnvisitedNeighbors(Cell c, Cell[][] grid) {
        List<Cell> neighbors = new ArrayList<>();
        int rows = grid.length;
        int cols = grid[0].length;

        // Se le celle sono dentro la griglia e non sono visitate, vengono aggiunte alla lista
        if (c.y > 0 && !grid[c.y - 1][c.x].visited) neighbors.add(grid[c.y - 1][c.x]);          // Nord
        if (c.y < rows - 1 && !grid[c.y + 1][c.x].visited) neighbors.add(grid[c.y + 1][c.x]);   // Sud
        if (c.x < cols - 1 && !grid[c.y][c.x + 1].visited) neighbors.add(grid[c.y][c.x + 1]);   // Est
        if (c.x > 0 && !grid[c.y][c.x - 1].visited) neighbors.add(grid[c.y][c.x - 1]);          // Ovest

        return neighbors;
    }

    private void removeWalls(Cell c1, Cell c2) {
        // Capire in che direzione si trova la cella successiva
        int dx = c1.x - c2.x;
        int dy = c1.y - c2.y;

        if (dx == 1) { c1.walls[3] = false; c2.walls[2] = false; }          // c2 è a sinistra di c1
        else if (dx == -1) { c1.walls[2] = false; c2.walls[3] = false; }    // c2 è a destra di c1
        if (dy == 1) { c1.walls[0] = false; c2.walls[1] = false; }          // c2 è sopra c1
        else if (dy == -1) { c1.walls[1] = false; c2.walls[0] = false; }    // c2 è sotto c1
    }
}