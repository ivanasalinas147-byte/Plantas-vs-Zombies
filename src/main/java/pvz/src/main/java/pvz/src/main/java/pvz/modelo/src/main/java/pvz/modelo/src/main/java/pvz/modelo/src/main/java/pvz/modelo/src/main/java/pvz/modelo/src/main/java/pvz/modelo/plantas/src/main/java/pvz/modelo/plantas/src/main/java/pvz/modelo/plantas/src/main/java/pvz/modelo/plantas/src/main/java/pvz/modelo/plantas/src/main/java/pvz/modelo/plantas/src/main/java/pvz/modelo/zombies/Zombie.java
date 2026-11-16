package pvz.modelo.zombies;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import pvz.Config;
import pvz.logica.Juego;
import pvz.modelo.Entidad;
import pvz.modelo.IAtacante;
import pvz.modelo.plantas.Planta;

/**
 * Clase ABSTRACTA de todos los zombies.
 * Avanzan de derecha a izquierda; si encuentran una planta se detienen a comérsela.
 */
public abstract class Zombie extends Entidad implements IAtacante {

    /** Vida del "cuerpo". Lo que tenga por encima de esto es el accesorio (cono, cubeta). */
    public static final int VIDA_CUERPO = 190;
    private static final int CADENCIA_MORDIDA = Config.segundos(0.5);

    private final double velocidad;
    private final int danio;
    private boolean comiendo;
    private int contadorMordida;
    private int ticks;

    public Zombie(String nombre, int fila, int vida, double velocidad, int danio) {
        // Aparece un poco a la derecha del tablero
        super(nombre, Config.ANCHO_VENTANA - 10, fila, vida);
        this.velocidad = velocidad;
        this.danio = danio;
    }

    @Override
    public void actualizar(Juego juego) {
        ticks++;
        avanzarDestello();

        Planta planta = juego.plantaFrenteA(this);
        if (planta != null) {
            comiendo = true;
            contadorMordida++;
            if (contadorMordida >= CADENCIA_MORDIDA) {
                atacar(juego);
                contadorMordida = 0;
            }
        } else {
            comiendo = false;
            x -= velocidad;
        }
    }

    @Override
    public void atacar(Juego juego) {
        Planta planta = juego.plantaFrenteA(this);
        if (planta != null) {
            planta.recibirDanio(danio);
        }
    }

    @Override
    public int getDanio() {
        return danio;
    }

    public boolean isComiendo() {
        return comiendo;
    }

    /** ¿Todavía trae su cono / cubeta? */
    protected boolean tieneAccesorio() {
        return getVida() > VIDA_CUERPO;
    }

    @Override
    public void dibujar(Graphics2D g) {
        dibujarCuerpo(g, (int) x, getY() + 40, ticks, recienGolpeado());
        if (tieneAccesorio()) {
            dibujarAccesorio(g, (int) x - 4, getY() + 40 - 100);
        }
    }

    /**
     * Cada tipo de zombie dibuja lo que trae en la cabeza.
     * (cx, cy) es la parte de arriba de la cabeza.
     */
    protected abstract void dibujarAccesorio(Graphics2D g, int cx, int cy);

    /**
     * Dibuja el cuerpo genérico de un zombie con los pies en (cx, pieY).
     * Es estático para poder dibujar zombies decorativos en la pantalla de inicio.
     */
    public static void dibujarCuerpo(Graphics2D g, int cx, int pieY, int ticks, boolean golpeado) {
        Color piel = golpeado ? new Color(205, 225, 195) : new Color(150, 175, 140);
        Color saco = new Color(95, 85, 70);
        Color pantalon = new Color(60, 70, 110);
        int paso = (int) (Math.sin(ticks * 0.12) * 6);

        // piernas
        g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(pantalon);
        g.drawLine(cx - 4, pieY - 30, cx - 8 + paso, pieY - 2);
        g.drawLine(cx + 4, pieY - 30, cx + 8 - paso, pieY - 2);
        g.setColor(new Color(50, 40, 30));
        g.fillOval(cx - 16 + paso, pieY - 6, 14, 7);
        g.fillOval(cx + 0 - paso, pieY - 6, 14, 7);

        // saco
        g.setColor(saco);
        g.fillRoundRect(cx - 15, pieY - 72, 30, 46, 8, 8);
        g.setColor(Color.WHITE);
        g.fillPolygon(new int[]{cx - 6, cx + 6, cx}, new int[]{pieY - 72, pieY - 72, pieY - 58}, 3);
        g.setColor(new Color(180, 20, 20));
        g.fillPolygon(new int[]{cx - 2, cx + 2, cx + 3, cx, cx - 3},
                new int[]{pieY - 68, pieY - 68, pieY - 52, pieY - 48, pieY - 52}, 5);

        // brazos estirados hacia la izquierda
        g.setColor(saco);
        g.drawLine(cx - 8, pieY - 64, cx - 32, pieY - 60 + paso / 2);
        g.setColor(piel);
        g.fillOval(cx - 40, pieY - 64 + paso / 2, 10, 9);

        // cabeza
        g.setColor(piel);
        g.fillOval(cx - 21, pieY - 100, 34, 34);
        // ojos
        g.setColor(Color.WHITE);
        g.fillOval(cx - 16, pieY - 90, 11, 11);
        g.fillOval(cx - 4, pieY - 92, 9, 9);
        g.setColor(Color.BLACK);
        g.fillOval(cx - 14, pieY - 87, 4, 4);
        g.fillOval(cx - 3, pieY - 89, 3, 3);
        // boca
        g.setColor(new Color(60, 30, 30));
        g.fillRect(cx - 14, pieY - 76, 12, 4);
        g.setColor(Color.WHITE);
        g.fillRect(cx - 12, pieY - 76, 3, 2);
        g.fillRect(cx - 7, pieY - 76, 3, 2);

        g.setStroke(new BasicStroke(1));
    }
}
