/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente_sources;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.image.ImageObserver.WIDTH;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.TOP;
import javax.swing.Timer;
import modelos.Atencion;
import modelos.Paciente;
import modelos.Ventanilla;
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
     *  Variable que se utiliza para generar una cola de llamadas por varios clientes.
     */
    public LinkedList<String> llamadaExecuting = new LinkedList<>();

    public Component[] componentesFilaActual = new Component[2];
    public Component[] componentesFilaAnterior = new Component[2];

    /**
     * Variable que se utiliza para mostrar el estado de la conexion.
     */
    public JLabel lblEstadoConexionPantalla;
    
    /**
     * Variable que sirve para verificar si hay conexion con el servidor del socket o no.
     */
    public boolean isConexion = false;
    
    /**
     * Variables que referencian iconos.
     */
    public ImageIcon iconoPreferencial = new ImageIcon(this.getClass().getResource("/icons/preferencialIcon.png"));
    public ImageIcon iconoPersonaGeneral = new ImageIcon(this.getClass().getResource("/icons/person-32.png"));
    public ImageIcon iconoPreferencial64x64 = new ImageIcon(this.getClass().getResource("/icons/preferencialIcon64x64.png"));
    public ImageIcon iconoPersonaGeneral64x64 = new ImageIcon(this.getClass().getResource("/icons/person-64.png"));

    /**
     * Variables que referencian iconos para la variable lblEstadConexionPantalla.
     */
    public final ImageIcon iconoStatusErrorPantalla = new ImageIcon(this.getClass().getResource("/icons/error(32).png"));
    public final ImageIcon iconoStatusWarningPantalla = new ImageIcon(this.getClass().getResource("/icons/triangulo(32).png"));
    public final ImageIcon iconoStatusSuccessPantalla = new ImageIcon(this.getClass().getResource("/icons/correcto(32).png"));
    public final ImageIcon iconoStatusLoadingPantalla = new ImageIcon(this.getClass().getResource("/icons/cargando(32).png"));

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
     * Variable que se usa como fuente para los JLabels de los nombres de los pacientes en cola.
     */
    public Font fuenteNombreTurno = new Font("Arial", Font.BOLD, 24);
    
    /**
     * Variable que se usa como fuente para los JLabels del DNI de los pacientes en cola.
     */
    public Font fuenteDniTurno = new Font("Arial", Font.ITALIC, 24);

    /**
     * Variable que se usa como fuente para el texto de los JLabels de las ventanillas que se muestra en las notificaciones.
     */
    public Font fuenteVentanillaAtencion = new Font("Arial", Font.BOLD, 36);
    
    /**
     * Variable que se usa como fuente para el texto de los JLabels de los pacientes que se muestra en las notificaciones.
     */
    public Font fuentePacienteAtencion = new Font("Arial", Font.BOLD, 24);

    /**
     * Variable que sirve como banderin para validar si se está notificando en la pantalla una atencion.
     */
    boolean isNotificando = false;
    
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
     *  @deprecated 
     * Agrega un contacto al JComboBox de contactos.
     *
     * @param contacto
     */
    void addContacto(String contacto) {
        listaContactos.add(contacto);
    }

    /**
     *  @deprecated 
     * Agrega un nuevo mensaje al historial de la conversación.
     *
     * @param emisor
     * @param mensaje
     */
    void addMensaje(String emisor, String mensaje) {
        System.out.println(emisor + ": " + mensaje);
        //txtHistorial.append("##### "+emisor + " ##### : \n" + mensaje+"\n");
    }

    /**
     *  @deprecated 
     * @param paciente 
     */
    void addPacienteCola(Paciente paciente) {}

    /**
     *  @deprecated 
     *
     * @param identificador
     */
    void sesionIniciada(String identificador) {
        //this.setTitle(" --- "+identificador+" --- ");
    }

    /**
     *  @deprecated 
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
                        JLabel lblNombre = new JLabel(paciente.getNombre().replace("-", " "), LEFT);
                        JLabel lblNroDni = new JLabel(ocultarDni(paciente.getNroDni(), 4), CENTER);
                        lblNombre.setSize(WIDTH, 50);
                        if (paciente.getPreferencial() == 1) {
                            lblNombre.setIcon(iconoPreferencial);
                        } else {
                            lblNombre.setIcon(iconoPersonaGeneral);
                        }
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
     * Metodo que elimina la notificacion de atencion con la ventanilla pasada por parametro.
     * @param ventanilla 
     */
    public void quitarNotifiacionVentanilla(String ventanilla) {
        Atencion atencionEncontrada = null;
        for (Atencion aten : atenciones) {
            if (aten.getNombreVentanilla().equals(ventanilla)) {
                atencionEncontrada = aten;
            }
        }
        if (atencionEncontrada != null) {
            atenciones.remove(atencionEncontrada);
        }
        listaSlotsNotificaciones.forEach((slot) -> {
            slot.removeAll();
        });
        rellenarFilaNotifiaciones();
    }

    /**
     * Metodo que agrega un paciente a la lista pacientes segun corresponda.
     * @param paciente 
     */
    public void agregarPacienteLista(Paciente paciente) {
        if (paciente.getPreferencial() == 1) {
            if (pacientes.isEmpty()) {
                pacientes.add(paciente);
            }
            for (int i = 0; i < pacientes.size(); i++) {
                Paciente pac = pacientes.get(i);
                if (pac.getPreferencial() != 1) {
                    pacientes.add(i, paciente);
                    break;
                }
            }
        } else {
            pacientes.add(paciente);
        }
    }

    /**
     * Metodo que rellena los paneles de la columna de la cola con los pacientes de la lista pacientes.
     */
    public void rellenarColumnaCola() {
        int count = 0;
        for (JPanel fila : listaFilasPaneles) {
            if (count < pacientes.size()) {
                Paciente pac = pacientes.get(count);
                JLabel lblNombre = new JLabel(pac.getNombre().replace("-", " "), LEFT);
                JLabel lblNroDni = new JLabel(ocultarDni(pac.getNroDni(), 4), CENTER);
                lblNombre.setSize(WIDTH, 50);
                if (pac.getPreferencial() == 1) {
                    lblNombre.setIcon(iconoPreferencial);
                } else {
                    lblNombre.setIcon(iconoPersonaGeneral);
                }
                lblNombre.setFont(fuenteNombreTurno);
                lblNroDni.setFont(fuenteDniTurno);
                fila.add(lblNombre);
                fila.add(lblNroDni);
                this.revalidate();
                this.repaint();
                count++;
            }
        }
    }

    /**
     * Metodo que rellena los paneles de las notificaciones de las antenciones con las atenciones de la lista atenciones.
     */
    public void rellenarFilaNotifiaciones() {
        int count = 0;
        for (JPanel slot : listaSlotsNotificaciones) {
            if (count < atenciones.size()) {
                Atencion atencion = atenciones.get(count);
                JLabel lblVentanilla = new JLabel(atencion.getNombreVentanilla().toUpperCase(), CENTER);
                JLabel lblPaciente = new JLabel(pacienteHtml(atencion.getPaciente().getNombre(), atencion.getPaciente().getNroDni()), CENTER);
                lblVentanilla.setForeground(Color.WHITE);
                lblPaciente.setForeground(Color.WHITE);
                lblPaciente.setFont(fuentePacienteAtencion);
                if (atencion.getPaciente().getPreferencial() == 1) {
                    lblPaciente.setIcon(iconoPreferencial64x64);
                } else {
                    lblPaciente.setIcon(iconoPersonaGeneral64x64);
                }

                lblVentanilla.setFont(fuenteVentanillaAtencion);
                lblVentanilla.setVerticalAlignment(TOP);
                lblVentanilla.setHorizontalAlignment(CENTER);
                lblPaciente.setVerticalAlignment(CENTER);
                lblPaciente.setHorizontalAlignment(CENTER);

                slot.add(lblVentanilla);
                slot.add(lblPaciente);
                this.revalidate();
                this.repaint();
                count++;
                //break;
            }
            //break;
        }
    }
    
    /**
     * Metodo que agrega un paciente a la lista pacientes e invoca al metodo rellenarColumnaCola()
     * 
     * @param nombre
     * @param nroDni
     * @param isPreferencial
     * @param idTurno 
     */
    public void agregarNombreTabla(String nombre, String nroDni, int isPreferencial, int idTurno) {
        Paciente paciente = new Paciente(nombre, nroDni, isPreferencial, idTurno);
        agregarPacienteLista(paciente);
        listaFilasPaneles.forEach((fila) -> {
            fila.removeAll();
        });
        rellenarColumnaCola();
    }

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
     * @deprecated
     * Metodo que permite semi ocultar el numero del DNI del paciente en la pantalla de visualizacion.
     * 
     * @param nroDni
     * @return 
     */
    public String ocultarDni(String nroDni) {
        String[] nroDniSeparada = nroDni.split("");
        String nroDniSemiOculto = "";
        int count = 0;
        for (String digito : nroDniSeparada) {
            if (count < nroDniSeparada.length) {
                digito = "*";
            }
            nroDniSemiOculto += digito;
            count++;
        }
        return nroDniSemiOculto;
    }

    /**
     * Metodo que permite semi ocultar el numero del DNI del paciente en la pantalla de visualizacion.
     * 
     * @param nroDni
     * @param cantCharMostrar
     * @return 
     */
    public String ocultarDni(String nroDni, int cantCharMostrar) {
        String[] nroDniSeparada = nroDni.split("");
        String nroDniSemiOculto = "";
        int count = 0;
        for (String digito : nroDniSeparada) {
            if (count < nroDniSeparada.length - cantCharMostrar) {
                digito = "*";
            }
            nroDniSemiOculto += digito;
            count++;
        }
        return nroDniSemiOculto;
    }

    /**
     * Metodo que retorna en formato html el nombre y el numero de DNI de un paciente.
     * 
     * @param nombre
     * @param nroDni
     * @return 
     */
    public String pacienteHtml(String nombre, String nroDni) {
        if (!nombre.isEmpty()) {
            String primerNombre = "";
            String apellido = "";
            if (nombre.contains("-")) {
                String[] nombreSeparado = nombre.split("-");
                if (nombreSeparado.length >= 3) {
                    primerNombre = nombreSeparado[0];
                    apellido = nombreSeparado[1] + " " + nombreSeparado[2];
                } else {
                    primerNombre = nombreSeparado[0];
                    apellido = nombreSeparado[1];
                }
            } else if (nombre.contains(" ")) {
                String[] nombreSeparado = nombre.split(" ");
                if (nombreSeparado.length >= 3) {
                    primerNombre = nombreSeparado[0];
                    apellido = nombreSeparado[1] + " " + nombreSeparado[2];
                } else {
                    primerNombre = nombreSeparado[0];
                    apellido = nombreSeparado[1];
                }
            } else {
                return "<html><div style=\"text-align:center;\">" + nombre + "</div><div style=\"text-align:center;\">" + ocultarDni(nroDni, 4) + "</div></html>";
            }
            return "<html><div style=\"text-align:center;\">" + primerNombre + "<br>" + apellido + "</div><div style=\"text-align:center;\">" + ocultarDni(nroDni, 4) + "</div></html>";
        } else {
            return "<html><div style=\"text-align:center;\">" + ocultarDni(nroDni, 4) + "</div></html>";
        }
    }

    /**
     * Metodo que realiza el parpadeo y reproduccion del sonido de un turno en pantalla
     * Recibe como parametro el slot del turno a notificar.
     * 
     * @param slot 
     */
    public void notificarTurno(JPanel slot) {
        Timer timer = new Timer(300, new ActionListener() {
            private String nextColor = "red";
            private String color = "#Ff0000";
            Sonido sonido = new Sonido();
            int limitFlicker = 6;
            int timesFlicker = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                isNotificando = true;
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
                } else if (timesFlicker == limitFlicker) {
                    llamadaExecuting.pop();
                }
                slot.revalidate();
                slot.repaint();
                timesFlicker++;
            }
        });
        timer.start();
    }

    /**
     * Metodo que notifica el turno colocandolo en el primer slot.
     * 
     * @param nombreVentanilla 
     */
    public void notificarTurnoVentanilla(String nombreVentanilla) {
        int count = 0;
        for (Atencion atencion : atenciones) {
            if (atencion.getNombreVentanilla().equals(nombreVentanilla)) {
                Atencion atencionNotificar = atenciones.remove(count);
                atenciones.add(0, atencionNotificar);
                break;
            }
            count++;
        }
        listaSlotsNotificaciones.forEach((slot) -> {
            slot.removeAll();
        });
        rellenarFilaNotifiaciones();
        notificarTurno(listaSlotsNotificaciones.get(0));
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
        listaSlotsNotificaciones.forEach((slot) -> {
            slot.removeAll();
        });
//        Thread.sleep(2000);
        rellenarFilaNotifiaciones();
        JPanel slot = listaSlotsNotificaciones.get(0);
        if (slot.getName().equals("Slot 1")) {
            notificarTurno(slot);
        }
    }

//    public void mostrarPaciente(String nroDni, String nombre) {
//        frmVentanillaAdmin.lblNroDniPaciente.setText(nroDni);
//        frmVentanillaAdmin.lblNombrePaciente.setText(nombre);
//    }
}
