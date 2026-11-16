package pvz.modelo;

import pvz.Config;
import pvz.logica.Juego;

/**
 * Clase base ABSTRACTA de todo lo que vive en el tablero
 * (plantas, zombies y proyectiles).
 *
 * Guarda lo que tienen en común: nombre, posición, fila y vida.
 * Cada subclase decide cómo se actualiza y cómo se dibuja.
 */
public abstract class Entidad implements IDibujable {

    private String nombre;
    protected double x;          // centro horizontal en pantalla
    protected int fila;          // fila del tablero (0 a 4)
    private int vida;
    private final int vidaMaxima;
    private int ticksGolpe;      // para hacer un pequeño destello al recibir daño

    public Entidad(String nombre, double x, int fila, int vida) {
        this.nombre = nombre;
        this.x = x;
        this.fila = fila;
        this.vida = vida;
        this.vidaMaxima = vida;
    }

    // ----- Comportamiento que cada subclase DEBE definir -----

    /** Se llama una vez por tick (60 veces por segundo). */
    public abstract void actualizar(Juego juego);

    // ----- Comportamiento común -----

    public void recibirDanio(int cantidad) {
        vida = Math.max(0, vida - cantidad);
        ticksGolpe = 6;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    /** Elimina la entidad del juego en el siguiente ciclo. */
    public void morir() {
        vida = 0;
    }

    /** Hay que llamarlo desde actualizar() para que el destello se apague. */
    protected void avanzarDestello() {
        if (ticksGolpe > 0) {
            ticksGolpe--;
        }
    }

    protected boolean recienGolpeado() {
        return ticksGolpe > 0;
    }

    /** Porcentaje de vida restante entre 0.0 y 1.0. */
    public double porcentajeVida() {
        return (double) vida / vidaMaxima;
    }

    // ----- Getters y setters -----

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getX() {
        return x;
    }

    public int getY() {
        return Config.centroFila(fila);
    }

    public int getFila() {
        return fila;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }
}
