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
        unvisitedCount = rows * cols;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x].visited = false;
                grid[y][x].isCurrent = false;
            }
        }

        int sx = rand.nextInt(cols);
        int sy = rand.nextInt(rows);
        current = grid[sy][sx];
        current.visited = true;
        unvisitedCount--;
        current.isCurrent = true;
    }

    @Override
    public boolean takeStep(Cell[][] grid) {
        if (unvisitedCount <= 0) {
            if (current != null) current.isCurrent = false;
            return false;
        }

        current.isCurrent = false;

        int[] dx = {0, 0, 1, -1};
        int[] dy = {-1, 1, 0, 0};
        int[] w1 = {0, 1, 2, 3}; 
        int[] w2 = {1, 0, 3, 2}; 

        int dir = rand.nextInt(4);
        int nx = current.x + dx[dir];
        int ny = current.y + dy[dir];

        while (nx < 0 || nx >= cols || ny < 0 || ny >= rows) {
            dir = rand.nextInt(4);
            nx = current.x + dx[dir];
            ny = current.y + dy[dir];
        }

        Cell next = grid[ny][nx];

        if (!next.visited) {
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