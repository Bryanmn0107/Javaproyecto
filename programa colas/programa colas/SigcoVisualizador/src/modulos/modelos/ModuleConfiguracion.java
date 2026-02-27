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
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import modelos.Area;
import modelos.ConfiguracionArea;

/**
 * @deprecated
 * @author jnxd_
 */
public class ModuleConfiguracion {
    
    public static ArrayList<ConfiguracionArea> listaConfiguraciones = new ArrayList<>();
    
    public void getConfiguracion(int idArea){
        try {
            establecerConexion();
            CallableStatement cst;
            String filtro = " WHERE C.IdArea = " + idArea;
            cst = llamarConexion.prepareCall("{call [MostrarConfiguracionesAreasVentanillas](?)}");
            cst.setString(1, filtro);
            rs = cst.executeQuery();
            int limiteVentanillas;
            if (rs.next()) {
                limiteVentanillas = rs.getInt(4);
                for (int i = 1; i <= limiteVentanillas; i++) {
                    
                }
            }
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void agregarNumeroVentanillasComboBox(Object areaObject, JComboBox comboBox) {
        try {
            establecerConexion();
            CallableStatement cst;
            comboBox.removeAllItems();
            comboBox.addItem("Seleccionar...");
            Area area = (Area) areaObject;
            String filtro = " WHERE C.IdArea = " + area.getIdArea();
            cst = llamarConexion.prepareCall("{call [MostrarConfiguracionesAreasVentanillas](?)}");
            cst.setString(1, filtro);
            rs = cst.executeQuery();
            int limiteVentanillas;
            if (rs.next()) {
                limiteVentanillas = rs.getInt(4);
                for (int i = 1; i <= limiteVentanillas; i++) {
                    comboBox.addItem(String.valueOf(i));
                }
            }
//            comboBox.addItem(1);
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        } catch (ClassCastException cce) {
            
        }
    }
}
