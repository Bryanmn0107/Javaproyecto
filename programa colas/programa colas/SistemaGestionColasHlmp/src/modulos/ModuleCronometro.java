/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulos;

import cliente_sources.VentanaCliente;
import static cliente_sources.VentanaCliente.bordeLblLlamarButton;
import static cliente_sources.VentanaCliente.lblLlamarIsVisible;
import static cliente_sources.VentanaCliente.trayIcon;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import static java.awt.TrayIcon.MessageType.INFO;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import static modulos.ModosCronometro.LLAMAR_TICKET;
import static modulos.ModosCronometro.NO_SE_PRESENTO;
import vistas.frmVentanillaAdmin;
import static vistas.frmVentanillaAdmin.lblLlamarButton;

/**
 * Clase para crear instancias de cronometros diferentes.
 * 
 * @author jnxd_
 */
public class ModuleCronometro extends frmVentanillaAdmin {

    /**
     * Variables que almacenan los segundos, minutos y horas del cronometro.
     */
    private int segundos = 0;
    private int minutos = 0;
    private int horas = 0;

    /**
     * Variable que referencia al timer del cronometro.
     */
    private Timer timer;

    /**
     * Variable que almacena el vetnana de frmVentanillaAdmin,
     * para acceder a algunos metodos de esta misma.
     */
    private frmVentanillaAdmin vtn;
    
    /**
     * @deprecated 
     */
    private VentanaCliente vtnC;
    
    /**
     * Variable que sirve como banderin para saber si se ha notificado un mensaje en el ordenador.
     */
    private boolean isNotificated = false;
    
    /**
     * Variable que almacena el tiempo promedio por atencion.
     */
    private int tiempoPromedioAtenciones;
    
    /**
     * Variable que almacena el tiempo extra otorgado.
     */
    private int tiempoExtra = 0;
    
    /**
     * @deprecated 
     */
    private boolean isVisibleLabel = false;

    /**
     * Constructor vacio de la clase.
     */
    public ModuleCronometro() {}

    /**
     * Constructor de la clase con el tiempo en segundos como parametro.
     * 
     * @param tiempoSegundos 
     */
    public ModuleCronometro(int tiempoSegundos) {
        int tiempoMilisegundos = tiempoSegundos * 1000;
        this.timer = new javax.swing.Timer(tiempoMilisegundos, (ActionEvent e) -> {
//            if (!pacienteAsistio) {
            lblLlamarButton.setForeground(Color.decode("#ffffff"));
            lblLlamarButton.setBackground(Color.decode("#ffffff"));
            lblLlamarButton.setIcon(iconoMegafonoLight30);
            lblLlamarButton.setBorder(bordeLblLlamarButton);
            lblLlamarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblLlamarButton.setOpaque(false);
            lblLlamarButton.revalidate();
            lblLlamarButton.repaint();
            lblLlamarIsVisible = true;
            resetStop();
//            } else {
//                lblLlamarIsVisible = false;
//                resetStop();
//            }
        });
    }

    /**
     * Constructor de la clase con el JLabel que muestra el tiempo, el tiempo promedio de atencion y 
     * la instancia de frmVentanillaAdmin como parametros.
     * 
     * @param label
     * @param tiempoPromedio
     * @param vtn 
     */
    public ModuleCronometro(JLabel label, int tiempoPromedio, frmVentanillaAdmin vtn) {
        this.vtn = vtn;
        this.tiempoPromedioAtenciones = tiempoPromedio;
        this.timer = new javax.swing.Timer(1000, (ActionEvent e) -> {
            segundos++;
            if (segundos == 60) {
                segundos = 0;
                minutos++;
            }
            if (minutos == 60) {
                minutos = 0;
                horas++;
            }

            if (minutos % tiempoPromedio == 0 && segundos == 0) {
                displayNotification();
                this.tiempoExtra = minutos + (tiempoPromedio / 2);
                isNotificated = true;
            }
            if (minutos == tiempoExtra && isNotificated) {
                this.timer.stop();
                vtn.isTiempoExcedido = true;
                vtn.iniciarDetenerAtencion();
                displayNotificationCustom("El tiempo terminó", "En unos segundos se llamara al siguiente paciente", INFO);
            }

            label.setText(String.format("%02d:%02d:%02d", horas, minutos, segundos));
        });
    }

    /**
     * Constructor de la clase con el tiempo en segundos, la instancia de frmVentanillaAdmin y el modo de ModosCronometros.
     * 
     * @param tiempoSegundos
     * @param vtn
     * @param modo 
     */
    public ModuleCronometro(int tiempoSegundos, frmVentanillaAdmin vtn, String modo) {
        int tiempoMilisegundos = tiempoSegundos * 1000;
        this.timer = new javax.swing.Timer(tiempoMilisegundos, (ActionEvent e) -> {
            switch (modo) {
                case LLAMAR_TICKET:
                    vtn.llamarPaciente();
                    break;
                case NO_SE_PRESENTO:
                    System.out.println("desde el timer del constructor");
                    vtn.noSePresento();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, e + " " + modo + " no existe", "Alerta", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        });
    }

    /**
     * Metodo que permite mostrar una notificacion en pantalla especificamente sobre el tiempo excedido.
     */
    private void displayNotification() {
        if (SystemTray.isSupported()) {
            trayIcon.displayMessage("Tiempo excedido", "Tendrá " + tiempoPromedioAtenciones / 2 + " minuto(s) más para atender, si no pasa al siguiente ticket, "
                    + "el sistema pasará automaticamente al siguiente ticket.", TrayIcon.MessageType.INFO);
        } else {
            JOptionPane.showMessageDialog(this.vtn, "Tendrá " + String.valueOf(tiempoPromedioAtenciones / 2) + " minuto(s) más para atender, si no pasa al siguiente ticket, "
                    + "el sistema pasará automaticamente al siguiente ticket.", "Tiempo excedido", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Metodo que permite mostrar una notificacion en pantalla personalizado.
     * 
     * @param title
     * @param text
     * @param icon 
     */
    public void displayNotificationCustom(String title, String text, TrayIcon.MessageType icon) {
        if (SystemTray.isSupported()) {
            trayIcon.displayMessage(title, text, icon);
        } else {
            JOptionPane.showMessageDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Metodo que permite iniciar el timer el cronometro.
     */
    public void start() {
        this.timer.start();
    }
    
    /**
     * Metodo que permite reiniciar y detener el cronometro.
     */
    public void resetStop() {
        this.stop();
        horas = 0;
        minutos = 0;
        segundos = 0;
    }

    /**
     * Metodo que permite detener el cronometro.
     */
    private void stop() {
        this.timer.stop();
    }
}
