/**
 * Prim lavora per espansione radiale: 
 * parte da una cella casuale e fa crescere il labirinto come una macchia d'olio.
 * Mantiene una lista "frontier" di tutti i muri che separano le celle già incluse 
 * nel labirinto da quelle ancora escluse. A ogni passo, sceglie un muro a caso dalla 
 * frontiera: se la cella adiacente è ancora inesplorata, abbatte il muro, la include 
 * nel labirinto e aggiorna la frontiera con i nuovi muri disponibili.
*/

package codice.algoritmi;
import codice.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizedPrim implements MazeAlgorithm {

    /**
     * Rappresenta un muro della "frontiera".
     * Mette in relazione una cella già facente parte del labirinto (inCell) 
     * con una cella adiacente che si trova ancora all'esterno (outCell).
    */
    private static class PrimWall {
        Cell inCell;    // Cella già nel labirinto
        Cell outCell;   // Cella potenzialmente fuori dal labirinto
        int wallIn, wallOut;

        PrimWall(Cell inCell, Cell outCell, int wallIn, int wallOut) {
            this.inCell = inCell;
            this.outCell = outCell;
            this.wallIn = wallIn;
            this.wallOut = wallOut;
        }
    }

    private List<PrimWall> frontier = new ArrayList<>();
    private Random rand = new Random();

    /**
     * Inizializza lo stato azzerando la griglia e impostando tutte le celle come non visitate.
     * Seleziona una cella di partenza in modo completamente casuale, la marca come 
     * visitata (inclusa nel labirinto) e popola la frontiera iniziale con i suoi muri.
    */
    @Override
    public void init(Cell[][] grid) {
        frontier.clear();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                grid[y][x].visited = false;
                grid[y][x].isCurrent = false;
            }
        }

        // Sceglie una cella di partenza casuale
        int startX = rand.nextInt(grid[0].length);
        int startY = rand.nextInt(grid.length);
        Cell start = grid[startY][startX];
        start.visited = true;

        addFrontier(start, grid);
    }

    /**
     * Estrae un muro casuale dalla frontiera tramite un'ottimizzazione O(1) swap-to-last.
     * Se la cella esterna collegata a quel muro non è ancora stata visitata, abbatte 
     * il passaggio, include la cella nel labirinto e ne espande la frontiera.
     * Ritorna true se il labirinto è ancora in generazione, false se la frontiera è vuota.
    */
    @Override
    public boolean takeStep(Cell[][] grid) {
        if (frontier.isEmpty()) {
            return false;
        }

        // Sceglie un muro di frontiera a caso
        int index = rand.nextInt(frontier.size());
        
        // --- OTTIMIZZAZIONE O(1) SWAP-TO-LAST ---
        PrimWall pw = frontier.get(index);
        int lastIndex = frontier.size() - 1;
        PrimWall lastWall = frontier.get(lastIndex);
        
        // Sovrascrive l'elemento estratto con l'ultimo e rimuove la coda
        frontier.set(index, lastWall);
        frontier.remove(lastIndex);

        if (!pw.outCell.visited) {
            pw.inCell.walls[pw.wallIn] = false;
            pw.outCell.walls[pw.wallOut] = false;
            pw.outCell.visited = true;
            
            // La cella estratta diventa la corrente per l'effetto grafico
            pw.outCell.isCurrent = true;
            addFrontier(pw.outCell, grid);
        }

        return true;
    }

    @Override
    public void generateFully(Cell[][] grid) {
        init(grid);
        while (takeStep(grid)) {
            // Sceglie ed esegue senza pause
        }
        // Pulisce l'evidenziazione grafica finale
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                grid[y][x].isCurrent = false;
            }
        }
    }

    /**
     * Analizza i 4 punti cardinali attorno alla cella passata.
     * Se un vicino rientra nei limiti della griglia e non fa ancora parte del labirinto 
     * (!visited), crea un nuovo oggetto PrimWall e lo aggiunge alla lista della frontiera.
    */
    private void addFrontier(Cell c, Cell[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Nord
        if (c.y > 0 && !grid[c.y - 1][c.x].visited) {
            frontier.add(new PrimWall(c, grid[c.y - 1][c.x], 0, 1));
        }
        // Sud
        if (c.y < rows - 1 && !grid[c.y + 1][c.x].visited) {
            frontier.add(new PrimWall(c, grid[c.y + 1][c.x], 1, 0));
        }
        // Est
        if (c.x < cols - 1 && !grid[c.y][c.x + 1].visited) {
            frontier.add(new PrimWall(c, grid[c.y][c.x + 1], 2, 3));
        }
        // Ovest
        if (c.x > 0 && !grid[c.y][c.x - 1].visited) {
            frontier.add(new PrimWall(c, grid[c.y][c.x - 1], 3, 2));
        }
    }
}