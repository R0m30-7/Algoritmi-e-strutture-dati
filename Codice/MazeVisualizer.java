import javax.swing.*;
import java.awt.*;

public class MazeVisualizer extends JFrame {
    private final int ROWS = 30;
    private final int COLS = 30;
    private final int CELL_SIZE = 20;
    private final int MARGIN = 20; // Margine di sicurezza per non nascondere i bordi
    
    private Cell[][] grid;
    private MazeAlgorithm algorithm;
    private JPanel canvas;
    private JComboBox<String> algoSelector;
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
        setTitle("Generatore di Labirinti - ASD");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        grid = new Cell[ROWS][COLS];
        resetGrid();

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Attiva l'antialiasing per linee più pulite
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sposta l'origine del disegno per creare il margine protettivo all'interfaccia
                g2d.translate(MARGIN, MARGIN); 
                
                for (int y = 0; y < ROWS; y++) {
                    for (int x = 0; x < COLS; x++) {
                        grid[y][x].draw(g2d, CELL_SIZE);
                    }
                }
            }
        };
        
        // Dimensione aumentata di (MARGIN * 2) per accogliere la spaziatura protettiva sui 4 lati
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
            openEntranceAndExit(); // Abbate i muri perimetrali di inizio e fine
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

    /**
     * Identifica l'inizio (Nord-Ovest) e la fine (Sud-Est) del labirinto
     * e ne abbatte i rispettivi muri esterni di confine.
     */
    private void openEntranceAndExit() {
        // Ingresso in alto a sinistra (0,0): abbattiamo il muro Ovest (indice 3)
        grid[0][0].walls[3] = false;
        
        // Uscita in basso a destra (ROWS-1, COLS-1): abbattiamo il muro Est (indice 2)
        grid[ROWS - 1][COLS - 1].walls[2] = false;
    }

    private void startAnimatedGeneration() {
        resetGrid();
        algorithm = algorithms[algoSelector.getSelectedIndex()];
        algorithm.init(grid);
        canvas.repaint();

        int estimatedSteps = ROWS * COLS * 2; 
        int calculatedDelay = 15000 / estimatedSteps;
        int delay = Math.max(1, Math.min(30, calculatedDelay));

        Timer timer = new Timer(delay, null);
        timer.addActionListener(e -> {
            boolean running = algorithm.takeStep(grid);
            canvas.repaint();
            if (!running) {
                timer.stop();
                openEntranceAndExit(); // Apertura dei varchi a fine animazione
                canvas.repaint();
                JOptionPane.showMessageDialog(this, "Labirinto Generato con Successo!");
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeVisualizer().setVisible(true));
    }
}