/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulos;

import conexion.ConexionSQL;
import static conexion.ConexionSQL.cerrarConexion;
import static conexion.ConexionSQL.establecerConexion;
import static conexion.ConexionSQL.llamarConexion;
import static conexion.ConexionSQL.rs;
import java.awt.event.ActionEvent;
import java.sql.CallableStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import static modulos.ModosCronometro.LLAMAR_TICKET;

/**
 * Clase que permite invocar los procedimientos respecto a los turnos.
 * 
 * @author jnxd_
 */
public class ModuleProcedimientosTurnos {

//    private Timer timer;
//    
//    public ModuleProcedimientosTurnos(int tiempoSegundos, String modo) {
//        int tiempoMilisegundos = tiempoSegundos * 1000;
//        this.timer = new javax.swing.Timer(tiempoMilisegundos, (ActionEvent e) -> {
//            switch (modo) {
//                case LLAMAR_TICKET:
//                    llamarTicketSiguiente();
//                    break;
//                case NO_SE_PRESENTO:
//                    vtnAdminTickets.marcarNoSePresento();
//                    break;
//                default:
//                    JOptionPane.showMessageDialog(null, e + " " + modo + " no existe", "Alerta", JOptionPane.ERROR_MESSAGE);
//                    break;
//            }
//        });
//    }
    
    /**
     * Metodo que permite llamar al siguiente ticket en base de datos.
     * 
     * @param idVentanilla
     * @param idTurno 
     */
    public static void llamarTicketSiguiente(int idVentanilla, String idTurno) {
        try {
            establecerConexion();
            CallableStatement cst;
            int idTurnoInteger = Integer.parseInt(idTurno);
            cst = llamarConexion.prepareCall("{call [actualizarVentanillaTickets](?,?)}");
            cst.setInt(1, idVentanilla);
            cst.setInt(2, idTurnoInteger);
            cst.executeUpdate();
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que permite actualizar la hora del inicio de atencion del ticket en base de datos.
     * 
     * @param idTurno
     * @param fechaHora 
     */
    public static void actualizarHoraAtencionInicioTicket(String idTurno, String fechaHora) {
        try {
            establecerConexion();
            CallableStatement cst;
            int idTurnoInteger = Integer.parseInt(idTurno);
            cst = llamarConexion.prepareCall("{call [actualizarHoraAtencionInicioTickets](?,?)}");
            cst.setInt(1, idTurnoInteger);
            cst.setString(2, fechaHora);
            cst.executeUpdate();
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que permite actualizar la hora del fin de atencion del ticket en base de datos.
     * 
     * @param idTurno
     * @param fechaHora
     * @param tiempoTranscurrido 
     */
    public static void actualizarHoraAtencionFinTicket(String idTurno, String fechaHora, String tiempoTranscurrido) {
        try {
            establecerConexion();
            CallableStatement cst;
            int idTurnoInteger = Integer.parseInt(idTurno);
            cst = llamarConexion.prepareCall("{call [actualizarHoraAtencionFinTickets](?,?,?)}");
            cst.setInt(1, idTurnoInteger);
            cst.setString(2, fechaHora);
            cst.setString(3, tiempoTranscurrido);
            cst.executeUpdate();
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Metodo que permite actualizar el estado del ticket en base de datos.
     * 
     * @param idTurno
     * @param idEstadoTicket 
     */
    public static void actualizarEstadoTicket(String idTurno, int idEstadoTicket) {
        try {
            establecerConexion();
            CallableStatement cst;
            int idTurnoInteger = Integer.parseInt(idTurno);
            cst = llamarConexion.prepareCall("{call [ActualizarEstadoTicket](?,?)}");
            cst.setInt(1, idTurnoInteger);
            cst.setInt(2, idEstadoTicket);
            cst.executeUpdate();
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }
}
