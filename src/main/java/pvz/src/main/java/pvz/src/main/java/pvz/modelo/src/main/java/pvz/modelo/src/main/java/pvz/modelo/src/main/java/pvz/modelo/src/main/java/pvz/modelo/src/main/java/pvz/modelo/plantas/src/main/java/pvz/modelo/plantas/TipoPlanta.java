package pvz.modelo.plantas;

import pvz.Config;

/**
 * Catálogo de plantas disponibles: nombre, costo en soles y tiempo de recarga.
 * Funciona como una "fábrica": crear() regresa el objeto Planta correcto.
 */
public enum TipoPlanta {

    GIRASOL("Girasol", 50, 7.5),
    LANZAGUISANTES("Lanzaguisantes", 100, 7.5),
    NUEZ("Nuez", 50, 25),
    PETACEREZA("PetaCereza", 150, 35);

    private final String nombre;
    private final int costo;
    private final int recargaTicks;

    TipoPlanta(String nombre, int costo, double recargaSegundos) {
        this.nombre = nombre;
        this.costo = costo;
        this.recargaTicks = Config.segundos(recargaSegundos);
    }

    /** Crea una planta nueva de este tipo en la celda indicada. */
    public Planta crear(int fila, int columna) {
        return switch (this) {
            case GIRASOL -> new Girasol(fila, columna);
            case LANZAGUISANTES -> new Lanzaguisantes(fila, columna);
            case NUEZ -> new Nuez(fila, columna);
            case PETACEREZA -> new PetaCereza(fila, columna);
        };
    }

    public String getNombre() {
        return nombre;
    }

    public int getCosto() {
        return costo;
    }

    public int getRecargaTicks() {
        return recargaTicks;
    }
}
