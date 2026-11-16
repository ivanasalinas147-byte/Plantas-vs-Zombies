package pvz.modelo;

import java.awt.Color;
import java.awt.Graphics2D;

/** El proyectil verde que dispara el Lanzaguisantes. */
public class Guisante extends Proyectil {

    public Guisante(double x, int fila) {
        super("Guisante", x, fila, 5.0, 20);
    }

    @Override
    public void dibujar(Graphics2D g) {
        int cx = (int) x;
        int cy = getY() - 22;
        g.setColor(new Color(40, 120, 20));
        g.fillOval(cx - 10, cy - 10, 20, 20);
        g.setColor(new Color(110, 210, 60));
        g.fillOval(cx - 8, cy - 9, 16, 16);
        g.setColor(new Color(200, 255, 170));
        g.fillOval(cx - 4, cy - 6, 6, 6);
    }
}
