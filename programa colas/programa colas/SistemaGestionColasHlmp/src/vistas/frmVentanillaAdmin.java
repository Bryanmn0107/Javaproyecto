/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

import cliente_sources.Cliente;
import cliente_sources.VentanaCliente;
import conexion.ConexionSQL;
import static conexion.ConexionSQL.cerrarConexion;
import static conexion.ConexionSQL.establecerConexion;
import static conexion.ConexionSQL.llamarConexion;
import static conexion.ConexionSQL.rs;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import loginSources.frmLogin;
import modelos.Area;
import modulos.ModosCronometro;
import static modulos.ModosCronometro.LLAMAR_TICKET;
import static modulos.ModosCronometro.NO_SE_PRESENTO;
import modulos.ModuleCronometro;
import modulos.ModuleHoraFecha;
import modulos.ModuleProcedimientosTurnos;
//import updaters.Cronometro;
//import updaters.HoraFecha;
//import static updaters.ModosCronometro.Desde.FORMULARIO;
//import static updaters.ModosCronometro.LLAMAR_TICKET;
//import static updaters.ModosCronometro.NO_SE_PRESENTO;
//import updaters.Sonido;

/**
 * Clase de la ventanilla admin.
 * 
 * @author jnxd_
 */
public class frmVentanillaAdmin extends VentanaCliente {

    /**
     * Variable del Cliente socket.
     */
    private Cliente cliente;
    
    /**
     * Constante que se inicializará con el nombre de la ventanilla.
     */
    private final String IDENTIFICADOR;
    
    /**
     * Constante del identificador del cliente de la pantalla.
     */
    private final String IDENTIFICADOR_PANTALLA = "Visualizador";

    /**
     * Variable que referencia al icono de la ventana.
     */
    private ImageIcon vtnIcon;

    /**
     * Variable que sirve como banderin, para saber si la ventana a sido maximizada.
     */
    private boolean isMaximized = false;
    
    /**
     * Variable que sirve como banderin, para saber si el tiempo de atencion se excedió.
     */
    public boolean isTiempoExcedido = false;
    
    /**
     * Variable que sirve como banderin, para saber si la atencion a sido iniciada.
     */
    private boolean isAtencionIniciada = false;
    
    /**
     * Variable creada para instanciar la ruta del manual.
     */
    public String rutaManual;
    
    /**
     * Variable creada para instanciar el archivo del manual.
     */
    public File manualFile;

    // ICONOS PARA LA INTERFAZ.
    public final ImageIcon iconoMaximizar30 = new ImageIcon(getClass().getResource("/icons/maximize-20.png"));
    public final ImageIcon iconoComprimir30 = new ImageIcon(getClass().getResource("/icons/compress-20.png"));
    public final ImageIcon iconoMegafonoLight30 = new ImageIcon(getClass().getResource("/icons/megaphone-light-30.png"));
    public final ImageIcon iconoMegafonoPrincipal30 = new ImageIcon(getClass().getResource("/icons/megaphone-principal-30.png"));

    /**
     * Variable que almacena la fecha actual.
     */
    private ModuleHoraFecha horaFecha;
    
    /**
     * Variable que almacena el tiempo de atencion.
     */
    private ModuleCronometro cronometroTiempoAtencion;

    /**
     * Variable que almacena el modelo de la tabla tbTurnosAnteriores
     */
    private DefaultTableModel modeloTabla;

    /**
     * Variable que almacena el tiempoPromedioAtencion.
     */
    private int tiempoPromedioAtencion;
    
    /**
     * Variable que almacena la ip del servidor de comunicacion de sockets.
     */
    private String IpPantallaVisualizacion;
    
    /**
     * Variable que almacena el puerto de escucha del servidor.
     */
    private int puerto;

    /**
     * Variable que almacena el tamaño de la letra de la cabecera de la tabla tbTurnosAnteriores.
     */
    private double tamañoLetraColumnBody = 20;
    
    /**
     * Variable que almacena el color hexadecimal de la cabecera de la tabla tbTurnosAnteriores.
     */
    private String ColorHexFondo = "#006666";
    
    /**
     * Variable que almacena el color hexadecimal del texto de la cabecera de la tabla tbTurnosAnteriores.
     */
    private String ColorHexTexto = "#ffffff";
    
    /**
     * Variable que almacena el tiempo de espera a los pacientes.
     */
    private int tiempoEsperaPaciente;

    /**
     * Variable que almacena el id de la ventanilla.
     */
    public static int idVentanilla;
    
    /**
     * Variable que almacena el area de la ventanilla.
     */
    private Area area;
    
    /**
     * Variable que almacena el id del turno actual.
     */
    public static String idTurno = "";

    /**
     * Variables para obtener la posicion x & y del mouse.
     */
    int xMouse;
    int yMouse;

    /**
     * Variable que almacena la instancia de la ventana de Seleccionar Ventanilla.
     */
    private frmSeleccionarVentanilla vtnSeleccionarVentanilla;

    /**
     * Constructor de la clase.
     * 
     * @param ventanilla
     * @param area
     * @param idVentanilla
     * @throws InterruptedException 
     */
    public frmVentanillaAdmin(String ventanilla, Area area, int idVentanilla) throws InterruptedException {
        vtnIcon = new ImageIcon(this.getClass().getResource("/icons/sigco-vtnadmin-64.png"));
        initComponents();
        this.setIconImage(vtnIcon.getImage());
        setVisibleLlamarButton(false);
        maximizar();
        initSystemTray();
        mostrarTicketLlamado(idVentanilla);
        moduloCronometro = new ModuleCronometro();
        rutaManual = "MANUAL_DEL_SITEMA_DE_GESTION_COLAS.pdf";
        manualFile = new File(rutaManual);
        obtenerConfiguracion(area.getIdArea());
        cronometroEsperaNoPresentó = new ModuleCronometro(this.tiempoEsperaPaciente, this, NO_SE_PRESENTO);
        cronometroEsperaSiguienteTicket = new ModuleCronometro(20, this, LLAMAR_TICKET);
        cronometroTiempoAtencion = new ModuleCronometro(lblTiempoTranscurrido, this.tiempoPromedioAtencion, this);
        cronometroHabilitarBotonVolverLlamar = new ModuleCronometro(10);
        this.IDENTIFICADOR = ventanilla;
        this.area = area;
        lblVentanillaArea.setText(ventanilla + " - " + area.getNombre());
        lblEstadoConexionPantalla = new JLabel();
        this.idVentanilla = idVentanilla;
        lblEstadoConexionPantalla.setIcon(iconoStatusLoadingPantalla);
        lblEstadoConexionPantalla.setFont(new Font("Arial", Font.PLAIN, 18));
        lblEstadoConexionPantalla.setText("Conectando...");
        lblEstadoConexionPantalla.setSize(32, 32);
        pnlBarraStatus.add(lblEstadoConexionPantalla);
        horaFecha = new ModuleHoraFecha(lblHoraFecha);
        modeloTabla = (DefaultTableModel) tbTurnosAnteriores.getModel();
        refreshingTable();
        JTableHeader headerVentanillaTicket = tbTurnosAnteriores.getTableHeader();
        headerVentanillaTicket.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Arial", 1, (int) tamañoLetraColumnBody));
                setBackground(Color.decode(ColorHexFondo));
                setForeground(Color.decode(ColorHexTexto));
                return this;
            }
        });
        cliente = new Cliente(this, this.IpPantallaVisualizacion, this.puerto, IDENTIFICADOR);
    }

    /**
     * Constructor vacio de la clase.
     */
    public frmVentanillaAdmin() {
        this.IDENTIFICADOR = "";
    }

    /**
     * Metodo que permite crear un icono en la bandeja del sistema
     * Sirve para poder mostrar notificaciones en pantalla segun el SO.
     */
    private void initSystemTray() {
        try {
            if (SystemTray.isSupported()) {
                systemTray = SystemTray.getSystemTray();
                trayIcon = new TrayIcon(vtnIcon.getImage(), "Ventanilla Admin");
                trayIcon.setImageAutoSize(true);
                systemTray.add(trayIcon);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Metodo que permite cambiar el color del label, de tal manera que no se vea dicho label segun
     * el parametro isVisible.
     * 
     * @param label
     * @param isVisible 
     */
    private void setVisibleLabel(JLabel label, boolean isVisible) {
        if (!isVisible) {
            label.setForeground(Color.decode("#006666"));
        } else {
            label.setForeground(Color.decode("#ffffff"));
        }
    }

    /**
     * Metodo que permite cambiar el color de lblLlamarButton, de tal manera que no se vea dicho label segun
     * el parametro isVisible.
     * 
     * @param isVisible 
     */
    public void setVisibleLlamarButton(boolean isVisible) {
        if (!isVisible) {
            lblLlamarButton.setForeground(Color.decode("#006666"));
            lblLlamarButton.setBackground(Color.decode("#006666"));
            lblLlamarButton.setOpaque(false);
            lblLlamarButton.setIcon(iconoMegafonoPrincipal30);
            lblLlamarButton.setBorder(null);
            lblLlamarButton.setCursor(Cursor.getDefaultCursor());
            lblLlamarButton.revalidate();
            lblLlamarButton.repaint();
            lblLlamarIsVisible = false;
        } else {
            lblLlamarButton.setForeground(Color.decode("#ffffff"));
            lblLlamarButton.setBackground(Color.decode("#ffffff"));
            lblLlamarButton.setOpaque(false);
            lblLlamarButton.setIcon(iconoMegafonoLight30);
            lblLlamarButton.setBorder(bordeLblLlamarButton);
            lblLlamarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblLlamarButton.revalidate();
            lblLlamarButton.repaint();
            lblLlamarIsVisible = true;
        }
    }

    /**
     * Metodo que sirve para abrir el formuario de frmSeleccionarVentanilla.
     */
    private void abrirFormSeleccionarVentanilla() {
        this.vtnSeleccionarVentanilla = new frmSeleccionarVentanilla();
        this.vtnSeleccionarVentanilla.setLocationRelativeTo(null);
        this.vtnSeleccionarVentanilla.setVisible(true);
    }

    /**
     * Metodo que sirve para cerrar la ventanilla desde base de datos.
     * 
     * @param idVentanilla 
     */
    private void cerrarVentanilla(int idVentanilla) {
        try {
            int confirm;

            confirm = JOptionPane.showOptionDialog(this, "¿Estás seguro que quieres cerrar la ventanilla?",
                    "Confirmación", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, null, null);

            if (confirm == 0) {
                if (lblNroDniPaciente.getText().equals("00000000") || lblNroDniPaciente.getText().equals("...")) {
                    establecerConexion();
                    System.out.println(idVentanilla);
                    CallableStatement cst;
                    cst = llamarConexion.prepareCall("{call [ActualizarEstadoVentanillas](?,?)}");
                    cst.setInt(1, idVentanilla);
                    cst.setInt(2, 0);
                    cst.executeUpdate();
                    System.out.println("Ventanilla Cerrada");
                    abrirFormSeleccionarVentanilla();
                    if (isConexion) {
                        cliente.confirmarDesconexion();
                    }
                    systemTray.remove(trayIcon);
                    this.dispose();
                    cerrarConexion();
                } else {
                    JOptionPane.showMessageDialog(this, "Aun tienes tickets atendiendo, intentalo mas tarde", "Alerta", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que sirve para mostrar el ticket llamado.
     * 
     * @param idVentanilla 
     */
    private void mostrarTicketLlamado(int idVentanilla) {
        try {
            establecerConexion();
            CallableStatement cst;
            cst = llamarConexion.prepareCall("{call [TicketsEnAtencion](?,?,?)}");
            cst.setInt(1, 1);
            cst.setInt(2, 0);
            cst.setInt(3, idVentanilla);
            rs = cst.executeQuery();
            String nombre = "";
            if (rs.next()) {
                nombre = rs.getString(4).contains("-") ? rs.getString(4).replace("-", " ") : rs.getString(4);
                idTurno = String.valueOf(rs.getInt(1));
                lblNombrePaciente.setText(nombre);
                lblNroDniPaciente.setText(rs.getString(3));
                if (rs.getInt(5) == 1) {
                    lblNombrePaciente.setIcon(iconoPreferencial48x48);
                } else {
                    lblNombrePaciente.setIcon(iconoPersonaGeneral);
                }
                btnLlamarSiguiente.setEnabled(false);
                btnIniciarDetenerAtencion.setEnabled(true);
                btnNoSePresento.setEnabled(true);
                lblLlamarButton.setVisible(true);
            }
            cerrarConexion();
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, sqle, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que sirve para obtener las configuraciones guardadas en base de datos.
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

                tiempoEsperaPaciente = rs.getInt(15);
            }
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que sirve para recargar la tabla tbTurnosAnteriores.
     */
    private void refreshingTable() {
        try {
            establecerConexion();
            modeloTabla.setRowCount(0);
            CallableStatement cst;
            Object[] fila = new Object[7];
            cst = llamarConexion.prepareCall("{call [MostrarTicketsFiltroVentanilla_TEST](?,?)}");
            cst.setInt(1, idVentanilla);
            cst.setInt(2, this.area.getIdArea());

            rs = cst.executeQuery();
            while (rs.next()) {
                fila[0] = rs.getInt(1);
                fila[1] = rs.getString(2);
                fila[2] = rs.getString(3);
                fila[3] = rs.getString(4).replace("-", " ");
                fila[4] = rs.getString(5);
                fila[5] = rs.getInt(6);
                fila[6] = rs.getString(7);
                modeloTabla.addRow(fila);
            }
            tbTurnosAnteriores.setModel(modeloTabla);
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que sirve para enviar una señal al servidor para llamar a un paciente usando la instancia del cliente socket.
     */
    public void llamarPaciente() {
        if (isConexion) {
            cliente.llamarPaciente(IDENTIFICADOR_PANTALLA);
        }
    }

    /**
     * Metodo que inicia y detiene la atencion de un paciente.
     */
    public void iniciarDetenerAtencion() {
        if (!isAtencionIniciada) {
            setVisibleLabel(lblTiempoTranscurrido, true);
            //lblLlamarButton.setVisible(false);
            setVisibleLlamarButton(false);
            cronometroHabilitarBotonVolverLlamar.resetStop();
            btnNoSePresento.setEnabled(false);
            btnIniciarDetenerAtencion.setIcon(iconoDetener);
            btnIniciarDetenerAtencion.setText("Detener Atencion");
            ModuleProcedimientosTurnos.actualizarHoraAtencionInicioTicket(idTurno, horaFecha.getCurrentTime());
            cronometroTiempoAtencion.start();
            isAtencionIniciada = true;
            cronometroEsperaNoPresentó.resetStop();
            cronometroEsperaSiguienteTicket.resetStop();
        } else {
            cronometroTiempoAtencion.resetStop();
            ModuleProcedimientosTurnos.actualizarHoraAtencionFinTicket(idTurno, horaFecha.getCurrentTime(), lblTiempoTranscurrido.getText());
            ModuleProcedimientosTurnos.actualizarEstadoTicket(idTurno, 1);
            refreshingTable();
            if (isConexion) {
                cliente.enviarLiberarNotificacion(IDENTIFICADOR_PANTALLA);
            }
            btnLlamarSiguiente.setEnabled(true);
            btnIniciarDetenerAtencion.setIcon(new ImageIcon(getClass().getResource("/icons/incio-48.png")));
            btnIniciarDetenerAtencion.setText("Iniciar Atencion");
            btnIniciarDetenerAtencion.setEnabled(false);
            lblTiempoTranscurrido.setText("00:00:00");
            setVisibleLabel(lblTiempoTranscurrido, false);
            idTurno = "";
            lblNombrePaciente.setText("...");
//            pacienteAsistio = false;
            lblNombrePaciente.setIcon(null);
            lblNroDniPaciente.setText("00000000");
            isAtencionIniciada = false;
            btnIniciarDetenerAtencion.revalidate();
            btnIniciarDetenerAtencion.repaint();
            cronometroEsperaSiguienteTicket.start();
            if (!isTiempoExcedido) {
                moduloCronometro.displayNotificationCustom("Paciente atendido", "¡Felicidades!, atendió un paciente, se marcará como atendido", TrayIcon.MessageType.INFO);
            }
        }
    }

    /**
     * Metodo para marcar como no se presento al ticket.
     */
    public void noSePresento() {
        ModuleProcedimientosTurnos.actualizarEstadoTicket(idTurno, 2);
        refreshingTable();
        btnIniciarDetenerAtencion.setEnabled(false);
        btnNoSePresento.setEnabled(false);
        btnLlamarSiguiente.setEnabled(true);
//        lblLlamarButton.setVisible(false);
        setVisibleLlamarButton(false);
        idTurno = "";
        lblNombrePaciente.setText("...");
        lblNombrePaciente.setIcon(null);
        lblNroDniPaciente.setText("00000000");
        cronometroEsperaNoPresentó.resetStop();
        cronometroHabilitarBotonVolverLlamar.resetStop();
        cronometroEsperaSiguienteTicket.start();
        if (isConexion) {
            cliente.enviarLiberarNotificacion(IDENTIFICADOR_PANTALLA);
        }
        moduloCronometro.displayNotificationCustom("Paciente no atendido", "El paciente no asistió, por lo que se marcará como no atendido", TrayIcon.MessageType.WARNING);
    }

    /**
     * Metodo que sirve para maximizar la vista.
     */
    private void maximizar() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
        screenSize.height -= screenInsets.bottom;
        this.setLocation(0, 0);
        this.setSize(screenSize);
//            setExtendedState(JFrame.MAXIMIZED_BOTH);
        lblMaximizeButton.setIcon(iconoComprimir30);
        this.isMaximized = true;
        this.revalidate();
        this.repaint();
    }

    /**
     * Metodo que sirve para contraer la vista.
     */
    private void contraer() {
        this.setSize(1054, 558);
        this.setLocationRelativeTo(null);
        lblMaximizeButton.setIcon(iconoMaximizar30);
        this.isMaximized = false;
        this.revalidate();
        this.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBackground = new javax.swing.JPanel();
        pnlContenedorAcciones = new javax.swing.JPanel();
        lblNroDniPaciente = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblNombrePaciente = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnLlamarSiguiente = new javax.swing.JButton();
        btnIniciarDetenerAtencion = new javax.swing.JButton();
        btnNoSePresento = new javax.swing.JButton();
        lblAyuda = new javax.swing.JLabel();
        btnCerrarVentanilla = new javax.swing.JButton();
        lblVentanillaArea = new javax.swing.JLabel();
        lblTiempoTranscurrido = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        lblLlamarButton = new javax.swing.JLabel();
        pnlBarraSuperior = new javax.swing.JPanel();
        pnlBarraStatus = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        lblHoraFecha = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbTurnosAnteriores = new javax.swing.JTable();
        pnlHeader = new javax.swing.JPanel();
        lblMinimizeButton = new javax.swing.JLabel();
        lblMaximizeButton = new javax.swing.JLabel();
        lblExitButton = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(new java.awt.Dimension(1138, 536));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnlBackground.setBackground(new java.awt.Color(255, 255, 255));
        pnlBackground.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        pnlContenedorAcciones.setBackground(new java.awt.Color(0, 102, 102));

        lblNroDniPaciente.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lblNroDniPaciente.setForeground(new java.awt.Color(255, 255, 255));
        lblNroDniPaciente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNroDniPaciente.setText("00000000");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("TURNO ACTUAL:");

        lblNombrePaciente.setFont(new java.awt.Font("Tahoma", 1, 32)); // NOI18N
        lblNombrePaciente.setForeground(new java.awt.Color(255, 255, 255));
        lblNombrePaciente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNombrePaciente.setText("...");
        lblNombrePaciente.setFocusable(false);

        jPanel1.setOpaque(false);

        btnLlamarSiguiente.setBackground(new java.awt.Color(0, 102, 102));
        btnLlamarSiguiente.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnLlamarSiguiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/megaphone-48.png"))); // NOI18N
        btnLlamarSiguiente.setText("Llamar Siguiente");
        btnLlamarSiguiente.setFocusable(false);
        btnLlamarSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLlamarSiguienteActionPerformed(evt);
            }
        });

        btnIniciarDetenerAtencion.setBackground(new java.awt.Color(0, 102, 102));
        btnIniciarDetenerAtencion.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnIniciarDetenerAtencion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/incio-48.png"))); // NOI18N
        btnIniciarDetenerAtencion.setText("Iniciar Atencion");
        btnIniciarDetenerAtencion.setEnabled(false);
        btnIniciarDetenerAtencion.setFocusable(false);
        btnIniciarDetenerAtencion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarDetenerAtencionActionPerformed(evt);
            }
        });

        btnNoSePresento.setBackground(new java.awt.Color(0, 102, 102));
        btnNoSePresento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnNoSePresento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/not-selfies-48.png"))); // NOI18N
        btnNoSePresento.setText("No se presentó");
        btnNoSePresento.setEnabled(false);
        btnNoSePresento.setFocusable(false);
        btnNoSePresento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoSePresentoActionPerformed(evt);
            }
        });

        lblAyuda.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblAyuda.setForeground(new java.awt.Color(255, 255, 255));
        lblAyuda.setText("<html><u>¿Ayuda?</u></html>");
        lblAyuda.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAyuda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAyudaMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAyudaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAyudaMouseExited(evt);
            }
        });

        btnCerrarVentanilla.setBackground(new java.awt.Color(0, 102, 102));
        btnCerrarVentanilla.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCerrarVentanilla.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/closed(24).png"))); // NOI18N
        btnCerrarVentanilla.setText("Cerrar");
        btnCerrarVentanilla.setFocusable(false);
        btnCerrarVentanilla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarVentanillaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLlamarSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnIniciarDetenerAtencion, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNoSePresento, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblAyuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCerrarVentanilla)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnLlamarSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnIniciarDetenerAtencion, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(btnNoSePresento, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCerrarVentanilla, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblAyuda, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13))
        );

        lblVentanillaArea.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblVentanillaArea.setForeground(new java.awt.Color(255, 255, 255));
        lblVentanillaArea.setText("Ventanilla 1 - Laboratorio");

        lblTiempoTranscurrido.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblTiempoTranscurrido.setForeground(new java.awt.Color(0, 102, 102));
        lblTiempoTranscurrido.setText("00:00:00");

        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));

        jSeparator2.setForeground(new java.awt.Color(255, 255, 255));

        lblLlamarButton.setBackground(new java.awt.Color(255, 255, 255));
        lblLlamarButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblLlamarButton.setForeground(new java.awt.Color(255, 255, 255));
        lblLlamarButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLlamarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/megaphone-light-30.png"))); // NOI18N
        lblLlamarButton.setText("Volver a llamar");
        lblLlamarButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        //lblLlamarButton.setVisible(false);
        lblLlamarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblLlamarButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblLlamarButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblLlamarButtonMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblLlamarButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblLlamarButtonMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlContenedorAccionesLayout = new javax.swing.GroupLayout(pnlContenedorAcciones);
        pnlContenedorAcciones.setLayout(pnlContenedorAccionesLayout);
        pnlContenedorAccionesLayout.setHorizontalGroup(
            pnlContenedorAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(pnlContenedorAccionesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedorAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlContenedorAccionesLayout.createSequentialGroup()
                        .addComponent(lblVentanillaArea)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTiempoTranscurrido))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContenedorAccionesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblNombrePaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlContenedorAccionesLayout.createSequentialGroup()
                        .addComponent(lblNroDniPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlContenedorAccionesLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblLlamarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        pnlContenedorAccionesLayout.setVerticalGroup(
            pnlContenedorAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorAccionesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedorAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTiempoTranscurrido)
                    .addComponent(lblVentanillaArea))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlContenedorAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLlamarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblNombrePaciente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblNroDniPaciente)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlBarraSuperior.setBackground(new java.awt.Color(204, 204, 204));
        pnlBarraSuperior.setLayout(new java.awt.GridLayout(1, 5));

        pnlBarraStatus.setMinimumSize(new java.awt.Dimension(52, 42));
        pnlBarraStatus.setOpaque(false);
        pnlBarraStatus.setPreferredSize(new java.awt.Dimension(52, 42));
        pnlBarraStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        pnlBarraSuperior.add(pnlBarraStatus);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridLayout(1, 2));

        lblHoraFecha.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblHoraFecha.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblHoraFecha.setText("22:39 | 23 Febrero 2024  ");
        jPanel5.add(lblHoraFecha);

        pnlBarraSuperior.add(jPanel5);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)), "TURNOS ANTERIORES", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        jPanel6.setOpaque(false);

        jScrollPane1.setOpaque(false);

        tbTurnosAnteriores.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tbTurnosAnteriores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IdTicket", "Turno", "DNI", "Paciente", "Estado", "IdEstado", "Duración"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbTurnosAnteriores.setOpaque(false);
        tbTurnosAnteriores.setRowHeight(20);
        jScrollPane1.setViewportView(tbTurnosAnteriores);
        if (tbTurnosAnteriores.getColumnModel().getColumnCount() > 0) {
            tbTurnosAnteriores.getColumnModel().getColumn(0).setMinWidth(0);
            tbTurnosAnteriores.getColumnModel().getColumn(0).setPreferredWidth(0);
            tbTurnosAnteriores.getColumnModel().getColumn(0).setMaxWidth(0);
            tbTurnosAnteriores.getColumnModel().getColumn(1).setMinWidth(70);
            tbTurnosAnteriores.getColumnModel().getColumn(1).setPreferredWidth(70);
            tbTurnosAnteriores.getColumnModel().getColumn(1).setMaxWidth(70);
            tbTurnosAnteriores.getColumnModel().getColumn(2).setMinWidth(100);
            tbTurnosAnteriores.getColumnModel().getColumn(2).setPreferredWidth(100);
            tbTurnosAnteriores.getColumnModel().getColumn(2).setMaxWidth(100);
            tbTurnosAnteriores.getColumnModel().getColumn(4).setMinWidth(100);
            tbTurnosAnteriores.getColumnModel().getColumn(4).setPreferredWidth(100);
            tbTurnosAnteriores.getColumnModel().getColumn(4).setMaxWidth(100);
            tbTurnosAnteriores.getColumnModel().getColumn(5).setMinWidth(0);
            tbTurnosAnteriores.getColumnModel().getColumn(5).setPreferredWidth(0);
            tbTurnosAnteriores.getColumnModel().getColumn(5).setMaxWidth(0);
            tbTurnosAnteriores.getColumnModel().getColumn(6).setMinWidth(100);
            tbTurnosAnteriores.getColumnModel().getColumn(6).setPreferredWidth(100);
            tbTurnosAnteriores.getColumnModel().getColumn(6).setMaxWidth(100);
        }

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addGap(13, 13, 13))
        );

        pnlHeader.setBackground(new java.awt.Color(0, 102, 102));
        pnlHeader.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pnlHeaderMouseDragged(evt);
            }
        });
        pnlHeader.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlHeaderMousePressed(evt);
            }
        });

        lblMinimizeButton.setBackground(new java.awt.Color(153, 153, 153));
        lblMinimizeButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMinimizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/minimize-20.png"))); // NOI18N
        lblMinimizeButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblMinimizeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMinimizeButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblMinimizeButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblMinimizeButtonMouseExited(evt);
            }
        });

        lblMaximizeButton.setBackground(new java.awt.Color(153, 153, 153));
        lblMaximizeButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMaximizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/maximize-20.png"))); // NOI18N
        lblMaximizeButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblMaximizeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMaximizeButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblMaximizeButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblMaximizeButtonMouseExited(evt);
            }
        });

        lblExitButton.setBackground(new java.awt.Color(204, 0, 0));
        lblExitButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblExitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/close-20.png"))); // NOI18N
        lblExitButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblExitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblExitButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblExitButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblExitButtonMouseExited(evt);
            }
        });

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblMinimizeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lblMaximizeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lblExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblMinimizeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblExitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMaximizeButton))
                .addGap(13, 13, 13))
        );

        javax.swing.GroupLayout pnlBackgroundLayout = new javax.swing.GroupLayout(pnlBackground);
        pnlBackground.setLayout(pnlBackgroundLayout);
        pnlBackgroundLayout.setHorizontalGroup(
            pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBackgroundLayout.createSequentialGroup()
                .addComponent(pnlContenedorAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBarraSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlBackgroundLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        pnlBackgroundLayout.setVerticalGroup(
            pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlContenedorAcciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlBackgroundLayout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlBarraSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cerrarVentanilla(this.idVentanilla);
        
        // Se cierra la conexion con el servidor de sockets.
        if (isConexion) {
            cliente.confirmarDesconexion();
        }
    }//GEN-LAST:event_formWindowClosing

    private void lblAyudaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaMouseEntered
        lblAyuda.setForeground(Color.black);
    }//GEN-LAST:event_lblAyudaMouseEntered

    private void lblAyudaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaMouseExited
        lblAyuda.setForeground(Color.white);
    }//GEN-LAST:event_lblAyudaMouseExited

    private void lblAyudaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaMouseClicked
        
        // Mostrar el manual.
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(manualFile);
            } catch (IOException ex) {
                Logger.getLogger(frmLogin.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Su sistema operativo no puede abrir el manual de uso del sistema. ", "Alerta", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_lblAyudaMouseClicked

    private void lblMinimizeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinimizeButtonMouseClicked
        
        // Minimiza la pantalla
        this.setState(Frame.ICONIFIED);
    }//GEN-LAST:event_lblMinimizeButtonMouseClicked

    private void lblMinimizeButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinimizeButtonMouseEntered
        lblMinimizeButton.setOpaque(true);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblMinimizeButtonMouseEntered

    private void lblMinimizeButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinimizeButtonMouseExited
        lblMinimizeButton.setOpaque(false);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblMinimizeButtonMouseExited

    private void lblMaximizeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMaximizeButtonMouseClicked
        // Maximiza si isMaximized es true, si es false se contrae.
        if (!isMaximized) {
            maximizar();
        } else {
            contraer();
        }
    }//GEN-LAST:event_lblMaximizeButtonMouseClicked

    private void lblMaximizeButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMaximizeButtonMouseEntered
        lblMaximizeButton.setOpaque(true);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblMaximizeButtonMouseEntered

    private void lblMaximizeButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMaximizeButtonMouseExited
        lblMaximizeButton.setOpaque(false);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblMaximizeButtonMouseExited

    private void lblExitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblExitButtonMouseClicked
        // Cierra la ventanilla.
        cerrarVentanilla(this.idVentanilla);
    }//GEN-LAST:event_lblExitButtonMouseClicked

    private void lblExitButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblExitButtonMouseEntered
        lblExitButton.setOpaque(true);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblExitButtonMouseEntered

    private void lblExitButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblExitButtonMouseExited
        lblExitButton.setOpaque(false);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblExitButtonMouseExited

    private void pnlHeaderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlHeaderMousePressed
        // Si isMaximized es true, se guardan las coordenadas del mouse.
        if (!isMaximized) {
            xMouse = evt.getX() + pnlHeader.getX();
            yMouse = evt.getY();
        }
    }//GEN-LAST:event_pnlHeaderMousePressed

    private void pnlHeaderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlHeaderMouseDragged
        // Si isMaximized es true, mueve la ventana segun las coordenadas del mouse guardadas
        if (!isMaximized) {
            int x = evt.getXOnScreen();
            int y = evt.getYOnScreen();
            this.setLocation(x - xMouse, y - yMouse);
        }
    }//GEN-LAST:event_pnlHeaderMouseDragged

    private void btnCerrarVentanillaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarVentanillaActionPerformed
        cerrarVentanilla(this.idVentanilla);
    }//GEN-LAST:event_btnCerrarVentanillaActionPerformed

    private void btnLlamarSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLlamarSiguienteActionPerformed
        llamarPaciente();
    }//GEN-LAST:event_btnLlamarSiguienteActionPerformed

    private void btnIniciarDetenerAtencionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarDetenerAtencionActionPerformed
        iniciarDetenerAtencion();
    }//GEN-LAST:event_btnIniciarDetenerAtencionActionPerformed

    private void btnNoSePresentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoSePresentoActionPerformed
        noSePresento();
    }//GEN-LAST:event_btnNoSePresentoActionPerformed

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        // Si isMaximized es true, se ajusta a la pantalla cuando el formulario es movido.
        if (isMaximized) {
            Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
            int taskBarSize = scnMax.bottom;

            GraphicsDevice gd = this.getGraphicsConfiguration().getDevice();
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();

            this.setSize(bounds.width, bounds.height - taskBarSize);
            lblMaximizeButton.setIcon(iconoComprimir30);
            lblMaximizeButton.revalidate();
            lblMaximizeButton.repaint();
        }
    }//GEN-LAST:event_formComponentMoved

    private void lblLlamarButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLlamarButtonMouseEntered
        if (lblLlamarIsVisible) {
            lblLlamarButton.setForeground(Color.decode("#006666"));
            lblLlamarButton.setIcon(iconoMegafonoPrincipal30);
            lblLlamarButton.setOpaque(true);
            lblLlamarButton.revalidate();
            lblLlamarButton.repaint();
        }
    }//GEN-LAST:event_lblLlamarButtonMouseEntered

    private void lblLlamarButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLlamarButtonMouseExited
        if (lblLlamarIsVisible) {
            lblLlamarButton.setForeground(Color.decode("#ffffff"));
            lblLlamarButton.setIcon(iconoMegafonoLight30);
            lblLlamarButton.setOpaque(false);
            lblLlamarButton.revalidate();
            lblLlamarButton.repaint();
        }
    }//GEN-LAST:event_lblLlamarButtonMouseExited

    private void lblLlamarButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLlamarButtonMousePressed
        if (lblLlamarIsVisible) {
            lblLlamarButton.setBackground(Color.LIGHT_GRAY);
        }
    }//GEN-LAST:event_lblLlamarButtonMousePressed

    private void lblLlamarButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLlamarButtonMouseReleased
        if (lblLlamarIsVisible) {
            lblLlamarButton.setBackground(Color.WHITE);
        }
    }//GEN-LAST:event_lblLlamarButtonMouseReleased

    private void lblLlamarButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLlamarButtonMouseClicked
        // Si el boton de llamar está visible, se oculta y se inicia el cronometro que lo volverá a habilitar nuevamente
        // Envia la señal a la pantalla de visualización.
        if (lblLlamarIsVisible) {
            setVisibleLlamarButton(false);
            cronometroHabilitarBotonVolverLlamar.start();
            if (isConexion) {
                cliente.enviarNotificarNuevamente(IDENTIFICADOR_PANTALLA);
            }
        }
    }//GEN-LAST:event_lblLlamarButtonMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrarVentanilla;
    public static javax.swing.JButton btnIniciarDetenerAtencion;
    public static javax.swing.JButton btnLlamarSiguiente;
    public static javax.swing.JButton btnNoSePresento;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblAyuda;
    private javax.swing.JLabel lblExitButton;
    private javax.swing.JLabel lblHoraFecha;
    public static javax.swing.JLabel lblLlamarButton;
    private javax.swing.JLabel lblMaximizeButton;
    private javax.swing.JLabel lblMinimizeButton;
    public static javax.swing.JLabel lblNombrePaciente;
    public static javax.swing.JLabel lblNroDniPaciente;
    private javax.swing.JLabel lblTiempoTranscurrido;
    private javax.swing.JLabel lblVentanillaArea;
    private javax.swing.JPanel pnlBackground;
    private javax.swing.JPanel pnlBarraStatus;
    private javax.swing.JPanel pnlBarraSuperior;
    private javax.swing.JPanel pnlContenedorAcciones;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JTable tbTurnosAnteriores;
    // End of variables declaration//GEN-END:variables
}
