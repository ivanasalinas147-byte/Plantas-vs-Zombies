package pvz.vista;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import pvz.Config;
import pvz.logica.EstadoJuego;
import pvz.logica.GestorOleadas;
import pvz.logica.Juego;
import pvz.modelo.Proyectil;
import pvz.modelo.Sol;
import pvz.modelo.plantas.Planta;
import pvz.modelo.plantas.TipoPlanta;
import pvz.modelo.zombies.Zombie;

/**
 * Pantalla donde se juega. Tiene un Timer que llama a juego.actualizar()
 * y repaint() 60 veces por segundo, y escucha los clics del ratón.
 */
public class PanelJuego extends JPanel implements ActionListener {

    // Tarjetas de la barra superior
    private static final int TARJETA_X = 95;
    private static final int TARJETA_Y = 8;
    private static final int TARJETA_ANCHO = 70;
    private static final int TARJETA_ALTO = 92;
    private static final int TARJETA_ESPACIO = 6;
    private static final int INDICE_PALA = TipoPlanta.values().length;

    private final VentanaJuego ventana;
    private final Timer timer = new Timer(Config.MS_POR_TICK, this);
    private final Map<TipoPlanta, Planta> iconos = new EnumMap<>(TipoPlanta.class);

    private Juego juego;
    private TipoPlanta seleccion;     // planta elegida (o null)
    private boolean palaActiva;
    private int ratonX = -1;
    private int ratonY = -1;
    private double anguloSol;

    public PanelJuego(VentanaJuego ventana) {
        this.ventana = ventana;
        setFocusable(true);

        // Una planta de muestra por tipo para dibujar las tarjetas
        for (TipoPlanta tipo : TipoPlanta.values()) {
            iconos.put(tipo, tipo.crear(0, 0));
        }

        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                manejarClic(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                ratonX = e.getX();
                ratonY = e.getY();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ratonX = -1;
                ratonY = -1;
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
        configurarTeclas();
    }

    private void configurarTeclas() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "pausa");
        getActionMap().put("pausa", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (juego != null) {
                    juego.alternarPausa();
                    repaint();
                }
            }
        });
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelar");
        getActionMap().put("cancelar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarSeleccion();
            }
        });
    }

    /** Empieza una partida nueva. */
    public void iniciar() {
        juego = new Juego();
        cancelarSeleccion();
        timer.start();
        requestFocusInWindow();
    }

    public void detener() {
        timer.stop();
    }

    /** Lo llama el Timer en cada tick. */
    @Override
    public void actionPerformed(ActionEvent e) {
        juego.actualizar();
        anguloSol += 0.02;
        repaint();

        EstadoJuego estado = juego.getEstado();
        if (estado == EstadoJuego.VICTORIA || estado == EstadoJuego.DERROTA) {
            timer.stop();
            // pequeña pausa para que se vea el final antes de cambiar de pantalla
            Timer espera = new Timer(1500, ev -> ventana.mostrarFin(juego));
            espera.setRepeats(false);
            espera.start();
        }
    }

    // =====================================================================
    //  RATÓN
    // =====================================================================

    private void manejarClic(MouseEvent e) {
        if (juego == null || juego.getEstado() != EstadoJuego.JUGANDO) {
            return;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            cancelarSeleccion();
            return;
        }
        int x = e.getX();
        int y = e.getY();

        // 1) Los soles van "encima" de todo
        if (juego.recogerSol(x, y)) {
            return;
        }
        // 2) Tarjetas de la barra superior
        int tarjeta = tarjetaEn(x, y);
        if (tarjeta >= 0) {
            elegirTarjeta(tarjeta);
            return;
        }
        // 3) El tablero
        int fila = Config.filaEn(y);
        int col = Config.columnaEn(x);
        if (fila < 0 || col < 0) {
            return;
        }
        if (palaActiva) {
            juego.quitarPlanta(fila, col);
            cancelarSeleccion();
        } else if (seleccion != null && juego.colocarPlanta(seleccion, fila, col)) {
            cancelarSeleccion();
        }
    }

    private void elegirTarjeta(int indice) {
        if (indice == INDICE_PALA) {
            boolean estabaActiva = palaActiva;
            cancelarSeleccion();
            palaActiva = !estabaActiva;
            return;
        }
        TipoPlanta tipo = TipoPlanta.values()[indice];
        if (seleccion == tipo) {
            cancelarSeleccion();
        } else if (juego.puedeComprar(tipo)) {
            cancelarSeleccion();
            seleccion = tipo;
        }
    }

    private void cancelarSeleccion() {
        seleccion = null;
        palaActiva = false;
    }

    /** Regresa el índice de la tarjeta bajo (x, y) o -1. */
    private int tarjetaEn(int x, int y) {
        if (y < TARJETA_Y || y > TARJETA_Y + TARJETA_ALTO || x < TARJETA_X) {
            return -1;
        }
        int paso = TARJETA_ANCHO + TARJETA_ESPACIO;
        int i = (x - TARJETA_X) / paso;
        int dentro = (x - TARJETA_X) % paso;
        if (i > INDICE_PALA || dentro > TARJETA_ANCHO) {
            return -1;
        }
        return i;
    }

    // =====================================================================
    //  DIBUJO
    // =====================================================================

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (juego == null) {
            return;
        }
        Graphics2D g = (Graphics2D) g0;
        Estilos.suavizar(g);

        dibujarFondo(g);
        dibujarVistaPrevia(g);
        dibujarEntidades(g);
        dibujarBarraSuperior(g);
        dibujarMensajes(g);
    }

    private void dibujarFondo(Graphics2D g) {
        // casa (lado izquierdo)
        g.setColor(new Color(190, 160, 120));
        g.fillRect(0, 0, Config.TABLERO_X, getHeight());
        g.setColor(new Color(160, 130, 95));
        for (int y = Config.TABLERO_Y; y < getHeight(); y += 30) {
            g.drawLine(0, y, Config.TABLERO_X, y);
        }
        // calle (lado derecho)
        int finTablero = Config.TABLERO_X + Config.COLUMNAS * Config.ANCHO_CELDA;
        g.setColor(new Color(110, 110, 115));
        g.fillRect(finTablero, 0, getWidth() - finTablero, getHeight());
        // barra superior
        g.setColor(new Color(90, 60, 30));
        g.fillRect(0, 0, getWidth(), Config.TABLERO_Y);

        // pasto a cuadros
        for (int f = 0; f < Config.FILAS; f++) {
            for (int c = 0; c < Config.COLUMNAS; c++) {
                g.setColor((f + c) % 2 == 0 ? Estilos.VERDE_CLARO : Estilos.VERDE_OSCURO);
                g.fillRect(Config.TABLERO_X + c * Config.ANCHO_CELDA, Config.TABLERO_Y + f * Config.ALTO_CELDA,
                        Config.ANCHO_CELDA, Config.ALTO_CELDA);
            }
        }
        // borde de tierra debajo del pasto
        int finPasto = Config.TABLERO_Y + Config.FILAS * Config.ALTO_CELDA;
        g.setColor(new Color(110, 75, 40));
        g.fillRect(Config.TABLERO_X, finPasto, finTablero - Config.TABLERO_X, getHeight() - finPasto);
    }

    private void dibujarVistaPrevia(Graphics2D g) {
        int fila = Config.filaEn(ratonY);
        int col = Config.columnaEn(ratonX);
        if (fila < 0 || col < 0) {
            return;
        }
        int x = Config.TABLERO_X + col * Config.ANCHO_CELDA;
        int y = Config.TABLERO_Y + fila * Config.ALTO_CELDA;

        if (palaActiva && !juego.celdaLibre(fila, col)) {
            g.setColor(new Color(255, 60, 60, 90));
            g.fillRect(x, y, Config.ANCHO_CELDA, Config.ALTO_CELDA);
        } else if (seleccion != null && juego.celdaLibre(fila, col)) {
            g.setColor(new Color(255, 255, 255, 70));
            g.fillRect(x, y, Config.ANCHO_CELDA, Config.ALTO_CELDA);
            Graphics2D copia = (Graphics2D) g.create();
            copia.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
            iconos.get(seleccion).dibujarCuerpo(copia, Config.centroColumna(col), Config.centroFila(fila));
            copia.dispose();
        }
    }

    private void dibujarEntidades(Graphics2D g) {
        for (int f = 0; f < Config.FILAS; f++) {
            for (int c = 0; c < Config.COLUMNAS; c++) {
                Planta p = juego.getPlanta(f, c);
                if (p != null) {
                    p.dibujar(g);
                }
            }
        }
        // Zombies ordenados por fila para que los de abajo tapen a los de arriba
        List<Zombie> ordenados = new ArrayList<>(juego.getZombies());
        ordenados.sort(Comparator.comparingInt(Zombie::getFila));
        for (Zombie z : ordenados) {
            z.dibujar(g);
        }
        for (Proyectil p : juego.getProyectiles()) {
            p.dibujar(g);
        }
        for (Sol s : juego.getSoles()) {
            s.dibujar(g);
        }
    }

    private void dibujarBarraSuperior(Graphics2D g) {
        // contador de soles
        g.setColor(Estilos.MADERA_CLARA);
        g.fillRoundRect(8, TARJETA_Y, 78, TARJETA_ALTO, 12, 12);
        Sol.dibujarIcono(g, 47, 42, 20, anguloSol);
        g.setColor(new Color(255, 250, 230));
        g.fillRoundRect(15, 74, 64, 22, 8, 8);
        g.setColor(Color.BLACK);
        g.setFont(Estilos.NORMAL);
        String puntos = String.valueOf(juego.getPuntosSol());
        g.drawString(puntos, 47 - g.getFontMetrics().stringWidth(puntos) / 2, 91);

        // tarjetas de plantas
        TipoPlanta[] tipos = TipoPlanta.values();
        for (int i = 0; i < tipos.length; i++) {
            dibujarTarjeta(g, tipos[i], TARJETA_X + i * (TARJETA_ANCHO + TARJETA_ESPACIO));
        }
        dibujarTarjetaPala(g, TARJETA_X + INDICE_PALA * (TARJETA_ANCHO + TARJETA_ESPACIO));

        dibujarProgreso(g);
    }

    private void dibujarTarjeta(Graphics2D g, TipoPlanta tipo, int x) {
        int y = TARJETA_Y;
        g.setColor(new Color(240, 230, 190));
        g.fillRoundRect(x, y, TARJETA_ANCHO, TARJETA_ALTO, 10, 10);

        // dibujo de la planta en pequeño
        Graphics2D copia = (Graphics2D) g.create();
        copia.translate(x + TARJETA_ANCHO / 2.0, y + 38);
        copia.scale(0.62, 0.62);
        iconos.get(tipo).dibujarCuerpo(copia, 0, 0);
        copia.dispose();

        // costo
        g.setColor(Color.BLACK);
        g.setFont(Estilos.NORMAL);
        String costo = String.valueOf(tipo.getCosto());
        g.drawString(costo, x + TARJETA_ANCHO / 2 - g.getFontMetrics().stringWidth(costo) / 2, y + TARJETA_ALTO - 7);

        // oscurecer si no alcanza el sol o está recargando
        if (!juego.puedeComprar(tipo)) {
            g.setColor(new Color(0, 0, 0, 90));
            g.fillRoundRect(x, y, TARJETA_ANCHO, TARJETA_ALTO, 10, 10);
        }
        double recarga = juego.porcentajeRecarga(tipo);
        if (recarga > 0) {
            g.setColor(new Color(0, 0, 0, 110));
            g.fillRect(x, y, TARJETA_ANCHO, (int) (TARJETA_ALTO * recarga));
        }
        dibujarBorde(g, x, seleccion == tipo);
    }

    private void dibujarTarjetaPala(Graphics2D g, int x) {
        int y = TARJETA_Y;
        g.setColor(new Color(150, 120, 80));
        g.fillRoundRect(x, y, TARJETA_ANCHO, TARJETA_ALTO, 10, 10);
        // mango
        g.setColor(new Color(110, 70, 30));
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x + 22, y + 20, x + 42, y + 52);
        g.setStroke(new BasicStroke(1));
        // hoja de metal
        g.setColor(new Color(190, 195, 205));
        g.fillPolygon(new int[]{x + 36, x + 54, x + 58, x + 44},
                new int[]{y + 48, y + 44, y + 72, y + 74}, 4);
        g.setColor(Color.WHITE);
        g.setFont(Estilos.CHICA);
        g.drawString("Pala", x + 21, y + TARJETA_ALTO - 7);
        dibujarBorde(g, x, palaActiva);
    }

    private void dibujarBorde(Graphics2D g, int x, boolean resaltado) {
        g.setStroke(new BasicStroke(resaltado ? 4 : 2));
        g.setColor(resaltado ? Color.YELLOW : new Color(60, 40, 20));
        g.drawRoundRect(x, TARJETA_Y, TARJETA_ANCHO, TARJETA_ALTO, 10, 10);
        g.setStroke(new BasicStroke(1));
    }

    private void dibujarProgreso(Graphics2D g) {
        GestorOleadas o = juego.getOleadas();
        int x = Config.ANCHO_VENTANA - 250;
        int y = 30;
        g.setFont(Estilos.NORMAL);
        g.setColor(Color.WHITE);
        if (o.isEsperandoPrimeraOleada()) {
            g.drawString("¡Prepárate! Planta girasoles", x, y + 10);
            g.setFont(Estilos.CHICA);
            g.drawString("Los zombies vienen en camino...", x, y + 32);
            return;
        }
        g.drawString("Oleada " + o.getOleadaVisible() + " / " + o.getTotalOleadas(), x, y + 10);
        g.setColor(new Color(50, 30, 10));
        g.fillRoundRect(x, y + 22, 220, 18, 10, 10);
        g.setColor(new Color(200, 60, 40));
        g.fillRoundRect(x, y + 22, (int) (220 * o.getProgreso()), 18, 10, 10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y + 22, 220, 18, 10, 10);
        g.setFont(Estilos.CHICA);
        g.drawString("Eliminados: " + juego.getZombiesEliminados(), x, y + 62);
    }

    private void dibujarMensajes(Graphics2D g) {
        String mensaje = juego.getOleadas().getMensaje();
        if (mensaje != null) {
            g.setFont(Estilos.TITULO);
            Estilos.textoCentrado(g, mensaje, getWidth() / 2, getHeight() / 2, new Color(230, 40, 30));
        }
        if (juego.getEstado() == EstadoJuego.PAUSA) {
            g.setColor(new Color(0, 0, 0, 140));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setFont(Estilos.TITULO);
            Estilos.textoCentrado(g, "PAUSA", getWidth() / 2, getHeight() / 2, Color.WHITE);
            g.setFont(Estilos.NORMAL);
            Estilos.textoCentrado(g, "Presiona P para continuar", getWidth() / 2, getHeight() / 2 + 40, Color.WHITE);
        }
        if (juego.getEstado() == EstadoJuego.DERROTA) {
            g.setFont(Estilos.TITULO);
            Estilos.textoCentrado(g, "¡Los zombies entraron!", getWidth() / 2, getHeight() / 2, new Color(230, 40, 30));
        }
        if (juego.getEstado() == EstadoJuego.VICTORIA) {
            g.setFont(Estilos.TITULO);
            Estilos.textoCentrado(g, "¡Sobreviviste!", getWidth() / 2, getHeight() / 2, Color.YELLOW);
        }
    }
}
