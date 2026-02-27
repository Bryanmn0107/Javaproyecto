/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.clases;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * JPanelImageVersion es una clase que extiende JLabel para mostrar una imagen
 * dentro de un JPanel.
 *
 * Esta clase permite cargar y mostrar una imagen en un JLabel, ajustando su
 * tamaño según las dimensiones del JPanel proporcionado.
 *
 * @author jnxd_
 */
public class JPanelImageVersion extends JLabel {

    /**
     * Coordenadas x e y para definir el tamaño del JLabel.
     */
    private int x, y;

    /**
     * Ruta de la imagen que se cargará y mostrará en el JLabel.
     */
    private String path;

    /**
     * Constructor que inicializa el JLabel con las dimensiones del JPanel
     * proporcionado.
     *
     * @param panel El JPanel cuyas dimensiones se usarán para ajustar el tamaño
     * del JLabel.
     */
    public JPanelImageVersion(JPanel panel) {
        this.x = panel.getWidth();
        this.y = panel.getHeight();
//        this.setSize(x, y);
//        this.setSize(panel.getWidth(), panel.getHeight());
    }

    /**
     * Constructor que inicializa el JLabel con las dimensiones del JPanel
     * proporcionado y carga una imagen desde una ruta especificada.
     *
     * @param panel El JPanel cuyas dimensiones se usarán para ajustar el tamaño
     * del JLabel.
     * @param path La ruta de la imagen que se cargará y mostrará en el JLabel.
     */
    public JPanelImageVersion(JPanel panel, String path) {
        this.path = path;
        this.x = panel.getWidth();
        this.y = panel.getHeight();
//        this.setSize(x, y);
        this.setSize(panel.getWidth(), panel.getHeight());
    }

    @Override
//    public void paint(Graphics g) {
//        ImageIcon img = new ImageIcon(getClass().getResource(path));
//        g.drawImage(img.getImage(), 0, 0, x, y, null);
//    }
    
    /**
     * Sobrescribe el método paint para dibujar la imagen en el JLabel.
     * 
     * @param g El contexto gráfico en el que dibujar.
     */
    public void paint(Graphics g) {
        super.paintComponent(g);
        ImageIcon img = new ImageIcon(getClass().getResource(path));
        //Image scaledImage = img.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        g.drawImage(img.getImage(), 0, 0, x, y, null);
    }

    /**
     * Método para redimensionar el JLabel.
     * 
     * @param x La nueva anchura del JLabel.
     * @param y La nueva altura del JLabel.
     */
    public void redimension(int x, int y) {
        this.x = x;
        this.y = y;
        this.setSize(x, y);
    }
}
