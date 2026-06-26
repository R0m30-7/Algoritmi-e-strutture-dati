package codice.algoritmi;
import codice.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizedPrim implements MazeAlgorithm {

    private static class PrimWall {
        Cell inCell;  // Cella già nel labirinto
        Cell outCell; // Cella potenzialmente fuori dal labirinto
        int wallIn, wallOut;

        PrimWall(Cell inCell, Cell outCell, int wallIn, int wallOut) {
            this.inCell = inCell;
            this.outCell = outCell;
            this.wallIn = wallIn;
            this.wallOut = wallOut;
        }
    }

    private List<PrimWall> frontier = new ArrayList<>();
    private Random rand = new Random();

    @Override
    public void init(Cell[][] grid) {
        frontier.clear();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                grid[y][x].visited = false;
                grid[y][x].isCurrent = false;
            }
        }

        // Sceglie una cella di partenza casuale
        int startX = rand.nextInt(grid[0].length);
        int startY = rand.nextInt(grid.length);
        Cell start = grid[startY][startX];
        start.visited = true;

        addFrontier(start, grid);
    }

    @Override
    public boolean takeStep(Cell[][] grid) {
        if (frontier.isEmpty()) {
            return false;
        }

        // Sceglie un muro di frontiera a caso
        int index = rand.nextInt(frontier.size());
        PrimWall pw = frontier.remove(index);

        if (!pw.outCell.visited) {
            pw.inCell.walls[pw.wallIn] = false;
            pw.outCell.walls[pw.wallOut] = false;
            pw.outCell.visited = true;
            
            // La cella estratta diventa la corrente per l'effetto grafico
            pw.outCell.isCurrent = true;
            addFrontier(pw.outCell, grid);
        }

        return true;
    }

    @Override
    public void generateFully(Cell[][] grid) {
        init(grid);
        while (takeStep(grid)) {
            // Sceglie ed esegue senza pause
        }
        // Pulisce l'evidenziazione grafica finale
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                grid[y][x].isCurrent = false;
            }
        }
    }

    private void addFrontier(Cell c, Cell[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Nord
        if (c.y > 0 && !grid[c.y - 1][c.x].visited) {
            frontier.add(new PrimWall(c, grid[c.y - 1][c.x], 0, 1));
        }
        // Sud
        if (c.y < rows - 1 && !grid[c.y + 1][c.x].visited) {
            frontier.add(new PrimWall(c, grid[c.y + 1][c.x], 1, 0));
        }
        // Est
        if (c.x < cols - 1 && !grid[c.y][c.x + 1].visited) {
            frontier.add(new PrimWall(c, grid[c.y][c.x + 1], 2, 3));
        }
        // Ovest
        if (c.x > 0 && !grid[c.y][c.x - 1].visited) {
            frontier.add(new PrimWall(c, grid[c.y][c.x - 1], 3, 2));
        }
    }
}