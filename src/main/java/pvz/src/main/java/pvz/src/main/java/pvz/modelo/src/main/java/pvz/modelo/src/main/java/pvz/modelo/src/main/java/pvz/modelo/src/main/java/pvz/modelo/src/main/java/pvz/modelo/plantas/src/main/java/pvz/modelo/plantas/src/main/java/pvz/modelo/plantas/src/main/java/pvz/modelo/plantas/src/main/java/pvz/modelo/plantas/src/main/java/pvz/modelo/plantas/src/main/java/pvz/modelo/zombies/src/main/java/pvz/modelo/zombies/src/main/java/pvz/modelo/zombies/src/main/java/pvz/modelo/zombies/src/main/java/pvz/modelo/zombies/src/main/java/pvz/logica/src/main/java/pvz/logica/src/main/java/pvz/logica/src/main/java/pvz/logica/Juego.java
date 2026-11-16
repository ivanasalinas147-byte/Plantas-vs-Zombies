package pvz.logica;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import pvz.Config;
import pvz.modelo.Proyectil;
import pvz.modelo.Sol;
import pvz.modelo.plantas.PetaCereza;
import pvz.modelo.plantas.Planta;
import pvz.modelo.plantas.TipoPlanta;
import pvz.modelo.zombies.Zombie;

/**
 * El "cerebro" del juego. Guarda el estado de la partida (plantas, zombies,
 * proyectiles, soles) y aplica las reglas en cada tick.
 *
 * No sabe nada de Swing: la vista solo le pide datos y le avisa de los clics.
 */
public class Juego {

    private static final int INTERVALO_SOL_CIELO = Config.segundos(9);

    private final Planta[][] plantas = new Planta[Config.FILAS][Config.COLUMNAS];
    private final List<Zombie> zombies = new ArrayList<>();
    private final List<Proyectil> proyectiles = new ArrayList<>();
    private final List<Sol> soles = new ArrayList<>();
    private final Map<TipoPlanta, Integer> recargas = new EnumMap<>(TipoPlanta.class);
    private final GestorOleadas oleadas = new GestorOleadas();
    private final Random azar = new Random();

    private EstadoJuego estado = EstadoJuego.JUGANDO;
    private int puntosSol = Config.SOLES_INICIALES;
    private int contadorSolCielo = Config.segundos(4);
    private int zombiesEliminados;
    private long ticks;

    public Juego() {
        for (TipoPlanta tipo : TipoPlanta.values()) {
            recargas.put(tipo, 0);
        }
        // Las plantas caras empiezan recargando, como en el juego original
        recargas.put(TipoPlanta.PETACEREZA, TipoPlanta.PETACEREZA.getRecargaTicks());
        recargas.put(TipoPlanta.NUEZ, Config.segundos(10));
    }

    // =====================================================================
    //  CICLO PRINCIPAL
    // =====================================================================

    /** Avanza el juego un tick. Lo llama el Timer del panel 60 veces por segundo. */
    public void actualizar() {
        if (estado != EstadoJuego.JUGANDO) {
            return;
        }
        ticks++;

        actualizarRecargas();
        generarSolDelCielo();
        oleadas.actualizar(this);

        // 1) Plantas
        for (int f = 0; f < Config.FILAS; f++) {
            for (int c = 0; c < Config.COLUMNAS; c++) {
                if (plantas[f][c] != null) {
                    plantas[f][c].actualizar(this);
                }
            }
        }
        // 2) Zombies
        for (Zombie z : zombies) {
            z.actualizar(this);
        }
        // 3) Proyectiles (aquí se detectan los impactos)
        for (Proyectil p : proyectiles) {
            p.actualizar(this);
        }
        // 4) Soles
        for (Sol s : soles) {
            s.actualizar();
        }

        limpiarMuertos();
        revisarFinDelJuego();
    }

    private void actualizarRecargas() {
        for (TipoPlanta tipo : TipoPlanta.values()) {
            int r = recargas.get(tipo);
            if (r > 0) {
                recargas.put(tipo, r - 1);
            }
        }
    }

    private void generarSolDelCielo() {
        contadorSolCielo--;
        if (contadorSolCielo <= 0) {
            int x = Config.TABLERO_X + 30 + azar.nextInt(Config.COLUMNAS * Config.ANCHO_CELDA - 60);
            int destino = Config.TABLERO_Y + 40 + azar.nextInt(Config.FILAS * Config.ALTO_CELDA - 80);
            soles.add(new Sol(x, Config.TABLERO_Y - 30, destino));
            contadorSolCielo = INTERVALO_SOL_CIELO;
        }
    }

    private void limpiarMuertos() {
        for (int f = 0; f < Config.FILAS; f++) {
            for (int c = 0; c < Config.COLUMNAS; c++) {
                if (plantas[f][c] != null && !plantas[f][c].estaVivo()) {
                    plantas[f][c] = null;
                }
            }
        }
        int antes = zombies.size();
        zombies.removeIf(z -> !z.estaVivo());
        zombiesEliminados += antes - zombies.size();

        proyectiles.removeIf(p -> !p.estaVivo());
        soles.removeIf(Sol::isTerminado);
    }

    private void revisarFinDelJuego() {
        for (Zombie z : zombies) {
            if (z.getX() < Config.TABLERO_X - 25) {
                estado = EstadoJuego.DERROTA; // un zombie llegó a la casa
                return;
            }
        }
        if (oleadas.terminadas() && zombies.isEmpty()) {
            estado = EstadoJuego.VICTORIA;
        }
    }

    // =====================================================================
    //  ACCIONES DEL JUGADOR
    // =====================================================================

    /** Intenta plantar. Regresa true si se pudo. */
    public boolean colocarPlanta(TipoPlanta tipo, int fila, int columna) {
        if (!celdaValida(fila, columna) || plantas[fila][columna] != null) {
            return false;
        }
        if (!puedeComprar(tipo)) {
            return false;
        }
        plantas[fila][columna] = tipo.crear(fila, columna);
        puntosSol -= tipo.getCosto();
        recargas.put(tipo, tipo.getRecargaTicks());
        return true;
    }

    /** La pala: quita la planta de una celda. */
    public boolean quitarPlanta(int fila, int columna) {
        if (!celdaValida(fila, columna) || plantas[fila][columna] == null) {
            return false;
        }
        plantas[fila][columna] = null;
        return true;
    }

    /** Si el clic cayó sobre un sol, lo recoge y suma puntos. */
    public boolean recogerSol(int px, int py) {
        // recorremos al revés para tomar el sol que está "encima"
        for (int i = soles.size() - 1; i >= 0; i--) {
            Sol s = soles.get(i);
            if (s.contiene(px, py)) {
                s.recoger();
                puntosSol += s.getValor();
                return true;
            }
        }
        return false;
    }

    public void alternarPausa() {
        if (estado == EstadoJuego.JUGANDO) {
            estado = EstadoJuego.PAUSA;
        } else if (estado == EstadoJuego.PAUSA) {
            estado = EstadoJuego.JUGANDO;
        }
    }

    // =====================================================================
    //  CONSULTAS QUE USAN LAS ENTIDADES (interacción entre objetos)
    // =====================================================================

    /** ¿Hay algún zombie visible a la derecha de x en esa fila? */
    public boolean hayZombieAdelante(int fila, double x) {
        double bordeDerecho = Config.TABLERO_X + Config.COLUMNAS * Config.ANCHO_CELDA + 10;
        for (Zombie z : zombies) {
            if (z.getFila() == fila && z.getX() > x && z.getX() < bordeDerecho) {
                return true;
            }
        }
        return false;
    }

    /** Regresa la planta que el zombie tiene enfrente (para comérsela), o null. */
    public Planta plantaFrenteA(Zombie z) {
        int col = Config.columnaEn(z.getX() - 18);
        if (col < 0) {
            return null;
        }
        Planta p = plantas[z.getFila()][col];
        if (p == null) {
            return null;
        }
        if (p instanceof PetaCereza cereza && cereza.estaExplotando()) {
            return null;
        }
        return p;
    }

    /** Regresa el primer zombie que toca el proyectil, o null. */
    public Zombie zombieGolpeadoPor(Proyectil p) {
        for (Zombie z : zombies) {
            if (z.estaVivo() && z.getFila() == p.getFila() && Math.abs(z.getX() - p.getX()) < 16) {
                return z;
            }
        }
        return null;
    }

    /** Zombies dentro de un cuadrado de (2*radio+1) celdas alrededor de (fila, columna). */
    public List<Zombie> zombiesEnArea(int fila, int columna, int radio) {
        List<Zombie> lista = new ArrayList<>();
        double xMin = Config.TABLERO_X + (columna - radio) * Config.ANCHO_CELDA - 20;
        double xMax = Config.TABLERO_X + (columna + radio + 1) * Config.ANCHO_CELDA + 20;
        for (Zombie z : zombies) {
            if (Math.abs(z.getFila() - fila) <= radio && z.getX() >= xMin && z.getX() <= xMax) {
                lista.add(z);
            }
        }
        return lista;
    }

    public void agregarProyectil(Proyectil p) {
        proyectiles.add(p);
    }

    public void agregarSol(Sol s) {
        soles.add(s);
    }

    public void agregarZombie(Zombie z) {
        zombies.add(z);
    }

    // =====================================================================
    //  GETTERS PARA LA VISTA
    // =====================================================================

    public boolean puedeComprar(TipoPlanta tipo) {
        return puntosSol >= tipo.getCosto() && recargas.get(tipo) == 0;
    }

    /** Recarga restante de 0.0 (lista) a 1.0 (recién usada). */
    public double porcentajeRecarga(TipoPlanta tipo) {
        return (double) recargas.get(tipo) / tipo.getRecargaTicks();
    }

    public boolean celdaValida(int fila, int columna) {
        return fila >= 0 && fila < Config.FILAS && columna >= 0 && columna < Config.COLUMNAS;
    }

    public boolean celdaLibre(int fila, int columna) {
        return celdaValida(fila, columna) && plantas[fila][columna] == null;
    }

    public Planta getPlanta(int fila, int columna) {
        return plantas[fila][columna];
    }

    public List<Zombie> getZombies() {
        return zombies;
    }

    public List<Proyectil> getProyectiles() {
        return proyectiles;
    }

    public List<Sol> getSoles() {
        return soles;
    }

    public GestorOleadas getOleadas() {
        return oleadas;
    }

    public EstadoJuego getEstado() {
        return estado;
    }

    public int getPuntosSol() {
        return puntosSol;
    }

    public int getZombiesEliminados() {
        return zombiesEliminados;
    }

    /** Tiempo jugado en segundos. */
    public long getSegundosJugados() {
        return ticks / Config.TICKS_POR_SEGUNDO;
    }
}
