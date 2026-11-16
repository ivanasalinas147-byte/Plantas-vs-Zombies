package pvz.logica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import pvz.Config;
import pvz.modelo.zombies.TipoZombie;

/** Una oleada es una lista de zombies que salen uno tras otro cada cierto tiempo. */
public class Oleada {

    private final int numero;
    private final Queue<TipoZombie> pendientes = new LinkedList<>();
    private final int intervaloTicks;
    private final int total;

    public Oleada(int numero, int normales, int conos, int cubetas, double intervaloSegundos) {
        this.numero = numero;
        this.intervaloTicks = Config.segundos(intervaloSegundos);

        List<TipoZombie> lista = new ArrayList<>();
        for (int i = 0; i < normales; i++) {
            lista.add(TipoZombie.NORMAL);
        }
        for (int i = 0; i < conos; i++) {
            lista.add(TipoZombie.CONO);
        }
        for (int i = 0; i < cubetas; i++) {
            lista.add(TipoZombie.CUBETA);
        }
        Collections.shuffle(lista); // orden aleatorio para que cada partida sea distinta
        pendientes.addAll(lista);
        total = lista.size();
    }

    /** Saca el siguiente zombie de la fila de espera, o null si ya no hay. */
    public TipoZombie siguiente() {
        return pendientes.poll();
    }

    public boolean quedanZombies() {
        return !pendientes.isEmpty();
    }

    public int getNumero() {
        return numero;
    }

    public int getIntervaloTicks() {
        return intervaloTicks;
    }

    public int getTotal() {
        return total;
    }

    public int getRestantes() {
        return pendientes.size();
    }
}
