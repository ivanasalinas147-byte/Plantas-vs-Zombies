package pvz.modelo.zombies;

/** Catálogo de zombies. Funciona como fábrica igual que TipoPlanta. */
public enum TipoZombie {

    NORMAL,
    CONO,
    CUBETA;

    public Zombie crear(int fila) {
        return switch (this) {
            case NORMAL -> new ZombieNormal(fila);
            case CONO -> new ZombieCono(fila);
            case CUBETA -> new ZombieCubeta(fila);
        };
    }
}
