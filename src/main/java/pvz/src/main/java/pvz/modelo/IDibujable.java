package pvz.modelo;

import java.awt.Graphics2D;

/**
 * Todo lo que se puede pintar en pantalla implementa esta interfaz.
 * Así el panel del juego solo llama dibujar(g) sin saber qué objeto es (polimorfismo).
 */
public interface IDibujable {

    void dibujar(Graphics2D g);
}
