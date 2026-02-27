/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generadorturnos;

import javax.swing.UIManager;
import vistas.frmGeneradorTurnos;

/**
 *
 * @author jnxd_
 */
public class GeneradorTurnos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
//            e.printStackTrace();
        }
        
        frmGeneradorTurnos vtnGeneradorTurnos = new frmGeneradorTurnos();
        vtnGeneradorTurnos.setVisible(true);
    }
    
}
