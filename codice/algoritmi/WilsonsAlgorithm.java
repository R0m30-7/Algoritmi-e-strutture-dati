package codice.algoritmi;
import codice.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WilsonsAlgorithm implements MazeAlgorithm {
    private boolean[][] inMaze;
    private boolean[][] inWalk; // Matrice O(1) per il tracciamento istantaneo del ciclo
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

        // Inserisce una prima cella casuale nel labirinto (albero iniziale)
        int startX = rand.nextInt(cols);
        int startY = rand.nextInt(rows);
        inMaze[startY][startX] = true;
        cellsInMaze = 1;
    }

    @Override
    public boolean takeStep(Cell[][] grid) {
        if (cellsInMaze >= rows * cols) {
            return false; // Labirinto completato
        }

        if (!walking) {
            // Seleziona una cella di partenza fuori dal labirinto
            currentCell = getRandomUnvisitedCell(grid);
            currentWalk.clear();
            currentWalk.add(currentCell);
            inWalk[currentCell.y][currentCell.x] = true;
            walking = true;
            return true;
        }

        Cell neighbor = getRandomNeighbor(currentCell, grid);

        if (inMaze[neighbor.y][neighbor.x]) {
            // Successo: la passeggiata ha intersecato il labirinto esistente
            currentWalk.add(neighbor);
            for (int i = 0; i < currentWalk.size() - 1; i++) {
                Cell c1 = currentWalk.get(i);
                Cell c2 = currentWalk.get(i + 1);
                breakWall(c1, c2); // Abbate i muri nel tuo codice originale
                inMaze[c1.y][c1.x] = true;
                inWalk[c1.y][c1.x] = false; // Reset dello stato di passeggiata
                cellsInMaze++;
            }
            currentWalk.clear();
            walking = false;
        } else if (inWalk[neighbor.y][neighbor.x]) {
            // OTTIMIZZAZIONE O(1): Ciclo rilevato all'istante senza indexOf!
            // Rimuoviamo gli elementi dalla coda dell'ArrayList fino al punto di intersezione
            while (currentWalk.get(currentWalk.size() - 1) != neighbor) {
                Cell removed = currentWalk.remove(currentWalk.size() - 1);
                inWalk[removed.y][removed.x] = false;
            }
            currentCell = neighbor; // Riparte dal punto in cui il cappio si è chiuso
        } else {
            // Avanzamento normale nella passeggiata casuale
            currentWalk.add(neighbor);
            inWalk[neighbor.y][neighbor.x] = true;
            currentCell = neighbor;
        }

        return true;
    }

    @Override
    public void generateFully(Cell[][] grid) {
        init(grid);
        while (takeStep(grid)) {
            // Cicla fino a totale convergenza
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
}