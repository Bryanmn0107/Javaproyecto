/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_reniec_conexion;

import static conexion.ConexionSQL.cerrarConexion;
import static conexion.ConexionSQL.establecerConexion;
import static conexion.ConexionSQL.llamarConexion;
import static conexion.ConexionSQL.rs;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * La clase ApiConexion se encarga de manejar la conexión con la API de RENIEC.
 * Proporciona métodos para enviar solicitudes y recibir respuestas.
 * También decodifica las credenciales de conexión.
 * Además, gestiona las respuestas de la API de RENIEC, mostrando mensajes de error
 * correspondientes a los códigos de respuesta.
 * 
 * Nota: Esta clase asume la existencia de una conexión a una base de datos SQL para obtener
 * las credenciales de conexión a la API.
 * 
 * Los métodos de esta clase están diseñados específicamente para interactuar con la API
 * de RENIEC y manejar las respuestas correspondientes.
 * 
 * @author jnxd_
 */
public class ApiConexion {

    private String url;
    private String credencial;
    private String usuarioAutorizado;
    private String clave;

    private int codigoResponse;

    private HttpURLConnection connection;
    private OutputStream outputStream;

    private BufferedReader bufferedReaderIn;

    /**
     * Constructor de la clase ApiConexion. 
     * Inicializa las credenciales de conexión obtenidas desde la base de datos.
     */
    public ApiConexion() {
        obtenerCredenciales();
    }

    /**
     * Método privado para obtener las credenciales necesarias para la conexión desde la base de datos.
     */
    private void obtenerCredenciales() {
        try {
            establecerConexion();
            CallableStatement cst;
            cst = llamarConexion.prepareCall("{call [credenciales_test]}");
            rs = cst.executeQuery();
            if (rs.next()) {
                this.url = rs.getString(1);
                this.credencial = rs.getString(2);
                this.usuarioAutorizado = rs.getString(3);
                this.clave = rs.getString(4);
            }
            cerrarConexion();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Método privado para decodificar la URL codificada en Base64.
     *
     * @return La URL decodificada.
     */
    private URL decodeURL() {
        try {
            byte[] bytesDecodificados = Base64.getDecoder().decode(this.url);
            URL urlDecoded = new URL(new String(bytesDecodificados));
            return urlDecoded;
        } catch (MalformedURLException ex) {
            Logger.getLogger(ApiConexion.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Método para decodificar la credencial codificada en Base64.
     *
     * @return La credencial decodificada.
     */
    public String decodeCredencial() {
        byte[] bytesDecodificados = Base64.getDecoder().decode(this.credencial);
        return new String(bytesDecodificados);
    }

    /**
     * Método para decodificar el usuario autorizado codificado en Base64.
     *
     * @return El usuario autorizado decodificado.
     */
    public String decodeUsuario() {
        byte[] bytesDecodificados = Base64.getDecoder().decode(this.usuarioAutorizado);
        return new String(bytesDecodificados);
    }

    /**
     * Método para decodificar la clave codificada en Base64.
     *
     * @return La clave decodificada.
     */
    public String decodeClave() {
        byte[] bytesDecodificados = Base64.getDecoder().decode(this.clave);
        return new String(bytesDecodificados);
    }

    /**
     * Método para enviar una solicitud HTTP a la API.
     *
     * @param metodo       El tipo de método HTTP (siempre debe ser POST).
     * @param bodySOAPXML  El cuerpo de la solicitud en formato SOAP XML.
     * @return Un arreglo de objetos que contiene el código de respuesta HTTP y el mensaje de respuesta.
     */
    public Object[] enviarRequest(String metodo, String bodySOAPXML) {
        try {
            if (metodo.equals("POST")) {
                connection = (HttpURLConnection) decodeURL().openConnection();
                connection.setRequestMethod(metodo);
                connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
                connection.setRequestProperty("User-Agent", "Java Client");
                connection.setDoOutput(true);

                outputStream = connection.getOutputStream();
                outputStream.write(bodySOAPXML.getBytes());
                outputStream.close();

                this.codigoResponse = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                Object[] response = new Object[2];
                response[0] = this.codigoResponse;
                response[1] = responseMessage;

                return response;
            } else {
                System.err.println("se esperó un tipo de metodo 'POST'. Obtubo: " + metodo);
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(ApiConexion.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Método para obtener la respuesta de la API en formato XML.
     *
     * @return El documento XML de la respuesta.
     */
    public Document obtenerResponseXML() {
        try {
            bufferedReaderIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = bufferedReaderIn.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReaderIn.close();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(response.toString()));
            Document doc = dBuilder.parse(is);
            return doc;
        } catch (IOException ex) {
            Logger.getLogger(ApiConexion.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ApiConexion.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (SAXException ex) {
            Logger.getLogger(ApiConexion.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                bufferedReaderIn.close();
            } catch (IOException ex) {
                Logger.getLogger(ApiConexion.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }

    /**
     * Manejador para los códigos de mensajes de respuesta específicos de la API RENIEC.
     *
     * @param codigoRespuestaReniec El código de respuesta de la API RENIEC.
     */
    public static void handlerCodigosMensajesRespuestaReniec(String codigoRespuestaReniec) {
        switch (codigoRespuestaReniec) {
            case "0000":
                //JOptionPane.showMessageDialog(null, "Sin ningún error", "Codigo: " + codigoRespuestaReniec, JOptionPane.I);
                break;
            case "5002":
                JOptionPane.showMessageDialog(null, "Versión inválida", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5003":
                JOptionPane.showMessageDialog(null, "Longitud de cabecera inválida", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5004":
                JOptionPane.showMessageDialog(null, "Caracteres de verificación incorrectos", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5008":
                JOptionPane.showMessageDialog(null, "Servidor no válido", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5009":
                JOptionPane.showMessageDialog(null, "Tipo de consulta inválido", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5010":
                JOptionPane.showMessageDialog(null, "Tipo de consulta no permitida", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5011":
                JOptionPane.showMessageDialog(null, "No se ha ingresado subtipo de consulta", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5020":
                JOptionPane.showMessageDialog(null, "No existe la empresa ingresada para usar el servicio", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5021":
                JOptionPane.showMessageDialog(null, "La empresa registrada no está habilitada para usar el servicio", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5030":
                JOptionPane.showMessageDialog(null, "El usuario final de consulta ingresado no es válido", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5031":
                JOptionPane.showMessageDialog(null, "No se tiene información solicitada del usuario ingresado", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5032":
                JOptionPane.showMessageDialog(null, "El DNI no puede realizar consultas por encontrarse cancelado en el RUIPN", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5033":
                JOptionPane.showMessageDialog(null, "El DNI no puede realizar consultas por encontrarse restringido en el RUIPN", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5034":
                JOptionPane.showMessageDialog(null, "El DNI no puede realizar consultas por encontrarse observado en el RUIPN", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5036":
                JOptionPane.showMessageDialog(null, "El DNI se encuentra con baja temporal en el servicio", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5037":
                JOptionPane.showMessageDialog(null, "El DNI se encuentra con baja definitiva en el servicio", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5100":
                JOptionPane.showMessageDialog(null, "Longitud de trama de consulta inválida", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5101":
                JOptionPane.showMessageDialog(null, "Error en número de coincidencias solicitadas o inicio de grupo", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5102":
                JOptionPane.showMessageDialog(null, "Coincidencias superan el límite establecido", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5103":
                JOptionPane.showMessageDialog(null, "Error en base de datos", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5104":
                JOptionPane.showMessageDialog(null, "No se encontró información de la estructura solicitada", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5105":
                JOptionPane.showMessageDialog(null, "No se encontró los campos a mostrar para la estructura solicitada", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5108":
                JOptionPane.showMessageDialog(null, "Carácter ingresado en apellido paterno es inválido", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5109":
                JOptionPane.showMessageDialog(null, "Carácter ingresado en apellido materno es inválido", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5110":
                JOptionPane.showMessageDialog(null, "Carácter ingresado en nombres es inválido", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5111":
                JOptionPane.showMessageDialog(null, "El DNI solicitado se encuentra cancelado en el archivo magnético del \n" +
                    "RUIPN", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5112":
                JOptionPane.showMessageDialog(null, "El DNI solicitado se encuentra restringido en el archivo magnético del \n" +
                    "RUIPN", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5113":
                JOptionPane.showMessageDialog(null, "El DNI solicitado se encuentra observado en el archivo magnético del \n" +
                    "RUIPN", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5114":
                JOptionPane.showMessageDialog(null, "El DNI ingresado no es válido ó el DNI ingresado es de un menor de edad.", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "5200":
                JOptionPane.showMessageDialog(null, "No existen los datos solicitados", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
                
            // DANGER ZONE............
            case "8000":
                JOptionPane.showMessageDialog(null, "Problemas de comunicacion de MQ entre el MINSA y RENIEC, notificar al \n" +
                    "administrador de MQ de MINSA", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "8100":
                JOptionPane.showMessageDialog(null, "Problemas con el MQ de MINSA, notificar al administrador de MQ", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "9991":
                JOptionPane.showMessageDialog(null, "Error en los datos de autenticacion, revisar los datos enviados", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "9992":
                JOptionPane.showMessageDialog(null, "Hay restricciones en el horario de envio, consultar con el administrador del \n" +
                    "WebService", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "9993":
                JOptionPane.showMessageDialog(null, "Se alcanzo la cantidad maxima de invocaciones por dia, consultar con el \n" +
                    "administrador del WebService", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "9994":
                JOptionPane.showMessageDialog(null, "Error en el DNI del usuario autorizador, consultar con el administrador del \n" +
                    "WebService", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            case "9999":
                JOptionPane.showMessageDialog(null, "Error interno, notificar al administrador de servidores del MINSA", "Codigo: "+codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Codigo de mensaje no registrado", "Codigo: " + codigoRespuestaReniec, JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    /**
     * Metodo que cierra la conexion con la API.
     */
    public void cerrarApiConexion() {
        connection.disconnect();
    }
}
