package pvz.modelo.plantas;

import java.awt.Color;
import java.awt.Graphics2D;
import pvz.Config;
import pvz.logica.Juego;
import pvz.modelo.Guisante;
import pvz.modelo.IAtacante;

/** Planta de ataque básica: dispara guisantes a los zombies de su fila. */
public class Lanzaguisantes extends Planta implements IAtacante {

    private static final int CADENCIA = Config.segundos(1.4);
    private int recarga = Config.segundos(0.5);
    private int ticksDesdeDisparo = 99;

    public Lanzaguisantes(int fila, int columna) {
        super("Lanzaguisantes", fila, columna, 300, TipoPlanta.LANZAGUISANTES.getCosto());
    }

    @Override
    protected void comportamiento(Juego juego) {
        ticksDesdeDisparo++;
        if (recarga > 0) {
            recarga--;
        }
        // Solo dispara si hay un zombie adelante en su misma fila
        if (recarga == 0 && juego.hayZombieAdelante(getFila(), x)) {
            atacar(juego);
            recarga = CADENCIA;
        }
    }

    @Override
    public void atacar(Juego juego) {
        juego.agregarProyectil(new Guisante(x + 28, getFila()));
        ticksDesdeDisparo = 0;
    }

    @Override
    public int getDanio() {
        return 20;
    }

    @Override
    public void dibujarCuerpo(Graphics2D g, int cx, int cy) {
        dibujarTallo(g, cx, cy);
        // pequeño retroceso al disparar
        int retroceso = ticksDesdeDisparo < 6 ? -3 : 0;
        int hx = cx + retroceso;
        int hy = cy - 12;

        Color verde = new Color(80, 190, 60);
        Color verdeOscuro = new Color(40, 120, 30);

        // hoja trasera
        g.setColor(verdeOscuro);
        g.fillOval(hx - 26, hy - 18, 18, 12);
        // cabeza
        g.setColor(verde);
        g.fillOval(hx - 20, hy - 18, 36, 36);
        // boca (tubo)
        g.fillRoundRect(hx + 6, hy - 9, 22, 18, 8, 8);
        g.setColor(verdeOscuro);
        g.fillOval(hx + 22, hy - 8, 10, 16);
        g.setColor(new Color(20, 60, 15));
        g.fillOval(hx + 24, hy - 5, 6, 10);
        // ojo
        g.setColor(Color.WHITE);
        g.fillOval(hx - 4, hy - 12, 12, 12);
        g.setColor(Color.BLACK);
        g.fillOval(hx + 1, hy - 9, 6, 7);
    }
}
