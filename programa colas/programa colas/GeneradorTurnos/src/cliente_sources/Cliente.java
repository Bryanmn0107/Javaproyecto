/*
 * Ejemplo desarrollado por Erick Navarro
 * Blog: e-navarro.blogspot.com
 * Noviembre - 2015
 */
package cliente_sources;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelos.Paciente;

/**
 * Clase en la que se maneja la comunicación del lado del cliente.
 *
 * @author Erick Navarro
 */
public class Cliente extends Thread {

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
        try {
//                this.sleep(1000L);
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusLoadingPantalla);
            }
            socket = new Socket(host, puerto);
            ventana.isConexion = true;
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Conexion exitosa!!!!");

            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusSuccessPantalla);
            }
            this.enviarSolicitudConexion(identificador);
            this.escuchar();
            //break;
        } catch (UnknownHostException ex) {
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusWarningPantalla);
            }
            ventana.isConexion = false;
//            JOptionPane.showMessageDialog(/*ventana*/null, "Conexión rehusada, servidor desconocido,\n"
//                    + "puede que haya ingresado una ip incorrecta\n"
//                    + "o que el servidor no este corriendo.\n"
//                    + "Esta aplicación se cerrará.");
//            System.exit(0);
        } catch (IOException ex) {
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusErrorPantalla);
            }
            ventana.isConexion = false;
//            JOptionPane.showMessageDialog(/*ventana*/null, "Conexión rehusada, error de Entrada/Salida,\n"
//                    + "puede que haya ingresado una ip o un puerto\n"
//                    + "incorrecto, o que el servidor no este corriendo.\n"
//                    + "Esta aplicación se cerrará.");
//            System.exit(0);
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
            }
        }
    }

    /**
     * Método que envia un determinado mensaje hacia el servidor.
     *
     * @param cliente_receptor
     * @param nombre
     * @param nroDni
     * @param isPreferencial
     * @param idTurno
     */
    public void enviarMensaje(String cliente_receptor, String nombre, String nroDni, String isPreferencial,String idTurno) {
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
        //ID del Turno
        lista.add(idTurno);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
            if (ventana.lblEstadoConexionPantalla != null) {
                ventana.lblEstadoConexionPantalla.setIcon(ventana.iconoStatusWarningPantalla);
            }
        }
    }

    /**
     * Método que envia un determinado mensaje hacia el servidor.
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
            }
        }
    }

    /*
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
            }
            ventana.isConexion = false;
//            JOptionPane.showMessageDialog(/*ventana*/null, "La comunicación con el servidor se ha\n"
//                    + "perdido, este chat tendrá que finalizar.\n"
//                    + "Esta aplicación se cerrará.");
//            System.exit(0);
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
                //ventana.sesionIniciada(identificador);
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
                // 1      - Cliente emisor
                // 2      - Cliente receptor
                // 3      - Nombre Paciente
                // 4      - DNI Paciente
                String destinatario = lista.get(2);
                System.out.println(destinatario);
                if (destinatario.equals("Visualizador")) {
                    String nombre = lista.get(3);
                    String nroDni = lista.get(4);
                    String preferencial = lista.get(5);
                    String idTurno=lista.get(6);
                    ventana.agregarNombreTabla(nombre, nroDni, Integer.parseInt(preferencial),Integer.parseInt(idTurno));
                } else if (destinatario.startsWith("Ventanilla")) {
                    String nombre = lista.get(3);
                    String nroDni = lista.get(4);
//                    ventana.mostrarPaciente(nroDni, nombre);
                }
                break;
            case "LLAMAR_PACIENTE":
                // 1      - Cliente emisor
                // 2      - Cliente receptor
                String ventanilla = lista.get(1);
                Paciente primerPaciente = ventana.obtenerPrimerPaciente();
                if (primerPaciente != null) {
                    String nombre = primerPaciente.getNombre();
                    String nroDni = primerPaciente.getNroDni();
                    String preferencial = String.valueOf(primerPaciente.getPreferencial());
                    String idTurno=String.valueOf(primerPaciente.getIdTurno());
                    ventana.agregarNotificacionAtencion(ventanilla, primerPaciente);
                    ventana.quitarNombreTabla();
                    enviarMensaje(ventanilla, nombre, nroDni, preferencial,idTurno);
                }
                break;
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
            }
        }
    }

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
