/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sigcovisualizador;

import javax.swing.UIManager;
import vistas.frmHandlerVisualizador;
import vistas.frmPantallaVisualizador;

/**
 *
 * @author jnxd_
 */
public class SigcoVisualizador {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frmHandlerVisualizador ventanaHandler = new frmHandlerVisualizador();
        ventanaHandler.pack();
        ventanaHandler.setLocationRelativeTo(null);
        ventanaHandler.setVisible(true);
    }
}
