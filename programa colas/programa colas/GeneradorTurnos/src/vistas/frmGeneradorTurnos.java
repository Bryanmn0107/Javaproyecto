/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

import api_reniec_conexion.ApiConexion;
import cliente_sources.Cliente;
import cliente_sources.VentanaCliente;
import static conexion.ConexionSQL.cerrarConexion;
import static conexion.ConexionSQL.establecerConexion;
import static conexion.ConexionSQL.llamarConexion;
import static conexion.ConexionSQL.rs;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import modelos.Turno;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Clase que contiene toda la interfaz del generador de turnos.
 *
 * @author jnxd_
 */
public class frmGeneradorTurnos extends VentanaCliente {

    /**
     * Variable que referencia al turno actual que se está registrando.
     */
    public static Turno turnoActual;

    int ValorConsulta;
    DefaultTableModel modeloTabla;
    boolean botonPresionado = false;
    private Object[] data;
    private Object[] pacienteEncontradoGalenhos;

    private Cliente cliente;
    private int tiempoPromedioAtencion;
    private String IpPantallaVisualizacion;
    private int puerto;

    private ApiConexion apiConexion;

    private final String IDENTIFICADOR = "GeneradorTurnos";
    private final String IDENTIFICADOR_PANTALLA = "Visualizador";

    /**
     * Constructor de la clase.
     */
    public frmGeneradorTurnos() {
        initComponents();
        mostrarPanelTipoPersona();
        turnoActual = new Turno();
        modeloTabla = (DefaultTableModel) tbPacientes.getModel();
        añadirAreas();
        setIconImage(new ImageIcon(getClass().getResource("/icons/imagen64.png")).getImage());
        JTableHeader headerTicketsGenerados = tbPacientes.getTableHeader();
        headerTicketsGenerados.setReorderingAllowed(false);
        headerTicketsGenerados.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Arial", 1, 24));
                setBackground(Color.decode("#009A96"));
                setForeground(Color.decode("#ffffff"));
                return this;
            }
        });

        DefaultTableCellRenderer cellRendererTicketsGenerados = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Arial", 1, 18));
                setBackground(Color.decode("#003333"));
                setForeground(Color.decode("#ffffff"));
//                setBorder(BorderFactory.createCompoundBorder(getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                return this;
            }
        };
        tbPacientes.setDefaultRenderer(Object.class, cellRendererTicketsGenerados);

        txtNroDocumentoIdentidad.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                adjustFontSize();
                //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                adjustFontSize();
                //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                adjustFontSize();
                //To change body of generated methods, choose Tools | Templates.
            }

            private void adjustFontSize() {
                String text = txtNroDocumentoIdentidad.getText();
                Font font = txtNroDocumentoIdentidad.getFont();
                int textWidth = txtNroDocumentoIdentidad.getFontMetrics(font).stringWidth(text);
                int fieldWidth = txtNroDocumentoIdentidad.getWidth();

                if (textWidth > fieldWidth) {
                    txtNroDocumentoIdentidad.setFont(new Font("Arial", Font.PLAIN, font.getSize() - 3));
                } else if (textWidth <= fieldWidth && font.getSize() < 42) {
                    txtNroDocumentoIdentidad.setFont(new Font("Arial", Font.PLAIN, font.getSize() + 3));
                }
            }
        });
    }

    /**
     * Metodo para mostrar el panel de seleccion publico preferencial o publico
     * general.
     */
    private void mostrarPanelTipoPersona() {
//        lblNombreSuccess.setText("");
        pnlContenedorTipoPersona.setVisible(true);
        pnlContenedorEscogerArea.setVisible(false);
        pnlContenedorIngresarDni.setVisible(false);
        pnlContenedorMensajeInconvenientes.setVisible(false);
        pnlContenedorMensajePregunta.setVisible(false);
        pnlContenedorMensajeSuccess.setVisible(false);
        pnlContenedorMensajeValidarUsuario.setVisible(false);
        pnlContenedorCargando.setVisible(false);
        pnlContenedorIngresarNombre.setVisible(false);
        if (isConexion) {
            cliente.confirmarDesconexion();
        }
    }

    /**
     * Metodo para mostrar el panel de seleccion de área.
     */
    private void mostrarPanelEscogerArea() {
        turnoActual.setIdArea(0);
        pnlContenedorEscogerArea.setVisible(true);
        pnlContenedorTipoPersona.setVisible(false);
        pnlContenedorIngresarDni.setVisible(false);
        pnlContenedorMensajeInconvenientes.setVisible(false);
        pnlContenedorMensajePregunta.setVisible(false);
        pnlContenedorMensajeSuccess.setVisible(false);
        pnlContenedorMensajeValidarUsuario.setVisible(false);
        pnlContenedorCargando.setVisible(false);
        pnlContenedorIngresarNombre.setVisible(false);
    }

    /**
     * Metodo para mostrar el panel de ingreso de DNI.
     */
    private void mostrarPanelIngresarDni() {
        turnoActual.setNroDni("");
        limpiartabla();
        txtNroDocumentoIdentidad.setText("");
        btnContinuarGenerador.setVisible(false);
        pnlContenedorIngresarDni.setVisible(true);
        pnlContenedorEscogerArea.setVisible(false);
        pnlContenedorTipoPersona.setVisible(false);
        pnlContenedorMensajeInconvenientes.setVisible(false);
        pnlContenedorMensajePregunta.setVisible(false);
        pnlContenedorMensajeSuccess.setVisible(false);
        pnlContenedorMensajeValidarUsuario.setVisible(false);
        pnlContenedorCargando.setVisible(false);
        pnlContenedorIngresarNombre.setVisible(false);
        obtenerConfiguracion(turnoActual.getIdArea());
        if (!isConexion) {
            cliente = new Cliente(this, this.IpPantallaVisualizacion, this.puerto, IDENTIFICADOR);
        }
    }

    /**
     * Metodo para mostrar el panel de mensaje de inconvenientes.
     */
    private void mostrarPanelMensajeInconvenientes() {
        botonPresionado = false;
        pnlContenedorMensajeInconvenientes.setVisible(true);
        pnlContenedorIngresarDni.setVisible(false);
        pnlContenedorEscogerArea.setVisible(false);
        pnlContenedorTipoPersona.setVisible(false);
        pnlContenedorMensajePregunta.setVisible(false);
        pnlContenedorMensajeSuccess.setVisible(false);
        pnlContenedorMensajeValidarUsuario.setVisible(false);
        pnlContenedorCargando.setVisible(false);
        pnlContenedorIngresarNombre.setVisible(false);
        iniciarTimer();
    }

    /**
     * Metodo para mostrar el panel de pregunta de continuidad.
     */
    private void mostrarPanelMensajePregunta() {
        pnlContenedorMensajePregunta.setVisible(true);
        pnlContenedorMensajeInconvenientes.setVisible(false);
        pnlContenedorIngresarDni.setVisible(false);
        pnlContenedorEscogerArea.setVisible(false);
        pnlContenedorTipoPersona.setVisible(false);
        pnlContenedorMensajeSuccess.setVisible(false);
        pnlContenedorMensajeValidarUsuario.setVisible(false);
        pnlContenedorCargando.setVisible(false);
        pnlContenedorIngresarNombre.setVisible(false);
    }

    /**
     * Metodo para mostrar el panel de cargando.
     */
    private void mostrarPanelCargando() {
        pnlContenedorCargando.setVisible(true);
        pnlContenedorMensajePregunta.setVisible(false);
        pnlContenedorMensajeInconvenientes.setVisible(false);
        pnlContenedorIngresarDni.setVisible(false);
        pnlContenedorEscogerArea.setVisible(false);
        pnlContenedorTipoPersona.setVisible(false);
        pnlContenedorMensajeSuccess.setVisible(false);
        pnlContenedorMensajeValidarUsuario.setVisible(false);
        pnlContenedorIngresarNombre.setVisible(false);
    }

    /**
     * Metodo para mostrar el panel de registro en cola exitoso.
     */
    private void mostrarPanelMensajeSuccess() {
        botonPresionado = false;
        pnlContenedorMensajeSuccess.setVisible(true);
        pnlContenedorMensajePregunta.setVisible(false);
        pnlContenedorMensajeInconvenientes.setVisible(false);
        pnlContenedorIngresarDni.setVisible(false);
        pnlContenedorEscogerArea.setVisible(false);
        pnlContenedorTipoPersona.setVisible(false);
        pnlContenedorMensajeValidarUsuario.setVisible(false);
        pnlContenedorCargando.setVisible(false);
        pnlContenedorIngresarNombre.setVisible(false);
        iniciarTimer();
    }

    /**
     * Metodo para mostrar el panel de validacion de usuario.
     */
    private void mostrarPanelMensajeValidarUsuario() {
        turnoActual.setNombrePaciente("");
//        botonPresionado = false;
        pnlContenedorMensajeValidarUsuario.setVisible(true);
        pnlContenedorMensajeSuccess.setVisible(false);
        pnlContenedorMensajePregunta.setVisible(false);
        pnlContenedorMensajeInconvenientes.setVisible(false);
        pnlContenedorIngresarDni.setVisible(false);
        pnlContenedorEscogerArea.setVisible(false);
        pnlContenedorTipoPersona.setVisible(false);
        pnlContenedorCargando.setVisible(false);
        pnlContenedorIngresarNombre.setVisible(false);
        //iniciarTimer();
    }

    /**
     * Metodo para mostrar el panel de ingreso de nombre completo.
     */
    private void mostrarPanelIngresarNombreCompleto() {
        turnoActual.setNombrePaciente("");
        txtNombreCompleto.setText("");
        pnlContenedorIngresarNombre.setVisible(true);
        pnlContenedorMensajeValidarUsuario.setVisible(false);
        pnlContenedorMensajeSuccess.setVisible(false);
        pnlContenedorMensajePregunta.setVisible(false);
        pnlContenedorMensajeInconvenientes.setVisible(false);
        pnlContenedorIngresarDni.setVisible(false);
        pnlContenedorEscogerArea.setVisible(false);
        pnlContenedorTipoPersona.setVisible(false);
        pnlContenedorCargando.setVisible(false);
    }

    /**
     * Metodo que agrega areas como botones.
     */
    private void añadirAreas() {
        try {
            establecerConexion();
            CallableStatement cst;
            String filtro = " WHERE Estado = 1";
            cst = llamarConexion.prepareCall("{call [MostrarAreaVentanilla](?)}");
            cst.setString(1, filtro);
            rs = cst.executeQuery();
            while (rs.next()) {
                agregarBotones(rs.getInt(1), rs.getString(2), 42);
            }
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
            cerrarConexion();
        }
    }

    /**
     * Metodo para obtener la ip y el puerto de servidor de sockets.
     *
     * @param idArea
     */
    private void obtenerConfiguracion(int idArea) {
        try {
            establecerConexion();
            CallableStatement cst;
            String filtro = " WHERE A.IdArea = " + idArea;
            cst = llamarConexion.prepareCall("{call [MostrarConfiguracionesAreasVentanillas](?)}");
            cst.setString(1, filtro);

            rs = cst.executeQuery();
            if (rs.next()) {
                tiempoPromedioAtencion = rs.getInt(5);

                if (rs.getString(6) == null || rs.getString(6).isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El valor de IP no está configurado", "Alerta", JOptionPane.ERROR_MESSAGE);
                } else {
                    IpPantallaVisualizacion = rs.getString(6);

                }

                if (rs.getString(7) == null || rs.getString(7).isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El valor del puerto no está configurado", "Alerta", JOptionPane.ERROR_MESSAGE);
                } else {
                    puerto = Integer.parseInt(rs.getString(7));
                }
            }
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que limpia la tabla del panel de ingreso de DNI.
     */
    public void limpiartabla() {
        Object[] fila = new Object[3];
        if (modeloTabla.getRowCount() < 100) {
            for (int i = modeloTabla.getRowCount(); i < 100 - modeloTabla.getRowCount(); i++) {
                fila[0] = "";
                fila[1] = "";
                fila[2] = "";
                modeloTabla.addRow(fila);
            }
        }
        tbPacientes.setModel(modeloTabla);
        tbPacientes.setGridColor(Color.decode("#003333"));
    }

    /**
     * Metodo que limpia la tabla del panel de ingreso de DNI con un parametro
     * fila.
     *
     * @param fila
     */
    public void limpiartabla(Object[] fila) {
        if (modeloTabla.getRowCount() < 100) {
            for (int i = modeloTabla.getRowCount(); i < 100 - modeloTabla.getRowCount(); i++) {
                fila[0] = "";
                fila[1] = "";
                fila[2] = "";
                modeloTabla.addRow(fila);
            }
        }
        tbPacientes.setModel(modeloTabla);
        tbPacientes.setGridColor(Color.decode("#003333"));
    }

    /**
     * Metodo que ayuda no generar un turno duplicado.
     *
     * @return
     */
    public Integer consultarPaciente() {
        try {
            establecerConexion();
            CallableStatement cst;
              int IdPaciente;
            if (turnoActual.getIdPaciente() == null) {
                IdPaciente = 0;
            } else {
                IdPaciente = turnoActual.getIdPaciente();
            }

            int areaId = turnoActual.getIdArea();

            String nroDocumento = turnoActual.getNroDni();
            int resultadoP = 0;

            cst = llamarConexion.prepareCall("{call PersonaTickects(?,?,?)}");
            cst.setInt(1, areaId);
            cst.setInt(2, IdPaciente);
            cst.setString(3, nroDocumento);
            rs = cst.executeQuery();
            // Verifica si hay al menos un resultado
            if (rs.next()) {
                // Mueve el cursor al primer resultado (si existe)
                resultadoP = rs.getInt(1);
//                System.out.println("El resultado del procedimiento almacenado es: " + resultado);
            }
            cerrarConexion();
            return resultadoP;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
            cerrarConexion();
            return null;
        }
    }

    /**
     * Metodo que agrega un ticket en la base de datos.
     *
     * @param nombreCompleto
     */
    public void agregarTikcts(String nombreCompleto) {

//        String NroDocumento = txtNroDocumentoIdentidad.getText();
//        turnoActual.setNroDni(NroDocumento);
//        System.out.println("el id area es : " + turnoActual.getIdArea());
//        System.out.println("Idpaciente" + turnoActual.getIdPaciente());
//        System.out.println("el DNI es : " + turnoActual.getNroDni());
//        System.out.println("el nombre del paciente es: " + turnoActual.getNombrePaciente());
//        EnvioDAtos();
        int resultadoP = consultarPaciente();
//        System.out.println("Es paciente esta Regristrado: " + resultadoP);
        if (resultadoP == 0) {
            int IdPaciente;
            int resultado = 0;
            //String textoObtenido = String.valueOf(turnoActual.getIdPaciente());
            Integer textoObtenido = turnoActual.getIdPaciente();
            if (textoObtenido != null) { // Verifica si el texto no está vacío
                IdPaciente = textoObtenido;
            } else {
                IdPaciente = 0;
            }
            String Nombrepaciente = turnoActual.getNombrePaciente();
            if (Nombrepaciente == null) {
//                System.out.println("no hay nombre");
                Nombrepaciente = " ";
            }
            try {
                establecerConexion();
                CallableStatement cst;

                int areaId = turnoActual.getIdArea();
//                System.out.println("El área id es: " + areaId);
                cst = llamarConexion.prepareCall("{call consultarVentanillas(?)}");
                cst.setInt(1, areaId);
                rs = cst.executeQuery();
                // Verifica si hay al menos un resultado
                if (rs.next()) {
                    // Mueve el cursor al primer resultado (si existe)
                    resultado = rs.getInt(1);
//                System.out.println("El resultado del procedimiento almacenado es: " + resultado);
                }
                cerrarConexion();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
                cerrarConexion();

            }
            if (resultado == 0) {
                mostrarPanelMensajePregunta();
            } else {
                try {
                    establecerConexion();
                    CallableStatement cst;
//                        obtenerConfiguracion(idAreaSeleccionada);
                    cst = llamarConexion.prepareCall("{call [AgregarTickets_Test](?,?,?,?,?,?,?)}");
                    cst.setInt(1, turnoActual.getIdArea());
                    cst.setInt(2, turnoActual.isPreferencial());
//                        cst.setInt(3, this.idEmpleado);
                    cst.setInt(3, 0);
                    cst.setInt(4, IdPaciente);
                    cst.setString(5, Nombrepaciente);
                    cst.setString(6, turnoActual.getNroDni());
                    cst.registerOutParameter(7, Types.INTEGER);
                    cst.executeUpdate();
                    int IdTurno = cst.getInt(7);
                    turnoActual.setIdTurno(IdTurno);
                    //pasar a la ventana frmMensaje
                    if (turnoActual.getNombrePaciente().equals("")) {
                        lblNombreSuccess.setText(turnoActual.getNroDni());
                    } else {
                        String nombreMostrar = turnoActual.getNombrePaciente().replace("-", " ");
                        lblNombreSuccess.setText(nombreMostrar);
                    }
                    if (isConexion) {
                        cliente.enviarMensaje(IDENTIFICADOR_PANTALLA, turnoActual.getNombrePaciente(), turnoActual.getNroDni(), String.valueOf(turnoActual.isPreferencial()), String.valueOf(turnoActual.getIdTurno()));
                    }
                    System.out.println("Mostrando panel success");
                    mostrarPanelMensajeSuccess();
                    cerrarConexion();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
                    cerrarConexion();
                }
            }
        } else {
            if (nombreCompleto.equals("")) {
                System.out.println("no tiene nombre");
                lblNombreSuccess.setText(turnoActual.getNroDni());
            } else {
                System.out.println("si tiene nombre");
                String nombreMostrar = nombreCompleto.replace("-", " ");
                lblNombreSuccess.setText(nombreMostrar);
            }
            if (turnoActual.getNombrePaciente() == null) {
                System.out.println("no tiene nombre");
                lblNombreSuccess.setText(turnoActual.getNroDni());
            } else {
                System.out.println("si tiene nombre");
                lblNombreSuccess.setText(turnoActual.getNombrePaciente().replace("-", " "));
            }
            mostrarPanelMensajeSuccess();
            JOptionPane.showMessageDialog(null, "La Persona ya tiene Ticket", "Alerta", JOptionPane.WARNING_MESSAGE);
            btnContinuarGenerador.setVisible(false);
            turnoActual.setIdPaciente(0);
        }
        txtNroDocumentoIdentidad.setText("");
    }

    /**
     * Metodo que consulta a un paciente en la base de datos por los digitos
     * ingresados.
     *
     * @param ValorConsulta
     * @param NombreConsulta
     * @return
     */
    private Object[] consultar(int ValorConsulta, String NombreConsulta) {
        try {
            Object[] pacEncontrado = new Object[5];
            establecerConexion();
            modeloTabla.setRowCount(0);
            CallableStatement cst;
            Object[] fila = new Object[3];
            String DNI = txtNroDocumentoIdentidad.getText();
            cst = llamarConexion.prepareCall("{call [consultarDatosPacientes_colas](?,?,?)}");
            cst.setString(1, DNI);
            cst.setString(2, NombreConsulta);
            cst.setInt(3, ValorConsulta);
            rs = cst.executeQuery();
            if (ValorConsulta == 1) {
                if (rs.next()) {
                    // Imprimir cada fila en la consola
                    String Nombre = rs.getString(1);
                    String Apellido = rs.getString(2);
                    String LetraApellido = rs.getString(3);
                    String Dni = rs.getString(4);
                    turnoActual.setNroDni(Dni);
                    String nombreCompleto = Nombre + "-" + Apellido + "-." + LetraApellido + ".";
//                    txtNombre.setText(Resultado);
                    txtNroDocumentoIdentidad.setText(Dni);
                    limpiartabla();
//                    jPanel4.setVisible(false);
                    pacEncontrado[0] = "true";
                    pacEncontrado[1] = nombreCompleto;
                    pacEncontrado[2] = Dni;
                    //seEncontro = true;
                } else {
                    pacEncontrado[0] = "false";
                    //seEncontro = false;
                }
            } else {
                while (rs.next()) {
                    // Imprimir cada fila en la consola
                    fila[0] = rs.getString(1);
                    fila[1] = rs.getString(2);
                    fila[2] = rs.getString(3);
                    modeloTabla.addRow(fila);
                }
                limpiartabla(fila);

                boolean todasLasFilasSonCero = true;

                // Iterar sobre el modelo de la tabla y verificar si todas las filas tienen el valor "0" en la primera columna
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    if (!modeloTabla.getValueAt(i, 0).equals("0")) {
                        todasLasFilasSonCero = false;
                        break;
                    }
                }

                // Mostrar un mensaje si todas las filas tienen el valor "0" en la primera columna
                //if (todasLasFilasSonCero) {
                if (txtNroDocumentoIdentidad.getText().length() >= 8) {
                    btnContinuarGenerador.setVisible(true);
                } else {
                    btnContinuarGenerador.setVisible(false);
                }
            }
            cerrarConexion();
            return pacEncontrado;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta en consultar", JOptionPane.ERROR_MESSAGE);
            cerrarConexion();
            return null;
        }
    }

    /**
     * Metodo que inicia un timer para cambiar de formulario despues de 5
     * segundos.
     */
    private void iniciarTimer() {
        // Iniciar temporizador para cambiar de formulario después de 5 segundos
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Acción a realizar después de 5 segundos si el botón no ha sido presionado
                if (!botonPresionado) {
                    mostrarPanelTipoPersona();
                    turnoActual.vaciarDatos();
                }
            }
        });
        timer.setRepeats(false); // Solo se ejecutará una vez
        timer.start(); // Iniciar temporizador
    }

    /**
     * Metodo que permite contar cuantas peticiones a la API de RENIEC se han
     * realizado, esto se ve directamente en base de datos.
     */
    private void contarCantidadPeticiones() {
        try {
            establecerConexion();
            CallableStatement cst;
            cst = llamarConexion.prepareCall("{call [actualizarCantidadConsultasReniec]()}");
            cst.executeUpdate();
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta en consultar", JOptionPane.ERROR_MESSAGE);
            cerrarConexion();
        }
    }

    /**
     * Metodo que busca un paciente en la API de RENIEC con el numero de DNI.
     * 
     * @param nroDni
     * @return 
     */
    private Object[] buscarPacienteReniecNroDNI(String nroDni) {
        apiConexion = new ApiConexion();

        String xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n"
                + "  <soap12:Header>\n"
                + "    <Credencialmq xmlns=\"http://tempuri.org/\">\n"
                + "      <app>" + apiConexion.decodeCredencial() + "</app>\n"
                + "      <usuario>" + apiConexion.decodeUsuario() + "</usuario>\n"
                + "      <clave>" + apiConexion.decodeClave() + "</clave>\n"
                + "    </Credencialmq>\n"
                + "  </soap12:Header>\n"
                + "  <soap12:Body>\n"
                + "    <obtenerDatosCompletos xmlns=\"http://tempuri.org/\">\n"
                + "      <nrodoc>" + nroDni + "</nrodoc>\n"
                + "    </obtenerDatosCompletos>\n"
                + "  </soap12:Body>\n"
                + "</soap12:Envelope>";

        Object[] respuesta = apiConexion.enviarRequest("POST", xmlContent);
        int codigoRespuesta = Integer.parseInt(respuesta[0].toString());
        contarCantidadPeticiones();
        System.out.println("Codigo: " + respuesta[0]);
        System.out.println("Mensaje: " + respuesta[1]);
        if (codigoRespuesta == 200) {
            Document resXML = apiConexion.obtenerResponseXML();
            Object[] pacienteEncontrado = leerData(resXML);
            apiConexion.cerrarApiConexion();
            return pacienteEncontrado;
        } else {
            return null;
        }
    }

    /**
     * Metodo que guarda al paciente encontrado en API RENIEC en la base de datos de GALENHOS.
     * 
     * @param paciente 
     */
    private void insertarPacienteEncontradoReniencToGalenhos(Object[] paciente) {
        try {
            establecerConexion();
            CallableStatement cst;
            cst = llamarConexion.prepareCall("{call [InsertarPacientesReniec_colas](?,?,?,?,?,?,?,?,?,?)}");
            cst.setInt(1, 0);
            cst.setString(2, paciente[2].toString());
            cst.setString(3, paciente[3].toString());
            cst.setString(4, paciente[4].toString());
            cst.setInt(5, Integer.parseInt(paciente[5].toString()));
            cst.setInt(6, Integer.parseInt(paciente[6].toString()));
            cst.setString(7, paciente[7].toString());
            cst.setString(8, paciente[8].toString());
            cst.setString(9, paciente[9].toString());
            cst.setString(10, paciente[11].toString());

            cst.executeUpdate();

            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta en consultar", JOptionPane.ERROR_MESSAGE);
            cerrarConexion();
        }
    }

    /**
     * Metodo que permite leer un documento SOAP XML de respuesta.
     * 
     * @param doc
     * @return Retorna la data necesaria de la respuesta de la API.
     */
    public Object[] leerData(Document doc) {
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("obtenerDatosCompletosResult");
        Object[] data = new Object[13];
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String codResReniec = eElement.getElementsByTagName("string").item(0).getTextContent();
                ApiConexion.handlerCodigosMensajesRespuestaReniec(codResReniec);
                if (codResReniec.equals("0000")) {
                    int idPaciente = 0;
                    String nroDni = eElement.getElementsByTagName("string").item(2).getTextContent();
                    String apellidoPat = eElement.getElementsByTagName("string").item(4).getTextContent();
                    String apellidoMat = eElement.getElementsByTagName("string").item(5).getTextContent();
                    String nombres = eElement.getElementsByTagName("string").item(7).getTextContent();
                    String sexo = eElement.getElementsByTagName("string").item(22).getTextContent();
                    String digitoVerificador = eElement.getElementsByTagName("string").item(3).getTextContent();
                    String fotoReniec = eElement.getElementsByTagName("string").item(47).getTextContent();
                    String fechaNacimiento = eElement.getElementsByTagName("string").item(29).getTextContent();
                    String fechaEmision = eElement.getElementsByTagName("string").item(33).getTextContent();

                    data[0] = codResReniec;
                    data[1] = idPaciente;
                    data[2] = apellidoPat;
                    data[3] = apellidoMat;
                    data[4] = nombres;
                    data[5] = sexo;
                    data[6] = digitoVerificador;
                    data[7] = fotoReniec;
                    data[8] = nroDni;
                    data[9] = fechaNacimiento;
                    data[10] = nombres + " " + apellidoPat + " " + apellidoMat;
                    data[11] = fechaEmision;

                    insertarPacienteEncontradoReniencToGalenhos(data);
                }
                data[0] = codResReniec;
            }
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pnlContenedorTipoPersona = new javax.swing.JPanel();
        pnlPreferencial = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pnlGeneral = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        pnlContenedorEscogerArea = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lblReturnButton = new javax.swing.JLabel();
        pnlContenedorAreasBotones = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        pnlContenedorIngresarDni = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnNumero1 = new javax.swing.JButton();
        btnNumero2 = new javax.swing.JButton();
        btnNumero3 = new javax.swing.JButton();
        btnNumero4 = new javax.swing.JButton();
        btnNumero5 = new javax.swing.JButton();
        btnNumero6 = new javax.swing.JButton();
        btnNumero7 = new javax.swing.JButton();
        btnNumero8 = new javax.swing.JButton();
        btnNumero9 = new javax.swing.JButton();
        btnRegresarEscogerArea = new javax.swing.JButton();
        btnNumero0 = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        panelTabla = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbPacientes = new javax.swing.JTable();
        btnContinuarGenerador = new javax.swing.JButton();
        txtNroDocumentoIdentidad = new javax.swing.JTextField();
        pnlContenedorMensajeSuccess = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lblNombreSuccess = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        pnlContenedorMensajeInconvenientes = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        lblNombreInconvenientes = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        pnlContenedorMensajePregunta = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        btnSI = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        btnNO = new javax.swing.JButton();
        pnlContenedorMensajeValidarUsuario = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        lblNombreUsuarioEncontrado = new javax.swing.JLabel();
        btnSIValidarUsu = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        btnNOValidarUsu = new javax.swing.JButton();
        lblDniDigitado = new javax.swing.JLabel();
        lblDigiteMalDniLink = new javax.swing.JLabel();
        pnlContenedorCargando = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        pnlContenedorIngresarNombre = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        pnlQwertyTeclado = new javax.swing.JPanel();
        pnlFila1 = new javax.swing.JPanel();
        btnQ = new javax.swing.JButton();
        btnW = new javax.swing.JButton();
        btnE = new javax.swing.JButton();
        btnR = new javax.swing.JButton();
        btnT = new javax.swing.JButton();
        btnY = new javax.swing.JButton();
        btnU = new javax.swing.JButton();
        btnI = new javax.swing.JButton();
        btnO = new javax.swing.JButton();
        btnP = new javax.swing.JButton();
        pnlFila2 = new javax.swing.JPanel();
        btnA = new javax.swing.JButton();
        btnS = new javax.swing.JButton();
        btnD = new javax.swing.JButton();
        btnF = new javax.swing.JButton();
        btnG = new javax.swing.JButton();
        btnH = new javax.swing.JButton();
        btnJ = new javax.swing.JButton();
        btnK = new javax.swing.JButton();
        btnL = new javax.swing.JButton();
        btnÑ = new javax.swing.JButton();
        pnlFila3 = new javax.swing.JPanel();
        btnZ = new javax.swing.JButton();
        btnX = new javax.swing.JButton();
        btnC = new javax.swing.JButton();
        btnV = new javax.swing.JButton();
        btnB = new javax.swing.JButton();
        btnN = new javax.swing.JButton();
        btnM = new javax.swing.JButton();
        btnBackSpace = new javax.swing.JButton();
        pnlFila4 = new javax.swing.JPanel();
        btnSpace = new javax.swing.JButton();
        txtNombreCompleto = new javax.swing.JTextField();
        btnContinuar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        lblMensaje = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setUndecorated(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        pnlContenedorTipoPersona.setBackground(new java.awt.Color(255, 255, 255));
        pnlContenedorTipoPersona.setLayout(new java.awt.GridLayout(1, 2));

        pnlPreferencial.setBackground(new java.awt.Color(0, 102, 102));
        pnlPreferencial.setFocusable(false);
        pnlPreferencial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlPreferencialMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlPreferencialMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pnlPreferencialMouseReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 72)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("<html><div style=\"text-align: center;\">PUBLICO<br>PREFERENCIAL</div></html>");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/prefrencial.png"))); // NOI18N

        javax.swing.GroupLayout pnlPreferencialLayout = new javax.swing.GroupLayout(pnlPreferencial);
        pnlPreferencial.setLayout(pnlPreferencialLayout);
        pnlPreferencialLayout.setHorizontalGroup(
            pnlPreferencialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPreferencialLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPreferencialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPreferencialLayout.setVerticalGroup(
            pnlPreferencialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPreferencialLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(0, 0, 0)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlContenedorTipoPersona.add(pnlPreferencial);

        pnlGeneral.setBackground(new java.awt.Color(0, 204, 204));
        pnlGeneral.setFocusable(false);
        pnlGeneral.setPreferredSize(new java.awt.Dimension(505, 196));
        pnlGeneral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlGeneralMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlGeneralMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pnlGeneralMouseReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 72)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("<html><div style=\"text-align: center;\">PUBLICO<br>GENERAL</div></html>");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/user-356.png"))); // NOI18N

        javax.swing.GroupLayout pnlGeneralLayout = new javax.swing.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlContenedorTipoPersona.add(pnlGeneral);

        pnlContenedorEscogerArea.setBackground(new java.awt.Color(255, 255, 255));
        pnlContenedorEscogerArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icono-areas-sin-fondo_1.png"))); // NOI18N

        lblReturnButton.setBackground(new java.awt.Color(255, 0, 0));
        lblReturnButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-return-64.png"))); // NOI18N
        lblReturnButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblReturnButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblReturnButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblReturnButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblReturnButtonMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblReturnButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblReturnButtonMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblReturnButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblReturnButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlContenedorAreasBotones.setBackground(new java.awt.Color(255, 255, 255));
        pnlContenedorAreasBotones.setLayout(new java.awt.GridLayout(5, 0, 0, 10));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        jLabel6.setText("Seleccione el área:");

        javax.swing.GroupLayout pnlContenedorEscogerAreaLayout = new javax.swing.GroupLayout(pnlContenedorEscogerArea);
        pnlContenedorEscogerArea.setLayout(pnlContenedorEscogerAreaLayout);
        pnlContenedorEscogerAreaLayout.setHorizontalGroup(
            pnlContenedorEscogerAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorEscogerAreaLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlContenedorEscogerAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlContenedorEscogerAreaLayout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(pnlContenedorAreasBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(100, 100, 100))
                    .addGroup(pnlContenedorEscogerAreaLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addContainerGap(217, Short.MAX_VALUE))))
        );
        pnlContenedorEscogerAreaLayout.setVerticalGroup(
            pnlContenedorEscogerAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlContenedorEscogerAreaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(75, 75, 75)
                .addComponent(pnlContenedorAreasBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(75, 75, 75))
        );

        pnlContenedorIngresarDni.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.GridLayout(4, 3));

        btnNumero1.setBackground(new java.awt.Color(255, 255, 255));
        btnNumero1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnNumero1.setForeground(new java.awt.Color(0, 51, 51));
        btnNumero1.setText("1");
        btnNumero1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnNumero1.setFocusable(false);
        btnNumero1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumero1ActionPerformed(evt);
            }
        });
        jPanel5.add(btnNumero1);

        btnNumero2.setBackground(new java.awt.Color(255, 255, 255));
        btnNumero2.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnNumero2.setForeground(new java.awt.Color(0, 51, 51));
        btnNumero2.setText("2");
        btnNumero2.setFocusable(false);
        btnNumero2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumero2ActionPerformed(evt);
            }
        });
        jPanel5.add(btnNumero2);

        btnNumero3.setBackground(new java.awt.Color(255, 255, 255));
        btnNumero3.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnNumero3.setForeground(new java.awt.Color(0, 51, 51));
        btnNumero3.setText("3");
        btnNumero3.setFocusable(false);
        btnNumero3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumero3ActionPerformed(evt);
            }
        });
        jPanel5.add(btnNumero3);

        btnNumero4.setBackground(new java.awt.Color(255, 255, 255));
        btnNumero4.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnNumero4.setForeground(new java.awt.Color(0, 51, 51));
        btnNumero4.setText("4");
        btnNumero4.setFocusable(false);
        btnNumero4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumero4ActionPerformed(evt);
            }
        });
        jPanel5.add(btnNumero4);

        btnNumero5.setBackground(new java.awt.Color(255, 255, 255));
        btnNumero5.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnNumero5.setForeground(new java.awt.Color(0, 51, 51));
        btnNumero5.setText("5");
        btnNumero5.setFocusable(false);
        btnNumero5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumero5ActionPerformed(evt);
            }
        });
        jPanel5.add(btnNumero5);

        btnNumero6.setBackground(new java.awt.Color(255, 255, 255));
        btnNumero6.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnNumero6.setForeground(new java.awt.Color(0, 51, 51));
        btnNumero6.setText("6");
        btnNumero6.setFocusable(false);
        btnNumero6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumero6ActionPerformed(evt);
            }
        });
        jPanel5.add(btnNumero6);

        btnNumero7.setBackground(new java.awt.Color(255, 255, 255));
        btnNumero7.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnNumero7.setForeground(new java.awt.Color(0, 51, 51));
        btnNumero7.setText("7");
        btnNumero7.setFocusable(false);
        btnNumero7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumero7ActionPerformed(evt);
            }
        });
        jPanel5.add(btnNumero7);

        btnNumero8.setBackground(new java.awt.Color(255, 255, 255));
        btnNumero8.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnNumero8.setForeground(new java.awt.Color(0, 51, 51));
        btnNumero8.setText("8");
        btnNumero8.setFocusable(false);
        btnNumero8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumero8ActionPerformed(evt);
            }
        });
        jPanel5.add(btnNumero8);

        btnNumero9.setBackground(new java.awt.Color(255, 255, 255));
        btnNumero9.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnNumero9.setForeground(new java.awt.Color(0, 51, 51));
        btnNumero9.setText("9");
        btnNumero9.setFocusable(false);
        btnNumero9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumero9ActionPerformed(evt);
            }
        });
        jPanel5.add(btnNumero9);

        btnRegresarEscogerArea.setBackground(new java.awt.Color(255, 255, 255));
        btnRegresarEscogerArea.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnRegresarEscogerArea.setForeground(new java.awt.Color(0, 51, 51));
        btnRegresarEscogerArea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-return-52.png"))); // NOI18N
        btnRegresarEscogerArea.setFocusable(false);
        btnRegresarEscogerArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegresarEscogerAreaActionPerformed(evt);
            }
        });
        jPanel5.add(btnRegresarEscogerArea);

        btnNumero0.setBackground(new java.awt.Color(255, 255, 255));
        btnNumero0.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnNumero0.setForeground(new java.awt.Color(0, 51, 51));
        btnNumero0.setText("0");
        btnNumero0.setFocusable(false);
        btnNumero0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumero0ActionPerformed(evt);
            }
        });
        jPanel5.add(btnNumero0);

        btnBorrar.setBackground(new java.awt.Color(255, 255, 255));
        btnBorrar.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnBorrar.setForeground(new java.awt.Color(0, 51, 51));
        btnBorrar.setText("<x|");
        btnBorrar.setFocusable(false);
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });
        btnBorrar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnBorrarKeyPressed(evt);
            }
        });
        jPanel5.add(btnBorrar);

        panelTabla.setBackground(new java.awt.Color(0, 51, 51));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        tbPacientes.setBackground(new java.awt.Color(0, 51, 51));
        tbPacientes.setForeground(new java.awt.Color(255, 255, 255));
        tbPacientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Id", "N° Doc", "Nombre Completo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbPacientes.setFocusable(false);
        tbPacientes.setGridColor(new java.awt.Color(0, 51, 51));
        tbPacientes.setInheritsPopupMenu(true);
        tbPacientes.setOpaque(false);
        tbPacientes.setRowHeight(60);
        tbPacientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPacientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbPacientes);
        if (tbPacientes.getColumnModel().getColumnCount() > 0) {
            tbPacientes.getColumnModel().getColumn(0).setMinWidth(0);
            tbPacientes.getColumnModel().getColumn(0).setPreferredWidth(0);
            tbPacientes.getColumnModel().getColumn(0).setMaxWidth(0);
            tbPacientes.getColumnModel().getColumn(1).setMinWidth(0);
            tbPacientes.getColumnModel().getColumn(1).setPreferredWidth(0);
            tbPacientes.getColumnModel().getColumn(1).setMaxWidth(0);
        }

        btnContinuarGenerador.setBackground(new java.awt.Color(75, 175, 78));
        btnContinuarGenerador.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnContinuarGenerador.setForeground(new java.awt.Color(255, 255, 255));
        btnContinuarGenerador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-arrow-right-64.png"))); // NOI18N
        btnContinuarGenerador.setText("CONTINUAR  ");
        btnContinuarGenerador.setFocusable(false);
        btnContinuarGenerador.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnContinuarGenerador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuarGeneradorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTablaLayout = new javax.swing.GroupLayout(panelTabla);
        panelTabla.setLayout(panelTablaLayout);
        panelTablaLayout.setHorizontalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                    .addComponent(btnContinuarGenerador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelTablaLayout.setVerticalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnContinuarGenerador, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        txtNroDocumentoIdentidad.setFont(new java.awt.Font("Arial Black", 0, 52)); // NOI18N
        txtNroDocumentoIdentidad.setForeground(new java.awt.Color(0, 51, 51));
        txtNroDocumentoIdentidad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtNroDocumentoIdentidad.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        txtNroDocumentoIdentidad.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNroDocumentoIdentidad.setFocusable(false);
        txtNroDocumentoIdentidad.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtNroDocumentoIdentidadInputMethodTextChanged(evt);
            }
        });
        txtNroDocumentoIdentidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNroDocumentoIdentidadActionPerformed(evt);
            }
        });
        txtNroDocumentoIdentidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNroDocumentoIdentidadKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNroDocumentoIdentidadKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout pnlContenedorIngresarDniLayout = new javax.swing.GroupLayout(pnlContenedorIngresarDni);
        pnlContenedorIngresarDni.setLayout(pnlContenedorIngresarDniLayout);
        pnlContenedorIngresarDniLayout.setHorizontalGroup(
            pnlContenedorIngresarDniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorIngresarDniLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedorIngresarDniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                    .addComponent(txtNroDocumentoIdentidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlContenedorIngresarDniLayout.setVerticalGroup(
            pnlContenedorIngresarDniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorIngresarDniLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtNroDocumentoIdentidad, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pnlContenedorMensajeSuccess.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 62)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/correcto 100 t.png"))); // NOI18N

        lblNombreSuccess.setFont(new java.awt.Font("Tahoma", 1, 58)); // NOI18N
        lblNombreSuccess.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNombreSuccess.setText("ESTEFANIA GUITIERREZ . Z");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("<html><style><center>Usted ya se encuentra </center></style></html>");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("<html><center>en la pantalla de espera </center></html>");

        jButton1.setBackground(new java.awt.Color(75, 175, 78));
        jButton1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("ACEPTAR");
        jButton1.setBorderPainted(false);
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlContenedorMensajeSuccessLayout = new javax.swing.GroupLayout(pnlContenedorMensajeSuccess);
        pnlContenedorMensajeSuccess.setLayout(pnlContenedorMensajeSuccessLayout);
        pnlContenedorMensajeSuccessLayout.setHorizontalGroup(
            pnlContenedorMensajeSuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorMensajeSuccessLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedorMensajeSuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreSuccess, javax.swing.GroupLayout.DEFAULT_SIZE, 990, Short.MAX_VALUE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContenedorMensajeSuccessLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlContenedorMensajeSuccessLayout.setVerticalGroup(
            pnlContenedorMensajeSuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContenedorMensajeSuccessLayout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(lblNombreSuccess)
                .addGap(50, 50, 50)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlContenedorMensajeInconvenientes.setBackground(new java.awt.Color(255, 255, 255));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 62)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/sad-100.png"))); // NOI18N

        lblNombreInconvenientes.setFont(new java.awt.Font("Tahoma", 1, 64)); // NOI18N
        lblNombreInconvenientes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNombreInconvenientes.setText("ESTEFANIA GUITIERREZ . Z");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 58)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("<html><style><center>Lamentamos los inconvenientes</center></style></html>");

        jButton2.setBackground(new java.awt.Color(75, 175, 78));
        jButton2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("ACEPTAR");
        jButton2.setBorderPainted(false);
        jButton2.setFocusable(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlContenedorMensajeInconvenientesLayout = new javax.swing.GroupLayout(pnlContenedorMensajeInconvenientes);
        pnlContenedorMensajeInconvenientes.setLayout(pnlContenedorMensajeInconvenientesLayout);
        pnlContenedorMensajeInconvenientesLayout.setHorizontalGroup(
            pnlContenedorMensajeInconvenientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorMensajeInconvenientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedorMensajeInconvenientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreInconvenientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 990, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContenedorMensajeInconvenientesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlContenedorMensajeInconvenientesLayout.setVerticalGroup(
            pnlContenedorMensajeInconvenientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContenedorMensajeInconvenientesLayout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(lblNombreInconvenientes)
                .addGap(70, 70, 70)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlContenedorMensajePregunta.setBackground(new java.awt.Color(255, 255, 255));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 62)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/interogacion-100.png"))); // NOI18N

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 64)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("<html><style><center>No Hay Ventanillas Abiertas</center></style></html>");

        btnSI.setBackground(new java.awt.Color(75, 175, 78));
        btnSI.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnSI.setForeground(new java.awt.Color(255, 255, 255));
        btnSI.setText("SI");
        btnSI.setBorderPainted(false);
        btnSI.setFocusable(false);
        btnSI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSIActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 64)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("<html><style><center>¿Quiere continuar?</center></style></html>");

        btnNO.setBackground(new java.awt.Color(255, 0, 0));
        btnNO.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnNO.setForeground(new java.awt.Color(255, 255, 255));
        btnNO.setText("NO");
        btnNO.setFocusable(false);
        btnNO.setMaximumSize(new java.awt.Dimension(193, 37));
        btnNO.setMinimumSize(new java.awt.Dimension(193, 37));
        btnNO.setPreferredSize(new java.awt.Dimension(193, 37));
        btnNO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNOActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlContenedorMensajePreguntaLayout = new javax.swing.GroupLayout(pnlContenedorMensajePregunta);
        pnlContenedorMensajePregunta.setLayout(pnlContenedorMensajePreguntaLayout);
        pnlContenedorMensajePreguntaLayout.setHorizontalGroup(
            pnlContenedorMensajePreguntaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorMensajePreguntaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedorMensajePreguntaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 990, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlContenedorMensajePreguntaLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSI, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(180, 180, 180)
                        .addComponent(btnNO, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlContenedorMensajePreguntaLayout.setVerticalGroup(
            pnlContenedorMensajePreguntaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContenedorMensajePreguntaLayout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addGroup(pnlContenedorMensajePreguntaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNO, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSI, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlContenedorMensajeValidarUsuario.setBackground(new java.awt.Color(255, 255, 255));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 62)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/interogacion-100.png"))); // NOI18N

        lblNombreUsuarioEncontrado.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        lblNombreUsuarioEncontrado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNombreUsuarioEncontrado.setText("JORGE EDUARDO NAVARRO NUÑEZ");

        btnSIValidarUsu.setBackground(new java.awt.Color(75, 175, 78));
        btnSIValidarUsu.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnSIValidarUsu.setForeground(new java.awt.Color(255, 255, 255));
        btnSIValidarUsu.setText("SI");
        btnSIValidarUsu.setBorderPainted(false);
        btnSIValidarUsu.setFocusable(false);
        btnSIValidarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSIValidarUsuActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 64)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("<html><style><center>¿Estos son sus datos?</center></style></html>");

        btnNOValidarUsu.setBackground(new java.awt.Color(255, 0, 0));
        btnNOValidarUsu.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnNOValidarUsu.setForeground(new java.awt.Color(255, 255, 255));
        btnNOValidarUsu.setText("NO");
        btnNOValidarUsu.setFocusable(false);
        btnNOValidarUsu.setMaximumSize(new java.awt.Dimension(193, 37));
        btnNOValidarUsu.setMinimumSize(new java.awt.Dimension(193, 37));
        btnNOValidarUsu.setPreferredSize(new java.awt.Dimension(193, 37));
        btnNOValidarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNOValidarUsuActionPerformed(evt);
            }
        });

        lblDniDigitado.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lblDniDigitado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDniDigitado.setText("DNI: 72839490");

        lblDigiteMalDniLink.setBackground(new java.awt.Color(255, 255, 255));
        lblDigiteMalDniLink.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblDigiteMalDniLink.setForeground(new java.awt.Color(0, 102, 102));
        lblDigiteMalDniLink.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDigiteMalDniLink.setText("<html><u>DIGITE MAL MI DNI</u></html>");
        lblDigiteMalDniLink.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblDigiteMalDniLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblDigiteMalDniLinkMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlContenedorMensajeValidarUsuarioLayout = new javax.swing.GroupLayout(pnlContenedorMensajeValidarUsuario);
        pnlContenedorMensajeValidarUsuario.setLayout(pnlContenedorMensajeValidarUsuarioLayout);
        pnlContenedorMensajeValidarUsuarioLayout.setHorizontalGroup(
            pnlContenedorMensajeValidarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorMensajeValidarUsuarioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedorMensajeValidarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNombreUsuarioEncontrado, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblDniDigitado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlContenedorMensajeValidarUsuarioLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                        .addGroup(pnlContenedorMensajeValidarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlContenedorMensajeValidarUsuarioLayout.createSequentialGroup()
                                .addComponent(btnSIValidarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnNOValidarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblDigiteMalDniLink))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlContenedorMensajeValidarUsuarioLayout.setVerticalGroup(
            pnlContenedorMensajeValidarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContenedorMensajeValidarUsuarioLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblDniDigitado)
                .addGap(18, 18, 18)
                .addComponent(lblNombreUsuarioEncontrado)
                .addGap(39, 39, 39)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addGroup(pnlContenedorMensajeValidarUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnNOValidarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSIValidarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45)
                .addComponent(lblDigiteMalDniLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlContenedorCargando.setBackground(new java.awt.Color(255, 255, 255));

        jLabel18.setBackground(new java.awt.Color(0, 0, 0));
        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 62)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/785 (1).gif"))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 89)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 102, 102));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Cargando...");

        javax.swing.GroupLayout pnlContenedorCargandoLayout = new javax.swing.GroupLayout(pnlContenedorCargando);
        pnlContenedorCargando.setLayout(pnlContenedorCargandoLayout);
        pnlContenedorCargandoLayout.setHorizontalGroup(
            pnlContenedorCargandoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorCargandoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedorCargandoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 990, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlContenedorCargandoLayout.setVerticalGroup(
            pnlContenedorCargandoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorCargandoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlContenedorIngresarNombre.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel12.setText("Ingrese su nombre completo:");

        pnlQwertyTeclado.setBackground(new java.awt.Color(255, 255, 255));
        pnlQwertyTeclado.setLayout(new java.awt.GridLayout(4, 1, 0, 5));

        pnlFila1.setBackground(new java.awt.Color(0, 51, 51));
        pnlFila1.setLayout(new java.awt.GridLayout(1, 11, 5, 0));

        btnQ.setBackground(new java.awt.Color(255, 255, 255));
        btnQ.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnQ.setForeground(new java.awt.Color(0, 51, 51));
        btnQ.setText("Q");
        btnQ.setFocusable(false);
        btnQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQActionPerformed(evt);
            }
        });
        pnlFila1.add(btnQ);

        btnW.setBackground(new java.awt.Color(255, 255, 255));
        btnW.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnW.setForeground(new java.awt.Color(0, 51, 51));
        btnW.setText("W");
        btnW.setFocusable(false);
        btnW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWActionPerformed(evt);
            }
        });
        pnlFila1.add(btnW);

        btnE.setBackground(new java.awt.Color(255, 255, 255));
        btnE.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnE.setForeground(new java.awt.Color(0, 51, 51));
        btnE.setText("E");
        btnE.setFocusable(false);
        btnE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEActionPerformed(evt);
            }
        });
        pnlFila1.add(btnE);

        btnR.setBackground(new java.awt.Color(255, 255, 255));
        btnR.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnR.setForeground(new java.awt.Color(0, 51, 51));
        btnR.setText("R");
        btnR.setFocusable(false);
        btnR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRActionPerformed(evt);
            }
        });
        pnlFila1.add(btnR);

        btnT.setBackground(new java.awt.Color(255, 255, 255));
        btnT.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnT.setForeground(new java.awt.Color(0, 51, 51));
        btnT.setText("T");
        btnT.setFocusable(false);
        btnT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTActionPerformed(evt);
            }
        });
        pnlFila1.add(btnT);

        btnY.setBackground(new java.awt.Color(255, 255, 255));
        btnY.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnY.setForeground(new java.awt.Color(0, 51, 51));
        btnY.setText("Y");
        btnY.setFocusable(false);
        btnY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnYActionPerformed(evt);
            }
        });
        pnlFila1.add(btnY);

        btnU.setBackground(new java.awt.Color(255, 255, 255));
        btnU.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnU.setForeground(new java.awt.Color(0, 51, 51));
        btnU.setText("U");
        btnU.setFocusable(false);
        btnU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUActionPerformed(evt);
            }
        });
        pnlFila1.add(btnU);

        btnI.setBackground(new java.awt.Color(255, 255, 255));
        btnI.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnI.setForeground(new java.awt.Color(0, 51, 51));
        btnI.setText("I");
        btnI.setFocusable(false);
        btnI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIActionPerformed(evt);
            }
        });
        pnlFila1.add(btnI);

        btnO.setBackground(new java.awt.Color(255, 255, 255));
        btnO.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnO.setForeground(new java.awt.Color(0, 51, 51));
        btnO.setText("O");
        btnO.setFocusable(false);
        btnO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOActionPerformed(evt);
            }
        });
        pnlFila1.add(btnO);

        btnP.setBackground(new java.awt.Color(255, 255, 255));
        btnP.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnP.setForeground(new java.awt.Color(0, 51, 51));
        btnP.setText("P");
        btnP.setFocusable(false);
        btnP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPActionPerformed(evt);
            }
        });
        pnlFila1.add(btnP);

        pnlQwertyTeclado.add(pnlFila1);

        pnlFila2.setBackground(new java.awt.Color(0, 51, 51));
        pnlFila2.setLayout(new java.awt.GridLayout(1, 10, 5, 0));

        btnA.setBackground(new java.awt.Color(255, 255, 255));
        btnA.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnA.setForeground(new java.awt.Color(0, 51, 51));
        btnA.setText("A");
        btnA.setFocusable(false);
        btnA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAActionPerformed(evt);
            }
        });
        pnlFila2.add(btnA);

        btnS.setBackground(new java.awt.Color(255, 255, 255));
        btnS.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnS.setForeground(new java.awt.Color(0, 51, 51));
        btnS.setText("S");
        btnS.setFocusable(false);
        btnS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSActionPerformed(evt);
            }
        });
        pnlFila2.add(btnS);

        btnD.setBackground(new java.awt.Color(255, 255, 255));
        btnD.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnD.setForeground(new java.awt.Color(0, 51, 51));
        btnD.setText("D");
        btnD.setFocusable(false);
        btnD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDActionPerformed(evt);
            }
        });
        pnlFila2.add(btnD);

        btnF.setBackground(new java.awt.Color(255, 255, 255));
        btnF.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnF.setForeground(new java.awt.Color(0, 51, 51));
        btnF.setText("F");
        btnF.setFocusable(false);
        btnF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFActionPerformed(evt);
            }
        });
        pnlFila2.add(btnF);

        btnG.setBackground(new java.awt.Color(255, 255, 255));
        btnG.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnG.setForeground(new java.awt.Color(0, 51, 51));
        btnG.setText("G");
        btnG.setFocusable(false);
        btnG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGActionPerformed(evt);
            }
        });
        pnlFila2.add(btnG);

        btnH.setBackground(new java.awt.Color(255, 255, 255));
        btnH.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnH.setForeground(new java.awt.Color(0, 51, 51));
        btnH.setText("H");
        btnH.setFocusable(false);
        btnH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHActionPerformed(evt);
            }
        });
        pnlFila2.add(btnH);

        btnJ.setBackground(new java.awt.Color(255, 255, 255));
        btnJ.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnJ.setForeground(new java.awt.Color(0, 51, 51));
        btnJ.setText("J");
        btnJ.setFocusable(false);
        btnJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJActionPerformed(evt);
            }
        });
        pnlFila2.add(btnJ);

        btnK.setBackground(new java.awt.Color(255, 255, 255));
        btnK.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnK.setForeground(new java.awt.Color(0, 51, 51));
        btnK.setText("K");
        btnK.setFocusable(false);
        btnK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKActionPerformed(evt);
            }
        });
        pnlFila2.add(btnK);

        btnL.setBackground(new java.awt.Color(255, 255, 255));
        btnL.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnL.setForeground(new java.awt.Color(0, 51, 51));
        btnL.setText("L");
        btnL.setFocusable(false);
        btnL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLActionPerformed(evt);
            }
        });
        pnlFila2.add(btnL);

        btnÑ.setBackground(new java.awt.Color(255, 255, 255));
        btnÑ.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnÑ.setForeground(new java.awt.Color(0, 51, 51));
        btnÑ.setText("Ñ");
        btnÑ.setFocusable(false);
        btnÑ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnÑActionPerformed(evt);
            }
        });
        pnlFila2.add(btnÑ);

        pnlQwertyTeclado.add(pnlFila2);

        pnlFila3.setBackground(new java.awt.Color(0, 51, 51));
        pnlFila3.setLayout(new java.awt.GridLayout(1, 9, 5, 0));

        btnZ.setBackground(new java.awt.Color(255, 255, 255));
        btnZ.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnZ.setForeground(new java.awt.Color(0, 51, 51));
        btnZ.setText("Z");
        btnZ.setFocusable(false);
        btnZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZActionPerformed(evt);
            }
        });
        pnlFila3.add(btnZ);

        btnX.setBackground(new java.awt.Color(255, 255, 255));
        btnX.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnX.setForeground(new java.awt.Color(0, 51, 51));
        btnX.setText("X");
        btnX.setFocusable(false);
        btnX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXActionPerformed(evt);
            }
        });
        pnlFila3.add(btnX);

        btnC.setBackground(new java.awt.Color(255, 255, 255));
        btnC.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnC.setForeground(new java.awt.Color(0, 51, 51));
        btnC.setText("C");
        btnC.setFocusable(false);
        btnC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCActionPerformed(evt);
            }
        });
        pnlFila3.add(btnC);

        btnV.setBackground(new java.awt.Color(255, 255, 255));
        btnV.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnV.setForeground(new java.awt.Color(0, 51, 51));
        btnV.setText("V");
        btnV.setFocusable(false);
        btnV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVActionPerformed(evt);
            }
        });
        pnlFila3.add(btnV);

        btnB.setBackground(new java.awt.Color(255, 255, 255));
        btnB.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnB.setForeground(new java.awt.Color(0, 51, 51));
        btnB.setText("B");
        btnB.setFocusable(false);
        btnB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBActionPerformed(evt);
            }
        });
        pnlFila3.add(btnB);

        btnN.setBackground(new java.awt.Color(255, 255, 255));
        btnN.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnN.setForeground(new java.awt.Color(0, 51, 51));
        btnN.setText("N");
        btnN.setFocusable(false);
        btnN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNActionPerformed(evt);
            }
        });
        pnlFila3.add(btnN);

        btnM.setBackground(new java.awt.Color(255, 255, 255));
        btnM.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnM.setForeground(new java.awt.Color(0, 51, 51));
        btnM.setText("M");
        btnM.setFocusable(false);
        btnM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMActionPerformed(evt);
            }
        });
        pnlFila3.add(btnM);

        btnBackSpace.setBackground(new java.awt.Color(255, 255, 255));
        btnBackSpace.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnBackSpace.setForeground(new java.awt.Color(0, 51, 51));
        btnBackSpace.setText("<x|");
        btnBackSpace.setFocusable(false);
        btnBackSpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackSpaceActionPerformed(evt);
            }
        });
        pnlFila3.add(btnBackSpace);

        pnlQwertyTeclado.add(pnlFila3);

        pnlFila4.setBackground(new java.awt.Color(0, 51, 51));
        pnlFila4.setLayout(new java.awt.GridLayout(1, 8, 5, 5));

        btnSpace.setBackground(new java.awt.Color(255, 255, 255));
        btnSpace.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        btnSpace.setForeground(new java.awt.Color(0, 51, 51));
        btnSpace.setText("____");
        btnSpace.setFocusable(false);
        btnSpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSpaceActionPerformed(evt);
            }
        });
        pnlFila4.add(btnSpace);

        pnlQwertyTeclado.add(pnlFila4);

        txtNombreCompleto.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        txtNombreCompleto.setFocusable(false);

        btnContinuar.setBackground(new java.awt.Color(75, 175, 78));
        btnContinuar.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnContinuar.setForeground(new java.awt.Color(255, 255, 255));
        btnContinuar.setText("CONTINUAR");
        btnContinuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuarActionPerformed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(255, 0, 0));
        btnCancelar.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("CANCELAR");
        btnCancelar.setFocusable(false);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jSeparator1.setBackground(new java.awt.Color(160, 160, 160));

        lblMensaje.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(204, 0, 0));
        lblMensaje.setText("Se necesitan mas de 10 caracteres.");
        lblMensaje.setVisible(false);

        javax.swing.GroupLayout pnlContenedorIngresarNombreLayout = new javax.swing.GroupLayout(pnlContenedorIngresarNombre);
        pnlContenedorIngresarNombre.setLayout(pnlContenedorIngresarNombreLayout);
        pnlContenedorIngresarNombreLayout.setHorizontalGroup(
            pnlContenedorIngresarNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorIngresarNombreLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedorIngresarNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlQwertyTeclado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNombreCompleto)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContenedorIngresarNombreLayout.createSequentialGroup()
                        .addComponent(lblMensaje, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar)
                        .addGap(18, 18, 18)
                        .addComponent(btnContinuar))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        pnlContenedorIngresarNombreLayout.setVerticalGroup(
            pnlContenedorIngresarNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorIngresarNombreLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(txtNombreCompleto, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlQwertyTeclado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(pnlContenedorIngresarNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnCancelar)
                    .addComponent(btnContinuar)
                    .addComponent(lblMensaje))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(pnlContenedorTipoPersona, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlContenedorEscogerArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlContenedorIngresarDni, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlContenedorMensajeSuccess, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlContenedorMensajeInconvenientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlContenedorMensajePregunta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlContenedorMensajeValidarUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlContenedorCargando, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlContenedorIngresarNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContenedorTipoPersona, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContenedorEscogerArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContenedorIngresarDni, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContenedorMensajeSuccess, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContenedorMensajeInconvenientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContenedorMensajePregunta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContenedorMensajeValidarUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContenedorCargando, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContenedorIngresarNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pnlPreferencialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlPreferencialMouseClicked
        turnoActual.setPreferencial(1);
        mostrarPanelEscogerArea();
    }//GEN-LAST:event_pnlPreferencialMouseClicked

    private void pnlPreferencialMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlPreferencialMousePressed
        pnlPreferencial.setBackground(Color.decode("#003333"));
    }//GEN-LAST:event_pnlPreferencialMousePressed

    private void pnlPreferencialMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlPreferencialMouseReleased
        pnlPreferencial.setBackground(Color.decode("#006666"));
    }//GEN-LAST:event_pnlPreferencialMouseReleased

    private void pnlGeneralMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlGeneralMouseClicked
        turnoActual.setPreferencial(0);
        mostrarPanelEscogerArea();
    }//GEN-LAST:event_pnlGeneralMouseClicked

    private void pnlGeneralMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlGeneralMousePressed
        pnlGeneral.setBackground(Color.decode("#009999"));
    }//GEN-LAST:event_pnlGeneralMousePressed

    private void pnlGeneralMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlGeneralMouseReleased
        pnlGeneral.setBackground(Color.decode("#00CCCC"));
    }//GEN-LAST:event_pnlGeneralMouseReleased

    private void lblReturnButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblReturnButtonMouseClicked
        pnlContenedorEscogerArea.setVisible(false);
        pnlContenedorTipoPersona.setVisible(true);
        pnlContenedorIngresarDni.setVisible(false);
        //this.isDisposed = true;
    }//GEN-LAST:event_lblReturnButtonMouseClicked

    private void lblReturnButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblReturnButtonMouseEntered
        lblReturnButton.setOpaque(true);
        lblReturnButton.revalidate();
        lblReturnButton.repaint();
    }//GEN-LAST:event_lblReturnButtonMouseEntered

    private void lblReturnButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblReturnButtonMouseExited
        lblReturnButton.setOpaque(false);
        lblReturnButton.revalidate();
        lblReturnButton.repaint();
    }//GEN-LAST:event_lblReturnButtonMouseExited

    private void lblReturnButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblReturnButtonMousePressed
        lblReturnButton.setBackground(Color.decode("#b81414"));
    }//GEN-LAST:event_lblReturnButtonMousePressed

    private void lblReturnButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblReturnButtonMouseReleased
        lblReturnButton.setBackground(Color.red);
    }//GEN-LAST:event_lblReturnButtonMouseReleased

    private void btnNumero1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumero1ActionPerformed
        if (txtNroDocumentoIdentidad.getText().length() <= 16) {
            txtNroDocumentoIdentidad.setText(txtNroDocumentoIdentidad.getText() + "1");
            ValorConsulta = 0;
            String NombreConsulta = "";
            if (txtNroDocumentoIdentidad.getText().length() >= 8) {
                consultar(ValorConsulta, NombreConsulta);
            }
        }
    }//GEN-LAST:event_btnNumero1ActionPerformed

    private void btnNumero2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumero2ActionPerformed
        if (txtNroDocumentoIdentidad.getText().length() <= 16) {
            txtNroDocumentoIdentidad.setText(txtNroDocumentoIdentidad.getText() + "2");
            ValorConsulta = 0;
            String NombreConsulta = "";
            if (txtNroDocumentoIdentidad.getText().length() >= 8) {
                consultar(ValorConsulta, NombreConsulta);
            }
        }
    }//GEN-LAST:event_btnNumero2ActionPerformed

    private void btnNumero3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumero3ActionPerformed
        if (txtNroDocumentoIdentidad.getText().length() <= 16) {
            txtNroDocumentoIdentidad.setText(txtNroDocumentoIdentidad.getText() + "3");
            ValorConsulta = 0;
            String NombreConsulta = "";
            if (txtNroDocumentoIdentidad.getText().length() >= 8) {
                consultar(ValorConsulta, NombreConsulta);
            }
        }
    }//GEN-LAST:event_btnNumero3ActionPerformed

    private void btnNumero4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumero4ActionPerformed
        if (txtNroDocumentoIdentidad.getText().length() <= 16) {
            txtNroDocumentoIdentidad.setText(txtNroDocumentoIdentidad.getText() + "4");
            ValorConsulta = 0;
            String NombreConsulta = "";
            if (txtNroDocumentoIdentidad.getText().length() >= 8) {
                consultar(ValorConsulta, NombreConsulta);
            }
        }
    }//GEN-LAST:event_btnNumero4ActionPerformed

    private void btnNumero5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumero5ActionPerformed
        if (txtNroDocumentoIdentidad.getText().length() <= 16) {
            txtNroDocumentoIdentidad.setText(txtNroDocumentoIdentidad.getText() + "5");
            ValorConsulta = 0;
            String NombreConsulta = "";
            if (txtNroDocumentoIdentidad.getText().length() >= 8) {
                consultar(ValorConsulta, NombreConsulta);
            }
        }
    }//GEN-LAST:event_btnNumero5ActionPerformed

    private void btnNumero6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumero6ActionPerformed
        if (txtNroDocumentoIdentidad.getText().length() <= 16) {
            txtNroDocumentoIdentidad.setText(txtNroDocumentoIdentidad.getText() + "6");
            ValorConsulta = 0;
            String NombreConsulta = "";
            if (txtNroDocumentoIdentidad.getText().length() >= 8) {
                consultar(ValorConsulta, NombreConsulta);
            }
        }
    }//GEN-LAST:event_btnNumero6ActionPerformed

    private void btnNumero7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumero7ActionPerformed
        if (txtNroDocumentoIdentidad.getText().length() <= 16) {
            txtNroDocumentoIdentidad.setText(txtNroDocumentoIdentidad.getText() + "7");
            ValorConsulta = 0;
            String NombreConsulta = "";
            if (txtNroDocumentoIdentidad.getText().length() >= 8) {
                consultar(ValorConsulta, NombreConsulta);
            }
        }
    }//GEN-LAST:event_btnNumero7ActionPerformed

    private void btnNumero8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumero8ActionPerformed
        if (txtNroDocumentoIdentidad.getText().length() <= 16) {
            txtNroDocumentoIdentidad.setText(txtNroDocumentoIdentidad.getText() + "8");
            ValorConsulta = 0;
            String NombreConsulta = "";
            if (txtNroDocumentoIdentidad.getText().length() >= 8) {
                consultar(ValorConsulta, NombreConsulta);
            }
        }
    }//GEN-LAST:event_btnNumero8ActionPerformed

    private void btnNumero9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumero9ActionPerformed
        if (txtNroDocumentoIdentidad.getText().length() <= 16) {
            txtNroDocumentoIdentidad.setText(txtNroDocumentoIdentidad.getText() + "9");
            ValorConsulta = 0;
            String NombreConsulta = "";
            if (txtNroDocumentoIdentidad.getText().length() >= 8) {
                consultar(ValorConsulta, NombreConsulta);
            }
        }
    }//GEN-LAST:event_btnNumero9ActionPerformed

    private void btnNumero0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumero0ActionPerformed
        if (txtNroDocumentoIdentidad.getText().length() <= 16) {
            txtNroDocumentoIdentidad.setText(txtNroDocumentoIdentidad.getText() + "0");
            ValorConsulta = 0;
            String NombreConsulta = "";
            if (txtNroDocumentoIdentidad.getText().length() >= 8) {
                consultar(ValorConsulta, NombreConsulta);
            }
        }
    }//GEN-LAST:event_btnNumero0ActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        String textoActual = txtNroDocumentoIdentidad.getText();
        if (textoActual != null && !textoActual.isEmpty()) {
            textoActual = textoActual.substring(0, textoActual.length() - 1);
            txtNroDocumentoIdentidad.setText(textoActual);
//            contadorPresiones--;
            ValorConsulta = 0;
            String NombreConsulta = "";
            if (textoActual.equals("")) {
                modeloTabla.setRowCount(0);
                Object[] fila = new Object[3];
                for (int i = 0; i < 100; i++) {
                    fila[0] = "";
                    fila[1] = "";
                    fila[2] = "";
                    modeloTabla.addRow(fila);
                }
                tbPacientes.setModel(modeloTabla);
                tbPacientes.setGridColor(Color.decode("#003333"));
            } else {
                if (txtNroDocumentoIdentidad.getText().length() <= 8) {
                    consultar(ValorConsulta, NombreConsulta);
                }
            }
        }
    }//GEN-LAST:event_btnBorrarActionPerformed

    private void txtNroDocumentoIdentidadInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtNroDocumentoIdentidadInputMethodTextChanged

    }//GEN-LAST:event_txtNroDocumentoIdentidadInputMethodTextChanged

    private void txtNroDocumentoIdentidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNroDocumentoIdentidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNroDocumentoIdentidadActionPerformed

    private void txtNroDocumentoIdentidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNroDocumentoIdentidadKeyPressed

    }//GEN-LAST:event_txtNroDocumentoIdentidadKeyPressed

    private void txtNroDocumentoIdentidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNroDocumentoIdentidadKeyTyped
        JTextField source = (JTextField) evt.getSource();
        String text = source.getText();
        int textWidth = source.getFontMetrics(source.getFont()).stringWidth(text);
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
        if (text.length() >= 16) {
            evt.consume();  // Ignora el último carácter introducido
        }
    }//GEN-LAST:event_txtNroDocumentoIdentidadKeyTyped

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        botonPresionado = true;
        mostrarPanelTipoPersona();
        turnoActual.vaciarDatos();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        botonPresionado = true;
        mostrarPanelTipoPersona();
        turnoActual.vaciarDatos();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnSIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSIActionPerformed
//        System.out.println("el id area es : "+turnoActual.getIdArea());
//        System.out.println("Idpaciente" + turnoActual.getIdPaciente());
//        System.out.println("el DNI es : "+turnoActual.getNroDni());
        int IdPaciente;
        Integer textoObtenido = turnoActual.getIdPaciente();

        if (textoObtenido != null) { // Verifica si el texto no está vacío
            IdPaciente = textoObtenido;
        } else {
            IdPaciente = 0;
        }
        String Nombrepaciente = turnoActual.getNombrePaciente();
        if (Nombrepaciente == null) {
            System.out.println("no hay nombre");
            Nombrepaciente = " ";
        }
        try {
            establecerConexion();
            CallableStatement cst;

//                        obtenerConfiguracion(idAreaSeleccionada);
            cst = llamarConexion.prepareCall("{call [AgregarTickets_Test](?,?,?,?,?,?,?)}");
            cst.setInt(1, turnoActual.getIdArea());
            cst.setInt(2, turnoActual.isPreferencial());
//                        cst.setInt(3, this.idEmpleado);
            cst.setInt(3, 0);
            cst.setInt(4, IdPaciente);
            cst.setString(5, Nombrepaciente);
            cst.setString(6, turnoActual.getNroDni());
            cst.registerOutParameter(7, Types.INTEGER);
            cst.executeUpdate();

            int IdTurno = cst.getInt(7);
            System.out.println(IdTurno);
            turnoActual.setIdTurno(IdTurno);
            System.out.println("su nombre es: " + turnoActual.getNombrePaciente());
            //pasar a la ventana frmMensaje
            if (turnoActual.getNombrePaciente().equals("")) {
                System.out.println("no tiene nombre");
                lblNombreSuccess.setText(turnoActual.getNroDni());
            } else {
                System.out.println("si tiene nombre");
                String nombreMostrar = turnoActual.getNombrePaciente().replace("-", " ");
                lblNombreSuccess.setText(nombreMostrar);
            }
            //pasar a la ventana frmMensaje
            if (isConexion) {
                cliente.enviarMensaje(IDENTIFICADOR_PANTALLA, turnoActual.getNombrePaciente(), turnoActual.getNroDni(), String.valueOf(turnoActual.isPreferencial()), String.valueOf(turnoActual.getIdTurno()));
            }

            mostrarPanelMensajeSuccess();
//            frmMensaje ventana = new frmMensaje(ResultadoN, areaId);
//            ventana.setLocationRelativeTo(null);
//            ventana.setVisible(true);
//            this.dispose();
//                        JOptionPane.showMessageDialog(null, "Correcto usted estara en la pantalla de espera : \n " + ResultadoN);
//                        refreshingTable(); 

            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
            cerrarConexion();
        }
    }//GEN-LAST:event_btnSIActionPerformed

    private void btnRegresarEscogerAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegresarEscogerAreaActionPerformed
        mostrarPanelEscogerArea();
        txtNroDocumentoIdentidad.setText("");
        if (isConexion) {
            cliente.confirmarDesconexion();
        }
    }//GEN-LAST:event_btnRegresarEscogerAreaActionPerformed

    private void tbPacientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPacientesMouseClicked
//        // TODO add your handling code here:
//        int row = tbPacientes.getSelectedRow();
//
//        Object value = tbPacientes.getValueAt(row, 0);
//        // Verificando si el valor en la columna seleccionada no es nulo
//        if (value != null) {
//            System.out.println("valor no null");
//            String Idpaciente = value.toString();
//            System.out.println("trae de la tabla" + Idpaciente);
//
//            if (!Idpaciente.isEmpty() /*&& !Idpaciente.equals("0")*/) {
//                System.out.println("entro a");
//                turnoActual.setIdPaciente(Integer.parseInt(Idpaciente));
//                // Objetos en variables 
//                Object valorCelda = tbPacientes.getValueAt(row, 2);
//                // Verificando si el valor no es nulo antes de convertirlo a String
//                String NombreConsulta = (valorCelda != null) ? valorCelda.toString() : " ";
//
//                System.out.println(NombreConsulta);
//
//                ValorConsulta = 1;
//                consultar(ValorConsulta, NombreConsulta);
//            } else {
//                System.out.println("no entro");
//            }
//        } else {
//            System.out.println("Valor en la columna 0 es nulo");
//        }
    }//GEN-LAST:event_tbPacientesMouseClicked

    private void btnNOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNOActionPerformed
        System.out.println("El usuario ha elegido 'No'");

        if (turnoActual.getNombrePaciente().equals("")) {
            System.out.println("no tiene nombre");
            lblNombreInconvenientes.setText(turnoActual.getNroDni());
        } else {
            System.out.println("si tiene nombre");
            String nombreMostrar = turnoActual.getNombrePaciente().replace("-", " ");
            lblNombreInconvenientes.setText(nombreMostrar);
        }
        mostrarPanelMensajeInconvenientes();
    }//GEN-LAST:event_btnNOActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
        if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (isConexion) {
                cliente.confirmarDesconexion();
            }
            System.exit(0);
        }
    }//GEN-LAST:event_formKeyPressed

    private void btnContinuarGeneradorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuarGeneradorActionPerformed
        mostrarPanelCargando();
        pacienteEncontradoGalenhos = consultar(1, "");
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                data = buscarPacienteReniecNroDNI(txtNroDocumentoIdentidad.getText());
                lblDniDigitado.setText("DNI: " + data[8].toString());
                lblNombreUsuarioEncontrado.setText((String) data[10]);
                System.out.println("lo encontró en reniec");
                return null;
            }

            @Override
            protected void done() {
                if (data[0].equals("0000")) {
                    mostrarPanelMensajeValidarUsuario();
                } else {
                    limpiartabla();
                    mostrarPanelIngresarDni();
                }
            }
        };
        if (!pacienteEncontradoGalenhos[0].equals("true")) {
            worker.execute();
        } else {
            lblNombreUsuarioEncontrado.setText(pacienteEncontradoGalenhos[1].toString().replace("-", " "));
            lblDniDigitado.setText("DNI: " + pacienteEncontradoGalenhos[2].toString());
            mostrarPanelMensajeValidarUsuario();
        }
    }//GEN-LAST:event_btnContinuarGeneradorActionPerformed

    private void btnBorrarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnBorrarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBorrarKeyPressed

    private void btnSIValidarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSIValidarUsuActionPerformed
        if (pacienteEncontradoGalenhos[0].equals("true")) {
            turnoActual.setNombrePaciente(pacienteEncontradoGalenhos[1].toString());
            turnoActual.setNroDni(pacienteEncontradoGalenhos[2].toString());
        } else {
            turnoActual.setNombrePaciente(data[10].toString());
            turnoActual.setNroDni(data[8].toString());
        }
        agregarTikcts(turnoActual.getNombrePaciente());
        limpiartabla();
    }//GEN-LAST:event_btnSIValidarUsuActionPerformed

    private void btnNOValidarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNOValidarUsuActionPerformed
        if (pacienteEncontradoGalenhos[0].equals("true")) {
            turnoActual.setNombrePaciente(pacienteEncontradoGalenhos[1].toString());
            turnoActual.setNroDni(pacienteEncontradoGalenhos[2].toString());
        } else {
            turnoActual.setNroDni(data[8].toString());
        }
        mostrarPanelIngresarNombreCompleto();
    }//GEN-LAST:event_btnNOValidarUsuActionPerformed

    private void lblDigiteMalDniLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDigiteMalDniLinkMouseClicked
        txtNroDocumentoIdentidad.setText("");
        limpiartabla();
        mostrarPanelIngresarDni();
    }//GEN-LAST:event_lblDigiteMalDniLinkMouseClicked

    private void btnIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "I");
        }
    }//GEN-LAST:event_btnIActionPerformed

    private void btnContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuarActionPerformed
        String nombreIngresado = txtNombreCompleto.getText();
        if (nombreIngresado.length() >= 10) {
//            Object[] paciente = new Object[2] 
//            insertarPacienteEncontradoReniencToGalenhos();
            turnoActual.setNombrePaciente(nombreIngresado);
            agregarTikcts(nombreIngresado);
            lblNombreSuccess.setText(nombreIngresado);
//            mostrarPanelMensajeSuccess();
        } else {
            lblMensaje.setVisible(true);
        }
    }//GEN-LAST:event_btnContinuarActionPerformed

    private void btnQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "Q");
        }
    }//GEN-LAST:event_btnQActionPerformed

    private void btnWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "W");
        }
    }//GEN-LAST:event_btnWActionPerformed

    private void btnEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "E");
        }
    }//GEN-LAST:event_btnEActionPerformed

    private void btnRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "R");
        }
    }//GEN-LAST:event_btnRActionPerformed

    private void btnTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "T");
        }
    }//GEN-LAST:event_btnTActionPerformed

    private void btnYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnYActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "Y");
        }
    }//GEN-LAST:event_btnYActionPerformed

    private void btnUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "U");
        }
    }//GEN-LAST:event_btnUActionPerformed

    private void btnOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "O");
        }
    }//GEN-LAST:event_btnOActionPerformed

    private void btnPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "P");
        }
    }//GEN-LAST:event_btnPActionPerformed

    private void btnAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "A");
        }
    }//GEN-LAST:event_btnAActionPerformed

    private void btnSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "S");
        }
    }//GEN-LAST:event_btnSActionPerformed

    private void btnDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "D");
        }
    }//GEN-LAST:event_btnDActionPerformed

    private void btnFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "F");
        }
    }//GEN-LAST:event_btnFActionPerformed

    private void btnGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "G");
        }
    }//GEN-LAST:event_btnGActionPerformed

    private void btnHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "H");
        }
    }//GEN-LAST:event_btnHActionPerformed

    private void btnJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "J");
        }
    }//GEN-LAST:event_btnJActionPerformed

    private void btnKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "K");
        }
    }//GEN-LAST:event_btnKActionPerformed

    private void btnLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "L");
        }
    }//GEN-LAST:event_btnLActionPerformed

    private void btnÑActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnÑActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "Ñ");
        }
    }//GEN-LAST:event_btnÑActionPerformed

    private void btnZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "Z");
        }
    }//GEN-LAST:event_btnZActionPerformed

    private void btnXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "X");
        }
    }//GEN-LAST:event_btnXActionPerformed

    private void btnCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "C");
        }
    }//GEN-LAST:event_btnCActionPerformed

    private void btnVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "V");
        }
    }//GEN-LAST:event_btnVActionPerformed

    private void btnBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "B");
        }
    }//GEN-LAST:event_btnBActionPerformed

    private void btnNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "N");
        }
    }//GEN-LAST:event_btnNActionPerformed

    private void btnMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + "M");
        }
    }//GEN-LAST:event_btnMActionPerformed

    private void btnSpaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSpaceActionPerformed
        if (txtNombreCompleto.getText().length() <= 100) {
            lblMensaje.setVisible(false);
            txtNombreCompleto.setText(txtNombreCompleto.getText() + " ");
        }
    }//GEN-LAST:event_btnSpaceActionPerformed

    private void btnBackSpaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackSpaceActionPerformed
        String textoActual = txtNombreCompleto.getText();
        if (textoActual != null && !textoActual.isEmpty()) {
            lblMensaje.setVisible(false);
            textoActual = textoActual.substring(0, textoActual.length() - 1);
            txtNombreCompleto.setText(textoActual);
        }
    }//GEN-LAST:event_btnBackSpaceActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        lblMensaje.setVisible(false);
        limpiartabla();
        mostrarPanelIngresarDni();
    }//GEN-LAST:event_btnCancelarActionPerformed

    public void agregarBotones(int idArea, String nombreArea, int sizeLetra) {
        JButton button = new JButton(nombreArea);
        button.setFont(new Font(nombreArea, Font.PLAIN, sizeLetra));
        button.setName(String.valueOf(idArea));
        button.setBackground(Color.white);
        button.setFocusable(false);
        button.addActionListener(new ButtonClickListener(button));
        pnlContenedorAreasBotones.add(button);
    }

    private class ButtonClickListener implements ActionListener {

        JButton button;

        public ButtonClickListener(JButton boton) {
            this.button = boton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            turnoActual.setIdArea(Integer.parseInt(this.button.getName()));
            mostrarPanelIngresarDni();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnA;
    private javax.swing.JButton btnB;
    private javax.swing.JButton btnBackSpace;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnC;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnContinuar;
    private javax.swing.JButton btnContinuarGenerador;
    private javax.swing.JButton btnD;
    private javax.swing.JButton btnE;
    private javax.swing.JButton btnF;
    private javax.swing.JButton btnG;
    private javax.swing.JButton btnH;
    private javax.swing.JButton btnI;
    private javax.swing.JButton btnJ;
    private javax.swing.JButton btnK;
    private javax.swing.JButton btnL;
    private javax.swing.JButton btnM;
    private javax.swing.JButton btnN;
    private javax.swing.JButton btnNO;
    private javax.swing.JButton btnNOValidarUsu;
    private javax.swing.JButton btnNumero0;
    private javax.swing.JButton btnNumero1;
    private javax.swing.JButton btnNumero2;
    private javax.swing.JButton btnNumero3;
    private javax.swing.JButton btnNumero4;
    private javax.swing.JButton btnNumero5;
    private javax.swing.JButton btnNumero6;
    private javax.swing.JButton btnNumero7;
    private javax.swing.JButton btnNumero8;
    private javax.swing.JButton btnNumero9;
    private javax.swing.JButton btnO;
    private javax.swing.JButton btnP;
    private javax.swing.JButton btnQ;
    private javax.swing.JButton btnR;
    private javax.swing.JButton btnRegresarEscogerArea;
    private javax.swing.JButton btnS;
    private javax.swing.JButton btnSI;
    private javax.swing.JButton btnSIValidarUsu;
    private javax.swing.JButton btnSpace;
    private javax.swing.JButton btnT;
    private javax.swing.JButton btnU;
    private javax.swing.JButton btnV;
    private javax.swing.JButton btnW;
    private javax.swing.JButton btnX;
    private javax.swing.JButton btnY;
    private javax.swing.JButton btnZ;
    private javax.swing.JButton btnÑ;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblDigiteMalDniLink;
    private javax.swing.JLabel lblDniDigitado;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblNombreInconvenientes;
    private javax.swing.JLabel lblNombreSuccess;
    private javax.swing.JLabel lblNombreUsuarioEncontrado;
    private javax.swing.JLabel lblReturnButton;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JPanel pnlContenedorAreasBotones;
    private javax.swing.JPanel pnlContenedorCargando;
    private javax.swing.JPanel pnlContenedorEscogerArea;
    private javax.swing.JPanel pnlContenedorIngresarDni;
    private javax.swing.JPanel pnlContenedorIngresarNombre;
    private javax.swing.JPanel pnlContenedorMensajeInconvenientes;
    private javax.swing.JPanel pnlContenedorMensajePregunta;
    private javax.swing.JPanel pnlContenedorMensajeSuccess;
    private javax.swing.JPanel pnlContenedorMensajeValidarUsuario;
    private javax.swing.JPanel pnlContenedorTipoPersona;
    private javax.swing.JPanel pnlFila1;
    private javax.swing.JPanel pnlFila2;
    private javax.swing.JPanel pnlFila3;
    private javax.swing.JPanel pnlFila4;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JPanel pnlPreferencial;
    private javax.swing.JPanel pnlQwertyTeclado;
    private javax.swing.JTable tbPacientes;
    private javax.swing.JTextField txtNombreCompleto;
    private javax.swing.JTextField txtNroDocumentoIdentidad;
    // End of variables declaration//GEN-END:variables
}
