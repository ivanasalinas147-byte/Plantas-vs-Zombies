package pvz.modelo;

import pvz.logica.Juego;

/**
 * Lo implementan las entidades que pueden hacer daño:
 * Lanzaguisantes, PetaCereza y todos los zombies.
 */
public interface IAtacante {

    /** Ejecuta un ataque usando la información del juego. */
    void atacar(Juego juego);

    /** Cuánto daño hace cada ataque. */
    int getDanio();
}
