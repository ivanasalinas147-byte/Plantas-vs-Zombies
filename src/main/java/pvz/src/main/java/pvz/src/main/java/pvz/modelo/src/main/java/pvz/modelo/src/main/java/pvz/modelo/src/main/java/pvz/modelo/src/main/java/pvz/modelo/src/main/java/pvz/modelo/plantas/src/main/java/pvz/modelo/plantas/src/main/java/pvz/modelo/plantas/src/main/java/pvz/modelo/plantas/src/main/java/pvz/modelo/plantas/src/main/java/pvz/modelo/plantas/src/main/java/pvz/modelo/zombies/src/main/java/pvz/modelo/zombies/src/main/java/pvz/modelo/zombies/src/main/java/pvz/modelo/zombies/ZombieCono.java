package pvz.modelo.zombies;

import java.awt.Color;
import java.awt.Graphics2D;

/** Zombie con un cono de tráfico que le da vida extra. */
public class ZombieCono extends Zombie {

    private static final int VIDA_CONO = 370;

    public ZombieCono(int fila) {
        super("Zombie Cono", fila, VIDA_CUERPO + VIDA_CONO, 0.25, 25);
    }

    @Override
    protected void dibujarAccesorio(Graphics2D g, int cx, int cy) {
        int[] xs = {cx - 17, cx + 15, cx};
        int[] ys = {cy + 8, cy + 8, cy - 32};
        g.setColor(new Color(240, 120, 20));
        g.fillPolygon(xs, ys, 3);
        g.setColor(new Color(255, 240, 220));
        g.fillRect(cx - 9, cy - 10, 17, 5);
        g.setColor(new Color(200, 90, 10));
        g.fillRoundRect(cx - 21, cy + 5, 40, 7, 4, 4);
    }
}
