package pvz.modelo.zombies;

import java.awt.Color;
import java.awt.Graphics2D;

/** Zombie con una cubeta de metal: el más resistente. */
public class ZombieCubeta extends Zombie {

    private static final int VIDA_CUBETA = 1100;

    public ZombieCubeta(int fila) {
        super("Zombie Cubeta", fila, VIDA_CUERPO + VIDA_CUBETA, 0.23, 25);
    }

    @Override
    protected void dibujarAccesorio(Graphics2D g, int cx, int cy) {
        int[] xs = {cx - 19, cx + 17, cx + 13, cx - 15};
        int[] ys = {cy + 14, cy + 14, cy - 18, cy - 18};
        g.setColor(new Color(140, 145, 155));
        g.fillPolygon(xs, ys, 4);
        g.setColor(new Color(190, 195, 205));
        g.fillRect(cx - 10, cy - 14, 6, 24);
        g.setColor(new Color(100, 105, 115));
        g.fillRect(cx - 21, cy + 10, 40, 6);
        // abolladura cuando ya recibió mucho daño
        if (getVida() < VIDA_CUERPO + 450) {
            g.setColor(new Color(90, 95, 105));
            g.fillOval(cx + 2, cy - 8, 10, 8);
        }
    }
}
