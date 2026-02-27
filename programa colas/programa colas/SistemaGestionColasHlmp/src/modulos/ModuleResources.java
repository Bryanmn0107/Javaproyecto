/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulos;

import java.io.File;
import javax.swing.ImageIcon;

/**
 *@deprecated 
 * @author jnxd_
 */
public class ModuleResources {
    /**
     * Variable que almacena la ruta del manual.
     */
    private String rutaManual = getClass().getResource("/recursos/miarchivo.txt").getPath();
    
    public File manualFile = new File(rutaManual); 
}
