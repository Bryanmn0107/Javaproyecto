/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import static conexion.ConexionSQL.cerrarConexion;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelos.Area;
import static modulos.modelos.ModuleArea.agregarAreasComboBox;
import static modulos.modelos.ModuleConfiguracion.agregarNumeroVentanillasComboBox;
import videoplayer.EscogerCarpeta;
import static videoplayer.EscogerCarpeta.ruta;
import static vistas.frmPantallaVisualizador.videoPlayer;

/**
 * Clase que permite instanciar el manejador de la pantalla de visualizacion.
 * 
 * @author jnxd_
 */
public class frmHandlerVisualizador extends javax.swing.JFrame {

    /**
     * Instancia de frmPantallaVisualizador para mostrar posteriormente.
     */
    public static frmPantallaVisualizador vtnPantallaVisualizador = null;

    /**
     * Variable para validar si la Pantalla Visualizadora está abierta o cerrada.
     */
    public static boolean isOpen = false;
    
    /**
     * Variable para validar si la ventana está maximizada o no.
     */
    private boolean isMinimized = false;
    
    /**
     * Variable que almacena el nombre del area seleccionada.
     */
    private String areaSeleccionada;
    
    /**
     * Variable que almacena el id del area seleccionada.
     */
    private Integer idArea;
    
    /**
     * Instancia de la libreria Gson que sirve para leer el archivo json 
     * que contiene la ruta de la carpeta de videos guardada.
     */
    private Gson gson;

    /**
     * Instancia de frmConfiguracion para mostrar posteriormente.
     */
    private frmConfiguracion vtnConfiguracion;

    /**
     * Variable que valida si la ventana esta cerrada. 
     */
    public boolean isDisposed = false;

    /**
     * Coordenadas de x & y del mouse.
     */
    int xMouse;
    int yMouse;

    /**
     * Constructor de la clase.
     */
    public frmHandlerVisualizador() {
        // new FileReader("configMedia.json");
        gson = new Gson();
        initComponents();
        agregarAreasComboBox(cbAreas);
        detectarAreaSeleccionada();
    }

    /**
     * Metodo que sirve para mostrar en pantalla la instancia de frmPantallaVisualizador.
     */
    private void abrirFrmPantallaVisualizador() {
        if (vtnPantallaVisualizador == null) {
            vtnPantallaVisualizador = new frmPantallaVisualizador(idArea, areaSeleccionada);
            vtnPantallaVisualizador.pack();
            vtnPantallaVisualizador.setLocationRelativeTo(this);
            vtnPantallaVisualizador.setVisible(true);
        }
    }

    /**
     * Metodo que sirve para detectar el area seleccionada del JComboBox cbAreas.
     */
    private void detectarAreaSeleccionada() {
        cbAreas.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (cbAreas.getSelectedIndex() != 0) {
                    Area area = (Area) cbAreas.getSelectedItem();
                    areaSeleccionada = area.getNombre();
                    idArea = area.getIdArea();
                    btnAbrirPantalla.setEnabled(true);
                    btnConfiguraciones.setEnabled(true);
                    btnChooseCarpetaVideos.setEnabled(true);
                    btnResetRutaVideos.setEnabled(true);
                } else {
                    btnAbrirPantalla.setEnabled(false);
                    btnConfiguraciones.setEnabled(false);
                    btnChooseCarpetaVideos.setEnabled(false);
                    btnResetRutaVideos.setEnabled(false);
                }
            }
        });
    }

    /**
     * Metodo que sirve para mostrar en pantalla la instancia de frmConfiguraciones.
     */
    private void abrirConfiguraciones() {
        if (this.vtnConfiguracion == null || this.vtnConfiguracion.isDisposed) {
            this.vtnConfiguracion = new frmConfiguracion(idArea, areaSeleccionada);
            this.vtnConfiguracion.pack();
            this.vtnConfiguracion.setLocationRelativeTo(null);
            this.vtnConfiguracion.setVisible(true);
        } else {
            this.vtnConfiguracion.toFront();
            this.vtnConfiguracion.repaint();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnAbrirPantalla = new javax.swing.JButton();
        btnMinimizarPantalla = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaTerminal = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        cbAreas = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblExitButton = new javax.swing.JLabel();
        lblMinimizeButton = new javax.swing.JLabel();
        btnConfiguraciones = new javax.swing.JButton();
        btnChooseCarpetaVideos = new javax.swing.JButton();
        btnResetRutaVideos = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnAbrirPantalla.setBackground(new java.awt.Color(255, 255, 255));
        btnAbrirPantalla.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAbrirPantalla.setText("Abrir Pantalla");
        btnAbrirPantalla.setEnabled(false);
        btnAbrirPantalla.setFocusable(false);
        btnAbrirPantalla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirPantallaActionPerformed(evt);
            }
        });

        btnMinimizarPantalla.setBackground(new java.awt.Color(255, 255, 255));
        btnMinimizarPantalla.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnMinimizarPantalla.setText("Minimizar Pantalla");
        btnMinimizarPantalla.setEnabled(false);
        btnMinimizarPantalla.setFocusable(false);
        btnMinimizarPantalla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMinimizarPantallaActionPerformed(evt);
            }
        });

        txaTerminal.setEditable(false);
        txaTerminal.setColumns(1);
        txaTerminal.setLineWrap(true);
        txaTerminal.setRows(5);
        txaTerminal.setWrapStyleWord(true);
        txaTerminal.setCaretPosition(txaTerminal.getDocument().getLength());
        jScrollPane1.setViewportView(txaTerminal);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Log:");

        cbAreas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cbAreas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar..." }));
        cbAreas.setFocusable(false);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Area: ");

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 51));
        jPanel2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel2MouseDragged(evt);
            }
        });
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel2MousePressed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Visualizador");

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 153, Short.MAX_VALUE)
                .addComponent(lblMinimizeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lblExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMinimizeButton)
                            .addComponent(lblExitButton))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        btnConfiguraciones.setBackground(new java.awt.Color(255, 255, 255));
        btnConfiguraciones.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnConfiguraciones.setText("Configuraciones");
        btnConfiguraciones.setEnabled(false);
        btnConfiguraciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfiguracionesActionPerformed(evt);
            }
        });

        btnChooseCarpetaVideos.setBackground(new java.awt.Color(255, 255, 255));
        btnChooseCarpetaVideos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnChooseCarpetaVideos.setText("Escoger carpeta de videos");
        btnChooseCarpetaVideos.setEnabled(false);
        btnChooseCarpetaVideos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseCarpetaVideosActionPerformed(evt);
            }
        });

        btnResetRutaVideos.setBackground(new java.awt.Color(255, 255, 255));
        btnResetRutaVideos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnResetRutaVideos.setText("Reiniciar ruta video");
        btnResetRutaVideos.setEnabled(false);
        btnResetRutaVideos.setFocusPainted(false);
        btnResetRutaVideos.setFocusable(false);
        btnResetRutaVideos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetRutaVideosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(btnAbrirPantalla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMinimizarPantalla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbAreas, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConfiguraciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnChooseCarpetaVideos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnResetRutaVideos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbAreas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMinimizarPantalla)
                .addGap(13, 13, 13)
                .addComponent(btnAbrirPantalla)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnConfiguraciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnResetRutaVideos)
                    .addComponent(btnChooseCarpetaVideos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    private void btnAbrirPantallaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirPantallaActionPerformed
        // Si isOpen es true, se muestra la pantalla de visualizacion de turnos, si no se cierra.
        if (!isOpen) {
            isOpen = true;
            btnConfiguraciones.setEnabled(false);
            btnChooseCarpetaVideos.setEnabled(false);
            btnResetRutaVideos.setEnabled(false);
            abrirFrmPantallaVisualizador();
            cbAreas.setEnabled(false);
            btnAbrirPantalla.setText("Cerrar Pantalla");
        } else {
            cbAreas.setEnabled(true);
            btnConfiguraciones.setEnabled(true);
            btnChooseCarpetaVideos.setEnabled(true);
            btnResetRutaVideos.setEnabled(true);
            btnAbrirPantalla.setText("Abrir Pantalla");
            btnMinimizarPantalla.setEnabled(false);
            if (vtnPantallaVisualizador.isConexion) {
                vtnPantallaVisualizador.cliente.confirmarDesconexion();
            }
            vtnPantallaVisualizador.dispose();
            if (videoPlayer != null) {
                videoPlayer.dispose();
            }
            vtnPantallaVisualizador = null;
            isOpen = false;
        }
    }//GEN-LAST:event_btnAbrirPantallaActionPerformed

    private void btnMinimizarPantallaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMinimizarPantallaActionPerformed
        vtnPantallaVisualizador.setState(Frame.ICONIFIED);
    }//GEN-LAST:event_btnMinimizarPantallaActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // Desconectar cliente del servidor de sockets si se cumple la condicion.
        if (vtnPantallaVisualizador != null && vtnPantallaVisualizador.isConexion) {
            vtnPantallaVisualizador.cliente.confirmarDesconexion();
        }
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // Si el formulario esta cerrado, validamos que la pantalla de visualizacion sea null para forzar
        // a cerrar el cliente.
        if (vtnPantallaVisualizador != null) {
            vtnPantallaVisualizador.cliente = null;
        }
    }//GEN-LAST:event_formWindowClosed

    private void lblExitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblExitButtonMouseClicked
        // Desconectar cliente del servidor de sockets si se cumple la condicion.
        if (vtnPantallaVisualizador != null && vtnPantallaVisualizador.isConexion) {
            vtnPantallaVisualizador.cliente.confirmarDesconexion();
        }
        isDisposed = true;
        System.exit(0);
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

    private void jPanel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MousePressed
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_jPanel2MousePressed

    private void jPanel2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xMouse, y - yMouse);
    }//GEN-LAST:event_jPanel2MouseDragged

    private void btnConfiguracionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfiguracionesActionPerformed
        abrirConfiguraciones();
    }//GEN-LAST:event_btnConfiguracionesActionPerformed

    private void btnChooseCarpetaVideosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseCarpetaVideosActionPerformed
        btnChooseCarpetaVideos.setEnabled(false);
        EscogerCarpeta escogerCarpeta = new EscogerCarpeta();
        escogerCarpeta.escogerC();
        btnChooseCarpetaVideos.setEnabled(true);
    }//GEN-LAST:event_btnChooseCarpetaVideosActionPerformed

    private void btnResetRutaVideosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetRutaVideosActionPerformed
        try {
            // new FileReader("configMedia.json");
            // new FileReader(this.getClass().getResource("/resources/configMedia.json").getPath());
            FileReader readerArchivoJsonConfig = new FileReader(this.getClass().getResource("/resources/configMedia.json").getPath());
            Map datos = gson.fromJson(readerArchivoJsonConfig, Map.class);
            readerArchivoJsonConfig.close();
            datos.put("rutaVideo", "");
            FileWriter writerArchivoJsonConfig = new FileWriter(this.getClass().getResource("/resources/configMedia.json").getPath());
            writerArchivoJsonConfig.write(gson.toJson(datos));
            writerArchivoJsonConfig.close();
            JOptionPane.showMessageDialog(this, "Ruta reiniciada!. Si desea puede escoger otra ruta", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(frmHandlerVisualizador.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            Logger.getLogger(frmHandlerVisualizador.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            frmHandlerVisualizador.txaTerminal.append(e.getMessage());
        }
    }//GEN-LAST:event_btnResetRutaVideosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JButton btnAbrirPantalla;
    public static javax.swing.JButton btnChooseCarpetaVideos;
    public static javax.swing.JButton btnConfiguraciones;
    public static javax.swing.JButton btnMinimizarPantalla;
    public static javax.swing.JButton btnResetRutaVideos;
    public static javax.swing.JComboBox<Object> cbAreas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblExitButton;
    private javax.swing.JLabel lblMinimizeButton;
    public static javax.swing.JTextArea txaTerminal;
    // End of variables declaration//GEN-END:variables
}
