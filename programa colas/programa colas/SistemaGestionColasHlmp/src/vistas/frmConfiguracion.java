/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

import conexion.ConexionSQL;
import static conexion.ConexionSQL.cerrarConexion;
import static conexion.ConexionSQL.establecerConexion;
import static conexion.ConexionSQL.llamarConexion;
import static conexion.ConexionSQL.rs;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Clase que contiene el formulario de Configuraciones.
 * 
 * @author jnxd_
 */
public class frmConfiguracion extends javax.swing.JFrame {

    /**
     * Variable que referencia al icono de la ventana.
     */
    private ImageIcon vtnIcon;
    
    /**
     * @deprecated 
     */
    public int NroVentanilla = 0;
    
    /**
     * Variable que almacena las areas y sus id.
     */
    private HashMap<String, Integer> areas = new HashMap<>();
    
    /**
     * Variable que almacena las areas.
     */
    private ArrayList<String> listaAreas = new ArrayList<>();
    
    /**
     * Variable que ayuda a evitar un error de Closed Connection de SQL.
     */
    private boolean isActualizando = false;

    /**
     * @deprecated 
     */
    public String rutaVideo;
    
    /**
     * @deprecated 
     */
    public int tiempoPromedioAtencion;
    
    /**
     * Variable que sirve como banderin, para saber si la ventana a sido cerrada o ocultada con this.dispose().
     */
    public boolean isDisposed = false;

    /**
     * Variables para obtener la posicion x & y del mouse.
     */
    int xMouse;
    int yMouse;

    /**
     * Variable que sirve como banderin, para saber si la ventana a sido maximizada.
     */
    private boolean isMaximized = false;

    /**
     * Constructor de la clase.
     */
    public frmConfiguracion() {
        vtnIcon = new ImageIcon(this.getClass().getResource("/icons/sigco-vtnadmin-64.png"));
        initComponents();
        this.setIconImage(vtnIcon.getImage());
        SpinnerNumberModel modelSpinnerTiempoAtencion = new SpinnerNumberModel(2, 2, 40, 1);
        spnTiempoPromedioAtencion.setModel(modelSpinnerTiempoAtencion);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        actualizarAreasComboBox();
        detectarAreaSeleccionada();
        asignarValoresConfiguraciones();
        obtenerDirectorHospital();
        
        // Los siguientes metodos detectan si hay cambios para habilitar el boton de "Aplicar".
        spnLimiteVentanillas.addChangeListener((ChangeEvent e) -> {
            btnAplicarCambios.setEnabled(true);
        });

        spnTiempoPromedioAtencion.addChangeListener((ChangeEvent e) -> {
            btnAplicarCambios.setEnabled(true);
        });
        
        spnTiempoEsperaPacientes.addChangeListener((ChangeEvent e) -> {
            btnAplicarCambios.setEnabled(true);
        });

        txtCampoPrincipal.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                btnAplicarCambios.setEnabled(true);
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                btnAplicarCambios.setEnabled(true);
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        txtCampoSecundario.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                btnAplicarCambios.setEnabled(true);
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                btnAplicarCambios.setEnabled(true);
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        txtIpPantallaVisualizacion.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                btnAplicarCambios.setEnabled(true);
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                btnAplicarCambios.setEnabled(true);
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        txtPuerto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                btnAplicarCambios.setEnabled(true);
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                btnAplicarCambios.setEnabled(true);
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    /**
     * Metodo que sirve para detectar el area que ha sido seleccionada en el JComboBox cbAreas.
     */
    public final void detectarAreaSeleccionada() {
        cbAreas.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (!isActualizando) {
                    asignarValoresConfiguraciones();
                    btnAplicarCambios.setEnabled(false);
                }
            }
        });
    }

    /**
     * Metodo que sirve para actualizar las areas del JComboBox cbAreas segun las areas registradas en base de datos.
     */
    public final void actualizarAreasComboBox() {
        try {
            establecerConexion();
            CallableStatement cst;
            cbAreas.removeAllItems();
            areas.clear();
            listaAreas.clear();
            String filtro = "WHERE Estado = 1";
            cst = llamarConexion.prepareCall("{call [MostrarAreaVentanilla](?)}");
            cst.setString(1, filtro);
            rs = cst.executeQuery();
            while (rs.next()) {
                areas.put(rs.getString(2), rs.getInt(1));
                cbAreas.addItem(rs.getString(2));
                listaAreas.add(rs.getString(2));
            }
            cerrarConexion();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que permite obtener el idArea pasado por el parametro.
     * @param nombreArea
     * @return 
     */
    private int seleccionarArea(String nombreArea) {
        int idArea = areas.get(nombreArea);
        return idArea;
    }

    /**
     * @deprecated 
     * @param idArea
     * @return 
     */
    private String seleccionarArea(int idArea) {
        for (Map.Entry<String, Integer> entrada : areas.entrySet()) {
            if (entrada.getValue().equals(idArea)) {
                return entrada.getKey();
            }
        }
        return null;
    }

    /**
     * Metodo que permite agregar una nueva área en base de datos.
     * 
     * @param nombre
     * @param letra 
     */
    private void agregarNuevaArea(String nombre, String letra) {
        try {
            establecerConexion();
            CallableStatement cst;
            cst = llamarConexion.prepareCall("{call [InsertarAreasVentanillas](?,?)}");
            cst.setString(1, nombre);
            cst.setString(2, letra);

            cst.executeUpdate();
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que permite asignar los valores de las configuraciones guardadas en basde de datos en la interfaz.
     */
    private void asignarValoresConfiguraciones() {
        try {
            establecerConexion();
            CallableStatement cst;
            int idArea = seleccionarArea(cbAreas.getSelectedItem().toString());
            String filtro = " WHERE C.IdArea = " + idArea;
            cst = llamarConexion.prepareCall("{call [MostrarConfiguracionesAreasVentanillas](?)}");
            cst.setString(1, filtro);
            TitledBorder border = new TitledBorder("<Area>");
            border.setBorder(new EtchedBorder());

            rs = cst.executeQuery();
            if (rs.next()) {
                border.setTitle(rs.getString(3));
                panAreaSeleccionada.setBorder(border);
                spnLimiteVentanillas.setValue(rs.getInt(4));
                spnTiempoPromedioAtencion.setValue(rs.getInt(5));
                txtIpPantallaVisualizacion.setText(rs.getString(6));
                txtPuerto.setText(rs.getString(7));
                spnTiempoEsperaPacientes.setValue(rs.getInt(15));
            }
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que permite obtener los campos del campo principal y del campo secundario.
     */
    private void obtenerDirectorHospital() {
        try {
            establecerConexion();
            CallableStatement cst;
            String filtro = "''";
            cst = llamarConexion.prepareCall("{call [MostrarDirector](?)}");
            cst.setString(1, filtro);

            rs = cst.executeQuery();
            if (rs.next()) {
                txtCampoPrincipal.setText(rs.getString(1));
                txtCampoSecundario.setText(rs.getString(2));
            }
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Metodo que sirve para eliminar una área pasada por parametro en base de datos.
     * 
     * @param nombreArea 
     */
    private void eliminarArea(String nombreArea) {
        try {
            establecerConexion();
            CallableStatement cst;
            cst = llamarConexion.prepareCall("{call [ActualizarAreasVentanillas](?,?)}");
            cst.setString(1, nombreArea);
            cst.setInt(2, 0);

            cst.executeUpdate();
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBackground = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblMinimizeButton = new javax.swing.JLabel();
        lblExitButton = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        panAreaSeleccionada = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtIpPantallaVisualizacion = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPuerto = new javax.swing.JTextField();
        spnLimiteVentanillas = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        spnTiempoPromedioAtencion = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        spnTiempoEsperaPacientes = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnAgregarArea = new javax.swing.JButton();
        btnEliminarArea = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnRegresar = new javax.swing.JButton();
        btnAplicarCambios = new javax.swing.JButton();
        btnAceptar = new javax.swing.JButton();
        cbAreas = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtCampoPrincipal = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtCampoSecundario = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Configuración");

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

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblMinimizeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lblExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMinimizeButton)
                            .addComponent(lblExitButton)))
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        panAreaSeleccionada.setBackground(new java.awt.Color(255, 255, 255));
        panAreaSeleccionada.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Admision"));

        jLabel3.setText("IP Servidor Pantalla: ");

        txtIpPantallaVisualizacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIpPantallaVisualizacionActionPerformed(evt);
            }
        });

        jLabel5.setText("Puerto: ");

        spnLimiteVentanillas.setValue(1);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Limite Ventanillas: ");

        jLabel10.setText("Tiempo promedio atencion: ");

        spnTiempoPromedioAtencion.setValue(0);

        jLabel14.setText("Tiempo de espera a los pacientes: ");

        jLabel4.setText("(segundos)");

        jLabel7.setText("(minutos)");

        javax.swing.GroupLayout panAreaSeleccionadaLayout = new javax.swing.GroupLayout(panAreaSeleccionada);
        panAreaSeleccionada.setLayout(panAreaSeleccionadaLayout);
        panAreaSeleccionadaLayout.setHorizontalGroup(
            panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panAreaSeleccionadaLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(spnLimiteVentanillas, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIpPantallaVisualizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnTiempoEsperaPacientes, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnTiempoPromedioAtencion, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panAreaSeleccionadaLayout.setVerticalGroup(
            panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panAreaSeleccionadaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(spnTiempoPromedioAtencion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(spnLimiteVentanillas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtIpPantallaVisualizacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(spnTiempoEsperaPacientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panAreaSeleccionadaLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, spnLimiteVentanillas});

        btnAgregarArea.setBackground(new java.awt.Color(255, 255, 255));
        btnAgregarArea.setText("Agregar Area");
        btnAgregarArea.setFocusable(false);
        btnAgregarArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarAreaActionPerformed(evt);
            }
        });

        btnEliminarArea.setBackground(new java.awt.Color(255, 255, 255));
        btnEliminarArea.setText("Eliminar Area");
        btnEliminarArea.setFocusable(false);
        btnEliminarArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarAreaActionPerformed(evt);
            }
        });

        btnRegresar.setBackground(new java.awt.Color(255, 255, 255));
        btnRegresar.setText("Regresar");
        btnRegresar.setFocusable(false);
        btnRegresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegresarActionPerformed(evt);
            }
        });

        btnAplicarCambios.setBackground(new java.awt.Color(255, 255, 255));
        btnAplicarCambios.setText("Aplicar");
        btnAplicarCambios.setEnabled(false);
        btnAplicarCambios.setFocusable(false);
        btnAplicarCambios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAplicarCambiosActionPerformed(evt);
            }
        });

        btnAceptar.setBackground(new java.awt.Color(255, 255, 255));
        btnAceptar.setText("Aceptar");
        btnAceptar.setFocusable(false);
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        jLabel6.setText("Configuracion de Area:");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Config. Global"));

        txtCampoPrincipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCampoPrincipalActionPerformed(evt);
            }
        });

        jLabel12.setText("Campo principal:");

        jLabel13.setText("Campo secundario:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtCampoPrincipal)
                    .addComponent(txtCampoSecundario))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCampoPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtCampoSecundario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panAreaSeleccionada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(cbAreas, 0, 197, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAgregarArea)
                        .addGap(18, 18, 18)
                        .addComponent(btnEliminarArea))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnRegresar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAceptar)
                        .addGap(18, 18, 18)
                        .addComponent(btnAplicarCambios))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(cbAreas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarArea)
                    .addComponent(btnEliminarArea))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panAreaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnRegresar)
                    .addComponent(btnAceptar)
                    .addComponent(btnAplicarCambios))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlBackgroundLayout = new javax.swing.GroupLayout(pnlBackground);
        pnlBackground.setLayout(pnlBackgroundLayout);
        pnlBackgroundLayout.setHorizontalGroup(
            pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlBackgroundLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlBackgroundLayout.setVerticalGroup(
            pnlBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBackgroundLayout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        cerrarConexion();
        isDisposed = true;
        this.dispose();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnRegresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegresarActionPerformed
        cerrarConexion();
        isDisposed = true;
        this.dispose();
    }//GEN-LAST:event_btnRegresarActionPerformed

    private void btnAgregarAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarAreaActionPerformed
        
        // Levanta un JOptionPane para ingresar la nueva área y poder registrarla.
        JTextField txtNombreArea = new JTextField();
        JTextField txtLetraArea = new JTextField();
        Object[] message = {"Nombre del Area:", txtNombreArea, "Letra para el Area: ", txtLetraArea};
        Object[] options = {"Agregar", "Cancelar"};

        int option = JOptionPane.showOptionDialog(this, message, "Entrada",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (option == JOptionPane.YES_OPTION) {
            isActualizando = true;
            if (txtNombreArea.getText().equals("") || txtLetraArea.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Rellene los campos", "Alerta", JOptionPane.ERROR_MESSAGE);
            } else {
                if (listaAreas.contains(txtNombreArea.getText())) {
                    JOptionPane.showMessageDialog(this, "El Area que desea agregar ya existe o los valores ingresados no son correctos", "Alerta", JOptionPane.ERROR_MESSAGE);
                } else {
                    agregarNuevaArea(txtNombreArea.getText(), txtLetraArea.getText());
                    actualizarAreasComboBox();
                }
            }
            isActualizando = false;
        }
    }//GEN-LAST:event_btnAgregarAreaActionPerformed

    private void btnAplicarCambiosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAplicarCambiosActionPerformed
        try {
            // Sirve para aplicar y guardar los cambios en base de datos y en la interfaz.
            establecerConexion();
            CallableStatement cst;
            int idArea = seleccionarArea(cbAreas.getSelectedItem().toString());
            cst = llamarConexion.prepareCall("{call [ActualizarConfiguracionArea](?,?,?,?,?,?,?,?)}");
            cst.setInt(1, idArea);
            cst.setInt(2, (int) spnLimiteVentanillas.getValue());
            cst.setInt(3, (int) spnTiempoPromedioAtencion.getValue());
            cst.setString(4, txtIpPantallaVisualizacion.getText());
            cst.setString(5, txtPuerto.getText());
            cst.setString(6, txtCampoPrincipal.getText());
            cst.setString(7, txtCampoSecundario.getText());
            cst.setInt(8, (int) spnTiempoEsperaPacientes.getValue());

            //ruta = txtDireccionVideoVisualizacion.getText();
            TitledBorder border = new TitledBorder("<Area>");
            border.setBorder(new EtchedBorder());

            cst.executeUpdate();
            btnAplicarCambios.setEnabled(false);
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAplicarCambiosActionPerformed

    private void btnEliminarAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarAreaActionPerformed
        
        // Levanta un JOptionPane para seleccionar un area que se desea eliminar
        JComboBox<String> cbAreaMostrar = new JComboBox<>(listaAreas.toArray(new String[0]));
        Object[] options = {"Eliminar", "Cancelar"};
        int option = JOptionPane.showOptionDialog(
                this,
                cbAreaMostrar,
                "Elige el area que desea eliminar: ",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );
        if (option == JOptionPane.YES_OPTION) {
            isActualizando = true;
            String nombreArea = cbAreaMostrar.getSelectedItem().toString();
            eliminarArea(nombreArea);
            actualizarAreasComboBox();
            isActualizando = false;
        }
    }//GEN-LAST:event_btnEliminarAreaActionPerformed

    private void txtIpPantallaVisualizacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIpPantallaVisualizacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIpPantallaVisualizacionActionPerformed

    private void txtCampoPrincipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCampoPrincipalActionPerformed

    }//GEN-LAST:event_txtCampoPrincipalActionPerformed

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

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(frmConfiguracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(frmConfiguracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(frmConfiguracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(frmConfiguracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new frmConfiguracion().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnAgregarArea;
    private javax.swing.JButton btnAplicarCambios;
    private javax.swing.JButton btnEliminarArea;
    private javax.swing.JButton btnRegresar;
    private javax.swing.JComboBox<String> cbAreas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblExitButton;
    private javax.swing.JLabel lblMinimizeButton;
    private javax.swing.JPanel panAreaSeleccionada;
    private javax.swing.JPanel pnlBackground;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JSpinner spnLimiteVentanillas;
    private javax.swing.JSpinner spnTiempoEsperaPacientes;
    private javax.swing.JSpinner spnTiempoPromedioAtencion;
    private javax.swing.JTextField txtCampoPrincipal;
    private javax.swing.JTextField txtCampoSecundario;
    private javax.swing.JTextField txtIpPantallaVisualizacion;
    private javax.swing.JTextField txtPuerto;
    // End of variables declaration//GEN-END:variables
}
