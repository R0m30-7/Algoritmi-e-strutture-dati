package codice;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import codice.algoritmi.*;

public class MazeVisualizer extends JFrame {
    private int rows = 30; 
    private int cols = 30; 
    private final int CELL_SIZE = 20;
    private final int MARGIN = 20;
    
    private Cell[][] grid;
    private MazeAlgorithm algorithm;
    private JPanel canvas;
    
    private JComboBox<String> algoSelector;
    private JSpinner spinnerCols; 
    private JSpinner spinnerRows; 
    private JButton btnAnimate;
    private JButton btnInstant;
    private JButton btnSolve;

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
        
        grid = new Cell[rows][cols];
        resetGrid();

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.translate(MARGIN, MARGIN); 
                
                for (int y = 0; y < rows; y++) {
                    for (int x = 0; x < cols; x++) {
                        grid[y][x].draw(g2d, CELL_SIZE);
                    }
                }

                if (solutionPath != null && !solutionPath.isEmpty()) {
                    g2d.setColor(new Color(33, 150, 243)); 
                    g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); 
                    
                    for (int i = 0; i < solutionPath.size() - 1; i++) {
                        Cell c1 = solutionPath.get(i);
                        Cell c2 = solutionPath.get(i + 1);
                        
                        int x1 = c1.x * CELL_SIZE + CELL_SIZE / 2;
                        int y1 = c1.y * CELL_SIZE + CELL_SIZE / 2;
                        int x2 = c2.x * CELL_SIZE + CELL_SIZE / 2;
                        int y2 = c2.y * CELL_SIZE + CELL_SIZE / 2;
                        
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }
            }
        };
        
        canvas.setBackground(Color.WHITE);
        updateCanvasSize(); 
        
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        
        // Pannello dei Controlli con wrapping intelligente dell'altezza
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)) {
            @Override
            public Dimension getPreferredSize() {
                if (canvas == null) return super.getPreferredSize();
                // Forza la larghezza dei controlli a combaciare con quella del labirinto
                int targetWidth = canvas.getPreferredSize().width;
                // Calcola l'altezza necessaria per ospitare i componenti mandandoli a capo
                int targetHeight = calculateWrappedHeight(this, targetWidth);
                return new Dimension(targetWidth, targetHeight);
            }
        };
        
        // Configurazione componenti interni
        String[] algoNames = { 
            "Randomized DFS", "Randomized Kruskal", "Randomized Prim", 
            "Aldous-Broder", "Wilson's Algorithm", "Recursive Division", "Eller's Algorithm"
        };
        algoSelector = new JComboBox<>(algoNames);
        controls.add(new JLabel("Algoritmo:"));
        controls.add(algoSelector);
        
        spinnerCols = new JSpinner(new SpinnerNumberModel(30, 5, 80, 1));
        spinnerRows = new JSpinner(new SpinnerNumberModel(30, 5, 80, 1));
        
        controls.add(new JLabel("Dim. X:"));
        controls.add(spinnerCols);
        controls.add(new JLabel("Dim. Y:"));
        controls.add(spinnerRows);
        
        btnAnimate = new JButton("Generazione Animata");
        btnInstant = new JButton("Generazione Istantanea");
        btnSolve = new JButton("Risolvi Labirinto");
        btnSolve.setEnabled(false); 
        
        controls.add(btnAnimate);
        controls.add(btnInstant);
        controls.add(btnSolve);
        
        add(controls, BorderLayout.SOUTH);

        // Gestione Azioni
        btnAnimate.addActionListener(e -> startAnimatedGeneration());
        
        btnInstant.addActionListener(e -> {
            applyNewDimensions();
            algorithm = algorithms[algoSelector.getSelectedIndex()];
            algorithm.generateFully(grid);
            openEntranceAndExit();
            btnSolve.setEnabled(true); 
            canvas.repaint();
        });

        btnSolve.addActionListener(e -> {
            solveMaze();
            canvas.repaint();
        });

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Algoritmo di simulazione del layout: calcola l'altezza verticale necessaria
     * per disporre tutti i componenti visibili senza che escano dalla larghezza massima consentita.
     */
    private int calculateWrappedHeight(JPanel panel, int maxWidth) {
        FlowLayout layout = (FlowLayout) panel.getLayout();
        int hgap = layout.getHgap();
        int vgap = layout.getVgap();
        
        int x = hgap;
        int y = vgap;
        int rowHeight = 0;
        boolean firstInRow = true;
        
        for (Component comp : panel.getComponents()) {
            if (!comp.isVisible()) continue;
            Dimension d = comp.getPreferredSize();
            
            // Se il componente sfora la riga corrente, va a capo
            if (!firstInRow && x + d.width + hgap > maxWidth) {
                x = hgap;
                y += rowHeight + vgap;
                rowHeight = 0;
                firstInRow = true;
            }
            
            x += d.width + hgap;
            rowHeight = Math.max(rowHeight, d.height);
            firstInRow = false;
        }
        
        return y + rowHeight + vgap;
    }

    private void applyNewDimensions() {
        solutionPath.clear();
        rows = (Integer) spinnerRows.getValue();
        cols = (Integer) spinnerCols.getValue();
        grid = new Cell[rows][cols];
        resetGrid();
        updateCanvasSize();
    }

    private void updateCanvasSize() {
        int canvasWidth = cols * CELL_SIZE + (MARGIN * 2) + 1;
        int canvasHeight = rows * CELL_SIZE + (MARGIN * 2) + 1;
        canvas.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        pack(); // Ricalcola le proporzioni della finestra richiamando il getPreferredSize() aggiornato
    }

    private void resetGrid() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x] = new Cell(x, y);
            }
        }
    }

    private void openEntranceAndExit() {
        grid[0][0].walls[3] = false; 
        grid[rows - 1][cols - 1].walls[2] = false; 
    }

    private void startAnimatedGeneration() {
        applyNewDimensions();
        btnSolve.setEnabled(false);
        btnAnimate.setEnabled(false);
        btnInstant.setEnabled(false);
        spinnerCols.setEnabled(false);
        spinnerRows.setEnabled(false);
        
        algorithm = algorithms[algoSelector.getSelectedIndex()];
        algorithm.init(grid);
        canvas.repaint();

        int estimatedSteps = rows * cols * 2; 
        int calculatedDelay = 15000 / estimatedSteps;
        int delay = Math.max(1, Math.min(30, calculatedDelay));

        javax.swing.Timer timer = new javax.swing.Timer(delay, null);
        timer.addActionListener(e -> {
            boolean running = algorithm.takeStep(grid);
            canvas.repaint();
            if (!running) {
                timer.stop();
                openEntranceAndExit();
                btnSolve.setEnabled(true); 
                btnAnimate.setEnabled(true);
                btnInstant.setEnabled(true);
                spinnerCols.setEnabled(true);
                spinnerRows.setEnabled(true);
                canvas.repaint();
                JOptionPane.showMessageDialog(this, "Labirinto Generato con Successo!");
            }
        });
        timer.start();
    }

    private void solveMaze() {
        solutionPath.clear();
        Cell start = grid[0][0];
        Cell end = grid[rows - 1][cols - 1];
        
        Queue<Cell> queue = new LinkedList<>();
        boolean[][] visited = new boolean[rows][cols];
        Cell[][] parent = new Cell[rows][cols]; 
        
        queue.add(start);
        visited[0][0] = true;
        boolean found = false;
        
        while (!queue.isEmpty()) {
            Cell curr = queue.poll();
            if (curr == end) {
                found = true;
                break;
            }
            
            if (!curr.walls[0] && curr.y > 0 && !visited[curr.y - 1][curr.x]) {
                queue.add(grid[curr.y - 1][curr.x]);
                visited[curr.y - 1][curr.x] = true;
                parent[curr.y - 1][curr.x] = curr;
            }
            if (!curr.walls[1] && curr.y < rows - 1 && !visited[curr.y + 1][curr.x]) {
                queue.add(grid[curr.y + 1][curr.x]);
                visited[curr.y + 1][curr.x] = true;
                parent[curr.y + 1][curr.x] = curr;
            }
            if (!curr.walls[2] && curr.x < cols - 1 && !visited[curr.y][curr.x + 1]) {
                queue.add(grid[curr.y][curr.x + 1]);
                visited[curr.y][curr.x + 1] = true;
                parent[curr.y][curr.x + 1] = curr;
            }
            if (!curr.walls[3] && curr.x > 0 && !visited[curr.y][curr.x - 1]) {
                queue.add(grid[curr.y][curr.x - 1]);
                visited[curr.y][curr.x - 1] = true;
                parent[curr.y][curr.x - 1] = curr;
            }
        }
        
        if (found) {
            Cell curr = end;
            while (curr != null) {
                solutionPath.add(0, curr);
                curr = parent[curr.y][curr.x];
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeVisualizer().setVisible(true));
    }
}