package pvz.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/** Colores, fuentes y botones compartidos por todas las pantallas. */
public final class Estilos {

    public static final Color VERDE_CLARO = new Color(120, 190, 70);
    public static final Color VERDE_OSCURO = new Color(100, 170, 55);
    public static final Color MADERA = new Color(120, 80, 40);
    public static final Color MADERA_CLARA = new Color(170, 120, 65);

    public static final Font TITULO = new Font("SansSerif", Font.BOLD, 54);
    public static final Font SUBTITULO = new Font("SansSerif", Font.BOLD, 22);
    public static final Font NORMAL = new Font("SansSerif", Font.BOLD, 15);
    public static final Font CHICA = new Font("SansSerif", Font.PLAIN, 13);

    private Estilos() {
    }

    public static JButton crearBoton(String texto) {
        JButton b = new JButton(texto);
        b.setFont(SUBTITULO);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(70, 140, 40));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 90, 20), 3),
                BorderFactory.createEmptyBorder(6, 18, 6, 18)));
        return b;
    }

    /** Activa el suavizado para que las figuras no se vean pixeladas. */
    public static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /** Texto con sombra, centrado en x. */
    public static void textoCentrado(Graphics2D g, String texto, int cx, int y, Color color) {
        int ancho = g.getFontMetrics().stringWidth(texto);
        int sombra = Math.max(1, g.getFont().getSize() / 18); // sombra proporcional al tamaño
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(texto, cx - ancho / 2 + sombra, y + sombra);
        g.setColor(color);
        g.drawString(texto, cx - ancho / 2, y);
    }
}
