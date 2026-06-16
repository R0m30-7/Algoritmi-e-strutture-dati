import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class MazeVisualizer extends JFrame {
    private final int ROWS = 30;
    private final int COLS = 30;
    private final int CELL_SIZE = 20;
    private final int MARGIN = 20;
    
    private Cell[][] grid;
    private MazeAlgorithm algorithm;
    private JPanel canvas;
    private JComboBox<String> algoSelector;
    private JButton btnAnimate;
    private JButton btnInstant;
    private JButton btnSolve; // Il nuovo pulsante di risoluzione

    // Lista che conterrà le celle del cammino risolutivo
    private List<Cell> solutionPath = new ArrayList<>();

    private MazeAlgorithm[] algorithms = { 
        new RandomizedDFS(), 
        new RandomizedKruskal(), 
        new RandomizedPrim(),
        new AldousBroder(),
        new WilsonsAlgorithm(),
        new RecursiveDivision(),
        new EllersAlgorithm()
    };

    public MazeVisualizer() {
        setTitle("Generatore e Risolutore di Labirinti - ASD");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        grid = new Cell[ROWS][COLS];
        resetGrid();

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.translate(MARGIN, MARGIN); 
                
                // 1. Disegna la griglia del labirinto
                for (int y = 0; y < ROWS; y++) {
                    for (int x = 0; x < COLS; x++) {
                        grid[y][x].draw(g2d, CELL_SIZE);
                    }
                }

                // 2. Disegna la striscia colorata della soluzione (se presente)
                if (solutionPath != null && !solutionPath.isEmpty()) {
                    g2d.setColor(new Color(33, 150, 243)); // Un bel blu acceso per la soluzione
                    g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); // Striscia spessa 5px
                    
                    for (int i = 0; i < solutionPath.size() - 1; i++) {
                        Cell c1 = solutionPath.get(i);
                        Cell c2 = solutionPath.get(i + 1);
                        
                        // Trova il centro della cella corrente
                        int x1 = c1.x * CELL_SIZE + CELL_SIZE / 2;
                        int y1 = c1.y * CELL_SIZE + CELL_SIZE / 2;
                        
                        // Trova il centro della cella successiva
                        int x2 = c2.x * CELL_SIZE + CELL_SIZE / 2;
                        int y2 = c2.y * CELL_SIZE + CELL_SIZE / 2;
                        
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }
            }
        };
        
        int canvasWidth = COLS * CELL_SIZE + (MARGIN * 2) + 1;
        int canvasHeight = ROWS * CELL_SIZE + (MARGIN * 2) + 1;
        canvas.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        canvas.setBackground(Color.WHITE);
        
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        
        String[] algoNames = { 
            "Randomized DFS", 
            "Randomized Kruskal", 
            "Randomized Prim", 
            "Aldous-Broder", 
            "Wilson's Algorithm",
            "Recursive Division",
            "Eller's Algorithm"
        };
        algoSelector = new JComboBox<>(algoNames);
        
        btnAnimate = new JButton("Generazione Animata");
        btnInstant = new JButton("Generazione Istantanea");
        btnSolve = new JButton("Risolvi Labirinto");
        btnSolve.setEnabled(false); // Disabilitato finché il labirinto non è pronto
        
        JPanel controls = new JPanel();
        controls.add(new JLabel("Algoritmo:"));
        controls.add(algoSelector);
        controls.add(btnAnimate);
        controls.add(btnInstant);
        controls.add(btnSolve);
        add(controls, BorderLayout.SOUTH);

        btnAnimate.addActionListener(e -> startAnimatedGeneration());
        
        btnInstant.addActionListener(e -> {
            resetGrid();
            btnSolve.setEnabled(false);
            algorithm = algorithms[algoSelector.getSelectedIndex()];
            algorithm.generateFully(grid);
            openEntranceAndExit();
            btnSolve.setEnabled(true); // Abilita il pulsante
            canvas.repaint();
        });

        btnSolve.addActionListener(e -> {
            solveMaze();
            canvas.repaint();
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void resetGrid() {
        solutionPath.clear();
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                grid[y][x] = new Cell(x, y);
            }
        }
    }

    private void openEntranceAndExit() {
        grid[0][0].walls[3] = false; // Ingresso a Ovest
        grid[ROWS - 1][COLS - 1].walls[2] = false; // Uscita a Est
    }

    private void startAnimatedGeneration() {
        resetGrid();
        btnSolve.setEnabled(false);
        btnAnimate.setEnabled(false);
        btnInstant.setEnabled(false);
        
        algorithm = algorithms[algoSelector.getSelectedIndex()];
        algorithm.init(grid);
        canvas.repaint();

        int estimatedSteps = ROWS * COLS * 2; 
        int calculatedDelay = 15000 / estimatedSteps;
        int delay = Math.max(1, Math.min(30, calculatedDelay));

        javax.swing.Timer timer = new javax.swing.Timer(delay, null);
        timer.addActionListener(e -> {
            boolean running = algorithm.takeStep(grid);
            canvas.repaint();
            if (!running) {
                timer.stop();
                openEntranceAndExit();
                btnSolve.setEnabled(true); // Abilita il pulsante a fine animazione
                btnAnimate.setEnabled(true);
                btnInstant.setEnabled(true);
                canvas.repaint();
                JOptionPane.showMessageDialog(this, "Labirinto Generato con Successo!");
            }
        });
        timer.start();
    }

    /**
     * Risolve il labirinto usando l'algoritmo BFS (Breadth-First Search)
     * partendo da [0][0] fino a [ROWS-1][COLS-1]
     */
    private void solveMaze() {
        solutionPath.clear();
        
        Cell start = grid[0][0];
        Cell end = grid[ROWS - 1][COLS - 1];
        
        Queue<Cell> queue = new LinkedList<>();
        boolean[][] visited = new boolean[ROWS][COLS];
        Cell[][] parent = new Cell[ROWS][COLS]; // Per ricostruire il cammino a ritroso
        
        queue.add(start);
        visited[0][0] = true;
        
        boolean found = false;
        
        while (!queue.isEmpty()) {
            Cell curr = queue.poll();
            
            if (curr == end) {
                found = true;
                break;
            }
            
            // Controlla i 4 vicini possibili basandosi sui muri ABBATTUTI (walls == false)
            // Nord (indice 0)
            if (!curr.walls[0] && curr.y > 0 && !visited[curr.y - 1][curr.x]) {
                queue.add(grid[curr.y - 1][curr.x]);
                visited[curr.y - 1][curr.x] = true;
                parent[curr.y - 1][curr.x] = curr;
            }
            // Sud (indice 1)
            if (!curr.walls[1] && curr.y < ROWS - 1 && !visited[curr.y + 1][curr.x]) {
                queue.add(grid[curr.y + 1][curr.x]);
                visited[curr.y + 1][curr.x] = true;
                parent[curr.y + 1][curr.x] = curr;
            }
            // Est (indice 2)
            if (!curr.walls[2] && curr.x < COLS - 1 && !visited[curr.y][curr.x + 1]) {
                queue.add(grid[curr.y][curr.x + 1]);
                visited[curr.y][curr.x + 1] = true;
                parent[curr.y][curr.x + 1] = curr;
            }
            // Ovest (indice 3)
            if (!curr.walls[3] && curr.x > 0 && !visited[curr.y][curr.x - 1]) {
                queue.add(grid[curr.y][curr.x - 1]);
                visited[curr.y][curr.x - 1] = true;
                parent[curr.y][curr.x - 1] = curr;
            }
        }
        
        // Se il percorso è stato trovato, lo ricostruisce partendo dalla fine
        if (found) {
            Cell curr = end;
            while (curr != null) {
                solutionPath.add(0, curr); // Inserisce in testa per avere l'ordine corretto da start a end
                curr = parent[curr.y][curr.x];
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeVisualizer().setVisible(true));
    }
}