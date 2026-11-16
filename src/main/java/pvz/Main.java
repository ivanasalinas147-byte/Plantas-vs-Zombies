package pvz;

import javax.swing.SwingUtilities;
import pvz.vista.VentanaJuego;

/**
 * Punto de entrada del programa.
 * Swing pide que la interfaz se cree dentro de su propio hilo (Event Dispatch Thread),
 * por eso usamos SwingUtilities.invokeLater.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaJuego ventana = new VentanaJuego();
            ventana.setVisible(true);
        });
    }
}
