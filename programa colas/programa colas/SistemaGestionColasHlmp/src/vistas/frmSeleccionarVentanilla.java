package vistas;

import static conexion.ConexionSQL.cerrarConexion;
import static conexion.ConexionSQL.establecerConexion;
import static conexion.ConexionSQL.llamarConexion;
import static conexion.ConexionSQL.rs;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import loginSources.Usuario;
import loginSources.frmLogin;
import static loginSources.frmLogin.user;
import modelos.Area;
import static modulos.modelos.ModuleArea.*;
import static modulos.modelos.ModuleConfiguracion.*;

/**
 * Clase que contiene el formulario de Seleccionar Ventanilla.
 * 
 * @author jnxd_
 */
public class frmSeleccionarVentanilla extends javax.swing.JFrame {

    /**
     * Variable que referencia al icono de la ventana.
     */
    private ImageIcon vtnIcon;

    /**
     * Variable que sirve como banderin, para saber si la ventana a sido maximizada.
     */
    private boolean isMaximized = false;
    
    /**
     * Variable que ayuda a evitar un error de Closed Connection de SQL.
     */
    private boolean isActualizando = false;

    /**
     * Variable creada para instanciar la ruta del manual.
     */
    public String rutaManual;
    
    /**
     * Variable creada para instanciar el archivo del manual.
     */
    public File manualFile;

    /**
     * Variable que almacena el tamaño de letra.
     */
    private double tamañoLetraColumnBody = 18;
    
    /**
     * Variable que almacena el color hexadecimal de las cabecera de las tablas.
     */
    private String ColorHexFondo = "#006666";
    
    /**
     * Variable que almacena el color hexadecimal de las letras de las cabeceras de las tablas.
     */
    private String ColorHexTexto = "#ffffff";
    
    /**
     * Variable que almacena el modelo de la tabla tbVentanillasAbiertas.
     */
    private DefaultTableModel modeloTablaVentanillas;

    // ICONOS PARA LA INTERFAZ.
    public final ImageIcon iconoMaximizar30 = new ImageIcon(getClass().getResource("/icons/maximize-20.png"));
    public final ImageIcon iconoComprimir30 = new ImageIcon(getClass().getResource("/icons/compress-20.png"));

    public final ImageIcon iconoActualizarWhite30 = new ImageIcon(getClass().getResource("/icons/actualizar-white-30.png"));
    public final ImageIcon iconoActualizarBlack30 = new ImageIcon(getClass().getResource("/icons/actualizar-black-30.png"));

    // INSTANCIAS PARA EL ACCESO A LAS OTRAS VENTANAS.
    frmConfiguracion vtnConfiguracion = null;
    frmVentanillaAdmin vtnVentanillaAdmin = null;
    frmReportes vtnReportes = null;

    /**
     * Variables para obtener la posicion x & y del mouse.
     */
    int xMouse;
    int yMouse;

    /**
     * Constructor de la clase.
     * 
     */
    public frmSeleccionarVentanilla() {
        vtnIcon = new ImageIcon(this.getClass().getResource("/icons/sigco-vtnadmin-64.png"));
        initComponents();
        this.setIconImage(vtnIcon.getImage());
        maximizar();
        rutaManual = "MANUAL_DEL_SITEMA_DE_GESTION_COLAS.pdf";
        manualFile = new File(rutaManual);
        lblTituloHeader.setText("¡Bienvenido " + user.getNombreCompleto().split(" ")[0] + "!");
        agregarAreasComboBox(cbSeleccionarArea);
        modeloTablaVentanillas = (DefaultTableModel) tbVentanillasAbiertas.getModel();

        detectarAreaSeleccionada();
        refreshTablaVentanillas(true, 0);
        //añadirNumeroVentanillasComboBox(cbSeleccionarArea.getSelectedItem(), cbSeleccionarArea);

        // Validar rol de usuario
        if (user.getIdRol() == 1) {
            btnConfiguraciones.setVisible(true);
            btnReportes.setVisible(true);
        } else {
            btnConfiguraciones.setVisible(false);
            btnReportes.setVisible(false);
        }

        // Estableciendo estilos a la tabla que muestra las ventanillas creadas
        JTableHeader headerVentanillaTicket = tbVentanillasAbiertas.getTableHeader();
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
    }

    /**
     * Metodo que sirve para recargar la tabla tbVentanillasAbiertas.
     * 
     * @param filtrarTodo
     * @param idArea 
     */
    private void refreshTablaVentanillas(boolean filtrarTodo, int idArea) {
        try {
            establecerConexion();
            modeloTablaVentanillas.setRowCount(0);
            CallableStatement cst;
            Object[] fila = new Object[9];
            cst = llamarConexion.prepareCall("{call [MostrarDatosVentanilla](?,?)}");
            if (filtrarTodo) {
                cst.setInt(1, 0);
                cst.setInt(2, idArea);
            } else {
                cst.setInt(1, 1);
                cst.setInt(2, idArea);
            }

            rs = cst.executeQuery();

            while (rs.next()) {
                fila[0] = rs.getInt(1);
                fila[1] = rs.getString(2);
                fila[2] = rs.getString(3);
                fila[3] = rs.getString(4);
                fila[4] = rs.getString(6);
                fila[5] = rs.getString(7);
                fila[6] = rs.getString(5);
                fila[7] = rs.getString(9);
                fila[8] = rs.getString(10);
                modeloTablaVentanillas.addRow(fila);
            }

            tbVentanillasAbiertas.setModel(modeloTablaVentanillas);
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que sirve para detectar el area seleccionada del JComboBox cbSeleccionarArea.
     */
    private void detectarAreaSeleccionada() {
        cbSeleccionarArea.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (!isActualizando) {
                    if (cbSeleccionarArea.getSelectedIndex() != 0) {
                        agregarNumeroVentanillasComboBox(cbSeleccionarArea.getSelectedItem(), cbSeleccionarNumero);
                        Area area = (Area) cbSeleccionarArea.getSelectedItem();
                        refreshTablaVentanillas(false, area.getIdArea());
                    } else {
                        cbSeleccionarNumero.removeAllItems();
                        cbSeleccionarNumero.addItem("Seleccionar...");
                        refreshTablaVentanillas(true, 0);
                    }
                }
            }
        });
    }
    
    /**
     * Metodo para validar si los datos ingresados por parametros son iguales.
     * 
     * @param idUsuarioVentanilla
     * @param idUsuarioLogeado
     * @return 
     */
    private boolean validarUsuarioVentanilla(int idUsuarioVentanilla, int idUsuarioLogeado) {
        return idUsuarioVentanilla == idUsuarioLogeado;
    }

    /**
     * Metodo que sirve para actualizar las tablas y JComboBox.
     */
    private void actualizarTodo() {
        this.isActualizando = true;
        agregarAreasComboBox(cbSeleccionarArea);
        cbSeleccionarNumero.removeAllItems();
        cbSeleccionarNumero.addItem("Seleccionar...");
        this.isActualizando = false;
    }

    /**
     * Metodo que sirve para abrir la vista de Configuraciones.
     */
    private void abrirConfiguraciones() {
        if (this.vtnConfiguracion == null || this.vtnConfiguracion.isDisposed) {
            this.vtnConfiguracion = new frmConfiguracion();
            this.vtnConfiguracion.pack();
            this.vtnConfiguracion.setLocationRelativeTo(null);
            this.vtnConfiguracion.setVisible(true);
        } else {
            this.vtnConfiguracion.toFront();
            this.vtnConfiguracion.repaint();
        }
    }

    /**
     * Metodo que sirve para abrir la vista de Reportes.
     */
    private void abrirFormReportes() {
        if (this.vtnReportes == null || this.vtnReportes.isDisposed) {
            this.vtnReportes = new frmReportes();
//            this.vtnReportes.pack();
            this.vtnReportes.setLocationRelativeTo(null);
            this.vtnReportes.setVisible(true);
        } else {
            this.vtnReportes.toFront();
            this.vtnReportes.repaint();
        }
    }

    /**
     * Metodo para abrir la vista de ventanilla admin.
     * 
     * @param ventanilla
     * @param area
     * @param idVentanilla
     * @throws InterruptedException 
     */
    private void abrirFormVentanillaAdmin(String ventanilla, Area area, int idVentanilla) throws InterruptedException {
        if (this.vtnReportes != null) {
            this.vtnReportes.dispose();
        }
        if (this.vtnConfiguracion != null) {
            this.vtnConfiguracion.dispose();
        }
        this.vtnVentanillaAdmin = new frmVentanillaAdmin(ventanilla, area, idVentanilla);
        this.vtnVentanillaAdmin.setLocationRelativeTo(null);
        this.vtnVentanillaAdmin.setVisible(true);
    }

    /**
     * Metodo para cerrar la ventanilla desde base de datos.
     * 
     * @param idVentanilla 
     */
    private void cerrarVentanilla(int idVentanilla) {
        try {
            establecerConexion();
//            System.out.println(idVentanilla);
            CallableStatement cst;
            cst = llamarConexion.prepareCall("{call [ActualizarEstadoVentanillas](?,?)}");
            cst.setInt(1, idVentanilla);
            cst.setInt(2, 0);
            cst.executeUpdate();
//            System.out.println("Ventanilla Cerrada");
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo para abrir la ventanilla desde base de datos.
     * 
     * @param idVentanilla 
     */
    private void abrirVentanilla(int idVentanilla) {
        try {
            establecerConexion();
            CallableStatement cst;
            cst = llamarConexion.prepareCall("{call [ActualizarEstadoVentanillas](?,?)}");
            cst.setInt(1, idVentanilla);
            cst.setInt(2, 1);

            cst.executeUpdate();
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Metodo para abrir y crerar una ventanilla, tanto en base de datos y en el mismo programa.
     * 
     * @param nroVentanilla
     * @param area
     * @throws InterruptedException 
     */
    private void aperturarVentanilla(int nroVentanilla, Area area) throws InterruptedException {
        try {
            establecerConexion();
            CallableStatement cst;
//            ResultSet rs2;
            int idArea = area.getIdArea();
            String ventanilla = "Ventanilla " + nroVentanilla;
            cst = llamarConexion.prepareCall("{call [AbrirInsertarVentanilla](?,?,?,?,?,?,?)}");
            cst.setInt(1, nroVentanilla);
            cst.setInt(2, idArea);
            cst.setInt(3, user.getIdUusario());
            cst.setString(4, getIpDispositivo());
            cst.registerOutParameter(5, Types.INTEGER);
            cst.registerOutParameter(6, Types.INTEGER);
            cst.registerOutParameter(7, Types.INTEGER);

            cst.executeUpdate();

            int idVentanilla = cst.getInt(5);
            int codResultado = cst.getInt(6);
            int idUsuario = cst.getInt(7);
//            System.out.println("Supuesto usuario devuelto: " + idUsuario);
            switch (codResultado) {
                case -1:
                    if (user.getIdUusario() == idUsuario) {
                        int continuarVentana = JOptionPane.showConfirmDialog(
                                this,
                                "La ventanilla " + nroVentanilla + " ya está creada, ¿Desea abrirla y continuar a la ventana admin?",
                                "Alerta",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );
                        if (continuarVentana == JOptionPane.OK_OPTION) {
                            abrirFormVentanillaAdmin(ventanilla, area, idVentanilla);
                            this.dispose();
                        } else {
                            cerrarVentanilla(idVentanilla);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Usted no se encuentra asignado a esta ventanilla", "Alerta", JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                case 1:
                    JOptionPane.showMessageDialog(this, "Seleccione una opcion valida.", "Alerta", JOptionPane.ERROR_MESSAGE);
                    break;
                case 0:
                    refreshTablaVentanillas(false, idArea);
                    abrirFormVentanillaAdmin(ventanilla, area, idVentanilla);
                    this.dispose();
//                    System.out.println("Muestra ventana admin");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(this, "Usted ya ha creado una ventanilla, para abrir una nueva debe cerrar la que está abierta.", "Alerta", JOptionPane.WARNING_MESSAGE);
                    refreshTablaVentanillas(false, idArea);
                    break;
                case 3:
                    JOptionPane.showMessageDialog(this, "No puede aperturar la ventanilla seleccionada desde este ordenador.\n"
                            + "Intente desde otro ordenador o intente abrir la ventanilla\nasignada a este ordenador.", "Alerta", JOptionPane.WARNING_MESSAGE);
                    refreshTablaVentanillas(false, idArea);
                    break;
                case 4:
                    JOptionPane.showMessageDialog(this, "La ventanilla no pertenece a este ordenador, intente abrir otra ventanilla o desde el ordenador correcto.", "Alerta", JOptionPane.WARNING_MESSAGE);
                    refreshTablaVentanillas(false, idArea);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Ha ocurrido un error.", "Alerta", JOptionPane.ERROR_MESSAGE);
                    break;
            }
//            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @deprecated 
     * @return 
     */
    private boolean validarVentanillasAbiertasPorMismoUsuario() {
        boolean existe = false;
        for (int i = 0; i < tbVentanillasAbiertas.getRowCount(); i++) {
            int idUsuario = Integer.parseInt(tbVentanillasAbiertas.getValueAt(i, 0).toString());
            String estadoVentanilla = tbVentanillasAbiertas.getValueAt(i, 4).toString();
            if (validarUsuarioVentanilla(idUsuario, user.getIdUusario()) && estadoVentanilla.equals("Abierto")) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    /**
     * Metodo que permite validar si la ventanilla seleccionada es la misma del dispositivo.
     * 
     * @param ipSeleccionado
     * @return 
     */
    private boolean validarVentanillasPorIp(String ipSeleccionado) {
        return ipSeleccionado.equals(getIpDispositivo());
    }

    /**
     * Metodo para obtener la ip del dispositivo.
     * 
     * @return 
     */
    private String getIpDispositivo() {
        try {
            InetAddress ipLocalHost = InetAddress.getLocalHost();
            String ipv4 = ipLocalHost.getHostAddress();
            return ipv4;
        } catch (UnknownHostException ex) {
            Logger.getLogger(frmSeleccionarVentanilla.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
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
        pnlHeader = new javax.swing.JPanel();
        lblTituloHeader = new javax.swing.JLabel();
        lblSalir = new javax.swing.JLabel();
        lblMinimizeButton = new javax.swing.JLabel();
        lblExitButton = new javax.swing.JLabel();
        lblMaximizeButton = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cbSeleccionarArea = new javax.swing.JComboBox<>();
        cbSeleccionarNumero = new javax.swing.JComboBox<>();
        btnAperturarVentanilla = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        btnConfiguraciones = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbVentanillasAbiertas = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        lblActualizarButton = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblAyudaAperturarVentanilla = new javax.swing.JLabel();
        btnReportes = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
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

        lblTituloHeader.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        lblTituloHeader.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloHeader.setText("¡Bienvenido Jorge!");

        lblSalir.setBackground(new java.awt.Color(204, 0, 0));
        lblSalir.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblSalir.setForeground(new java.awt.Color(255, 255, 255));
        lblSalir.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-logout-32_1.png"))); // NOI18N
        lblSalir.setText("Salir");
        lblSalir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblSalir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSalirMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblSalirMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblSalirMouseExited(evt);
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

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lblTituloHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 516, Short.MAX_VALUE)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                        .addComponent(lblMinimizeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblMaximizeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                        .addComponent(lblSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblMinimizeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblExitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblMaximizeButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))
                    .addComponent(lblTituloHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "APERTURAR VENTANILLA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        jPanel1.setOpaque(false);

        cbSeleccionarArea.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cbSeleccionarArea.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar..." }));

        cbSeleccionarNumero.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cbSeleccionarNumero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar..." }));

        btnAperturarVentanilla.setBackground(new java.awt.Color(255, 255, 255));
        btnAperturarVentanilla.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnAperturarVentanilla.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/open-sign-48.png"))); // NOI18N
        btnAperturarVentanilla.setText("Aperturar");
        btnAperturarVentanilla.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 255), 2));
        btnAperturarVentanilla.setBorderPainted(false);
        btnAperturarVentanilla.setFocusable(false);
        btnAperturarVentanilla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAperturarVentanillaActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Área:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Número:");

        btnConfiguraciones.setBackground(new java.awt.Color(255, 255, 255));
        btnConfiguraciones.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConfiguraciones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/configuration-24.png"))); // NOI18N
        btnConfiguraciones.setText("Configuraciones");
        btnConfiguraciones.setFocusable(false);
        btnConfiguraciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfiguracionesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbSeleccionarArea, 0, 271, Short.MAX_VALUE)
                    .addComponent(btnAperturarVentanilla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbSeleccionarNumero, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2)
                    .addComponent(btnConfiguraciones, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbSeleccionarArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbSeleccionarNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAperturarVentanilla, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 105, Short.MAX_VALUE)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConfiguraciones)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "VENTANILLAS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        jPanel2.setOpaque(false);

        tbVentanillasAbiertas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tbVentanillasAbiertas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "idUsuario", "Codigo", "Nombre", "Area", "Estado", "Fecha", "Usuario", "idArea", "ipDispositivoLogeado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbVentanillasAbiertas.setRowHeight(20);
        tbVentanillasAbiertas.setShowVerticalLines(false);
        tbVentanillasAbiertas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbVentanillasAbiertasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbVentanillasAbiertas);
        if (tbVentanillasAbiertas.getColumnModel().getColumnCount() > 0) {
            tbVentanillasAbiertas.getColumnModel().getColumn(0).setMinWidth(0);
            tbVentanillasAbiertas.getColumnModel().getColumn(0).setPreferredWidth(0);
            tbVentanillasAbiertas.getColumnModel().getColumn(0).setMaxWidth(0);
            tbVentanillasAbiertas.getColumnModel().getColumn(1).setMinWidth(0);
            tbVentanillasAbiertas.getColumnModel().getColumn(1).setPreferredWidth(0);
            tbVentanillasAbiertas.getColumnModel().getColumn(1).setMaxWidth(0);
            tbVentanillasAbiertas.getColumnModel().getColumn(2).setMinWidth(100);
            tbVentanillasAbiertas.getColumnModel().getColumn(2).setPreferredWidth(100);
            tbVentanillasAbiertas.getColumnModel().getColumn(2).setMaxWidth(100);
            tbVentanillasAbiertas.getColumnModel().getColumn(3).setMinWidth(150);
            tbVentanillasAbiertas.getColumnModel().getColumn(3).setPreferredWidth(150);
            tbVentanillasAbiertas.getColumnModel().getColumn(3).setMaxWidth(150);
            tbVentanillasAbiertas.getColumnModel().getColumn(4).setMinWidth(100);
            tbVentanillasAbiertas.getColumnModel().getColumn(4).setPreferredWidth(100);
            tbVentanillasAbiertas.getColumnModel().getColumn(4).setMaxWidth(100);
            tbVentanillasAbiertas.getColumnModel().getColumn(5).setMinWidth(100);
            tbVentanillasAbiertas.getColumnModel().getColumn(5).setPreferredWidth(100);
            tbVentanillasAbiertas.getColumnModel().getColumn(5).setMaxWidth(100);
            tbVentanillasAbiertas.getColumnModel().getColumn(7).setMinWidth(0);
            tbVentanillasAbiertas.getColumnModel().getColumn(7).setPreferredWidth(0);
            tbVentanillasAbiertas.getColumnModel().getColumn(7).setMaxWidth(0);
            tbVentanillasAbiertas.getColumnModel().getColumn(8).setMinWidth(0);
            tbVentanillasAbiertas.getColumnModel().getColumn(8).setPreferredWidth(0);
            tbVentanillasAbiertas.getColumnModel().getColumn(8).setMaxWidth(0);
        }

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setText("Nota: Si ha creado una ventanilla, verifique si se encuentra en la tabla y de doble clic sobre su ventanilla para abrirla");

        lblActualizarButton.setBackground(new java.awt.Color(0, 102, 102));
        lblActualizarButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblActualizarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/actualizar-black-30.png"))); // NOI18N
        lblActualizarButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblActualizarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblActualizarButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblActualizarButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblActualizarButtonMouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblActualizarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(lblActualizarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel3.setOpaque(false);

        lblAyudaAperturarVentanilla.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblAyudaAperturarVentanilla.setText("<html><u>¿Cómo aperturo una ventanilla?<u></html>");
        lblAyudaAperturarVentanilla.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAyudaAperturarVentanilla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAyudaAperturarVentanillaMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAyudaAperturarVentanillaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAyudaAperturarVentanillaMouseExited(evt);
            }
        });

        btnReportes.setBackground(new java.awt.Color(255, 255, 255));
        btnReportes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnReportes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/reportes-24.png"))); // NOI18N
        btnReportes.setText("Reportes");
        btnReportes.setFocusable(false);
        btnReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAyudaAperturarVentanilla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnReportes)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAyudaAperturarVentanilla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReportes))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlBackgroundLayout = new javax.swing.GroupLayout(pnlBackground);
        pnlBackground.setLayout(pnlBackgroundLayout);
        pnlBackgroundLayout.setHorizontalGroup(
            pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBackgroundLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlBackgroundLayout.setVerticalGroup(
            pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBackgroundLayout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBackgroundLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    }//GEN-LAST:event_formWindowClosing

    private void lblAyudaAperturarVentanillaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaAperturarVentanillaMouseEntered
        lblAyudaAperturarVentanilla.setForeground(Color.decode("#006666"));
    }//GEN-LAST:event_lblAyudaAperturarVentanillaMouseEntered

    private void lblAyudaAperturarVentanillaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaAperturarVentanillaMouseExited
        lblAyudaAperturarVentanilla.setForeground(Color.black);
    }//GEN-LAST:event_lblAyudaAperturarVentanillaMouseExited

    private void lblSalirMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSalirMouseEntered
        lblSalir.setOpaque(true);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblSalirMouseEntered

    private void lblSalirMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSalirMouseExited
        lblSalir.setOpaque(false);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblSalirMouseExited

    private void lblSalirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSalirMouseClicked
        user.limpiarDatos();
        if (this.vtnConfiguracion != null) {
            this.vtnConfiguracion.dispose();
        }
        if (this.vtnReportes != null) {
            this.vtnReportes.dispose();
        }

        this.dispose();

        frmLogin vtnLogin = new frmLogin();
        vtnLogin.pack();
        vtnLogin.setLocationRelativeTo(this);
        vtnLogin.setVisible(true);
    }//GEN-LAST:event_lblSalirMouseClicked

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

    private void lblExitButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblExitButtonMouseExited
        lblExitButton.setOpaque(false);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblExitButtonMouseExited

    private void lblExitButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblExitButtonMouseEntered
        lblExitButton.setOpaque(true);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblExitButtonMouseEntered

    private void lblExitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblExitButtonMouseClicked
        cerrarConexion();
        System.exit(0);
    }//GEN-LAST:event_lblExitButtonMouseClicked

    private void lblMinimizeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinimizeButtonMouseClicked
        this.setState(Frame.ICONIFIED);
    }//GEN-LAST:event_lblMinimizeButtonMouseClicked

    private void lblAyudaAperturarVentanillaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaAperturarVentanillaMouseClicked
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(manualFile);
            } catch (IOException ex) {
                Logger.getLogger(frmLogin.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Su sistema operativo no puede abrir el manual de uso del sistema. ", "Alerta", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_lblAyudaAperturarVentanillaMouseClicked

    private void btnAperturarVentanillaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAperturarVentanillaActionPerformed
        if (cbSeleccionarArea.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un area", "Alerta", JOptionPane.WARNING_MESSAGE);
        } else if (cbSeleccionarNumero.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un numero ventanilla", "Alerta", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                int nroVentanilla = Integer.parseInt(cbSeleccionarNumero.getSelectedItem().toString());
                Area area = (Area) cbSeleccionarArea.getSelectedItem();
                aperturarVentanilla(nroVentanilla, area);
            } catch (InterruptedException ex) {
                Logger.getLogger(frmSeleccionarVentanilla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnAperturarVentanillaActionPerformed

    private void btnReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportesActionPerformed
        abrirFormReportes();
    }//GEN-LAST:event_btnReportesActionPerformed

    private void lblMaximizeButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMaximizeButtonMouseExited
        lblMaximizeButton.setOpaque(false);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblMaximizeButtonMouseExited

    private void lblMaximizeButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMaximizeButtonMouseEntered
        lblMaximizeButton.setOpaque(true);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblMaximizeButtonMouseEntered

    private void lblMaximizeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMaximizeButtonMouseClicked
        if (!isMaximized) {
            maximizar();
        } else {
            contraer();
        }
    }//GEN-LAST:event_lblMaximizeButtonMouseClicked

    private void pnlHeaderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlHeaderMousePressed
        if (!isMaximized) {
            xMouse = evt.getX();
            yMouse = evt.getY();
        }
    }//GEN-LAST:event_pnlHeaderMousePressed

    private void pnlHeaderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlHeaderMouseDragged
        if (!isMaximized) {
            int x = evt.getXOnScreen();
            int y = evt.getYOnScreen();
            this.setLocation(x - xMouse, y - yMouse);
        }
    }//GEN-LAST:event_pnlHeaderMouseDragged

    private void btnConfiguracionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfiguracionesActionPerformed
        abrirConfiguraciones();
    }//GEN-LAST:event_btnConfiguracionesActionPerformed

    private void lblActualizarButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblActualizarButtonMouseEntered
        lblActualizarButton.setIcon(iconoActualizarWhite30);
        lblActualizarButton.setOpaque(true);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblActualizarButtonMouseEntered

    private void lblActualizarButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblActualizarButtonMouseExited
        lblActualizarButton.setIcon(iconoActualizarBlack30);
        lblActualizarButton.setOpaque(false);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblActualizarButtonMouseExited

    private void lblActualizarButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblActualizarButtonMouseClicked
        actualizarTodo();
        refreshTablaVentanillas(true, 0);
    }//GEN-LAST:event_lblActualizarButtonMouseClicked

    private void tbVentanillasAbiertasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbVentanillasAbiertasMouseClicked
        
        // ABRIR VENTANILLA DESDE LA TABLA, DANDO DOBLE CLIC EN LA FILA DE LA VENTANILLA.
        
        //Valida doble clic
        if (evt.getClickCount() == 2) {
            //Obtiene informacion de la fila.
            int row = tbVentanillasAbiertas.getSelectedRow();
            int idUsuario = Integer.parseInt(tbVentanillasAbiertas.getValueAt(row, 0).toString());
            int idVentanilla = Integer.parseInt(tbVentanillasAbiertas.getValueAt(row, 1).toString());
            String nombreVentanilla = tbVentanillasAbiertas.getValueAt(row, 2).toString();
            String nombreArea = tbVentanillasAbiertas.getValueAt(0, 3).toString();
            String ipVentanilla = tbVentanillasAbiertas.getValueAt(row, 7).toString();
            int idArea = Integer.parseInt(tbVentanillasAbiertas.getValueAt(row, 8).toString());
            Area area = new Area(idArea,nombreArea);
            
            // Validar usuario logeado con el id del usuario que abrio la ventanilla seleccionada.
            if (validarUsuarioVentanilla(idUsuario, user.getIdUusario())) {
                // Validar ip del dispositivo con la ip del dispositivo de donde se abrio la ventanilla seleccionada.
                if (validarVentanillasPorIp(ipVentanilla)) {
                    try {
                        // Abrir ventanilla.
                        abrirVentanilla(idVentanilla);
                        abrirFormVentanillaAdmin(nombreVentanilla, area, idVentanilla);
                        this.dispose();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(frmSeleccionarVentanilla.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No puede abrir esta ventanilla desde este ordenador\nporque se aperturo desde otro ordenador en el\nturno actual.", "Alerta", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Usted no se encuentra asignado a esta ventanilla", "Alerta", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_tbVentanillasAbiertasMouseClicked

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        // Cambiar el tamaño de la vista cuando se cambia de pantalla, solo si esta maximizado
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

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAperturarVentanilla;
    private javax.swing.JButton btnConfiguraciones;
    private javax.swing.JButton btnReportes;
    private javax.swing.JComboBox<Object> cbSeleccionarArea;
    private javax.swing.JComboBox<String> cbSeleccionarNumero;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblActualizarButton;
    private javax.swing.JLabel lblAyudaAperturarVentanilla;
    private javax.swing.JLabel lblExitButton;
    private javax.swing.JLabel lblMaximizeButton;
    private javax.swing.JLabel lblMinimizeButton;
    private javax.swing.JLabel lblSalir;
    private javax.swing.JLabel lblTituloHeader;
    private javax.swing.JPanel pnlBackground;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JTable tbVentanillasAbiertas;
    // End of variables declaration//GEN-END:variables
}
