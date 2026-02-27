/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.clases;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * ImageComponent es una clase que extiende JComponent para mostrar una imagen en un componente Swing
 * 
 * Esta clase permite mostrar una imagen en un componente gráfico dentro de un JFrame
 * Puede sobreescribir el método paintComponent para dibujar la imagen en el componente.
 *
 * @author jnxd_
 */
public class ImageComponent extends JComponent{
    /**
     * Imagen que se dibujará en el componente.
     */
    private Image imagen;
    
    /**
     * JFrame en el que se mostrará el componente.
     */
    private JFrame frame;

    /**
     * Constructor de la clase ImageComponent.
     * 
     * @param imagen La imagen que se dibujará en el componente.
     * @param frame El JFrame en el que se mostrará el componente.
     */
    public ImageComponent(Image imagen, JFrame frame) {
        this.imagen = imagen;
        this.frame = frame;
    }

    /**
     * Sobrescribe el método paintComponent para dibujar la imagen.
     * 
     * @param g El contexto gráfico en el que dibujar.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this.frame);
    }
    
// Descomentar si se quiere establecer un tamaño preferido para el componente.    
//    @Override
//    public Dimension getPreferredSize() {
//        return new Dimension(500, 500);
//    }
}
