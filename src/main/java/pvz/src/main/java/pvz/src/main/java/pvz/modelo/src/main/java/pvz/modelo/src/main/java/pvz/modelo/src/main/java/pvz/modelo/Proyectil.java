package pvz.modelo;

import pvz.Config;
import pvz.logica.Juego;
import pvz.modelo.zombies.Zombie;

/**
 * Clase ABSTRACTA para cualquier cosa que una planta dispare.
 * Se mueve en línea recta por su fila y daña al primer zombie que toca.
 */
public abstract class Proyectil extends Entidad {

    private final double velocidad;
    private final int danio;

    public Proyectil(String nombre, double x, int fila, double velocidad, int danio) {
        super(nombre, x, fila, 1);
        this.velocidad = velocidad;
        this.danio = danio;
    }

    @Override
    public void actualizar(Juego juego) {
        x += velocidad;

        Zombie objetivo = juego.zombieGolpeadoPor(this);
        if (objetivo != null) {
            objetivo.recibirDanio(danio);
            alImpactar(objetivo);
            morir();
        } else if (x > Config.ANCHO_VENTANA) {
            morir(); // salió de la pantalla
        }
    }

    /** Las subclases pueden agregar efectos al impactar (por ejemplo, congelar). */
    protected void alImpactar(Zombie zombie) {
        // Por defecto no hace nada extra.
    }

    public int getDanio() {
        return danio;
    }
}
