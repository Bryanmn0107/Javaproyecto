/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import loginSources.frmLogin;
import static loginSources.frmLogin.user;
import modelos.Area;
import static modulos.ModuleHoraFecha.formatearFecha;
import static modulos.modelos.ModuleArea.agregarAreasComboBox;
import static modulos.modelos.ModuleConfiguracion.agregarNumeroVentanillasComboBox;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import reporteSources.Reporte;
import reporteSources.ReporteTickets;

/**
 *
 * @author jnxd_
 */
public class frmReportes extends javax.swing.JFrame {

    /**
     * Variable que referencia al icono de la ventana.
     */
    private ImageIcon vtnIcon;
    
    /**
     * Variable que almacena el modelo de la tabla tbReporteVentanilla.
     */
    DefaultTableModel modeloTablaReporteVentanilla;
    
    /**
     * Variable que almacena el modelo de la tabla tbReporteTickets.
     */
    DefaultTableModel modeloTablaReporteTickets;
    
    /**
     * Variable que ayuda a evitar un error de Closed Connection de SQL.
     */
    private boolean isActualizando = false;
    
    /**
     * Variable que sirve como banderin, para saber si la ventana a sido maximizada.
     */
    private boolean isMaximized = false;
    
    /**
     * Variable que sirve como banderin, para saber si la ventana a sido cerrada o ocultada con this.dispose().
     */
    public boolean isDisposed = false;

    /**
     * Variable creada para instanciar la ruta del manual.
     */
    public String rutaManual;
    
    /**
     * Variable creada para instanciar el archivo del manual.
     */
    public File manualFile;

    // ICONOS PARA LOS BOTONES.
    public final ImageIcon iconoMaximizar30 = new ImageIcon(getClass().getResource("/icons/maximize-20.png"));
    public final ImageIcon iconoComprimir30 = new ImageIcon(getClass().getResource("/icons/compress-20.png"));
    
    public final ImageIcon iconoActualizarWhite30 = new ImageIcon(getClass().getResource("/icons/actualizar-white-30.png"));
    public final ImageIcon iconoActualizarBlack30 = new ImageIcon(getClass().getResource("/icons/actualizar-black-30.png"));

    public final ImageIcon iconoLimpiarBlanco30 = new ImageIcon(getClass().getResource("/icons/clean-blanco-30.png"));
    public final ImageIcon iconoLimpiarNegro30 = new ImageIcon(getClass().getResource("/icons/clean-negro-30.png"));

    /**
     * Variable que almacena el tamaño de letra.
     */
    private double tamañoLetraColumnBody = 15;
    
    /**
     * Variable que almacena el color hexadecimal de las cabecera de las tablas.
     */
    private String ColorHexFondo = "#006666";
    
    /**
     * Variable que almacena el color hexadecimal de las letras de las cabeceras de las tablas.
     */
    private String ColorHexTexto = "#ffffff";
    
    /**
     * Variable que almacena la cantidad de tickets no preferenciales.
     */
    int filasNoPreferenciales;
    
    /**
     * Variable que almacena la cantidad de tickets preferenciales.
     */
    int filaspreferenciales;
    
    /**
     * Variable que almacena la cantidad de tickets.
     */
    int contadorFilas;

    /**
     * Variables para obtener la posicion x & y del mouse.
     */
    int xMouse;
    int yMouse;

    /**
     * Constructor de la clase.
     */
    public frmReportes() {
        vtnIcon = new ImageIcon(this.getClass().getResource("/icons/sigco-vtnadmin-64.png"));
        initComponents();
        this.setIconImage(vtnIcon.getImage());
        rutaManual = "MANUAL_DEL_SITEMA_DE_GESTION_COLAS.pdf";
        manualFile = new File(rutaManual);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        modeloTablaReporteVentanilla = (DefaultTableModel) tbReporteVentanilla.getModel();
        modeloTablaReporteTickets = (DefaultTableModel) tbReporteTickets.getModel();
        
        agregarAreasComboBox(cbArea);

        // CAMBIO DE COLOR DE LA TABLA tbReportesVentanilla.
        JTableHeader headerReporteVentanilla = tbReporteVentanilla.getTableHeader();
        headerReporteVentanilla.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setPreferredSize(new Dimension(getWidth(), getHeight() + 50));
                setFont(new Font("Arial", 1, (int) tamañoLetraColumnBody));
                setBackground(Color.decode(ColorHexFondo));
                setForeground(Color.decode(ColorHexTexto));
                return this;
            }
        });

        // CAMBIO DE COLOR DE LA TABLA tbReporteTickets
        JTableHeader headerReporteTickets = tbReporteTickets.getTableHeader();
        headerReporteTickets.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setPreferredSize(new Dimension(getWidth(), getHeight() + 50));
                setFont(new Font("Arial", 1, (int) tamañoLetraColumnBody));
                setBackground(Color.decode(ColorHexFondo));
                setForeground(Color.decode(ColorHexTexto));
                return this;
            }
        });

        detectarAreaSeleccionada();
        maximizarVentana();
    }

    /**
     * Metodo que sirve para maximizar la ventana.
     */
    private void maximizarVentana() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
        screenSize.height -= screenInsets.bottom;
        this.setLocation(0, 0);
        this.setSize(screenSize);
        lblMaximizeButton.setIcon(iconoComprimir30);
        this.isMaximized = true;
        this.revalidate();
        this.repaint();
    }
    
    /**
     * Metodo que sirve para detectar el area seleccionada del JComboBox cbArea.
     */
    private void detectarAreaSeleccionada() {
        cbArea.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (!isActualizando) {
                    if (cbArea.getSelectedIndex() != 0) {
                        agregarNumeroVentanillasComboBox(cbArea.getSelectedItem(), cbVentanilla);
                        Area area = (Area) cbArea.getSelectedItem();
                        mostrarTablaVentanillas();
                        //refreshTablaVentanillas(true, seleccionarArea(nombreArea));
                    } else {
                        cbVentanilla.removeAllItems();
                        cbVentanilla.addItem("Seleccionar...");
                        mostrarTablaVentanillas();
                        //refreshTablaVentanillas(false, seleccionarArea(cbSeleccionarArea.getSelectedItem().toString()));
                    }
                }
            }
        });
    }

    /**
     * Metodo que sirve para mostrar los datos en la tabla tbReporteVentanillas.
     */
    private void mostrarTablaVentanillas() {
        try {
            establecerConexion();
            String nombreEmpleado = txtNombreEmpleado.getText();
            String nombreArea;
            String nroVentanilla;
            String fechaInicio;
            String fechaFin;
            if (cbArea.getSelectedIndex() > 0) {
                Area area = (Area) cbArea.getSelectedItem();
                nombreArea = area.getNombre();
            } else {
                nombreArea = "";
            }

            if (cbVentanilla.getSelectedIndex() > 0) {
                nroVentanilla = cbVentanilla.getSelectedItem().toString();
            } else {
                nroVentanilla = "";
            }
            if (dcFechaInicio.getDate() != null) {
                fechaInicio = formatearFecha(dcFechaInicio.getDate(), "dd/MM/yyyy");
            } else {
                fechaInicio = "";
            }
            if (dcFechaFin.getDate() != null) {
                fechaFin = formatearFecha(dcFechaFin.getDate(), "dd/MM/yyyy");
            } else {
                fechaFin = "";
            }

            modeloTablaReporteVentanilla.setRowCount(0);
            CallableStatement cst;
            Object[] filas = new Object[11];
            cst = llamarConexion.prepareCall("{call [reporteVentanilla_Test](?,?,?,?,?)}");

            cst.setString(1, nombreEmpleado);
            cst.setString(2, nombreArea);
            cst.setString(3, nroVentanilla);
            cst.setString(4, fechaInicio);
            cst.setString(5, fechaFin);
            rs = cst.executeQuery();
            
            while (rs.next()) {
                filas[0] = rs.getString(1);
                filas[1] = rs.getString(5);
                filas[2] = rs.getString(3);
                filas[3] = rs.getString(4).split(" ")[1];
                filas[4] = rs.getString(6);
                filas[5] = rs.getString(7);
                filas[6] = rs.getString(8);
                filas[7] = rs.getString(9);
                filas[8] = rs.getString(10);
                filas[9] = rs.getString(11);
                filas[10] = rs.getString(2);
                modeloTablaReporteVentanilla.addRow(filas);
            }
            tbReporteVentanilla.setModel(modeloTablaReporteVentanilla);
            modeloTablaReporteVentanilla.fireTableDataChanged();
            cerrarConexion();
        } catch (SQLException e) {
            // Manejar la excepción
            e.printStackTrace();
        }
    }

    /**
     * Metodo que sirve para mostrar los datos en la tabla tbReporteTickets.
     */
    private boolean mostrarTablaTurnos(int idVentanilla) {
        try {
            establecerConexion();
            boolean obtuboResultados = false;
            modeloTablaReporteTickets.setRowCount(0);
            CallableStatement cst;
            Object[] filas = new Object[8];
            cst = llamarConexion.prepareCall("{call [flitrarTickestVEntanilla](?)}");
            cst.setInt(1, idVentanilla);
            rs = cst.executeQuery();
            contadorFilas = 0;
            filaspreferenciales = 0;
            filasNoPreferenciales = 0;
            while (rs.next()) {
                obtuboResultados = true;
                filas[0] = rs.getString(3);
                filas[1] = rs.getString(4);
                filas[2] = rs.getString(5);
                filas[3] = rs.getString(6);
                filas[4] = rs.getString(7);
                filas[5] = rs.getString(8);
                filas[6] = rs.getString(9);
                filas[7] = rs.getString(10);
                modeloTablaReporteTickets.addRow(filas);
                contadorFilas++;
                 // Verificar la condición
                if (rs.getString(7).equals("Si")) { 
                    filaspreferenciales++;
                } else {
                    filasNoPreferenciales++;
                }
            }
            tbReporteTickets.setModel(modeloTablaReporteTickets);
            cerrarConexion();
            return obtuboResultados;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Metodo que sirve para generar un reporte de tickets.
     * 
     * @param empleado
     * @param area
     * @param nroVentanilla 
     */
    public void generarReporteTickets(String empleado, String area, String nroVentanilla) {
        try {
            JasperReport jasperReporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/ReporteTickets/ReporteTickes.jasper"));

            // Crear una lista de objetos claseReporte a partir de los datos en la tabla
            ArrayList<ReporteTickets> lista = new ArrayList<>();
            for (int i = 0; i < tbReporteTickets.getRowCount(); i++) {
                // Simplificar la obtención de valores
                ReporteTickets reportet = new ReporteTickets(
                        tbReporteTickets.getValueAt(i, 0) == null ? "" : tbReporteTickets.getValueAt(i, 0).toString(),
                        tbReporteTickets.getValueAt(i, 1) == null ? "" : tbReporteTickets.getValueAt(i, 1).toString(),
                        tbReporteTickets.getValueAt(i, 2) == null ? "" : tbReporteTickets.getValueAt(i, 2).toString(),
                        tbReporteTickets.getValueAt(i, 3) == null ? "" : tbReporteTickets.getValueAt(i, 3).toString(),
                        tbReporteTickets.getValueAt(i, 4) == null ? "" : tbReporteTickets.getValueAt(i, 4).toString(),
                        tbReporteTickets.getValueAt(i, 5) == null ? "" : tbReporteTickets.getValueAt(i, 5).toString(),
                        tbReporteTickets.getValueAt(i, 6) == null ? "" : tbReporteTickets.getValueAt(i, 6).toString(),
                        tbReporteTickets.getValueAt(i, 7) == null ? "" : tbReporteTickets.getValueAt(i, 7).toString()
                );
                lista.add(reportet);
            }

            // Crear parámetros para el informe
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("nombretext", empleado);
            parametros.put("ventanillatext", nroVentanilla);
            parametros.put("areatext", area);
            parametros.put("usuariologeado", user.getNombreCompleto());
            
            parametros.put("totaltxt",String.valueOf(contadorFilas));
            parametros.put("preferencialtxt",String.valueOf(filaspreferenciales));
            parametros.put("nopreferencialtxt", String.valueOf(filasNoPreferenciales));
            
            // Llenar el informe con datos y parámetros
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReporte, parametros, new JRBeanCollectionDataSource(lista));

            // Mostrar el informe
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Informe de Ventanillas");
            viewer.setVisible(true);

        } catch (JRException e) {
            JOptionPane.showMessageDialog(this, "Error al generar el informe: " + e, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error general: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que genera un reporte general.
     */
    public void generarReporte() {
        try {
            JasperReport jasperReporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/reporteSourcesj/rptReporteVentanilla.jasper"));

            // Crear una lista de objetos claseReporte a partir de los datos en la tabla
            ArrayList<Reporte> lista = new ArrayList<>();
            for (int i = 0; i < tbReporteVentanilla.getRowCount(); i++) {
                
                Reporte reporte = new Reporte(
                        tbReporteVentanilla.getValueAt(i, 1) == null ? "" : tbReporteVentanilla.getValueAt(i, 1).toString(),
                        tbReporteVentanilla.getValueAt(i, 2) == null ? "" : tbReporteVentanilla.getValueAt(i, 2).toString(),
                        tbReporteVentanilla.getValueAt(i, 3) == null ? "" : tbReporteVentanilla.getValueAt(i, 3).toString(),
                        tbReporteVentanilla.getValueAt(i, 4) == null ? "" : tbReporteVentanilla.getValueAt(i, 4).toString(),
                        tbReporteVentanilla.getValueAt(i, 5) == null ? "" : tbReporteVentanilla.getValueAt(i, 5).toString(),
                        tbReporteVentanilla.getValueAt(i, 6) == null ? "" : tbReporteVentanilla.getValueAt(i, 6).toString(),
                        tbReporteVentanilla.getValueAt(i, 7) == null ? "" : tbReporteVentanilla.getValueAt(i, 7).toString(),
                        tbReporteVentanilla.getValueAt(i, 8) == null ? "" : tbReporteVentanilla.getValueAt(i, 8).toString(),
                        tbReporteVentanilla.getValueAt(i, 9) == null ? "" : tbReporteVentanilla.getValueAt(i, 9).toString(),
                        tbReporteVentanilla.getValueAt(i, 10) == null ? "" : tbReporteVentanilla.getValueAt(i, 10).toString()
                );
                lista.add(reporte);
            }

            // Crear parámetros para el informe
            String nombreEmpleado;
            HashMap<String, Object> parametros = new HashMap<>();
            if (txtNombreEmpleado.getText().isEmpty()) {
                nombreEmpleado = "";
            } else {
                nombreEmpleado = txtNombreEmpleado.getText();
            }
            parametros.put("nombretext", nombreEmpleado);

            String Ventanilla = cbVentanilla.getSelectedItem().toString();
            if ("Seleccionar...".equals(Ventanilla.trim())) {
                Ventanilla = "";
            }
            parametros.put("ventanillatext", Ventanilla);
            String area = cbArea.getSelectedItem().toString();
            if ("Seleccionar...".equals(area)) {
                area = "";  // Asigna una cadena vacía si es igual a "Seleccionar área..."
            }
            parametros.put("areatext", area);

            parametros.put("usuariologeado", user.getNombreCompleto());
            // Llenar el informe con datos y parámetros
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReporte, parametros, new JRBeanCollectionDataSource(lista));

            // Mostrar el informe
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Informe de Ventanillas");
            viewer.setVisible(true);

        } catch (JRException e) {
            JOptionPane.showMessageDialog(this, "Error al generar el informe: " + e, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error general: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que sirve para actualizar las tablas y JComboBox de las areas.
     */
    private void actualizarTodo() {
        this.isActualizando = true;
        agregarAreasComboBox(cbArea);
        mostrarTablaVentanillas();
        this.isActualizando = false;
    }

    /**
     * Metodo que sirve para vaciar los datos de las tablas.
     */
    private void limpiarTablas() {
        modeloTablaReporteTickets.setRowCount(0);
        modeloTablaReporteVentanilla.setRowCount(0);
        lblGenerarReporteUnitario.setVisible(false);
        txtNombreEmpleado.setText("");
        dcFechaInicio.setDate(null);
        dcFechaFin.setDate(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBackground = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblGenerarReporteGeneral = new javax.swing.JLabel();
        lblMinimizeButton = new javax.swing.JLabel();
        lblMaximizeButton = new javax.swing.JLabel();
        lblExitButton = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbReporteVentanilla = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        pnlListadoTickets = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbReporteTickets = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cbArea = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        cbVentanilla = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtNombreEmpleado = new javax.swing.JTextField();
        dcFechaInicio = new com.toedter.calendar.JDateChooser();
        dcFechaFin = new com.toedter.calendar.JDateChooser();
        jPanel5 = new javax.swing.JPanel();
        lblAyudaGenerarReporte = new javax.swing.JLabel();
        lblActualizarButton = new javax.swing.JLabel();
        lblLimpiarButton = new javax.swing.JLabel();
        lblGenerarReporteUnitario = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 40)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Reportes");

        lblGenerarReporteGeneral.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblGenerarReporteGeneral.setForeground(new java.awt.Color(255, 255, 255));
        lblGenerarReporteGeneral.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGenerarReporteGeneral.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-terms-and-conditions-32.png"))); // NOI18N
        lblGenerarReporteGeneral.setText("<html>Reporte<br>general</html>");
        lblGenerarReporteGeneral.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        lblGenerarReporteGeneral.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblGenerarReporteGeneral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblGenerarReporteGeneralMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblGenerarReporteGeneralMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblGenerarReporteGeneralMouseExited(evt);
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
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblGenerarReporteGeneral, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                        .addComponent(lblMinimizeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblMaximizeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(lblExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblMinimizeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblExitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblMaximizeButton))
                        .addGap(11, 11, 11)
                        .addComponent(lblGenerarReporteGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 12, Short.MAX_VALUE))
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(11, 11, 11))))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "GENERAL", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        jPanel3.setOpaque(false);

        tbReporteVentanilla.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tbReporteVentanilla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "idVentanilla", "Empleado", "Area", "Ventanilla", "<html>Tickets<br>Atendidos</html>", "<html>Tickets<br>No Atendidos</html>", "Estado", "Fecha", "<html>Hora<br>Apertura</html>", "<html>Hora<br>Clausura</html>", "<html>Tiempo<br>Inactivo</html>"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbReporteVentanilla.setRowHeight(30);
        tbReporteVentanilla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbReporteVentanillaMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tbReporteVentanilla);
        if (tbReporteVentanilla.getColumnModel().getColumnCount() > 0) {
            tbReporteVentanilla.getColumnModel().getColumn(0).setMinWidth(0);
            tbReporteVentanilla.getColumnModel().getColumn(0).setPreferredWidth(0);
            tbReporteVentanilla.getColumnModel().getColumn(0).setMaxWidth(40);
            tbReporteVentanilla.getColumnModel().getColumn(1).setMinWidth(350);
            tbReporteVentanilla.getColumnModel().getColumn(1).setPreferredWidth(350);
            tbReporteVentanilla.getColumnModel().getColumn(5).setMinWidth(100);
            tbReporteVentanilla.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setText("Nota: Para mostrar el listado de tickets seleccione la ventanilla filtrada en la tabla general.");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1132, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addGap(17, 17, 17)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        pnlListadoTickets.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LISTADO DE TICKETS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        pnlListadoTickets.setOpaque(false);

        tbReporteTickets.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tbReporteTickets.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numero Ticket", "Fecha Creacion", "Estado", "Area", "Preferencial", "Inicio Atencion", "Fin Atencion", "Tiempo de Atencion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbReporteTickets.setRowHeight(30);
        jScrollPane2.setViewportView(tbReporteTickets);

        javax.swing.GroupLayout pnlListadoTicketsLayout = new javax.swing.GroupLayout(pnlListadoTickets);
        pnlListadoTickets.setLayout(pnlListadoTicketsLayout);
        pnlListadoTicketsLayout.setHorizontalGroup(
            pnlListadoTicketsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListadoTicketsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1132, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlListadoTicketsLayout.setVerticalGroup(
            pnlListadoTicketsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListadoTicketsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Empleado:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Área:");

        cbArea.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbArea.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar..." }));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Ventanilla:");

        cbVentanilla.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbVentanilla.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar..." }));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Fecha Inicio:");

        btnBuscar.setBackground(new java.awt.Color(204, 204, 204));
        btnBuscar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/buscar (32).png"))); // NOI18N
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("Fecha Fin:");

        txtNombreEmpleado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        dcFechaInicio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        dcFechaFin.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbArea, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbVentanilla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dcFechaInicio, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dcFechaFin, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnBuscar)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel8)
                    .addComponent(cbVentanilla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(dcFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(dcFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addGap(15, 15, 15))
        );

        jPanel5.setOpaque(false);

        lblAyudaGenerarReporte.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblAyudaGenerarReporte.setText("<html><u>¿Cómo genero un reporte?</u></html>");
        lblAyudaGenerarReporte.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAyudaGenerarReporte.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAyudaGenerarReporteMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAyudaGenerarReporteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAyudaGenerarReporteMouseExited(evt);
            }
        });

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

        lblLimpiarButton.setBackground(new java.awt.Color(0, 102, 102));
        lblLimpiarButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLimpiarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/clean-negro-30.png"))); // NOI18N
        lblLimpiarButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblLimpiarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblLimpiarButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblLimpiarButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblLimpiarButtonMouseExited(evt);
            }
        });

        lblGenerarReporteUnitario.setBackground(new java.awt.Color(0, 102, 102));
        lblGenerarReporteUnitario.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblGenerarReporteUnitario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGenerarReporteUnitario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-terms-and-conditions-32.png"))); // NOI18N
        lblGenerarReporteUnitario.setText("<html>Reporte unitario</html>");
        lblGenerarReporteUnitario.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblGenerarReporteUnitario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblGenerarReporteUnitario.setVisible(false);
        lblGenerarReporteUnitario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblGenerarReporteUnitarioMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblGenerarReporteUnitarioMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblGenerarReporteUnitarioMouseExited(evt);
            }
        });

        jSeparator1.setBackground(new java.awt.Color(160, 160, 160));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAyudaGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblGenerarReporteUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblLimpiarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblActualizarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblActualizarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAyudaGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLimpiarButton, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(lblGenerarReporteUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator1))
                .addGap(4, 4, 4))
        );

        javax.swing.GroupLayout pnlBackgroundLayout = new javax.swing.GroupLayout(pnlBackground);
        pnlBackground.setLayout(pnlBackgroundLayout);
        pnlBackgroundLayout.setHorizontalGroup(
            pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlBackgroundLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlListadoTickets, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlBackgroundLayout.setVerticalGroup(
            pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBackgroundLayout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlListadoTickets, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void tbReporteVentanillaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbReporteVentanillaMouseClicked
        
        // SIRVE PARA MOSTRAR INFORMACION DE LA TABLA tbReporteTurnos, SOLO SI ESTE DA CLIC EN UNA FILA DE
        // LA TABLA tbReporteVentanilla.
        int row = tbReporteVentanilla.getSelectedRow();
        int idVentanilla = Integer.parseInt(tbReporteVentanilla.getValueAt(row, 0).toString());
        Object nombreVentanilla = tbReporteVentanilla.getValueAt(row, 3);
        Object area = tbReporteVentanilla.getValueAt(row, 2);
        String nuevoTitulo = "LISTADO DE TICKETS: " + "Ventanilla " + nombreVentanilla + " - " + area;
        ((TitledBorder) pnlListadoTickets.getBorder()).setTitle(nuevoTitulo);
        pnlListadoTickets.repaint();

        // SE HABILITA UN BOTON PARA LA GENERACION DEL REPORTE UNITARIO O REPORTE DE TURNOS.
        if (mostrarTablaTurnos(idVentanilla)) {
            lblGenerarReporteUnitario.setVisible(true);
        } else {
            lblGenerarReporteUnitario.setVisible(false);
        }
        
//        if (evt.getClickCount() == 2) {
//            generarReporteTickets();
//        }
    }//GEN-LAST:event_tbReporteVentanillaMouseClicked

    private void lblGenerarReporteGeneralMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblGenerarReporteGeneralMouseEntered
        lblGenerarReporteGeneral.setForeground(Color.black);
        lblGenerarReporteGeneral.setOpaque(true);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblGenerarReporteGeneralMouseEntered

    private void lblGenerarReporteGeneralMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblGenerarReporteGeneralMouseExited
        lblGenerarReporteGeneral.setForeground(Color.white);
        lblGenerarReporteGeneral.setOpaque(false);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblGenerarReporteGeneralMouseExited

    private void lblAyudaGenerarReporteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaGenerarReporteMouseEntered
        lblAyudaGenerarReporte.setForeground(Color.decode("#006666"));
    }//GEN-LAST:event_lblAyudaGenerarReporteMouseEntered

    private void lblAyudaGenerarReporteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaGenerarReporteMouseExited
        lblAyudaGenerarReporte.setForeground(Color.black);
    }//GEN-LAST:event_lblAyudaGenerarReporteMouseExited

    private void lblMinimizeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinimizeButtonMouseClicked
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
        
        // SI NO ESTA MAXIMIZADO, SE MAXIMIZA.
        if (!isMaximized) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
            screenSize.height -= screenInsets.bottom;
            this.setLocation(0, 0);
            this.setSize(screenSize);
            lblMaximizeButton.setIcon(iconoComprimir30);
            this.isMaximized = true;
            this.revalidate();
            this.repaint();
            
        // SI ESTA MAXIMIZADO, SE CONTRAE.
        } else {
            this.setSize(1123, 570);
            this.setLocationRelativeTo(this);
            lblMaximizeButton.setIcon(iconoMaximizar30);
            this.isMaximized = false;
            this.revalidate();
            this.repaint();
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
        cerrarConexion();
        isDisposed = true;
        this.dispose();
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

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        mostrarTablaVentanillas();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void lblActualizarButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblActualizarButtonMouseClicked
        actualizarTodo();
    }//GEN-LAST:event_lblActualizarButtonMouseClicked

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

    private void lblGenerarReporteGeneralMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblGenerarReporteGeneralMouseClicked
        generarReporte();
    }//GEN-LAST:event_lblGenerarReporteGeneralMouseClicked

    private void lblAyudaGenerarReporteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaGenerarReporteMouseClicked
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(manualFile);
            } catch (IOException ex) {
                Logger.getLogger(frmLogin.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Su sistema operativo no puede abrir el manual de uso del sistema. ", "Alerta", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_lblAyudaGenerarReporteMouseClicked

    private void lblLimpiarButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarButtonMouseEntered
        lblLimpiarButton.setIcon(iconoLimpiarBlanco30);
        lblLimpiarButton.setOpaque(true);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblLimpiarButtonMouseEntered

    private void lblLimpiarButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarButtonMouseExited
        lblLimpiarButton.setIcon(iconoLimpiarNegro30);
        lblLimpiarButton.setOpaque(false);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblLimpiarButtonMouseExited

    private void lblLimpiarButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLimpiarButtonMouseClicked
        limpiarTablas();
    }//GEN-LAST:event_lblLimpiarButtonMouseClicked

    private void lblGenerarReporteUnitarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblGenerarReporteUnitarioMouseClicked
        int row = tbReporteVentanilla.getSelectedRow();
        String empleado = tbReporteVentanilla.getValueAt(row, 1).toString();
        String area = tbReporteVentanilla.getValueAt(row, 2).toString();
        String nroVentanilla = tbReporteVentanilla.getValueAt(row, 3).toString();
        generarReporteTickets(empleado, area, nroVentanilla);
    }//GEN-LAST:event_lblGenerarReporteUnitarioMouseClicked

    private void lblGenerarReporteUnitarioMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblGenerarReporteUnitarioMouseEntered
        lblGenerarReporteUnitario.setForeground(Color.white);
        lblGenerarReporteUnitario.setOpaque(true);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblGenerarReporteUnitarioMouseEntered

    private void lblGenerarReporteUnitarioMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblGenerarReporteUnitarioMouseExited
        lblGenerarReporteUnitario.setForeground(Color.black);
        lblGenerarReporteUnitario.setOpaque(false);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblGenerarReporteUnitarioMouseExited

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JComboBox<Object> cbArea;
    private javax.swing.JComboBox<String> cbVentanilla;
    private com.toedter.calendar.JDateChooser dcFechaFin;
    private com.toedter.calendar.JDateChooser dcFechaInicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblActualizarButton;
    private javax.swing.JLabel lblAyudaGenerarReporte;
    private javax.swing.JLabel lblExitButton;
    private javax.swing.JLabel lblGenerarReporteGeneral;
    private javax.swing.JLabel lblGenerarReporteUnitario;
    private javax.swing.JLabel lblLimpiarButton;
    private javax.swing.JLabel lblMaximizeButton;
    private javax.swing.JLabel lblMinimizeButton;
    private javax.swing.JPanel pnlBackground;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlListadoTickets;
    private javax.swing.JTable tbReporteTickets;
    private javax.swing.JTable tbReporteVentanilla;
    private javax.swing.JTextField txtNombreEmpleado;
    // End of variables declaration//GEN-END:variables
}
