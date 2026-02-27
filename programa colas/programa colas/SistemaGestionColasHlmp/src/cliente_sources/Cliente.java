/*
 * Ejemplo desarrollado por Erick Navarro
 * Blog: e-navarro.blogspot.com
 * Noviembre - 2015
 */
package cliente_sources;

import java.awt.TrayIcon;
import static java.awt.TrayIcon.MessageType.WARNING;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelos.Paciente;
import modulos.ModuleCronometro;

/**
 * Clase en la que se maneja la comunicación del lado del cliente.
 *
 * @author Erick Navarro
 */
public class Cliente extends Thread {

    /**
     * Variable utilizada como banderin para activar y/o desactivar el reintento de conexion - jn.
     */
    private boolean tryingConnect;
    
    /**
     * Socket utilizado para comunicarse con el servidor.
     */
    private Socket socket;
    /**
     * Stream utilizado para el envío de objetos al servidor.
     */
    private ObjectOutputStream objectOutputStream;
    /**
     * Stream utilizado para el envío de objetos al servidor.
     */
    private ObjectInputStream objectInputStream;
    /**
     * Ventana utilizada para la interfaz gráfica del cliente.
     */
    private final VentanaCliente ventana;
    /**
     * Identificador único del cliente dentro del chat.
     */
    private String identificador;
    /**
     * Variable que determina si el cliente escucha o no al servidor, una vez
     * que se arranca el hilo de comunicación del cliente.
     */
    private boolean escuchando;
    /**
     * Variable que almacena la IP del host en el que se ejecuta el servidor.
     */
    private final String host;
    /**
     * Varable que almacena el puerto por el cual el servidor escucha las
     * conexiones de los diversos clientes.
     */
    private final int puerto;

    /**
     * Constructor de la clase cliente.
     *
     * @param ventana
     * @param host
     * @param puerto
     * @param nombre
     */
    public Cliente(VentanaCliente ventana, String host, Integer puerto, String nombre) {
        this.ventana = ventana;
        this.host = host;
        this.puerto = puerto;
        this.identificador = nombre;
        escuchando = true;
        tryingConnect = true;
        this.start();
    }

    /**
     * Método run del hilo de comunicación del lado del cliente.
     */
    public void run() {
        /**
                * Bucle para reconexion con el servidor del socket.
                */
        while (tryingConnect) {
            try {
                this.sleep(1000L);
                if (ventana.lblEstadoConexionPantalla != null) {
                    ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusLoadingPantalla);
                    ventana.lblEstadoConexionPantalla.setText("Conectando...");
                }
                socket = new Socket(host, puerto);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                System.out.println("Conexion exitosa!!!!");
                ventana.isConexion = true; 
                if (ventana.lblEstadoConexionPantalla != null) {
                    ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusSuccessPantalla);
                    ventana.lblEstadoConexionPantalla.setText("Conectado");
                }
                this.enviarSolicitudConexion(identificador);
                this.escuchar();
                //break;
                
            } catch (UnknownHostException ex) {
                // Host desconocido o servidor al que intenta conectarse no existe
                if (ventana.lblEstadoConexionPantalla != null) {
                    ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusWarningPantalla);
                    ventana.lblEstadoConexionPantalla.setText("Conexion perdida");
                }
                ventana.isConexion = false;
            } catch (IOException ex) {
                if (ventana.lblEstadoConexionPantalla != null) {
                    ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusErrorPantalla);
                    ventana.lblEstadoConexionPantalla.setText("Error al conectar");
                }
                ventana.isConexion = false;
            } catch (InterruptedException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Método que cierra el socket y los streams de comunicación.
     */
    public void desconectar() {
        try {
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
            escuchando = false;
        } catch (Exception e) {
            System.err.println("Error al cerrar los elementos de comunicación del cliente.");
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusErrorPantalla);
                ventana.lblEstadoConexionPantalla.setText("Error al conectar");
            }
        }
    }

    /**
     * Método que envia los datos del paciente hacia el servidor.
     *
     * @param cliente_receptor
     * @param nombre
     * @param nroDni
     * @param isPreferencial
     */
    public void enviarMensaje(String cliente_receptor, String nombre, String nroDni, String isPreferencial) {
        LinkedList<String> lista = new LinkedList<>();
        //tipo
        lista.add("MENSAJE");
        //cliente emisor
        lista.add(identificador);
        //cliente receptor
        lista.add(cliente_receptor);
        //nombre del paciente
        lista.add(nombre);
        //nroDni del paciente
        lista.add(nroDni);
        //paciente preferencial
        lista.add(isPreferencial);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusWarningPantalla);
                ventana.lblEstadoConexionPantalla.setText("Conexion perdida");
            }
        }
    }
    
    /**
     * Método que envia una señal para que se libere la notificacion de la pantalla de visualizacion.
     *
     * @param cliente_receptor
     */
    public void enviarLiberarNotificacion(String cliente_receptor) {
        LinkedList<String> lista = new LinkedList<>();
        //tipo
        lista.add("LIBERA_NOTIFICACION");
        //cliente emisor
        lista.add(identificador);
        //cliente receptor
        lista.add(cliente_receptor);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusWarningPantalla);
                ventana.lblEstadoConexionPantalla.setText("Conexion perdida");
            }
        }
    }

    /**
     * Método que envia una señal para que se vuelva a llamar al paciente.
     *
     * @param cliente_receptor
     */
    public void enviarNotificarNuevamente(String cliente_receptor) {
        LinkedList<String> lista = new LinkedList<>();
        //tipo
        lista.add("NOTIFICAR_NUEVAMENTE");
        //cliente emisor
        lista.add(identificador);
        //cliente receptor
        lista.add(cliente_receptor);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusWarningPantalla);
                ventana.lblEstadoConexionPantalla.setText("Conexion perdida");
            }
        }
    }
    
    /**
     * Método que envia una señal hacia el servidor para llamar al proximo paciente
     *
     * @param cliente_receptor
     */
    public void llamarPaciente(String cliente_receptor) {
        LinkedList<String> lista = new LinkedList<>();
        //tipo
        lista.add("LLAMAR_PACIENTE");
        //cliente emisor
        lista.add(identificador);
        //cliente receptor
        lista.add(cliente_receptor);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusWarningPantalla);
                ventana.lblEstadoConexionPantalla.setText("Conexion perdida");
            }
        }
    }

    /**
     * Método que escucha constantemente lo que el servidor dice.
     */
    public void escuchar() {
        try {
            while (escuchando) {
                Object aux = objectInputStream.readObject();
                if (aux != null) {
                    if (aux instanceof LinkedList) {
                        //Si se recibe una LinkedList entonces se procesa
                        ejecutar((LinkedList<String>) aux); // AQUI HAY UN ERROR CON EL OTRO FORMULARIO (LA COPIA)
                    } else {
                        System.err.println("Se recibió un Objeto desconocido a través del socket");
                    }
                } else {
                    System.err.println("Se recibió un null a través del socket");
                }
            }
        } catch (Exception e) {
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusWarningPantalla);
                ventana.lblEstadoConexionPantalla.setText("Conexion perdida");
            }
            ventana.isConexion = false;
        }
    }

    /**
     * Método que ejecuta una serie de instruccines dependiendo del mensaje que
     * el cliente reciba del servidor.
     *
     * @param lista
     */
    public void ejecutar(LinkedList<String> lista) {
        
        // 0 - El primer elemento de la lista es siempre el tipo
        String tipo = lista.get(0);
        
        switch (tipo) {
            case "CONEXION_ACEPTADA":
                // 1      - Identificador propio del nuevo usuario
                // 2 .. n - Identificadores de los clientes conectados actualmente
                identificador = lista.get(1);
                for (int i = 2; i < lista.size(); i++) {
                    //ventana.addContacto(lista.get(i));
                }
                break;
            case "NUEVO_USUARIO_CONECTADO":
                // 1      - Identificador propio del cliente que se acaba de conectar
                //ventana.addContacto(lista.get(1));
                break;
            case "USUARIO_DESCONECTADO":
                // 1      - Identificador propio del cliente que se acaba de conectar
                //ventana.eliminarContacto(lista.get(1));
                break;
            case "MENSAJE":
                // lista.get(1)      - Cliente emisor.
                // lista.get(2)      - Cliente receptor.
                // lista.get(3)      - Nombre Paciente.
                // lista.get(4)      - DNI Paciente.
                // lista.get(5)      - Preferencial.
                // lista.get(6)      - IdTurno.
                String destinatario = lista.get(2);
                System.out.println(destinatario);
                if (destinatario.equals("Visualizador")) {
                    String nombre = lista.get(3);
                    String nroDni = lista.get(4);
                    String preferencial = lista.get(5);
                    //ventana.agregarNombreTabla(nombre, nroDni, Integer.parseInt(preferencial));
                } else if (destinatario.startsWith("Ventanilla")) {
                    ventana.existenTurnosEspera = true;
                    String nombre = lista.get(3);
                    String nroDni = lista.get(4);
                    String preferencial = lista.get(5);
                    String idTurno = lista.get(6);
                    ventana.mostrarPaciente(nroDni, nombre, preferencial, idTurno);
                }
                break;
            case "LLAMAR_PACIENTE":
                // lista.get(1)      - Cliente emisor
                // lista.get(2)      - Cliente receptor
                String ventanilla = lista.get(1);
                Paciente primerPaciente = ventana.obtenerPrimerPaciente();
                if (primerPaciente != null) {
                    String nombre = primerPaciente.getNombre();
                    String nroDni = primerPaciente.getNroDni();
                    String preferencial = String.valueOf(primerPaciente.getPreferencial());
                    ventana.agregarNotificacionAtencion(ventanilla, primerPaciente);
                    //ventana.quitarNombreTabla();
                    enviarMensaje(ventanilla, nombre, nroDni, preferencial);
                }
                break;
            case "NO_HAY_TURNOS":
                ModuleCronometro notificacion = new ModuleCronometro();
                notificacion.displayNotificationCustom("No hay turnos", "Por ahora no hay personas esperando su turno, intentelo mas tarde", WARNING);
                ventana.existenTurnosEspera = false;
            default:
                break;
        }
    }

    /**
     * Al conectarse el cliente debe solicitar al servidor que lo agregue a la
     * lista de clientes, para ello se ejecuta este método.
     *
     * @param identificador
     */
    private void enviarSolicitudConexion(String identificador) {
        LinkedList<String> lista = new LinkedList<>();
        //tipo
        lista.add("SOLICITUD_CONEXION");
        //cliente solicitante
        lista.add(identificador);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusWarningPantalla);
                ventana.lblEstadoConexionPantalla.setText("Conexion perdida");
            }
        }
    }

    /**
     * Cuando se cierra una ventana cliente, se debe notificar al servidor que
     * el cliente se ha desconectado para que lo elimine de la lista de clientes
     * y todos los clientes lo eliminen de su lista de contactos.
     */
    public void confirmarDesconexion() {
        LinkedList<String> lista = new LinkedList<>();
        //tipo
        lista.add("SOLICITUD_DESCONEXION");
        //cliente solicitante
        lista.add(identificador);
        try {
            this.tryingConnect = false;
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusWarningPantalla);
                ventana.lblEstadoConexionPantalla.setText("Conexion perdida");
            }
        }
    }

    // METODO NO EMPLEADO CON FRECUENCIA.
    
    /**
     * Método que retorna el identificador del cliente que es único dentro del
     * chat.
     *
     * @return
     */
    String getIdentificador() {
        return identificador;
    }
}
