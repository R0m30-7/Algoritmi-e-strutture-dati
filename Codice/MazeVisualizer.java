import javax.swing.*;
import java.awt.*;

public class MazeVisualizer extends JFrame {
    private final int ROWS = 30;
    private final int COLS = 30;
    private final int CELL_SIZE = 20;
    
    private Cell[][] grid;
    private MazeAlgorithm algorithm;
    private JPanel canvas;

    // Sostituisci la dichiarazione della variabile d'istanza algoritmica con una combo box:
    private JComboBox<String> algoSelector;
    private MazeAlgorithm[] algorithms = { new RandomizedDFS(), new RandomizedKruskal(), new RandomizedPrim() };

    // Modifica il costruttore di MazeVisualizer:
    public MazeVisualizer() {
        setTitle("Generatore di Labirinti - ASD");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        grid = new Cell[ROWS][COLS];
        resetGrid();

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int y = 0; y < ROWS; y++) {
                    for (int x = 0; x < COLS; x++) {
                        grid[y][x].draw(g, CELL_SIZE);
                    }
                }
            }
        };
        canvas.setPreferredSize(new Dimension(COLS * CELL_SIZE + 1, ROWS * CELL_SIZE + 1));
        
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        
        String[] algoNames = { "Randomized DFS", "Randomized Kruskal", "Randomized Prim" };
        algoSelector = new JComboBox<>(algoNames);
        
        JButton btnAnimate = new JButton("Generazione Animata");
        JButton btnInstant = new JButton("Generazione Istantanea");
        
        JPanel controls = new JPanel();
        controls.add(new JLabel("Algoritmo:"));
        controls.add(algoSelector);
        controls.add(btnAnimate);
        controls.add(btnInstant);
        add(controls, BorderLayout.SOUTH);

        btnAnimate.addActionListener(e -> startAnimatedGeneration());
        btnInstant.addActionListener(e -> {
            resetGrid();
            algorithm = algorithms[algoSelector.getSelectedIndex()];
            algorithm.generateFully(grid);
            canvas.repaint();
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void resetGrid() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                grid[y][x] = new Cell(x, y);
            }
        }
    }

    private void startAnimatedGeneration() {
        resetGrid();
        algorithm = algorithms[algoSelector.getSelectedIndex()];
        algorithm.init(grid);
        canvas.repaint();

        int estimatedSteps = ROWS * COLS * 2; 
        int calculatedDelay = 15000 / estimatedSteps;
        int delay = Math.max(1, Math.min(15, calculatedDelay));

        Timer timer = new Timer(delay, null);
        timer.addActionListener(e -> {
            boolean running = algorithm.takeStep(grid);
            canvas.repaint();
            if (!running) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Labirinto Generato!");
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeVisualizer().setVisible(true));
    }
}