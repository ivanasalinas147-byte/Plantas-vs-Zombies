package pvz.modelo.plantas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import pvz.Config;
import pvz.logica.Juego;
import pvz.modelo.Entidad;

/**
 * Clase ABSTRACTA de todas las plantas.
 * Una planta ocupa una celda (fila, columna), cuesta soles y no se mueve.
 */
public abstract class Planta extends Entidad {

    private final int columna;
    private final int costo;
    protected int ticks; // tiempo que lleva viva, útil para animaciones

    public Planta(String nombre, int fila, int columna, int vida, int costo) {
        super(nombre, Config.centroColumna(columna), fila, vida);
        this.columna = columna;
        this.costo = costo;
    }

    /**
     * Método plantilla: hace lo común a todas las plantas y luego
     * delega en comportamiento(), que cada planta define a su manera.
     */
    @Override
    public final void actualizar(Juego juego) {
        ticks++;
        avanzarDestello();
        comportamiento(juego);
    }

    /** Lo que hace cada planta en cada tick (disparar, generar sol, explotar...). */
    protected abstract void comportamiento(Juego juego);

    /**
     * Dibuja la planta con el centro en (cx, cy).
     * Se separa de dibujar() para poder reutilizarlo en las tarjetas del menú.
     */
    public abstract void dibujarCuerpo(Graphics2D g, int cx, int cy);

    @Override
    public void dibujar(Graphics2D g) {
        dibujarCuerpo(g, (int) x, getY());
        if (recienGolpeado()) {
            // destello rojo cuando un zombie la muerde
            g.setColor(new Color(255, 80, 80, 45));
            g.fillOval((int) x - 28, getY() - 32, 56, 64);
        }
    }

    /** Tallo y hojas: lo comparten Girasol y Lanzaguisantes. */
    protected static void dibujarTallo(Graphics2D g, int cx, int cy) {
        g.setColor(new Color(40, 130, 40));
        g.setStroke(new BasicStroke(5));
        g.drawLine(cx, cy - 5, cx, cy + 32);
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(60, 170, 50));
        g.fillOval(cx - 22, cy + 22, 22, 11);
        g.fillOval(cx, cy + 22, 22, 11);
    }

    public int getColumna() {
        return columna;
    }

    public int getCosto() {
        return costo;
    }
}
