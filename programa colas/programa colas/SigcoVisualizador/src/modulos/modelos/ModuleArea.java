/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulos.modelos;

import conexion.ConexionSQL;
import static conexion.ConexionSQL.cerrarConexion;
import static conexion.ConexionSQL.establecerConexion;
import static conexion.ConexionSQL.llamarConexion;
import static conexion.ConexionSQL.rs;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import modelos.Area;

/**
  * La clase ModuleArea proporciona métodos para gestionar las áreas dentro de la aplicación.
 * 
 * Este módulo incluye funciones para interactuar con la base de datos y poblar componentes de la interfaz de usuario,
 * como JComboBox, con datos de áreas.
 * 
 * @author jnxd_
 */
public class ModuleArea {

//    public static ArrayList<Area> listaAreas = new ArrayList<>();
    
    /**
     * Método para agregar las áreas disponibles a un JComboBox.
     * 
     * Este método establece una conexión con la base de datos, ejecuta un procedimiento almacenado para obtener las áreas activas,
     * y agrega estas áreas al JComboBox proporcionado.
     * 
     * @param comboBox El JComboBox que será poblado con las áreas disponibles.
     */
    public static void agregarAreasComboBox(JComboBox comboBox) {
        try {
            establecerConexion();
            CallableStatement cst;
            comboBox.removeAllItems();
            //areas.clear();
//            listaAreas.clear();
            String filtro = " WHERE Estado = 1";
            cst = llamarConexion.prepareCall("{call [MostrarAreaVentanilla](?)}");
            cst.setString(1, filtro);
            rs = cst.executeQuery();
            comboBox.addItem("Seleccionar..."); // AQUI ESTA EL ERROR
            while (rs.next()) {
                int idArea = rs.getInt(1);
                String nombreArea = rs.getString(2);
                Area nuevaArea = new Area(idArea, nombreArea);
                comboBox.addItem(nuevaArea);
//                listaAreas.add(nuevaArea);
            }
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }
}
