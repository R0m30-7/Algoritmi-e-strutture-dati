import javax.swing.*;
import java.awt.*;

public class MazeVisualizer extends JFrame {
    private final int ROWS = 30;
    private final int COLS = 30;
    private final int CELL_SIZE = 20;
    
    private Cell[][] grid;
    private MazeAlgorithm algorithm;
    private JPanel canvas;

    public MazeVisualizer() {
        setTitle("Generatore di Labirinti - ASD");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        grid = new Cell[ROWS][COLS];
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                grid[y][x] = new Cell(x, y);
            }
        }

        algorithm = new RandomizedDFS();

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
        
        JButton btnAnimate = new JButton("Generazione Animata");
        JButton btnInstant = new JButton("Generazione Istantanea");
        
        JPanel controls = new JPanel();
        controls.add(btnAnimate);
        controls.add(btnInstant);
        add(controls, BorderLayout.SOUTH);

        btnAnimate.addActionListener(e -> startAnimatedGeneration());
        btnInstant.addActionListener(e -> {
            algorithm.generateFully(grid);
            canvas.repaint();
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void startAnimatedGeneration() {
        algorithm.init(grid);
        canvas.repaint();

        // Calcolo del delay dinamico per un massimo di 15 secondi (15000 ms)
        int estimatedSteps = ROWS * COLS * 2; 
        int calculatedDelay = 15000 / estimatedSteps;
        int delay = Math.max(1, Math.min(30, calculatedDelay)); // cap compreso tra 1 e 30ms

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