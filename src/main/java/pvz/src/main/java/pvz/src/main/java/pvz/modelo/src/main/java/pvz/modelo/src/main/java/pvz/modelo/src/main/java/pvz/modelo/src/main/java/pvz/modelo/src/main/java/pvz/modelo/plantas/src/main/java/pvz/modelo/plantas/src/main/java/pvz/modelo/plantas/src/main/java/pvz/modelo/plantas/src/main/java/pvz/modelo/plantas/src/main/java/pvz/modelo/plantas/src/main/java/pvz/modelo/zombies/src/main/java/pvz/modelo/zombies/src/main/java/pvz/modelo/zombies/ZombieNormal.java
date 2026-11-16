package pvz.modelo.zombies;

import java.awt.Graphics2D;

/** El zombie básico: lento y sin protección. */
public class ZombieNormal extends Zombie {

    public ZombieNormal(int fila) {
        super("Zombie", fila, VIDA_CUERPO, 0.25, 25);
    }

    @Override
    protected void dibujarAccesorio(Graphics2D g, int cx, int cy) {
        // El zombie normal no trae nada en la cabeza.
    }
}
