package codice;

import java.util.ArrayList;
import java.util.List;
import codice.algoritmi.*;

public class MazeBenchmark {

    private static final int[] SIZES = {10, 20, 30, 50, 100};
    private static final int WARMUP_ITERATIONS = 100;
    private static final int MEASURE_ITERATIONS = 50;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   AVVIO BENCHMARK ALGORITMI DI GENERAZIONE      ");
        System.out.println("==================================================");

        MazeAlgorithm[] algorithms = {
            new RandomizedDFS(),
            new RandomizedKruskal(),
            new RandomizedPrim(),
            new AldousBroder(),
            new WilsonsAlgorithm(),
            new RecursiveDivision(),
            new EllersAlgorithm()
        };

        String[] algoNames = {
            "Randomized DFS",
            "Randomized Kruskal",
            "Randomized Prim",
            "Aldous-Broder",
            "Wilson's Algorithm",
            "Recursive Division",
            "Eller's Algorithm"
        };

        // 1. Fase di Verifica Correttezza Preventiva
        System.out.println("\n[1/3] Verifica preventiva di correttezza in corso...");
        for (int i = 0; i < algorithms.length; i++) {
            boolean isCorrect = verifyAlgorithm(algorithms[i]);
            if (!isCorrect) {
                System.err.println("ERRORE: L'algoritmo " + algoNames[i] + " ha generato un labirinto non valido!");
                System.exit(1);
            }
            System.out.println("  -> " + algoNames[i] + ": SUPERATO");
        }

        // 2. Fase di Warm-up per stabilizzare il JIT Compiler della JVM
        System.out.println("\n[2/3] Fase di JIT Warm-up avviata (attendere...)");
        for (MazeAlgorithm algo : algorithms) {
            runWarmup(algo);
        }
        System.out.println("  -> Warm-up completato. Codice nativo ottimizzato.");

        // 3. Fase di Misurazione Reale delle Prestazioni
        System.out.println("\n[3/3] Raccolta dati sperimentali (Tempi medi in ms):");
        System.out.printf("%-20s %-10s %-10s %-10s %-10s %-10s\n", "Algoritmo", "10x10", "20x20", "30x30", "50x50", "100x100");
        System.out.println("----------------------------------------------------------------------");

        for (int a = 0; a < algorithms.length; a++) {
            System.out.printf("%-20s ", algoNames[a]);
            for (int size : SIZES) {
                double avgTime = measurePerformance(algorithms[a], size);
                System.out.printf("%-10.4f ", avgTime);
            }
            System.out.println();
        }
        System.out.println("======================================================\n");
    }

    private static boolean verifyAlgorithm(MazeAlgorithm algo) {
        int size = 20;
        Cell[][] grid = createEmptyGrid(size, size);
        algo.generateFully(grid);

        // Verifica strutturale: conta quanti muri sono stati abbattuti in totale
        // Per uno Spanning Tree perfetto su una griglia, gli archi aperti devono essere esattamente V - 1
        int totalCells = size * size;
        int brokenWalls = 0;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (x < size - 1 && !grid[y][x].walls[2]) brokenWalls++; // Muro Est abbattuto
                if (y < size - 1 && !grid[y][x].walls[1]) brokenWalls++; // Muro Sud abbattuto
            }
        }

        return brokenWalls == (totalCells - 1);
    }

    private static void runWarmup(MazeAlgorithm algo) {
        // Esegue molte iterazioni su griglie piccole per forzare la JVM a compilare in C2 (ottimizzazione massima)
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            Cell[][] grid = createEmptyGrid(15, 15);
            algo.generateFully(grid);
        }
    }

    private static double measurePerformance(MazeAlgorithm algo, int size) {
        long totalDurationNano = 0;

        for (int i = 0; i < MEASURE_ITERATIONS; i++) {
            Cell[][] grid = createEmptyGrid(size, size);
            
            long startTime = System.nanoTime();
            algo.generateFully(grid);
            long endTime = System.nanoTime();
            
            totalDurationNano += (endTime - startTime);
        }

        // Calcola il tempo medio convertendolo da nanosecondi a millisecondi
        double avgNano = (double) totalDurationNano / MEASURE_ITERATIONS;
        return avgNano / 1_000_000.0;
    }

    private static Cell[][] createEmptyGrid(int rows, int cols) {
        Cell[][] grid = new Cell[rows][cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x] = new Cell(x, y);
            }
        }
        return grid;
    }
}