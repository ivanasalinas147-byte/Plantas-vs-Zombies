package pvz.vista;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import pvz.Config;
import pvz.logica.EstadoJuego;
import pvz.logica.Juego;

/**
 * Ventana principal. Usa un CardLayout para cambiar entre las tres pantallas:
 * inicio, juego y fin.
 */
public class VentanaJuego extends JFrame {

    private static final String INICIO = "inicio";
    private static final String JUEGO = "juego";
    private static final String FIN = "fin";

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenedor = new JPanel(tarjetas);
    private final PanelInicio panelInicio;
    private final PanelJuego panelJuego;
    private final PanelFin panelFin;

    public VentanaJuego() {
        super("Plantas vs Zombies - Proyecto POO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        panelInicio = new PanelInicio(this);
        panelJuego = new PanelJuego(this);
        panelFin = new PanelFin(this);

        contenedor.setPreferredSize(new Dimension(Config.ANCHO_VENTANA, Config.ALTO_VENTANA));
        contenedor.add(panelInicio, INICIO);
        contenedor.add(panelJuego, JUEGO);
        contenedor.add(panelFin, FIN);
        setContentPane(contenedor);

        pack();
        setLocationRelativeTo(null); // centrar en la pantalla
        mostrarInicio();
    }

    public void mostrarInicio() {
        panelJuego.detener();
        tarjetas.show(contenedor, INICIO);
    }

    public void iniciarPartida() {
        tarjetas.show(contenedor, JUEGO);
        panelJuego.iniciar();
    }

    public void mostrarFin(Juego juego) {
        boolean gano = juego.getEstado() == EstadoJuego.VICTORIA;
        panelFin.setResultado(gano, juego.getZombiesEliminados(), juego.getSegundosJugados());
        tarjetas.show(contenedor, FIN);
    }
}
