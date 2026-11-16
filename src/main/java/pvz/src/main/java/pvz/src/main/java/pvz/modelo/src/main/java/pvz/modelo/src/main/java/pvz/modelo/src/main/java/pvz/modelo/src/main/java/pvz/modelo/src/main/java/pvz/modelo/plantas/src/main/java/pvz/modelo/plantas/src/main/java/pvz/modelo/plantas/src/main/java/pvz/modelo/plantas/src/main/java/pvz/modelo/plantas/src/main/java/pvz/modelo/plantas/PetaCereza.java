package pvz.modelo.plantas;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import pvz.Config;
import pvz.logica.Juego;
import pvz.modelo.IAtacante;
import pvz.modelo.zombies.Zombie;

/**
 * Planta explosiva: al poco tiempo de plantarse explota y destruye
 * a los zombies que estén en un área de 3x3 celdas alrededor.
 */
public class PetaCereza extends Planta implements IAtacante {

    private static final int TIEMPO_MECHA = Config.segundos(1.2);
    private static final int DURACION_EXPLOSION = Config.segundos(0.5);

    private boolean exploto;
    private int ticksExplosion;

    public PetaCereza(int fila, int columna) {
        super("PetaCereza", fila, columna, 300, TipoPlanta.PETACEREZA.getCosto());
    }

    @Override
    protected void comportamiento(Juego juego) {
        if (!exploto && ticks >= TIEMPO_MECHA) {
            atacar(juego);
        } else if (exploto) {
            ticksExplosion++;
            if (ticksExplosion >= DURACION_EXPLOSION) {
                morir(); // la planta desaparece después de explotar
            }
        }
    }

    @Override
    public void atacar(Juego juego) {
        exploto = true;
        List<Zombie> alcanzados = juego.zombiesEnArea(getFila(), getColumna(), 1);
        for (Zombie z : alcanzados) {
            z.recibirDanio(getDanio());
        }
    }

    @Override
    public int getDanio() {
        return 1800;
    }

    /** Mientras explota los zombies no pueden comérsela. */
    public boolean estaExplotando() {
        return exploto;
    }

    @Override
    public void dibujarCuerpo(Graphics2D g, int cx, int cy) {
        if (exploto) {
            dibujarExplosion(g, cx, cy);
            return;
        }
        // se "infla" cuando está a punto de explotar
        double inflado = 1.0 + Math.min(1.0, (double) ticks / Math.max(1, TIEMPO_MECHA)) * 0.25;
        int r = (int) (16 * inflado);

        // ramitas
        g.setColor(new Color(40, 120, 30));
        g.drawLine(cx - 10, cy - 2, cx, cy - 28);
        g.drawLine(cx + 12, cy + 2, cx, cy - 28);
        g.setColor(new Color(60, 160, 40));
        g.fillOval(cx - 4, cy - 34, 16, 9);

        dibujarCereza(g, cx - 12, cy + 8, r);
        dibujarCereza(g, cx + 12, cy + 12, r);
    }

    private void dibujarCereza(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(170, 0, 20));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(230, 30, 40));
        g.fillOval(cx - r + 3, cy - r + 2, r * 2 - 8, r * 2 - 8);
        g.setColor(new Color(255, 170, 170));
        g.fillOval(cx - r / 2, cy - r / 2 - 2, r / 2, r / 2);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 6, cy - 3, 4, 5);
        g.fillOval(cx + 2, cy - 3, 4, 5);
    }

    private void dibujarExplosion(Graphics2D g, int cx, int cy) {
        double t = (double) ticksExplosion / DURACION_EXPLOSION;
        int r = (int) (40 + t * 90);
        int alfa = (int) (230 * (1 - t));
        g.setColor(new Color(255, 120, 0, Math.max(0, alfa)));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(255, 230, 60, Math.max(0, alfa)));
        g.fillOval(cx - r / 2, cy - r / 2, r, r);
    }
}
