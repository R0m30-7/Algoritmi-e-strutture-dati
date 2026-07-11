package codice;

public interface MazeAlgorithm {
    void init(Cell[][] grid);
    boolean takeStep(Cell[][] grid);    // Restituisce false quando l'algoritmo ha finito
    void generateFully(Cell[][] grid);
}