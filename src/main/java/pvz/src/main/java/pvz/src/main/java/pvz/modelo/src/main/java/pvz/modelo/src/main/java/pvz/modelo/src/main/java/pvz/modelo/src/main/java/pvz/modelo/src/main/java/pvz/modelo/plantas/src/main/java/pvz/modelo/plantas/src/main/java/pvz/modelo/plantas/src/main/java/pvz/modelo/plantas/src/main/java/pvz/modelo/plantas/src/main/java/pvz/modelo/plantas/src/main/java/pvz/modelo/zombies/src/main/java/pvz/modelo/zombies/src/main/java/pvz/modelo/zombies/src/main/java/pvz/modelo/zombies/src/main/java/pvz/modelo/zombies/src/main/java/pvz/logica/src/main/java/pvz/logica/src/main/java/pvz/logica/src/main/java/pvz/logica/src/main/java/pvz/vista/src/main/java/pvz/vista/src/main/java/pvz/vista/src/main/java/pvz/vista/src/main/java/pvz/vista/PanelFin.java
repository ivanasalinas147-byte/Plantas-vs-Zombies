package pvz.vista;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JButton;
import javax.swing.JPanel;
import pvz.Config;
import pvz.modelo.plantas.Girasol;
import pvz.modelo.zombies.Zombie;

/** Pantalla final: muestra si ganaste o perdiste y unas estadísticas. */
public class PanelFin extends JPanel {

    private final Girasol girasol = new Girasol(0, 0);
    private boolean victoria;
    private int eliminados;
    private long segundos;

    public PanelFin(VentanaJuego ventana) {
        setLayout(null);
        int ancho = 240;
        int cx = Config.ANCHO_VENTANA / 2 - ancho / 2;

        JButton otraVez = Estilos.crearBoton("Jugar otra vez");
        otraVez.setBounds(cx, 380, ancho, 50);
        otraVez.addActionListener(e -> ventana.iniciarPartida());

        JButton menu = Estilos.crearBoton("Menú principal");
        menu.setBackground(new Color(120, 90, 40));
        menu.setBounds(cx, 445, ancho, 50);
        menu.addActionListener(e -> ventana.mostrarInicio());

        add(otraVez);
        add(menu);
    }

    public void setResultado(boolean victoria, int eliminados, long segundos) {
        this.victoria = victoria;
        this.eliminados = eliminados;
        this.segundos = segundos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        Estilos.suavizar(g);
        int w = getWidth();
        int h = getHeight();

        if (victoria) {
            g.setPaint(new GradientPaint(0, 0, new Color(120, 200, 250), 0, h, new Color(140, 210, 90)));
        } else {
            g.setPaint(new GradientPaint(0, 0, new Color(20, 15, 25), 0, h, new Color(90, 20, 20)));
        }
        g.fillRect(0, 0, w, h);

        // dibujo grande al centro
        Graphics2D copia = (Graphics2D) g.create();
        copia.translate(w / 2.0, 200);
        copia.scale(1.6, 1.6);
        if (victoria) {
            girasol.dibujarCuerpo(copia, 0, 0);
        } else {
            Zombie.dibujarCuerpo(copia, 8, 50, 0, false);
        }
        copia.dispose();

        g.setFont(Estilos.TITULO);
        if (victoria) {
            Estilos.textoCentrado(g, "¡GANASTE!", w / 2, 80, Color.YELLOW);
        } else {
            Estilos.textoCentrado(g, "¡PERDISTE!", w / 2, 80, new Color(230, 50, 40));
        }

        g.setFont(Estilos.SUBTITULO);
        String frase = victoria
                ? "Tu jardín resistió las 3 oleadas"
                : "Los zombies se comieron tus cerebros...";
        Estilos.textoCentrado(g, frase, w / 2, 315, Color.WHITE);

        g.setFont(Estilos.NORMAL);
        String stats = "Zombies eliminados: " + eliminados
                + "     Tiempo: " + (segundos / 60) + ":" + String.format("%02d", segundos % 60);
        Estilos.textoCentrado(g, stats, w / 2, 350, Color.WHITE);
    }
}
