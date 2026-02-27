/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Clase que permite crear horas y fechas.
 * 
 * @author jnxd_
 */
public class ModuleHoraFecha {

    /**
     * Variable que crea un formato de hora en horas y minutos.
     */
    private final DateFormat horaFormat = new SimpleDateFormat("HH:mm");
    
    /**
     * Variable que crea un formato de hora en horas, minutos y segundos.
     */
    private final DateFormat horaFormatSeconds = new SimpleDateFormat("HH:mm:ss");
    
    /**
     * Variable que crea un formato de fecha en dias, meses y años.
     */
    private final DateTimeFormatter fechaFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("es", "ES"));

    /**
     * Variable que referencia al timer de la hora.
     */
    private Timer timer;
    
    /**
     * Variable que referencia la hora actual.
     */
    private Calendar hora;
    
    /**
     * Variable que referencia la fecha actual.
     */
    private LocalDate fecha;
    
    /**
     * Constructor que asigna la fecha a un JLabel pasado por parametro.
     * 
     * @param lblHoraFecha
     * @throws InterruptedException 
     */
    public ModuleHoraFecha(JLabel lblHoraFecha) throws InterruptedException {
        fecha = LocalDate.now();
        hora = Calendar.getInstance();
        
        //String fechaFormateada = fecha.format(fechaFormat);
        
        //lblHoraFecha.setText(horaFormat.format(hora.getTime()) + " | " + );
        lblHoraFecha.setText(horaFormat.format(hora.getTime()) + " | " + fecha.format(fechaFormat) + "  ");

        timer = new Timer(1000, (ActionEvent e) -> {
            fecha = LocalDate.now();
            hora = Calendar.getInstance();
            lblHoraFecha.setText(horaFormat.format(hora.getTime()) + " | " + fecha.format(fechaFormat) + "  ");
        });

        timer.start();
    }
    
    /**
     * Metodo que permite formatear la fecha en un formato especifico, ambos pasados por parametros.
     * 
     * @param fecha
     * @param formato
     * @return 
     */
    public static String formatearFecha(Date fecha, String formato){
        String fechaFormateada = null;
        if (fecha != null) {
            // Crea un objeto SimpleDateFormat con el formato deseado
            SimpleDateFormat formatoFecha = new SimpleDateFormat(formato);

            // Formatea la fecha y asigna el resultado a fechaFormateada
            fechaFormateada = formatoFecha.format(fecha);
        }
        return fechaFormateada;
    }
    
    /**
     * Constructor de la clase que instancia la fecha actual.
     * 
     * @throws InterruptedException 
     */
    public ModuleHoraFecha() throws InterruptedException {
        fecha = LocalDate.now();
    }
    
    /**
     * Metodo que permite obtener a hora actual.
     * 
     * @return 
     */
    public String getCurrentTime(){
        Calendar horaActual = Calendar.getInstance();
        return horaFormatSeconds.format(horaActual.getTime());
    }
    
    /**
     * Metodo que permite iniciar el timer de la hora.
     */
    public void start() {
        timer.start();
    }

    /**
     * Metodo que permite detener el timer de la hora.
     */
    public void stop() {
        timer.stop();
    }
}
