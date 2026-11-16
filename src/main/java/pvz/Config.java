package pvz;

/**
 * Constantes del juego: tamaño del tablero, de la ventana y velocidad.
 * Tenerlas en un solo lugar facilita ajustar el juego sin buscar números
 * sueltos en todo el código.
 */
public final class Config {

    // ----- Tablero -----
    public static final int FILAS = 5;
    public static final int COLUMNAS = 9;
    public static final int ANCHO_CELDA = 80;
    public static final int ALTO_CELDA = 90;

    /** Esquina superior izquierda del pasto. */
    public static final int TABLERO_X = 70;
    public static final int TABLERO_Y = 110;

    // ----- Ventana -----
    public static final int ANCHO_VENTANA = TABLERO_X + COLUMNAS * ANCHO_CELDA + 90;
    public static final int ALTO_VENTANA = TABLERO_Y + FILAS * ALTO_CELDA + 20;

    // ----- Tiempo -----
    /** Milisegundos entre cada actualización (≈60 cuadros por segundo). */
    public static final int MS_POR_TICK = 16;
    /** Cuántos "ticks" hay en un segundo. Sirve para escribir tiempos en segundos. */
    public static final int TICKS_POR_SEGUNDO = 60;

    // ----- Economía -----
    public static final int SOLES_INICIALES = 150;
    public static final int VALOR_SOL = 25;

    private Config() {
        // Clase de utilidades: no se crean objetos de ella.
    }

    /** Coordenada X del centro de una columna. */
    public static int centroColumna(int columna) {
        return TABLERO_X + columna * ANCHO_CELDA + ANCHO_CELDA / 2;
    }

    /** Coordenada Y del centro de una fila. */
    public static int centroFila(int fila) {
        return TABLERO_Y + fila * ALTO_CELDA + ALTO_CELDA / 2;
    }

    /** Devuelve la columna que corresponde a una X de pantalla, o -1 si está fuera. */
    public static int columnaEn(double x) {
        if (x < TABLERO_X) {
            return -1;
        }
        int c = (int) ((x - TABLERO_X) / ANCHO_CELDA);
        return c < COLUMNAS ? c : -1;
    }

    /** Devuelve la fila que corresponde a una Y de pantalla, o -1 si está fuera. */
    public static int filaEn(double y) {
        if (y < TABLERO_Y) {
            return -1;
        }
        int f = (int) ((y - TABLERO_Y) / ALTO_CELDA);
        return f < FILAS ? f : -1;
    }

    /** Convierte segundos a ticks. */
    public static int segundos(double s) {
        return (int) Math.round(s * TICKS_POR_SEGUNDO);
    }
}
