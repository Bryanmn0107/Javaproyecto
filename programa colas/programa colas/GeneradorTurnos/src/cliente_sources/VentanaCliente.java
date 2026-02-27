/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente_sources;

//import interfaces.frmHandlerTurnosAtender1;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.image.ImageObserver.WIDTH;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.Timer;
import modelos.Atencion;
import modelos.Paciente;
import modelos.Ventanilla;
//import resources.Sonido;

/**
 *
 * @author jnxd_
 */
public class VentanaCliente extends javax.swing.JFrame {

    public Component[] filas;
    public Component[] slots;
    
    public JLabel lblEstadoConexionPantalla;
    
    /**
     * Variable que sirve para verificar si hay conexion con el servidor del socket o no.
     */
    public boolean isConexion = false;
    
    /**
     * Variables que referencian iconos.
     */
    public ImageIcon iconoPreferencial = new ImageIcon("src\\interfaces\\preferencialIcon.png");
    public ImageIcon iconoPreferencial64x64 = new ImageIcon("src\\interfaces\\preferencialIcon64x64.png");

    /**
     * Variables que referencian iconos para la variable lblEstadoConexionPantalla.
     */
    public final ImageIcon iconoStatusErrorPantalla = new ImageIcon("src\\icons\\error(32).png");
    public final ImageIcon iconoStatusWarningPantalla = new ImageIcon("src\\icons\\triangulo(32).png");
    public final ImageIcon iconoStatusSuccessPantalla = new ImageIcon("src\\icons\\correcto(32).png");
    public final ImageIcon iconoStatusLoadingPantalla = new ImageIcon("src\\icons\\cargando(32).png");

    public ArrayList<JPanel> listaFilasPaneles = new ArrayList();
    public ArrayList<Paciente> pacientes = new ArrayList();
    public ArrayList<Atencion> atenciones = new ArrayList();
    public ArrayList<JPanel> listaSlotsNotificaciones = new ArrayList();

    public Font fuenteNombreTurno = new Font("Arial", Font.BOLD, 24);
    public Font fuenteDniTurno = new Font("Arial", Font.PLAIN, 18);

    public Font fuenteVentanillaAtencion = new Font("Arial", Font.BOLD, 36);
    public Font fuentePacienteAtencion = new Font("Arial", Font.BOLD, 24);

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
     * @deprecated
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

    /**
     * @deprecated
     */
    public void quitarNombreTabla() {
        int count = 0;
        if (!pacientes.isEmpty()) {
            pacientes.remove(count);
            for (JPanel fila : listaFilasPaneles) {
                if (fila.getComponentCount() > 0) {
                    fila.removeAll();
                    if (count < pacientes.size()) {
                        Paciente paciente = pacientes.get(count);
                        JLabel lblNombre = new JLabel(paciente.getNombre(), CENTER);
                        JLabel lblNroDni = new JLabel(paciente.getNroDni(), CENTER);
                        lblNombre.setSize(WIDTH, 50);
                        lblNombre.setIcon(iconoPreferencial);
                        lblNombre.setFont(fuenteNombreTurno);
                        lblNroDni.setFont(fuenteDniTurno);
                        fila.add(lblNombre);
                        fila.add(lblNroDni);
                        count++;
                    }
                }
                this.revalidate();
                this.repaint();
            }
        }
    }

    /**
     * @deprecated
     * @param nombre
     * @param nroDni
     * @param preferencial
     * @param idTurno 
     */
    public void agregarNombreTabla(String nombre, String nroDni, int preferencial,int idTurno) {
        Paciente paciente = new Paciente(nombre, nroDni, preferencial,idTurno);
        pacientes.add(paciente);
        for (JPanel fila : listaFilasPaneles) {
            if (fila.getComponentCount() == 0) {
                JLabel lblNombre = new JLabel(nombre, CENTER);
                JLabel lblNroDni = new JLabel(nroDni, CENTER);
                lblNombre.setSize(WIDTH, 50);
                lblNombre.setIcon(iconoPreferencial);
                lblNombre.setFont(fuenteNombreTurno);
                lblNroDni.setFont(fuenteDniTurno);
                fila.add(lblNombre);
                fila.add(lblNroDni);
                this.revalidate();
                this.repaint();
                break;
            }
        }
    }

    /**
     * @deprecated
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
     * @deprecated 
     * @param nombre
     * @param nroDni
     * @return 
     */
    private String pacienteHtml(String nombre, String nroDni) {
        return "<html><div style=\"text-align:left;\">" + nombre + "</div><div style=\"text-align:left;\">" + nroDni + "</div></html>";
    }

    /**
     * @deprecated 
     * @param slot 
     */
    private void notificarTurno(JPanel slot) {
        Timer timer = new Timer(500, new ActionListener() {
            private String nextColor = "red";
            private String color = "#Ff0000";
//            Sonido sonido = new Sonido();
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
                    if (timesFlicker % 2 == 0){
//                        sonido.reproducirSonido();
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
     * @deprecated 
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
                    lblPaciente.setIcon(iconoPreferencial64x64);
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

//    public void mostrarPaciente(String nroDni, String nombre) {
//        frmHandlerTurnosAtender1.lblNroDni.setText(nroDni);
//        frmHandlerTurnosAtender1.lblNombre.setText(nombre);
//    }
}
