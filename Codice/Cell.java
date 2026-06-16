import java.awt.Graphics;
import java.awt.Color;

public class Cell {
    public final int x, y;
    // Muri: 0=Nord, 1=Sud, 2=Est, 3=Ovest
    public boolean[] walls = {true, true, true, true};
    public boolean visited = false;
    public boolean isCurrent = false; // Per evidenziare la testa dell'algoritmo

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g, int size) {
        int px = x * size;
        int py = y * size;

        if (visited) {
            g.setColor(new Color(240, 240, 240)); // Colore di sfondo cella visitata
            g.fillRect(px, py, size, size);
        }
        if (isCurrent) {
            g.setColor(new Color(255, 87, 34)); // Evidenzia la cella corrente in arancione
            g.fillRect(px, py, size, size);
        }

        g.setColor(Color.BLACK);
        if (walls[0]) g.drawLine(px, py, px + size, py);             // Nord
        if (walls[1]) g.drawLine(px, py + size, px + size, py + size); // Sud
        if (walls[2]) g.drawLine(px + size, py, px + size, py + size); // Est
        if (walls[3]) g.drawLine(px, py, px, py + size);             // Ovest
    }
}