/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loginSources;

import conexion.ConexionSQL;
import static conexion.ConexionSQL.cerrarConexion;
import static conexion.ConexionSQL.establecerConexion;
import static conexion.ConexionSQL.llamarConexion;
import static conexion.ConexionSQL.rs;
import java.awt.Color;
import java.awt.Desktop;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import vistas.frmSeleccionarVentanilla;

/**
 * Clase del formulario de Inicio de Sesion.
 *
 * @author jnxd_
 */
public class frmLogin extends javax.swing.JFrame {

    /**
     * Variable que referencia al icono de la ventana.
     */
    private ImageIcon vtnIcon;

    /**
     * @deprecated Variable que almacena el codigo de resultado al ejecutar el
     * procedimiento.
     */
    private String codigoResultado = "";

    /**
     *@deprecated 
     */
    private HashMap<String, Integer> areas = new HashMap<>();
    
    /**
     * @deprecated 
     */
    private ArrayList<String> listaAreas = new ArrayList<>();
    
    /**
     * Variable creada para instanciar el formulario de frmSeleccionarVentanilla.
     */
    private frmSeleccionarVentanilla vtnSeleccionarVentanilla;
    
    /**
     * Variable creada para instanciar la ruta del manual.
     */
    public String rutaManual;
    
    /**
     * Variable creada para instanciar el archivo del manual.
     */
    public File manualFile;

    /**
     * Variable creada para instanciar al usuario logeado.
     */
    public static Usuario user;
    
    /**
     * Variables para obtener la posicion x & y del mouse.
     */
    int xMouse;
    int yMouse;

    /**
     * Constructor de la clase.
     */
    public frmLogin() {
        vtnIcon = new ImageIcon(this.getClass().getResource("/icons/sigco-vtnadmin-64.png"));
        initComponents();
        this.setIconImage(vtnIcon.getImage());
        this.getRootPane().setDefaultButton(btnAcceder);
        rutaManual = "MANUAL_DEL_SITEMA_DE_GESTION_COLAS.pdf";
        manualFile = new File(rutaManual);
//        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        //añadirAreasHashMap();
    }

//    private void validarIpDispositivo(Usuario user) throws InterruptedException {
//        try {
//            InetAddress ipLocalHost = InetAddress.getLocalHost();
//            String ipv4 = ipLocalHost.getHostAddress();
//            int idArea = obtenerIdAreaPorIp(ipv4);
//            if (idArea != 0) {
//                mostrarEscogerCarpetaVideo(idArea);
//            } else {
//                mostrarFrmSeleccionarVentanilla(user, idArea);
//            }
//
//        } catch (UnknownHostException ex) {
//            JOptionPane.showMessageDialog(this, ex, "Alerta", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//    private int obtenerIdAreaPorIp(String ip) {
//        int idArea = 0;
//        try {
//            establecerConexion();
//            CallableStatement cst;
//            String filtro = " WHERE IpPantallaVisualizacion = '" + ip + "'";
//            cst = llamarConexion.prepareCall("{call [MostrarConfiguracionesAreasVentanillas](?)}");
//            cst.setString(1, filtro);
//
//            rs = cst.executeQuery();
//            if (rs.next()) {
//                idArea = rs.getInt(1);
//            }
//            cerrarConexion();
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
//        }
//        return idArea;
//    }
//    private Integer seleccionarArea(String nombreArea) {
//        if (areas.containsKey(nombreArea)) {
//            return areas.get(nombreArea);
//        } else {
//            return 0;
//        }
//    }
    
    /**
     * Metodo que permite validar los datos ingresados por el usuario.
     * 
     * @throws InterruptedException 
     */
    private void validarUsuario() throws InterruptedException {
        try {
            establecerConexion();
            CallableStatement cst;
            String usuarioIngresado = txtNombreUsuario.getText();
            cst = llamarConexion.prepareCall("{call [loginVentanilla_test](?,?,?)}");
            cst.setString(1, usuarioIngresado);
            char[] contrasena = pwdContrasena.getPassword();
            cst.setString(2, new String(contrasena));
            Arrays.fill(contrasena, '0');
            cst.registerOutParameter(3, Types.VARCHAR);

            boolean obtuboResultados = cst.execute();
            if (obtuboResultados) {
                rs = cst.getResultSet();
                if (rs.next()) {
                    int IdRol = 0;
                    this.codigoResultado = "0000";
                    try {
                        String rol = "";
                        cst = llamarConexion.prepareCall("{call [VerificarRolVentanilla](?)}");
                        cst.setString(1, usuarioIngresado);
                        try (ResultSet rs = cst.executeQuery()) {
                            if (rs.next()) {
                                rol = rs.getString(5);
                                IdRol = rs.getInt(4);
                            }
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, "Error al obtener el ID de descanso: " + e.getMessage(), "Alerta", JOptionPane.ERROR_MESSAGE);
                    }
                    user = new Usuario(rs.getInt(4), rs.getString(1), IdRol);
                    mostrarFrmSeleccionarVentanilla();
                    //validarIpDispositivo(user);
                } else {
                    this.codigoResultado = "0051";
                    JOptionPane.showMessageDialog(this, "Contraseña incorrecta", "Alerta", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "El usuario no existe", "Alerta", JOptionPane.ERROR_MESSAGE);
            }
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que sirve para mostrar la ventana de frmSeleccionarVentanilla.
     */
    private void mostrarFrmSeleccionarVentanilla() {
        this.dispose();
        this.vtnSeleccionarVentanilla = new frmSeleccionarVentanilla();
        this.vtnSeleccionarVentanilla.setLocationRelativeTo(null);
        this.vtnSeleccionarVentanilla.setVisible(true);
    }

    /**
     * @deprecated 
     * 
     * @return 
     */
    public String getCodigoResultado() {
        return codigoResultado;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblHeader = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnAcceder = new javax.swing.JButton();
        pwdContrasena = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        txtNombreUsuario = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        lblAyudaInicioSesion = new javax.swing.JLabel();
        lblSalir = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        jButton1.setText("Configuracion");
        jButton1.setVisible(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(0, 204, 255));

        lblHeader.setBackground(new java.awt.Color(0, 102, 102));
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 28)); // NOI18N
        lblHeader.setForeground(new java.awt.Color(255, 255, 255));
        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader.setText("<html><div style=\"text-align:center;\">GESTION DE COLAS</div></html>");
        lblHeader.setToolTipText("");
        lblHeader.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblHeader.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblHeader.setOpaque(true);
        lblHeader.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                lblHeaderMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                lblHeaderMouseMoved(evt);
            }
        });
        lblHeader.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblHeaderMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(lblHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Inter", 0, 24)); // NOI18N
        jLabel4.setText("Inicio de Sesión");

        btnAcceder.setBackground(new java.awt.Color(255, 255, 255));
        btnAcceder.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAcceder.setText("Acceder");
        btnAcceder.setBorder(null);
        btnAcceder.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnAcceder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccederActionPerformed(evt);
            }
        });

        pwdContrasena.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pwdContrasena.setBorder(null);

        jLabel3.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        jLabel3.setText("Contraseña: ");

        txtNombreUsuario.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombreUsuario.setBorder(null);

        jLabel2.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        jLabel2.setText("Usuario: ");

        jSeparator2.setBackground(new java.awt.Color(0, 102, 102));
        jSeparator2.setForeground(new java.awt.Color(0, 102, 102));

        jSeparator3.setBackground(new java.awt.Color(0, 102, 102));
        jSeparator3.setForeground(new java.awt.Color(0, 102, 102));

        jPanel4.setBackground(new java.awt.Color(0, 102, 102));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 4, Short.MAX_VALUE)
        );

        lblAyudaInicioSesion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblAyudaInicioSesion.setText("<html><u>¿Cómo inicio sesión?</u></html>");
        lblAyudaInicioSesion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAyudaInicioSesion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAyudaInicioSesionMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAyudaInicioSesionMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAyudaInicioSesionMouseExited(evt);
            }
        });

        lblSalir.setBackground(new java.awt.Color(204, 0, 0));
        lblSalir.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblSalir.setForeground(new java.awt.Color(204, 0, 0));
        lblSalir.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-logout-32_1.png"))); // NOI18N
        lblSalir.setText("Salir");
        lblSalir.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0)));
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                            .addComponent(lblAyudaInicioSesion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(pwdContrasena, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtNombreUsuario, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnAcceder, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jSeparator3)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(7, 7, 7)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pwdContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAcceder, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAyudaInicioSesion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAccederActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccederActionPerformed
        /**
                * Permite validar si los campos estan vacios.
                */
        if (txtNombreUsuario.getText().isEmpty() || pwdContrasena.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Rellene todos los campos", "Alerta", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                validarUsuario();
            } catch (InterruptedException ex) {
                Logger.getLogger(frmLogin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnAccederActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cerrarConexion();
    }//GEN-LAST:event_formWindowClosing

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
//        if (vtnConfiguracion == null) {
//            vtnConfiguracion = new frmConfiguracion();
//            vtnConfiguracion.addWindowListener(new WindowAdapter() {
//                @Override
//                public void windowClosing(WindowEvent e) {
//                    vtnConfiguracion = null;
//                }
//            });
//            vtnConfiguracion.pack();
//            vtnConfiguracion.setLocationRelativeTo(null);
//            vtnConfiguracion.getContentPane().setBackground(Color.decode("#ffffff"));
//            vtnConfiguracion.setVisible(true);
//        } else {
//            vtnConfiguracion.toFront();
//            vtnConfiguracion.repaint();
//        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void lblHeaderMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblHeaderMouseMoved

    }//GEN-LAST:event_lblHeaderMouseMoved

    private void lblHeaderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblHeaderMousePressed
        // Se toman las coordenadas del mouse en pantalla.
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_lblHeaderMousePressed

    private void lblHeaderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblHeaderMouseDragged
        // Mover la interfaz en la pantalla.
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xMouse, y - yMouse);
    }//GEN-LAST:event_lblHeaderMouseDragged

    private void lblAyudaInicioSesionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaInicioSesionMouseClicked
        //Se abre el manual.
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(manualFile);
            } catch (IOException ex) {
                Logger.getLogger(frmLogin.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "El sistema no soporta la función Desktop");
            System.out.println("El sistema no soporta la función Desktop");
        }
    }//GEN-LAST:event_lblAyudaInicioSesionMouseClicked

    private void lblAyudaInicioSesionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaInicioSesionMouseEntered
        lblAyudaInicioSesion.setForeground(Color.decode("#006666"));
    }//GEN-LAST:event_lblAyudaInicioSesionMouseEntered

    private void lblAyudaInicioSesionMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAyudaInicioSesionMouseExited
        lblAyudaInicioSesion.setForeground(Color.decode("#000000"));
    }//GEN-LAST:event_lblAyudaInicioSesionMouseExited

    private void lblSalirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSalirMouseClicked
        System.exit(0);
    }//GEN-LAST:event_lblSalirMouseClicked

    private void lblSalirMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSalirMouseEntered
        lblSalir.setOpaque(true);
        lblSalir.setForeground(Color.white);
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblSalirMouseEntered

    private void lblSalirMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSalirMouseExited
        lblSalir.setOpaque(false);
        lblSalir.setForeground(Color.decode("#CC0000"));
        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_lblSalirMouseExited

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
//            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new frmLogin().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAcceder;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblAyudaInicioSesion;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblSalir;
    private javax.swing.JPasswordField pwdContrasena;
    private javax.swing.JTextField txtNombreUsuario;
    // End of variables declaration//GEN-END:variables
}
