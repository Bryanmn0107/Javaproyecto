/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente_sources;

import vistas.frmVentanillaAdmin;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import static java.awt.TrayIcon.MessageType.INFO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.image.ImageObserver.WIDTH;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.Timer;
import javax.swing.border.Border;
import modelos.Atencion;
import modelos.Paciente;
import modulos.ModuleCronometro;
import modulos.ModuleProcedimientosTurnos;
import resources.Sonido;

/**
 * Clase padre de los formularios que se conectan al servidor socket.
 *
 * @author jnxd_
 */
public class VentanaCliente extends javax.swing.JFrame {

    public Component[] filas;
    public Component[] slots;
    
    /**
     * Variable que se utiliza para mostrar el estado de la conexion.
     */
    public JLabel lblEstadoConexionPantalla;
    
    /**
     * Variable que sirve para verificar si hay conexion con el servidor del socket o no.
     */
    public boolean isConexion = false;
    
    /**
     * Variable que valida si existen turnos en espera.
     */
    public boolean existenTurnosEspera = false;
    
    /**
     * Variable que valida si el Label de volver a llamar está visible o no.
     */
    public static boolean lblLlamarIsVisible = false;
    
    /**
     * Variable que valida si el paciente asistio a su atencion.
     */
    public static boolean pacienteAsistio = false;
    
    /**
     * Instancia de un borde para el Label de volver a llamar al paciente.
     */
    public static Border bordeLblLlamarButton = BorderFactory.createLineBorder(Color.decode("#ffffff"), 1);
    
    /**
     * Variables que referencian iconos.
     */
    //public ImageIcon iconoPreferencial = new ImageIcon(getClass().getResource("/icons/preferencialIcon.png"));
    public ImageIcon iconoPersonaGeneral = new ImageIcon(getClass().getResource("/icons/person-rever-48.png"));
    public ImageIcon iconoPreferencial48x48 = new ImageIcon(getClass().getResource("/icons/preferencialIcon48x48.png"));
    public ImageIcon iconoPersonaGeneral48x48 = new ImageIcon(getClass().getResource("/icons/person-48.png"));

    /**
     * Variables que referencian iconos para la variable lblEstadoConexionPantalla.
     */
    public ImageIcon iconoStatusErrorPantalla = new ImageIcon(getClass().getResource("/icons/error(32).png"));
    public ImageIcon iconoStatusWarningPantalla = new ImageIcon(getClass().getResource("/icons/triangulo(32).png"));
    public ImageIcon iconoStatusSuccessPantalla = new ImageIcon(getClass().getResource("/icons/correcto(32).png"));
    public ImageIcon iconoStatusLoadingPantalla = new ImageIcon(getClass().getResource("/icons/cargando(32).png"));
    
    /**
     * Variables que referencian iconos para los botones de detener e iniciar atencion.
     */
    public ImageIcon iconoDetener = new ImageIcon(getClass().getResource("/icons/stop-48.png"));
    public ImageIcon iconoIniciar = new ImageIcon(getClass().getResource("/icons/incio-48.png"));
    
    /**
     * Variable que almacena los paneles que muestran a los pacientes esperando en cola.
     */
    public ArrayList<JPanel> listaFilasPaneles = new ArrayList();
    
    /**
     * Variable que almacena los pacientes esperando en cola.
     */
    public ArrayList<Paciente> pacientes = new ArrayList();
    
    /**
     * Variable que almacena las atenciones que se estan dando en el momento.
     */
    public ArrayList<Atencion> atenciones = new ArrayList();
    
    /**
     * Variable que almacena los paneles que muestran los turnos llamados.
     */
    public ArrayList<JPanel> listaSlotsNotificaciones = new ArrayList();
    
    /**
     * Variables que instancian los cronometros para los timers respectivos en cada situacion.
     */
    public ModuleCronometro cronometroHabilitarBotonVolverLlamar; // Para habilitar el boton que permite volver a llamar a un paciente.
    public ModuleCronometro cronometroEsperaSiguienteTicket; // Para esperar y llamar automaticamente al siguiente turno.
    public ModuleCronometro cronometroEsperaNoPresentó; // Para esperar y marcar como no se presentó.
    public ModuleCronometro moduloCronometro; //
    
    /**
     * Variable que permite acceder a la bandeja del sistema.
     */
    public static SystemTray systemTray;
    
    /**
     * Variable que almacena un icono de bandeja.
     */
    public static TrayIcon trayIcon;
    
    /**
     * Variable que se usa como fuente para los JLabels de los nombres de los pacientes en cola.
     */
    public Font fuenteNombreTurno = new Font("Arial", Font.BOLD, 24);
    
    /**
     * Variable que se usa como fuente para los JLabels del DNI de los pacientes en cola.
     */
    public Font fuenteDniTurno = new Font("Arial", Font.PLAIN, 18);

    /**
     * Variable que se usa como fuente para el texto de los JLabels de las ventanillas que se muestra en las notificaciones.
     */
    public Font fuenteVentanillaAtencion = new Font("Arial", Font.BOLD, 36);
    
    /**
     * Variable que se usa como fuente para el texto de los JLabels de los pacientes que se muestra en las notificaciones.
     */
    public Font fuentePacienteAtencion = new Font("Arial", Font.BOLD, 24);

    /**
     * Variable que instancia un sonido.
     */
    Sonido sonido = new Sonido();
    
    /**
     * Constante que almacena el puerto por defecto para la aplicación.
     */
    private final String DEFAULT_PORT = "5000";
    /**
     * Constante que almacena la IP por defecto (localhost) para el servidor.
     */
    private final String DEFAULT_IP = "127.0.0.1";
    /**
     * Constante que almacena el cliente, con el cual se gestiona la
     * comunicación con el servidor.
     */
    private ArrayList<String> listaContactos;

    /**
     * Agrega un contacto al JComboBox de contactos.
     *
     * @param contacto
     */
    void addContacto(String contacto) {
        listaContactos.add(contacto);
    }

    /**
     * Agrega un nuevo mensaje al historial de la conversación.
     *
     * @param emisor
     * @param mensaje
     */
    void addMensaje(String emisor, String mensaje) {
        System.out.println(emisor + ": " + mensaje);
        //txtHistorial.append("##### "+emisor + " ##### : \n" + mensaje+"\n");
    }

    void addPacienteCola(Paciente paciente) {

    }

    /**
     * Se configura el título de la ventana para una nueva sesión.
     *
     * @param identificador
     */
    void sesionIniciada(String identificador) {
        //this.setTitle(" --- "+identificador+" --- ");
    }

    /**
     * Método que abre una ventana para que el usuario ingrese la IP del host en
     * el que corre el servidor, el puerto con el que escucha y el nombre con el
     * que quiere participar en el chat.
     *
     * @return
     */
    private String[] getIP_Puerto_Nombre() {
        String s[] = new String[3];
        s[0] = DEFAULT_IP;
        s[1] = DEFAULT_PORT;
        JTextField ip = new JTextField(20);
        JTextField puerto = new JTextField(20);
        JTextField usuario = new JTextField(20);
        ip.setText(DEFAULT_IP);
        puerto.setText(DEFAULT_PORT);
        usuario.setText("Usuario");
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(3, 2));
        myPanel.add(new JLabel("IP del Servidor:"));
        myPanel.add(ip);
        myPanel.add(new JLabel("Puerto de la conexión:"));
        myPanel.add(puerto);
        myPanel.add(new JLabel("Escriba su nombre:"));
        myPanel.add(usuario);
        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Configuraciones de la comunicación", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            s[0] = ip.getText();
            s[1] = puerto.getText();
            s[2] = usuario.getText();
        } else {
            System.exit(0);
        }
        return s;
    }

    /**
     * Método que elimina cierto cliente de la lista de contactos, este se llama
     * cuando cierto usuario cierra sesión.
     *
     * @param identificador
     */
    void eliminarContacto(String identificador) {
        for (int i = 0; i < listaContactos.size(); i++) {
            if (listaContactos.get(i).equals(identificador)) {
                listaContactos.remove(i);
                return;
            }
        }
    }

//    public void quitarNombreTabla() {
//        int count = 0;
//        if (!pacientes.isEmpty()) {
//            pacientes.remove(count);
//            for (JPanel fila : listaFilasPaneles) {
//                if (fila.getComponentCount() > 0) {
//                    fila.removeAll();
//                    if (count < pacientes.size()) {
//                        Paciente paciente = pacientes.get(count);
//                        JLabel lblNombre = new JLabel(paciente.getNombre(), CENTER);
//                        JLabel lblNroDni = new JLabel(paciente.getNroDni(), CENTER);
//                        lblNombre.setSize(WIDTH, 50);
//                        lblNombre.setIcon(iconoPreferencial);
//                        lblNombre.setFont(fuenteNombreTurno);
//                        lblNroDni.setFont(fuenteDniTurno);
//                        fila.add(lblNombre);
//                        fila.add(lblNroDni);
//                        count++;
//                    }
//                }
//                this.revalidate();
//                this.repaint();
//            }
//        }
//    }

//    public void agregarNombreTabla(String nombre, String nroDni, int preferencial) {
//        Paciente paciente = new Paciente(nombre, nroDni, preferencial);
//        pacientes.add(paciente);
//        for (JPanel fila : listaFilasPaneles) {
//            if (fila.getComponentCount() == 0) {
//                JLabel lblNombre = new JLabel(nombre, CENTER);
//                JLabel lblNroDni = new JLabel(nroDni, CENTER);
//                lblNombre.setSize(WIDTH, 50);
//                lblNombre.setIcon(iconoPreferencial);
//                lblNombre.setFont(fuenteNombreTurno);
//                lblNroDni.setFont(fuenteDniTurno);
//                fila.add(lblNombre);
//                fila.add(lblNroDni);
//                this.revalidate();
//                this.repaint();
//                break;
//            }
//        }
//    }

    /**
     * Metodo que retorna al primer Paciente en cola.
     * @return 
     */
    public Paciente obtenerPrimerPaciente() {
        if (!pacientes.isEmpty()) {
            return pacientes.get(0);
        } else {
            return null;
        }
    }

    /**
     * Metodo que retorna en formato html el nombre y el numero de DNI de un paciente.
     * 
     * @param nombre
     * @param nroDni
     * @return 
     */
    public String pacienteHtml(String nombre, String nroDni) {
        return "<html><div style=\"text-align:left;\">" + nombre + "</div><div style=\"text-align:left;\">" + nroDni + "</div></html>";
    }

    /**
     * Metodo que retorna en formato html el nombre completo de un paciente.
     * 
     * @param nombre
     * @return 
     */
    public String pacienteHtml(String nombre) {
        String[] nombreApellidos = nombre.split("-");
        String nombre1 = nombreApellidos[0];
        return "<html><div style=\"text-align:center;\">" + nombre1 + "</div><divstyle=\"text-align:center;\">" + nombreApellidos[1] + " " + nombreApellidos[2] + "</div></html>";
    }
    
    /**
     * Metodo que realiza el parpadeo y reproduccion del sonido de un turno en pantalla
     * Recibe como parametro el slot del turno a notificar.
     * 
     * @param slot 
     */
    public void notificarTurno(JPanel slot) {
        Timer timer = new Timer(500, new ActionListener() {
            private String nextColor = "red";
            private String color = "#Ff0000";
            Sonido sonido = new Sonido();
            int limitFlicker = 6;
            int timesFlicker = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (timesFlicker < limitFlicker) {
                    for (Component component : slot.getComponents()) {
                        component.setForeground(Color.decode(color));
                    }
                    if (nextColor.equals("red")) {
                        color = "#ffffff";
                        nextColor = "white";
                    } else {
                        color = "#ff0000";
                        nextColor = "red";
                    }
                    if (timesFlicker % 2 == 0) {
                        sonido.reproducirSonido();
                    }
                }
                slot.revalidate();
                slot.repaint();
                timesFlicker++;
            }
        });
        timer.start();
    }

    /**
     * Metodo que crea, agrega y notifica una atencion
     * 
     * @param ventanilla
     * @param paciente 
     */
    public void agregarNotificacionAtencion(String ventanilla, Paciente paciente) {
        Atencion atencionActual = new Atencion(ventanilla, paciente);
        atenciones.add(0, atencionActual);
        //System.out.println(atenciones.get(0).getPaciente().getNombre() + ", " + atenciones.get(0).getPaciente().getNroDni());
        int count = 0;
        for (JPanel slot : listaSlotsNotificaciones) {
            if (slot.getComponents() != null) {
                slot.removeAll();
            }
            if (count < atenciones.size()) {
                Atencion atencion = atenciones.get(count);
                count++;
                if (slot.getComponentCount() == 0) {
                    JLabel lblVentanilla = new JLabel(atencion.getNombreVentanilla().toUpperCase(), CENTER);
                    JLabel lblPaciente = new JLabel(pacienteHtml(atencion.getPaciente().getNombre(), atencion.getPaciente().getNroDni()), CENTER);
                    lblVentanilla.setForeground(Color.WHITE);
                    lblPaciente.setForeground(Color.WHITE);
                    //lblNombre.setSize(WIDTH, 50);
                    lblPaciente.setIcon(iconoPreferencial48x48);
                    lblVentanilla.setFont(fuenteVentanillaAtencion);
                    lblPaciente.setFont(fuentePacienteAtencion);
                    slot.add(lblVentanilla);
                    slot.add(lblPaciente);
                    if (slot.getName().equals("Slot 1")) {
                        notificarTurno(slot);
                    }
                    this.revalidate();
                    this.repaint();
                    //break;
                }
            }
            //break;
        }
    }

    /**
     * Metodo que muestra al paciente llamado en la ventana de administrador de ventanilla.
     * 
     * @param nroDni
     * @param nombre
     * @param preferencial
     * @param idTurno 
     */
    public void mostrarPaciente(String nroDni, String nombre, String preferencial, String idTurno) {
        cronometroEsperaSiguienteTicket.resetStop();
        frmVentanillaAdmin.lblNroDniPaciente.setText(nroDni);
        frmVentanillaAdmin.lblNombrePaciente.setToolTipText(nombre.replace("-", " "));
        frmVentanillaAdmin.lblNombrePaciente.setText(nombre.replace("-", " "));
        frmVentanillaAdmin.idTurno = idTurno;
        frmVentanillaAdmin.btnLlamarSiguiente.setEnabled(false);
        frmVentanillaAdmin.btnIniciarDetenerAtencion.setEnabled(true);
        frmVentanillaAdmin.btnNoSePresento.setEnabled(true);
//        frmVentanillaAdmin.lblLlamarButton.setVisible(true);
        frmVentanillaAdmin vtn = new frmVentanillaAdmin();
        vtn.setVisibleLlamarButton(true);
        if (preferencial.equals("1")) {
            frmVentanillaAdmin.lblNombrePaciente.setIcon(iconoPreferencial48x48);
        } else {
            frmVentanillaAdmin.lblNombrePaciente.setIcon(iconoPersonaGeneral);
        }
        sonido.reproducirSonido();
        ModuleProcedimientosTurnos.llamarTicketSiguiente(frmVentanillaAdmin.idVentanilla, idTurno);
        cronometroEsperaNoPresentó.start();
        moduloCronometro.displayNotificationCustom("Nuevo paciente", "Hay un nuevo paciente esperando que lo atiendas", INFO);
    }
}
