package pvz.modelo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import pvz.Config;

/**
 * Un sol que el jugador recoge con un clic para ganar puntos.
 * Puede caer del cielo o salir de un Girasol.
 */
public class Sol implements IDibujable {

    private static final int RADIO = 22;
    private static final int TIEMPO_EN_SUELO = Config.segundos(9);

    private double x;
    private double y;
    private final double yDestino;
    private int ticksEnSuelo;
    private boolean recogido;
    private boolean terminado;
    private double angulo;

    public Sol(double x, double y, double yDestino) {
        this.x = x;
        this.y = y;
        this.yDestino = yDestino;
    }

    public void actualizar() {
        angulo += 0.03;
        if (recogido) {
            // Vuela hacia el contador de soles (esquina superior izquierda)
            x += (40 - x) * 0.2;
            y += (45 - y) * 0.2;
            if (Math.abs(x - 40) < 5 && Math.abs(y - 45) < 5) {
                terminado = true;
            }
        } else if (y < yDestino) {
            y += 1.2; // cae lentamente
        } else {
            ticksEnSuelo++;
            if (ticksEnSuelo > TIEMPO_EN_SUELO) {
                terminado = true; // desaparece si nadie lo recoge
            }
        }
    }

    /** ¿El clic (px, py) cae sobre este sol? */
    public boolean contiene(int px, int py) {
        double dx = px - x;
        double dy = py - y;
        return !recogido && dx * dx + dy * dy <= (RADIO + 8) * (RADIO + 8);
    }

    public void recoger() {
        recogido = true;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getValor() {
        return Config.VALOR_SOL;
    }

    @Override
    public void dibujar(Graphics2D g) {
        dibujarIcono(g, (int) x, (int) y, RADIO, angulo);
    }

    /** Método estático para reutilizar el dibujo del sol en el contador. */
    public static void dibujarIcono(Graphics2D g, int cx, int cy, int r, double angulo) {
        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(255, 200, 0, 180));
        for (int i = 0; i < 12; i++) {
            double a = angulo + i * Math.PI / 6;
            int x1 = (int) (cx + Math.cos(a) * (r - 4));
            int y1 = (int) (cy + Math.sin(a) * (r - 4));
            int x2 = (int) (cx + Math.cos(a) * (r + 8));
            int y2 = (int) (cy + Math.sin(a) * (r + 8));
            g.drawLine(x1, y1, x2, y2);
        }
        g.setColor(new Color(255, 225, 40));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(255, 245, 160));
        g.fillOval(cx - r / 2, cy - r / 2, r, r);
        g.setStroke(new BasicStroke(1));
    }
}
