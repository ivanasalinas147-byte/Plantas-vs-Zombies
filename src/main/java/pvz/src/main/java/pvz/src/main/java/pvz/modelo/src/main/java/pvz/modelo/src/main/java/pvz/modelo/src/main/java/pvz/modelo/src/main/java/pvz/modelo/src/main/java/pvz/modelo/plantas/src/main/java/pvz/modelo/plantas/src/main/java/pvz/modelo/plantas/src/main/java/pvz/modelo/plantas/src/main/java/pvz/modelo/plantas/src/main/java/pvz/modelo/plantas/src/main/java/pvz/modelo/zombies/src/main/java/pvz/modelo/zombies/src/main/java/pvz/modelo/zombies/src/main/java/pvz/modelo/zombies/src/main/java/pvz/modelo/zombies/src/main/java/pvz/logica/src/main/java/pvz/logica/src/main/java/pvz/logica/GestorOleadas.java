package pvz.logica;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import pvz.Config;
import pvz.modelo.zombies.TipoZombie;

/**
 * Controla cuándo y dónde aparecen los zombies.
 * Hay 3 oleadas; la siguiente empieza cuando la anterior terminó
 * y ya no queda ningún zombie vivo en el tablero.
 */
public class GestorOleadas {

    private static final int ESPERA_INICIAL = Config.segundos(18);
    private static final int ESPERA_ENTRE_OLEADAS = Config.segundos(6);
    private static final int DURACION_MENSAJE = Config.segundos(2.5);

    private final List<Oleada> oleadas = new ArrayList<>();
    private final Random azar = new Random();
    private int indice;           // oleada actual
    private int contador;         // ticks para el siguiente evento
    private boolean enDescanso = true;
    private String mensaje = "";
    private int ticksMensaje;

    public GestorOleadas() {
        //                      #  normales conos cubetas  segundos entre zombies
        oleadas.add(new Oleada(1, 6, 0, 0, 8.0));
        oleadas.add(new Oleada(2, 6, 4, 0, 5.0));
        oleadas.add(new Oleada(3, 7, 5, 3, 3.5));
        contador = ESPERA_INICIAL;
    }

    public void actualizar(Juego juego) {
        if (ticksMensaje > 0) {
            ticksMensaje--;
        }
        if (terminadas()) {
            return;
        }

        Oleada actual = oleadas.get(indice);
        contador--;

        if (enDescanso) {
            if (contador <= 0) {
                enDescanso = false;
                contador = 0;
                mostrarMensaje(indice == oleadas.size() - 1
                        ? "¡OLEADA FINAL!"
                        : "Oleada " + actual.getNumero() + " de " + oleadas.size());
            }
            return;
        }

        if (actual.quedanZombies()) {
            if (contador <= 0) {
                TipoZombie tipo = actual.siguiente();
                int fila = azar.nextInt(Config.FILAS);
                juego.agregarZombie(tipo.crear(fila));
                contador = actual.getIntervaloTicks();
            }
        } else if (juego.getZombies().isEmpty()) {
            // Oleada limpia: pasar a la siguiente
            indice++;
            enDescanso = true;
            contador = ESPERA_ENTRE_OLEADAS;
        }
    }

    private void mostrarMensaje(String texto) {
        mensaje = texto;
        ticksMensaje = DURACION_MENSAJE;
    }

    /** True cuando ya salieron y se eliminaron todas las oleadas. */
    public boolean terminadas() {
        return indice >= oleadas.size();
    }

    /** Número de oleada a mostrar en pantalla (1, 2 o 3). */
    public int getOleadaVisible() {
        return Math.min(indice + 1, oleadas.size());
    }

    public int getTotalOleadas() {
        return oleadas.size();
    }

    /** Progreso total de 0.0 a 1.0 para la barra de nivel. */
    public double getProgreso() {
        int total = 0;
        int salieron = 0;
        for (int i = 0; i < oleadas.size(); i++) {
            Oleada o = oleadas.get(i);
            total += o.getTotal();
            salieron += o.getTotal() - o.getRestantes();
        }
        return total == 0 ? 1.0 : (double) salieron / total;
    }

    public String getMensaje() {
        return ticksMensaje > 0 ? mensaje : null;
    }

    public boolean isEsperandoPrimeraOleada() {
        return indice == 0 && enDescanso;
    }
}
