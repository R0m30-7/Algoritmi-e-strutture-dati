package codice.algoritmi;
import codice.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WilsonsAlgorithm implements MazeAlgorithm {
    private Random rand = new Random();
    private boolean[][] inTree;
    private List<Cell> walkPath = new ArrayList<>();
    private Cell walker;
    private int rows, cols;

    @Override
    public void init(Cell[][] grid) {
        rows = grid.length;
        cols = grid[0].length;
        inTree = new boolean[rows][cols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x].visited = false;
                grid[y][x].isCurrent = false;
            }
        }

        int tx = rand.nextInt(cols);
        int ty = rand.nextInt(rows);
        inTree[ty][tx] = true;
        grid[ty][tx].visited = true;

        walker = null;
        walkPath.clear();
    }

    @Override
    public boolean takeStep(Cell[][] grid) {
        if (isTreeFull()) {
            clearCurrentFlags(grid);
            return false;
        }

        if (walker == null) {
            walker = getRandomUnvisitedCell(grid);
            if (walker == null) return false;
            walkPath.add(walker);
            walker.isCurrent = true;
            return true;
        }

        int[] dx = {0, 0, 1, -1};
        int[] dy = {-1, 1, 0, 0};
        int dir = rand.nextInt(4);
        int nx = walker.x + dx[dir];
        int ny = walker.y + dy[dir];

        while (nx < 0 || nx >= cols || ny < 0 || ny >= rows) {
            dir = rand.nextInt(4);
            nx = walker.x + dx[dir];
            ny = walker.y + dy[dir];
        }

        Cell next = grid[ny][nx];
        int loopIndex = walkPath.indexOf(next);

        if (loopIndex != -1) {
            for (int i = walkPath.size() - 1; i > loopIndex; i--) {
                walkPath.get(i).isCurrent = false;
            }
            walkPath.subList(loopIndex + 1, walkPath.size()).clear();
            walker = next;
        } else {
            walker = next;
            walkPath.add(walker);
            walker.isCurrent = true;
        }

        if (inTree[walker.y][walker.x]) {
            for (int i = 0; i < walkPath.size() - 1; i++) {
                Cell c1 = walkPath.get(i);
                Cell c2 = walkPath.get(i + 1);
                removeWalls(c1, c2);
                inTree[c1.y][c1.x] = true;
                c1.visited = true;
                c1.isCurrent = false;
            }
            inTree[walker.y][walker.x] = true;
            walker.isCurrent = false;
            walkPath.clear();
            walker = null;
        }

        return true;
    }

    @Override
    public void generateFully(Cell[][] grid) {
        init(grid);
        while (takeStep(grid)) {
            // Esegue loop di campionamento continuo
        }
    }

    private boolean isTreeFull() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (!inTree[y][x]) return false;
            }
        }
        return true;
    }

    private Cell getRandomUnvisitedCell(Cell[][] grid) {
        List<Cell> candidates = new ArrayList<>();
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (!inTree[y][x]) candidates.add(grid[y][x]);
            }
        }
        if (candidates.isEmpty()) return null;
        return candidates.get(rand.nextInt(candidates.size()));
    }

    private void removeWalls(Cell c1, Cell c2) {
        int dx = c1.x - c2.x;
        int dy = c1.y - c2.y;
        if (dx == 1) { c1.walls[3] = false; c2.walls[2] = false; }
        else if (dx == -1) { c1.walls[2] = false; c2.walls[3] = false; }
        if (dy == 1) { c1.walls[0] = false; c2.walls[1] = false; }
        else if (dy == -1) { c1.walls[1] = false; c2.walls[0] = false; }
    }

    private void clearCurrentFlags(Cell[][] grid) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x].isCurrent = false;
            }
        }
    }
}