/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videoplayer;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * La clase JPanelImage extiende JLabel y permite mostrar una imagen de fondo en un JPanel.
 * La imagen se redimensiona automáticamente para ajustarse al tamaño del panel.
 * 
 * @autor jnxd_
 */
public class JPanelImage extends JLabel {

    private int x, y; // Dimensiones del panel
    private String path; // Ruta de la imagen a mostrar

    /**
     * Constructor que inicializa el panel con las dimensiones de un JPanel existente.
     * 
     * @param panel El JPanel existente del cual se tomarán las dimensiones.
     */
    public JPanelImage(JPanel panel) {
        this.x = panel.getWidth();
        this.y = panel.getHeight();
        this.setSize(x, y);
//        this.setSize(panel.getWidth(), panel.getHeight());
    }
    
    /**
     * Constructor que inicializa el panel con las dimensiones de un JPanel existente
     * y la ruta de la imagen a mostrar.
     * 
     * @param panel El JPanel existente del cual se tomarán las dimensiones.
     * @param path La ruta de la imagen a mostrar.
     */
    public JPanelImage(JPanel panel, String path) {
        this.path = path;
        this.x = panel.getWidth();
        this.y = panel.getHeight();
//        this.setSize(x, y);
        this.setSize(panel.getWidth(), panel.getHeight());
    }

    /**
     * Sobrescribe el método paint para dibujar la imagen en el panel.
     * 
     * @param g El objeto Graphics que se utilizará para dibujar la imagen.
     */
    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        ImageIcon img = new ImageIcon(getClass().getResource(path));
        //Image scaledImage = img.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        g.drawImage(img.getImage(), 0, 0, x, y, null);
    }
    
    /**
     * Redimensiona el panel y ajusta las dimensiones para la imagen.
     * 
     * @param x La nueva anchura del panel.
     * @param y La nueva altura del panel.
     */
    public void redimension(int x, int y){
        this.x = x;
        this.y = y;
        this.setSize(x, y);
    }
}
