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
 * Clase que permite instanciar el formulario de configuraciones.
 * 
 * @author jnxd_
 */
public class frmConfiguracion extends javax.swing.JFrame {

    /**
     * Variable que almacena el icono de la aplicacion.
     */
    private ImageIcon vtnIcon;

    /**
     * @deprecated 
     */
    private int NroVentanilla = 0;
    
    /**
     * Variable que almacena por clave - valor de las areas.
     */
    private HashMap<String, Integer> areas = new HashMap<>();
    
   /**
    * Variable que almacena en lista las areas.
    */
    private ArrayList<String> listaAreas = new ArrayList<>();
    
    /**
     * Variable que ayuda a evitar un error de Closed Connection de SQL.
     * @deprecated 
     */
    private boolean isActualizando = false;

    /**
     * Variable que almacena la ruta del video.
     * @deprecated
     */
    private String rutaVideo;
    
    /**
     * Variable que almacena el tiempo promedio de atencion.
     * @deprecated
     */
    public int tiempoPromedioAtencion;
    public boolean isDisposed = false;

    int xMouse;
    int yMouse;

    private boolean isMaximized = false;

    int areaid;
    String areaSeleccionada;

    public frmConfiguracion(int areaId, String areaSeleccionada) {
        this.areaid = areaId;
        this.areaSeleccionada = areaSeleccionada;
        vtnIcon = new ImageIcon(this.getClass().getResource("/icons/imagen64.png"));
        initComponents();
        this.setIconImage(vtnIcon.getImage());
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        asignarValoresConfiguraciones();
        obtenerDirectorHospital();
        lblNombreArea.setText(areaSeleccionada);
        lblTectoEncabezado.setText(areaSeleccionada);

        txtTituloProfesional.getDocument().addDocumentListener(new DocumentListener() {
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

        txtDirectorNombre.getDocument().addDocumentListener(new DocumentListener() {
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
    
    private void obtenerDirectorHospital() {
        try {
            establecerConexion();
            CallableStatement cst;
            String filtro = "''";
            cst = llamarConexion.prepareCall("{call [MostrarDirector](?)}");
            cst.setString(1, filtro);

            rs = cst.executeQuery();
            if (rs.next()) {
                txtTituloProfesional.setText(rs.getString(1));
                txtDirectorNombre.setText(rs.getString(2));
            }
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void asignarValoresConfiguraciones() {
        try {
            establecerConexion();
            CallableStatement cst;
            int idArea = areaid;
            String filtro = " WHERE C.IdArea = " + idArea;
            cst = llamarConexion.prepareCall("{call [MostrarConfiguracionesAreasVentanillas](?)}");
            cst.setString(1, filtro);
            TitledBorder border = new TitledBorder("<Area>");
            border.setBorder(new EtchedBorder());

            rs = cst.executeQuery();
            if (rs.next()) {
                border.setTitle(rs.getString(3));
                panAreaSeleccionada.setBorder(border);
                lblColorHexadecimalFondo.setText(rs.getString(9));
                panSeleccionarColorFondo.setBackground(Color.decode(rs.getString(9)));
                PanPreEncabezado.setBackground(Color.decode(rs.getString(9)));
                lblColorHexadecimalTexto.setText(rs.getString(10));
                panSeleccionarColorTexto.setBackground(Color.decode(rs.getString(10)));
                lbltextoF1.setForeground(Color.decode(rs.getString(10)));
                lbltextoF2.setForeground(Color.decode(rs.getString(10)));
                lbltextoF3.setForeground(Color.decode(rs.getString(10)));
                lblColorFondoEncabezado.setText(rs.getString(11));
                panColorFondoEncabezado.setBackground(Color.decode(rs.getString(11)));
                panPreEncabezado.setBackground(Color.decode(rs.getString(11)));
                lblColorTextoEncabezado.setText(rs.getString(12));
                panColorTextoEncabezado.setBackground(Color.decode(rs.getString(12)));
                lblTectoEncabezado.setForeground(Color.decode(rs.getString(12)));
//                txtTituloProfesional.setText(rs.getString(13));
//                txtDirectorNombre.setText(rs.getString(14));

            }
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
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
        lblNombreArea = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        panAreaSeleccionada = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        panSeleccionarColorFondo = new javax.swing.JPanel();
        lblColorHexadecimalFondo = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        panSeleccionarColorTexto = new javax.swing.JPanel();
        lblColorHexadecimalTexto = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        panColorFondoEncabezado = new javax.swing.JPanel();
        lblColorFondoEncabezado = new javax.swing.JLabel();
        panColorTextoEncabezado = new javax.swing.JPanel();
        lblColorTextoEncabezado = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        PanPreEncabezado = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        lbltextoF1 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        lbltextoF2 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        lbltextoF3 = new javax.swing.JLabel();
        panPreEncabezado = new javax.swing.JPanel();
        lblTectoEncabezado = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        btnRegresar = new javax.swing.JButton();
        btnAplicarCambios = new javax.swing.JButton();
        btnAceptar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txtTituloProfesional = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtDirectorNombre = new javax.swing.JTextField();

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
        jLabel1.setText("Configuración -");

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

        lblNombreArea.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblNombreArea.setForeground(new java.awt.Color(255, 255, 255));
        lblNombreArea.setText("Laboratorio");

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNombreArea)
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
                        .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblNombreArea))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        panAreaSeleccionada.setBackground(new java.awt.Color(255, 255, 255));
        panAreaSeleccionada.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Admision"));

        jLabel4.setText("Color Fondo:");
        jLabel4.setEnabled(false);

        panSeleccionarColorFondo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panSeleccionarColorFondo.setEnabled(false);
        panSeleccionarColorFondo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panSeleccionarColorFondoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panSeleccionarColorFondoLayout = new javax.swing.GroupLayout(panSeleccionarColorFondo);
        panSeleccionarColorFondo.setLayout(panSeleccionarColorFondoLayout);
        panSeleccionarColorFondoLayout.setHorizontalGroup(
            panSeleccionarColorFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        panSeleccionarColorFondoLayout.setVerticalGroup(
            panSeleccionarColorFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );

        lblColorHexadecimalFondo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblColorHexadecimalFondo.setText("#000000");
        lblColorHexadecimalFondo.setEnabled(false);

        jLabel7.setText("Color Texto:");
        jLabel7.setEnabled(false);

        panSeleccionarColorTexto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panSeleccionarColorTexto.setEnabled(false);
        panSeleccionarColorTexto.setPreferredSize(new java.awt.Dimension(17, 17));
        panSeleccionarColorTexto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panSeleccionarColorTextoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panSeleccionarColorTextoLayout = new javax.swing.GroupLayout(panSeleccionarColorTexto);
        panSeleccionarColorTexto.setLayout(panSeleccionarColorTextoLayout);
        panSeleccionarColorTextoLayout.setHorizontalGroup(
            panSeleccionarColorTextoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        panSeleccionarColorTextoLayout.setVerticalGroup(
            panSeleccionarColorTextoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );

        lblColorHexadecimalTexto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblColorHexadecimalTexto.setText("#000000");
        lblColorHexadecimalTexto.setEnabled(false);

        jLabel9.setText("Color Fondo Encabezado:");
        jLabel9.setEnabled(false);

        jLabel11.setText("Color Texto Encabezado:");
        jLabel11.setEnabled(false);

        panColorFondoEncabezado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panColorFondoEncabezado.setEnabled(false);
        panColorFondoEncabezado.setPreferredSize(new java.awt.Dimension(17, 17));
        panColorFondoEncabezado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panColorFondoEncabezadoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panColorFondoEncabezadoLayout = new javax.swing.GroupLayout(panColorFondoEncabezado);
        panColorFondoEncabezado.setLayout(panColorFondoEncabezadoLayout);
        panColorFondoEncabezadoLayout.setHorizontalGroup(
            panColorFondoEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        panColorFondoEncabezadoLayout.setVerticalGroup(
            panColorFondoEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );

        lblColorFondoEncabezado.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblColorFondoEncabezado.setText("#000000");
        lblColorFondoEncabezado.setEnabled(false);

        panColorTextoEncabezado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panColorTextoEncabezado.setEnabled(false);
        panColorTextoEncabezado.setPreferredSize(new java.awt.Dimension(17, 17));
        panColorTextoEncabezado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panColorTextoEncabezadoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panColorTextoEncabezadoLayout = new javax.swing.GroupLayout(panColorTextoEncabezado);
        panColorTextoEncabezado.setLayout(panColorTextoEncabezadoLayout);
        panColorTextoEncabezadoLayout.setHorizontalGroup(
            panColorTextoEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );
        panColorTextoEncabezadoLayout.setVerticalGroup(
            panColorTextoEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 15, Short.MAX_VALUE)
        );

        lblColorTextoEncabezado.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblColorTextoEncabezado.setText("#000000");
        lblColorTextoEncabezado.setEnabled(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setOpaque(false);

        PanPreEncabezado.setLayout(new java.awt.GridLayout(1, 3));

        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel11.setOpaque(false);

        lbltextoF1.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        lbltextoF1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltextoF1.setText("AaBb123");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbltextoF1, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbltextoF1, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
        );

        PanPreEncabezado.add(jPanel11);

        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel12.setOpaque(false);

        lbltextoF2.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        lbltextoF2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltextoF2.setText("AaBb123");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbltextoF2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbltextoF2, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
        );

        PanPreEncabezado.add(jPanel12);

        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel13.setOpaque(false);

        lbltextoF3.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        lbltextoF3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltextoF3.setText("AaBb123");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbltextoF3, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbltextoF3, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
        );

        PanPreEncabezado.add(jPanel13);

        lblTectoEncabezado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblTectoEncabezado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTectoEncabezado.setText("Laboratorio");

        javax.swing.GroupLayout panPreEncabezadoLayout = new javax.swing.GroupLayout(panPreEncabezado);
        panPreEncabezado.setLayout(panPreEncabezadoLayout);
        panPreEncabezadoLayout.setHorizontalGroup(
            panPreEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTectoEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panPreEncabezadoLayout.setVerticalGroup(
            panPreEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTectoEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridLayout(6, 1));

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        jPanel6.add(jPanel4);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        jPanel6.add(jPanel5);

        jPanel10.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        jPanel6.add(jPanel10);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        jPanel6.add(jPanel7);

        jPanel8.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        jPanel6.add(jPanel8);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        jPanel6.add(jPanel9);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(PanPreEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panPreEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PanPreEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panPreEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panAreaSeleccionadaLayout = new javax.swing.GroupLayout(panAreaSeleccionada);
        panAreaSeleccionada.setLayout(panAreaSeleccionadaLayout);
        panAreaSeleccionadaLayout.setHorizontalGroup(
            panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panAreaSeleccionadaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panAreaSeleccionadaLayout.createSequentialGroup()
                        .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel7)
                            .addComponent(jLabel4)
                            .addComponent(jLabel11))
                        .addGap(18, 18, 18)
                        .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(panColorFondoEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(panSeleccionarColorTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(panSeleccionarColorFondo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(panColorTextoEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblColorTextoEncabezado)
                            .addComponent(lblColorFondoEncabezado)
                            .addComponent(lblColorHexadecimalTexto)
                            .addComponent(lblColorHexadecimalFondo)))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panAreaSeleccionadaLayout.setVerticalGroup(
            panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panAreaSeleccionadaLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(panSeleccionarColorFondo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblColorHexadecimalFondo))
                .addGap(10, 10, 10)
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panSeleccionarColorTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblColorHexadecimalTexto))
                .addGap(24, 24, 24)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9)
                    .addComponent(panColorFondoEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblColorFondoEncabezado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panAreaSeleccionadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblColorTextoEncabezado)
                    .addComponent(panColorTextoEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panAreaSeleccionadaLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

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

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Config. Global"));

        txtTituloProfesional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTituloProfesionalActionPerformed(evt);
            }
        });

        jLabel12.setText("Texto Principal: ");

        jLabel13.setText("Texto Secundario: ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTituloProfesional)
                    .addComponent(txtDirectorNombre))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTituloProfesional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtDirectorNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(panAreaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
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
                .addComponent(panAreaSeleccionada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(7, 7, 7)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
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

    private void btnAplicarCambiosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAplicarCambiosActionPerformed
        try {
            establecerConexion();
            CallableStatement cst;
            int idArea = areaid;
            cst = llamarConexion.prepareCall("{call [ActualizarConfiguracionColorArea](?,?,?,?,?,?,?)},tt");
            cst.setInt(1, idArea);
            cst.setString(2, lblColorHexadecimalFondo.getText());
            cst.setString(3, lblColorHexadecimalTexto.getText());
            cst.setString(4, lblColorFondoEncabezado.getText());
            cst.setString(5, lblColorTextoEncabezado.getText());
            cst.setString(6, txtTituloProfesional.getText());
            cst.setString(7, txtDirectorNombre.getText());

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

    private void panSeleccionarColorTextoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panSeleccionarColorTextoMouseClicked
        Color color = JColorChooser.showDialog(null, "Elige un color", Color.WHITE);
        if (color != null) {
            panSeleccionarColorTexto.setBackground(color);
            String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            lblColorHexadecimalTexto.setText(hexColor);
            btnAplicarCambios.setEnabled(true);
            lbltextoF1.setForeground(color);
            lbltextoF2.setForeground(color);
            lbltextoF3.setForeground(color);
        }
    }//GEN-LAST:event_panSeleccionarColorTextoMouseClicked

    private void panColorFondoEncabezadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panColorFondoEncabezadoMouseClicked
        Color color = JColorChooser.showDialog(null, "Elige un color", Color.WHITE);
        if (color != null) {
            panColorFondoEncabezado.setBackground(color);
            String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            lblColorFondoEncabezado.setText(hexColor);
            btnAplicarCambios.setEnabled(true);
            panPreEncabezado.setBackground(color);
        }
    }//GEN-LAST:event_panColorFondoEncabezadoMouseClicked

    private void panSeleccionarColorFondoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panSeleccionarColorFondoMouseClicked
        Color color = JColorChooser.showDialog(null, "Elige un color", Color.WHITE);
        if (color != null) {
            panSeleccionarColorFondo.setBackground(color);
            String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            lblColorHexadecimalFondo.setText(hexColor);
            btnAplicarCambios.setEnabled(true);
            PanPreEncabezado.setBackground(color);
        }
    }//GEN-LAST:event_panSeleccionarColorFondoMouseClicked

    private void panColorTextoEncabezadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panColorTextoEncabezadoMouseClicked
        Color color = JColorChooser.showDialog(null, "Elige un color", Color.WHITE);
        if (color != null) {
            panColorTextoEncabezado.setBackground(color);
            String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            lblColorTextoEncabezado.setText(hexColor);
            btnAplicarCambios.setEnabled(true);
            lblTectoEncabezado.setForeground(color);
        }
    }//GEN-LAST:event_panColorTextoEncabezadoMouseClicked

    private void txtTituloProfesionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTituloProfesionalActionPerformed

    }//GEN-LAST:event_txtTituloProfesionalActionPerformed

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
    private javax.swing.JPanel PanPreEncabezado;
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnAplicarCambios;
    private javax.swing.JButton btnRegresar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblColorFondoEncabezado;
    private javax.swing.JLabel lblColorHexadecimalFondo;
    private javax.swing.JLabel lblColorHexadecimalTexto;
    private javax.swing.JLabel lblColorTextoEncabezado;
    private javax.swing.JLabel lblExitButton;
    private javax.swing.JLabel lblMinimizeButton;
    private javax.swing.JLabel lblNombreArea;
    private javax.swing.JLabel lblTectoEncabezado;
    private javax.swing.JLabel lbltextoF1;
    private javax.swing.JLabel lbltextoF2;
    private javax.swing.JLabel lbltextoF3;
    private javax.swing.JPanel panAreaSeleccionada;
    private javax.swing.JPanel panColorFondoEncabezado;
    private javax.swing.JPanel panColorTextoEncabezado;
    private javax.swing.JPanel panPreEncabezado;
    private javax.swing.JPanel panSeleccionarColorFondo;
    private javax.swing.JPanel panSeleccionarColorTexto;
    private javax.swing.JPanel pnlBackground;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JTextField txtDirectorNombre;
    private javax.swing.JTextField txtTituloProfesional;
    // End of variables declaration//GEN-END:variables
}
