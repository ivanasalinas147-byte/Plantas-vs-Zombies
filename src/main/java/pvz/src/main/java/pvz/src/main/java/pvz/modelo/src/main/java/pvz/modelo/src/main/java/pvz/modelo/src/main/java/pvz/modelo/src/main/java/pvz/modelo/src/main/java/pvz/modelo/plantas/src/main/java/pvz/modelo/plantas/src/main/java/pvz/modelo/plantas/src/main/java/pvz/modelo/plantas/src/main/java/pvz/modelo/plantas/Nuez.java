package pvz.modelo.plantas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import pvz.logica.Juego;

/** Planta defensiva: no ataca, pero tiene muchísima vida y frena a los zombies. */
public class Nuez extends Planta {

    public Nuez(int fila, int columna) {
        super("Nuez", fila, columna, 4000, TipoPlanta.NUEZ.getCosto());
    }

    @Override
    protected void comportamiento(Juego juego) {
        // La nuez solo aguanta. No necesita hacer nada cada tick.
    }

    @Override
    public void dibujarCuerpo(Graphics2D g, int cx, int cy) {
        g.setColor(new Color(150, 95, 40));
        g.fillOval(cx - 26, cy - 32, 52, 66);
        g.setColor(new Color(185, 125, 60));
        g.fillOval(cx - 20, cy - 28, 40, 56);

        // ojos (se ven preocupados cuando tiene poca vida)
        g.setColor(Color.WHITE);
        g.fillOval(cx - 13, cy - 12, 12, 14);
        g.fillOval(cx + 2, cy - 12, 12, 14);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 9, cy - 7, 6, 7);
        g.fillOval(cx + 6, cy - 7, 6, 7);

        // grietas según el daño recibido
        g.setColor(new Color(80, 45, 15));
        g.setStroke(new BasicStroke(2));
        if (porcentajeVida() < 0.66) {
            g.drawLine(cx - 18, cy - 20, cx - 8, cy - 15);
            g.drawLine(cx - 8, cy - 15, cx - 12, cy - 5);
        }
        if (porcentajeVida() < 0.33) {
            g.drawLine(cx + 18, cy + 5, cx + 8, cy + 12);
            g.drawLine(cx + 8, cy + 12, cx + 12, cy + 22);
            g.drawLine(cx - 15, cy + 15, cx - 5, cy + 25);
        }
        g.setStroke(new BasicStroke(1));
        g.drawArc(cx - 8, cy + 8, 16, 8, 20, 140);
    }
}
