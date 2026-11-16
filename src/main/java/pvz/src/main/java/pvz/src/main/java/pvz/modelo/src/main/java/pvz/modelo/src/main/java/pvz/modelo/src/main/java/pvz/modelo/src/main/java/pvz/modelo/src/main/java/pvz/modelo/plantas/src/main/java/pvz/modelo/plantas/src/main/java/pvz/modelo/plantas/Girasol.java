package pvz.modelo.plantas;

import java.awt.Color;
import java.awt.Graphics2D;
import pvz.Config;
import pvz.logica.Juego;
import pvz.modelo.Sol;

/** Planta económica: no ataca, pero genera soles cada cierto tiempo. */
public class Girasol extends Planta {

    private static final int INTERVALO_SOL = Config.segundos(12);
    private int contador = Config.segundos(5); // el primer sol sale más rápido

    public Girasol(int fila, int columna) {
        super("Girasol", fila, columna, 300, TipoPlanta.GIRASOL.getCosto());
    }

    @Override
    protected void comportamiento(Juego juego) {
        contador--;
        if (contador <= 0) {
            juego.agregarSol(new Sol(x + 12, getY() - 25, getY() + 20));
            contador = INTERVALO_SOL;
        }
    }

    @Override
    public void dibujarCuerpo(Graphics2D g, int cx, int cy) {
        dibujarTallo(g, cx, cy);
        int cabezaY = cy - 12;
        // vaivén suave
        int balanceo = (int) (Math.sin(ticks * 0.05) * 2);
        int hx = cx + balanceo;

        // pétalos
        g.setColor(new Color(255, 200, 0));
        for (int i = 0; i < 12; i++) {
            double a = i * Math.PI / 6;
            int px = (int) (hx + Math.cos(a) * 19);
            int py = (int) (cabezaY + Math.sin(a) * 19);
            g.fillOval(px - 8, py - 8, 16, 16);
        }
        // centro
        g.setColor(new Color(130, 75, 20));
        g.fillOval(hx - 15, cabezaY - 15, 30, 30);
        // cara
        g.setColor(Color.BLACK);
        g.fillOval(hx - 7, cabezaY - 6, 4, 6);
        g.fillOval(hx + 3, cabezaY - 6, 4, 6);
        g.drawArc(hx - 7, cabezaY - 3, 14, 10, 200, 140);
    }
}
