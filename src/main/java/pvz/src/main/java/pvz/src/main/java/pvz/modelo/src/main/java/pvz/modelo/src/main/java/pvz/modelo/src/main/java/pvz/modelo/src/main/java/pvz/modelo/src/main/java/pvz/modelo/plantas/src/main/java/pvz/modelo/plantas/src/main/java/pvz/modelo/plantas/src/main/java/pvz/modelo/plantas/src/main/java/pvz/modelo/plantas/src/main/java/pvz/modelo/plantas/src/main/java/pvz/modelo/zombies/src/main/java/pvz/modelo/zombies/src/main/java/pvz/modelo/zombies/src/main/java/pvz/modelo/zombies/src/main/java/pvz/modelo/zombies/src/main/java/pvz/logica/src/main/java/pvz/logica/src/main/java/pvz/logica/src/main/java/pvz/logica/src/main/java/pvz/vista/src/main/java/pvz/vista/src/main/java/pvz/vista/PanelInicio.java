package pvz.vista;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import pvz.Config;
import pvz.modelo.plantas.Girasol;
import pvz.modelo.plantas.Lanzaguisantes;
import pvz.modelo.plantas.Nuez;
import pvz.modelo.plantas.Planta;
import pvz.modelo.zombies.Zombie;
import pvz.modelo.zombies.ZombieCono;
import pvz.modelo.zombies.ZombieCubeta;

/** Pantalla de inicio con el título y los botones Jugar, Instrucciones y Salir. */
public class PanelInicio extends JPanel {

    // Reutilizamos las mismas clases del juego como decoración
    private final Planta[] plantasDeco = {new Girasol(0, 0), new Lanzaguisantes(0, 0), new Nuez(0, 0)};
    private final Zombie[] zombiesDeco = {new ZombieCono(0), new ZombieCubeta(0)};

    public PanelInicio(VentanaJuego ventana) {
        setLayout(null); // posiciones manuales, la pantalla es de tamaño fijo

        int ancho = 240;
        int cx = Config.ANCHO_VENTANA / 2 - ancho / 2;

        JButton jugar = Estilos.crearBoton("Jugar");
        jugar.setBounds(cx, 270, ancho, 50);
        jugar.addActionListener(e -> ventana.iniciarPartida());

        JButton instrucciones = Estilos.crearBoton("Instrucciones");
        instrucciones.setBounds(cx, 335, ancho, 50);
        instrucciones.addActionListener(e -> mostrarInstrucciones());

        JButton salir = Estilos.crearBoton("Salir");
        salir.setBackground(new Color(150, 50, 40));
        salir.setBounds(cx, 400, ancho, 50);
        salir.addActionListener(e -> System.exit(0));

        add(jugar);
        add(instrucciones);
        add(salir);
    }

    private void mostrarInstrucciones() {
        String texto = """
                <html><body style='width:330px'>
                <h2>Cómo jugar</h2>
                <ul>
                <li>Haz clic en los <b>soles</b> para juntar puntos.</li>
                <li>Elige una tarjeta de planta arriba y haz clic en el pasto para plantarla.</li>
                <li>Cada planta cuesta soles y tiene un tiempo de <b>recarga</b>.</li>
                <li>Usa la <b>pala</b> para quitar una planta.</li>
                <li>Clic derecho o <b>ESC</b> cancela la selección. <b>P</b> pausa el juego.</li>
                </ul>
                <h3>Plantas</h3>
                <ul>
                <li><b>Girasol (50):</b> genera soles.</li>
                <li><b>Lanzaguisantes (100):</b> dispara a los zombies de su fila.</li>
                <li><b>Nuez (50):</b> aguanta mucho daño y frena zombies.</li>
                <li><b>PetaCereza (150):</b> explota y elimina zombies en un área de 3x3.</li>
                </ul>
                <p><b>Ganas</b> si sobrevives a las 3 oleadas.
                <b>Pierdes</b> si un zombie llega a tu casa.</p>
                </body></html>""";
        JOptionPane.showMessageDialog(this, texto, "Instrucciones", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        Estilos.suavizar(g);
        int w = getWidth();
        int h = getHeight();

        // cielo y pasto
        g.setPaint(new GradientPaint(0, 0, new Color(110, 180, 240), 0, h, new Color(200, 235, 255)));
        g.fillRect(0, 0, w, h);
        g.setColor(Estilos.VERDE_OSCURO);
        g.fillRect(0, h - 140, w, 140);
        g.setColor(Estilos.VERDE_CLARO);
        for (int i = 0; i < w; i += 80) {
            g.fillRect(i, h - 140, 40, 140);
        }

        // título
        g.setFont(Estilos.TITULO);
        Estilos.textoCentrado(g, "PLANTAS", w / 2, 95, new Color(120, 220, 60));
        g.setFont(Estilos.SUBTITULO);
        Estilos.textoCentrado(g, "vs", w / 2, 135, Color.WHITE);
        g.setFont(Estilos.TITULO);
        Estilos.textoCentrado(g, "ZOMBIES", w / 2, 195, new Color(170, 200, 160));
        g.setFont(Estilos.NORMAL);
        Estilos.textoCentrado(g, "Proyecto de Programación Orientada a Objetos", w / 2, 235, Color.WHITE);

        // plantas a la izquierda
        for (int i = 0; i < plantasDeco.length; i++) {
            plantasDeco[i].dibujarCuerpo(g, 60 + i * 80, h - 80);
        }
        // zombies a la derecha (los movemos con translate a donde queremos)
        for (int i = 0; i < zombiesDeco.length; i++) {
            Graphics2D copia = (Graphics2D) g.create();
            double dx = (w - 140 + i * 80) - zombiesDeco[i].getX();
            double dy = (h - 40) - (zombiesDeco[i].getY() + 40);
            copia.translate(dx, dy);
            zombiesDeco[i].dibujar(copia);
            copia.dispose();
        }

        g.setFont(Estilos.CHICA);
        Estilos.textoCentrado(g,
                "Equipo: Sarai Rodríguez · Alejandra Parra · Ivana Salinas · Eduardo Covarrubias",
                w / 2, h - 12, Color.WHITE);
    }
}
