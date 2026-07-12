package codice;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

import codice.algoritmi.*;

public class MazeVisualizer extends JFrame {
    // Dimensione di default del labirinto
    private int rows = 30;
    private int cols = 30;

    private final int CELL_SIZE = 20;
    private final int MARGIN = 20;

    private Cell[][] grid;
    private MazeAlgorithm algorithm;
    private JPanel canvas;

    private JComboBox<String> algoSelector; // Selettore di algoritmo
    private JSpinner spinnerCols;           // Colonne del labirinto
    private JSpinner spinnerRows;           // Righe del labirinto
    private JButton btnAnimate;             // Pulsante di avvio animazione
    private JButton btnInstant;             // Pulsante di generazione istantanea
    private JButton btnStop;                // Pulsante di interruzione generazione
    private JButton btnSolve;               // Pulsante per risoluzione del labirinto

    // Impostiamo il delay in modo che in totale dovrebbero risultare 60 aggiornamenti al secondo
    private int animationDelay = (int) Math.ceil(1000.0 / 60.0);  // ms

    private javax.swing.Timer animationTimer;   // Timer spostato a livello di classe per essere interrotto dal pulsante

    private List<Cell> solutionPath = new ArrayList<>();    // Necessario per salvare la soluzione del labirinto

    private MazeAlgorithm[] algorithms = {  // Collezione di algoritmi di generazione
        new RandomizedDFS(),
        new RandomizedKruskal(),
        new RandomizedPrim(),
        new AldousBroder(),
        new WilsonsAlgorithm(),
        new RecursiveDivision(),
        new EllersAlgorithm()
    };

    public MazeVisualizer() {
        setTitle("Generatore e risolutore di labirinti");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        grid = new Cell[rows][cols];    // Inizializzo la griglia del labirinto
        resetGrid();    // Reimposto le celle (alzo i muri)

        // Inizio e fine del labirinto
        Cell startCell = grid[0][0];
        Cell endCell = grid[rows - 1][cols - 1];

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Attiviamo l'antialiasing per sfumare i bordi delle figure
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Impostiamo il margine per staccare il labirinto dai bordi
                g2d.translate(MARGIN, MARGIN);
                
                // Disegna tutte le celle
                for (int y = 0; y < rows; y++) {
                    for (int x = 0; x < cols; x++) {
                        grid[y][x].draw(g2d, CELL_SIZE);
                    }
                }

                if (!solutionPath.isEmpty()) {
                    // Disegna la soluzione se è stata generata
                    g2d.setColor(new Color(33, 150, 243));  // Azzurro
                    // Imposto lo stile del pennello
                    g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    
                    for (int i = 0; i < solutionPath.size() - 1; i++) {
                        Cell c1 = solutionPath.get(i);
                        Cell c2 = solutionPath.get(i + 1);
                        
                        // Voglio la linea al centro delle celle
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
        
        // Utile per ridimensionare correttamente il FlowLayout che contiene i pulsanti
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            @Override
            public Dimension getPreferredSize() {
                if (canvas == null) return super.getPreferredSize();
                int targetWidth = canvas.getPreferredSize().width;
                int targetHeight = calculateWrappedHeight(this, targetWidth);
                return new Dimension(targetWidth, targetHeight);
            }
        };
        
        String[] algoNames = {
            "Randomized DFS", "Randomized Kruskal", "Randomized Prim", 
            "Aldous-Broder", "Wilson's Algorithm", "Recursive Division", "Eller's Algorithm"
        };
        algoSelector = new JComboBox<>(algoNames);
        controls.add(new JLabel("Algoritmo:"));
        controls.add(algoSelector);
        
        spinnerCols = new JSpinner(new SpinnerNumberModel(cols, 5, 80, 1));
        spinnerRows = new JSpinner(new SpinnerNumberModel(rows, 5, 80, 1));
        
        controls.add(new JLabel("Dim. X:"));
        controls.add(spinnerCols);
        controls.add(new JLabel("Dim. Y:"));
        controls.add(spinnerRows);
        
        btnAnimate = new JButton("Generazione animata");
        btnInstant = new JButton("Generazione istantanea");
        btnStop = new JButton("Interrompere");  // Disabilitato di default
        btnStop.setEnabled(false);
        btnSolve = new JButton("Genera soluzione");
        btnSolve.setEnabled(false);
        
        controls.add(btnAnimate);
        controls.add(btnInstant);
        controls.add(btnStop);
        controls.add(btnSolve);
        
        add(controls, BorderLayout.SOUTH);  // Voglio i pulsanti in fondo all'interfaccia

        // Aggiungo gli action listener ai pulsanti
        btnAnimate.addActionListener(e -> startAnimatedGeneration());
        btnStop.addActionListener(e -> stopAnimatedGeneration());
        
        btnInstant.addActionListener(e -> {
            applyNewDimensions();
            algorithm = algorithms[algoSelector.getSelectedIndex()];
            algorithm.generateFully(grid);  // Genero il labirinto istantaneamente
            openEntranceAndExit();          // Genero le aperture del labirinto
            btnSolve.setEnabled(true);
            canvas.repaint();
        });

        btnSolve.addActionListener(e -> {
            solveMaze();    // Risoluzione del labirinto
            canvas.repaint();
        });

        pack();
        setLocationRelativeTo(null);
    }

    // Necessario per calcolare la dimensione necessaria per mostrare i pulsanti correttamente
    private int calculateWrappedHeight(JPanel panel, int maxWidth) {
        FlowLayout layout = (FlowLayout) panel.getLayout();
        int hgap = layout.getHgap();
        int vgap = layout.getVgap();
        
        int x = hgap;
        int y = vgap;
        int rowHeight = 0;
        boolean firstInRow = true;
        
        // Per ogni (for) oggetto di tipo Component (che chiameremo comp) contenuto
        // dentro (:) la lista panel.getComponents()...
        for (Component comp : panel.getComponents()) {
            // if (!comp.isVisible()) continue; // Non serve
            Dimension d = comp.getPreferredSize();
            
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

    // Aggiorniamo le dimensioni del labirinto, quindi resettiamo la soluzione e la griglia
    // Le dimensioni vengono aggiornate quando si genera un labirinto
    private void applyNewDimensions() {
        solutionPath.clear();
        rows = (Integer) spinnerRows.getValue();
        cols = (Integer) spinnerCols.getValue();
        grid = new Cell[rows][cols];
        resetGrid();
        updateCanvasSize();
    }

    // Rendiamo l'interfaccia responsive ai cambiamenti delle dimensioni del labirinto
    private void updateCanvasSize() {
        int canvasWidth = cols * CELL_SIZE + (MARGIN * 2) + 1;
        int canvasHeight = rows * CELL_SIZE + (MARGIN * 2) + 1;
        canvas.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        pack();
    }

    private void resetGrid() {
        // Reimposto tutte le celle (tutti i muri saranno alzati)
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[y][x] = new Cell(x, y);
            }
        }
    }

    private void openEntranceAndExit() {
        // Posiziono l'ingresso in alto a sinistra e l'uscita in basso a destra
        startCell.walls[3] = false;
        endCell.walls[2] = false;
    }

    private void startAnimatedGeneration() {
        applyNewDimensions();

        // Disattiviamo tutti i pulsanti e attiviamo quello di interruzione
        btnSolve.setEnabled(false);
        btnAnimate.setEnabled(false);
        btnInstant.setEnabled(false);
        spinnerCols.setEnabled(false);
        spinnerRows.setEnabled(false);
        algoSelector.setEnabled(false);
        btnStop.setEnabled(true);
        
        // Impostiamo l'algoritmo all'ultimo selezionato
        algorithm = algorithms[algoSelector.getSelectedIndex()];
        algorithm.init(grid);
        canvas.repaint();

        animationTimer = new javax.swing.Timer(animationDelay, null);
        // Definizione dell'azione per ogni tick del timer:
        animationTimer.addActionListener(e -> {
            // Codice eseguito ad ogni tick
            boolean running = algorithm.takeStep(grid); // Funzione definita nei file degli algoritmi
            canvas.repaint();
            if (!running) {     // L'algoritmo ha finito
                animationTimer.stop();
                openEntranceAndExit();
                setControlsEnabled(true);
                JOptionPane.showMessageDialog(this, "Labirinto generato con successo.");
            }
        });
        animationTimer.start();
    }

    // Gestione manuale dell'interruzione del labirinto
    private void stopAnimatedGeneration() {
        // Condizione eccessiva: animationTimer != null && animationTimer.isRunning()
        if (animationTimer.isRunning()) {
            animationTimer.stop();
        }
        resetGrid();    // Pulisce la griglia parziale riportandola allo stato iniziale
        setControlsEnabled(false);  // Sblocca i controlli standard e spegne il tasto stop
        canvas.repaint();
    }

    // Metodo centralizzato per la gestione dei pulsanti
    private void setControlsEnabled(boolean generationSuccessful) {
        btnAnimate.setEnabled(true);
        btnInstant.setEnabled(true);
        spinnerCols.setEnabled(true);
        spinnerRows.setEnabled(true);
        algoSelector.setEnabled(true);
        btnStop.setEnabled(false);  // Stop disattivato dato che l'animazione non è in corso
        btnSolve.setEnabled(generationSuccessful);  // Attivo solo se il labirinto è completo
    }

    private void solveMaze() {
        solutionPath.clear();
        
        // Utilizziamo BFS per risolvere il labirinto, che necessita della coda FIFO
        Queue<Cell> queue = new LinkedList<>();
        // Utilizziamo una nuova matrice e non la variabile delle celle integrata
        // perché così non si deve azzerare tutta la griglia del labirinto
        boolean[][] visited = new boolean[rows][cols];
        Cell[][] parent = new Cell[rows][cols];     // Necessaria per salvare il percorso effettuato
        
        queue.add(startCell);
        visited[0][0] = true;
        boolean found = false;
        
        while (!queue.isEmpty()) {
            Cell curr = queue.poll();
            if (curr == endCell) {
                found = true;
                break;
            }
            
            // Controllo che non ci siano muri alzati, che non usciamo dalla griglia e che
            // la cella non sia stata già visitata
            if (!curr.walls[0] && curr.y > 0 && !visited[curr.y - 1][curr.x]) {
                queue.add(grid[curr.y - 1][curr.x]);    // Si aggiunge alla coda
                visited[curr.y - 1][curr.x] = true;     // Segna come visitata
                parent[curr.y - 1][curr.x] = curr;      // Segnamo da dove siamo venuti
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
        
        if (found) {    // Soluzione trovata
            Cell curr = endCell;    // Parte dalla fine
            while (curr != null) {  // Si ferma alla cella di partenza perché essa non possiede un padre
                solutionPath.add(0, curr);  // Inseriamo la cella corrente in cima alla lista,
                // in questo modo la soluzione potrà essere letta dal primo all'ultimo elemento della lista
                curr = parent[curr.y][curr.x];  // Ci spostiamo sulla cella precedente
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MazeVisualizer().setVisible(true));
    }
}