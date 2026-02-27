/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

import cliente_sources.Cliente;
import cliente_sources.VentanaCliente;
import com.clases.ImageComponent;
import com.clases.JPanelImageVersion;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import static conexion.ConexionSQL.cerrarConexion;
import static conexion.ConexionSQL.establecerConexion;
import static conexion.ConexionSQL.llamarConexion;
import static conexion.ConexionSQL.rs;
import modelos.Paciente;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.UIManager;
import modelos.Atencion;
import videoplayer.EscogerCarpeta;
import static videoplayer.EscogerCarpeta.videoPaths;
import videoplayer.JPanelImage;
import videoplayer.VideoPlayer;

/**
 * Clase que permite instanciar la pantalla de visualizacion de turnos.
 * 
 * @author jnxd_
 */
public class frmPantallaVisualizador extends VentanaCliente {

    /**
     * Variable que instancia un cliente de socket.
     */
    public Cliente cliente;
    
    /**
     * Constante que identifica al cliente.
     */
    private final String IDENTIFICADOR = "Visualizador";
    
    /**
     * Variable que valida si la ventana esta cerrada.
     */
    public boolean isDisposed = false;
    
    /**
     * Variable que permite visualizar un video.
     */
    JPanelImage imagenFondo;
    
    /**
     * Variable que permite visualizar una imagen.
     */
    JPanelImageVersion imagenFondoVersion;

    /**
     * Variable que almaecena el json de las configuraciones de la ruta de videos.
     */
    private JsonObject configMediaJson;
    
    /**
     * Variable que instancia la libreria Gson.
     */
    private Gson gson = new Gson();
    
    /**
     * Variable que instancia un VideoPlayer.
     */
    public static VideoPlayer videoPlayer;
    //Component[] filas;
    //ImageIcon iconoPreferencial = new ImageIcon("src\\interfaces\\preferencialIcon.png");
    //ArrayList<JPanel> listaFilasPaneles = new ArrayList();
    //ArrayList<Paciente> pacientes = new ArrayList();
    //Font fuenteNombreTurno = new Font("Arial", Font.BOLD, 24);
    //Font fuenteDniTurno = new Font("Arial", Font.PLAIN, 18);

    /**
     * Variable que almacena la ip del servidor de sockets.
     */
    private String IpServidorSocket;
    
    /**
     * Variable que almacena el puerto de comunicacion TCP hacia el servidor.
     */
    private int puerto;

    /**
     * Variable que almacena el texto principal.
     */
    private String textoPrincipal;
    
    /**
     * Variable que almacena el texto secundario.
     */
    private String textoSecundario;

    /**
     * Constructor de la clase.
     * 
     * @param idArea --> El id del area.
     * @param nombreArea --> El nombre del area.
     */
    public frmPantallaVisualizador(Integer idArea, String nombreArea) {
        try {
            initComponents();
            lblArea.setText(nombreArea.toUpperCase());
            filas = pnlTablaContenedora.getComponents();
            slots = pnlContenedorNotificaciones.getComponents();
            //imagenFondo = new JPanelImage(pnlContenedorImagen, "/vistas/hlmpImageView2.jpeg");
            //pnlContenedorImagen.add(imagenFondo).repaint();
            //pnlContenedorImagen.revalidate();
            //pnlContenedorImagen.repaint();
            filtrarFilas();
            filtrarSlots();
            obtenerConfiguracion(idArea);
            obtenerDirectorHospital();
            obtenerPacientesEnCola(idArea);
            lblCampoPrincipal.setText(this.textoPrincipal);
            lblCampoSecundario.setText(this.textoSecundario);
            // new FileReader("configMedia.json");
            // new FileReader(this.getClass().getResource("/resources/configMedia.json").getPath());
            configMediaJson = gson.fromJson(new FileReader(this.getClass().getResource("/resources/configMedia.json").getPath()), JsonObject.class);
            EscogerCarpeta.setVideoPaths(configMediaJson.get("rutaVideo").getAsString());
            if (videoPaths == null || videoPaths.isEmpty()) {
                imagenFondoVersion = new JPanelImageVersion(pnlContenedorImagen, "/vistas/hlmpImageView2.jpeg");
                pnlContenedorImagen.add(imagenFondoVersion).repaint();
                pnlContenedorImagen.revalidate();
                pnlContenedorImagen.repaint();
            } else {
                imagenFondo = new JPanelImage(pnlContenedorImagen);
                videoPlayer = new VideoPlayer(pnlContenedorImagen);
                videoPlayer.play();
            }
            this.pack();
            cliente = new Cliente(this, IpServidorSocket, puerto, IDENTIFICADOR);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(frmPantallaVisualizador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo que permite agregar a una lista de las instancias de los paneles que mostraran
     * a los pacientes en cola.
     */
    private void filtrarFilas() {
        for (Component component : filas) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                if (panel.getName() != null && panel.getName().startsWith("Fila")) {
                    listaFilasPaneles.add(panel);
                }
            }
        }
    }

    /**
     * Metodo que permite agregar a una lista de las instancias de los paneles qe mostraran
     * los turnos en atencion o que son llamados.
     */
    private void filtrarSlots() {
        for (Component component : slots) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                if (panel.getName() != null && panel.getName().startsWith("Slot")) {
                    listaSlotsNotificaciones.add(panel);
                }
            }
        }
    }

    /**
     * Metodo para obtener la configuracion guardada en la base de datos.
     * 
     * @param idArea 
     */
    private void obtenerConfiguracion(Integer idArea) {
        try {
            establecerConexion();
            CallableStatement cst;
            String filtro = " WHERE A.IdArea = '" + idArea + "'";
            cst = llamarConexion.prepareCall("{call [MostrarConfiguracionesAreasVentanillas](?)}");
            cst.setString(1, filtro);

            rs = cst.executeQuery();
            if (rs.next()) {
                //tiempoPromedioAtencion = rs.getInt(5);
                pnlEncabezado.setBackground(Color.decode(rs.getString(11)));
                pnlSlot1.setBackground(Color.decode(rs.getString(9)));
                pnlSlot2.setBackground(Color.decode(rs.getString(9)));
                pnlSlot3.setBackground(Color.decode(rs.getString(9)));
                jSeparator1.setForeground(Color.decode(rs.getString(12)));
                lblTituloEncabezado.setForeground(Color.decode(rs.getString(12)));
                lblArea.setForeground(Color.decode(rs.getString(12)));
                //colorTextoNotificacion=rs.getString(10);
                if (rs.getString(6) == null || rs.getString(6).isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El valor de IP no está configurado", "Alerta", JOptionPane.ERROR_MESSAGE);
                } else {
                    IpServidorSocket = rs.getString(6);
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
     * Metodo que permite obtener los textos principales y secundarios guardados en base de datos.
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
                this.textoPrincipal = rs.getString(1);
                this.textoSecundario = rs.getString(2);
            }
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo que permite obtener al paciente en cola por id de área.
     * 
     * @param idArea --> id del área.
     */
    private void obtenerPacientesEnCola(int idArea) {
        try {
            establecerConexion();
            CallableStatement cst;
            cst = llamarConexion.prepareCall("{call [MostrarTicketPantallaVentanilla](?)}");
            cst.setInt(1, idArea);

            rs = cst.executeQuery();
            while (rs.next()) {
                Paciente pac = new Paciente(rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(1));
                pacientes.add(pac);
            }
            rellenarColumnaCola();
            cerrarConexion();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo reemplazado con obtenerPacientesEnCola().
     * 
     * @deprecated
     * @param idArea 
     */
    private void mostrarTicketsLlamados(int idArea) {
        try {
            establecerConexion();
            CallableStatement cst;
            cst = llamarConexion.prepareCall("{call [TicketsEnAtencion](?,?,?)}");
            cst.setInt(1, 0);
            cst.setInt(2, idArea);
            cst.setInt(3, 0);
            rs = cst.executeQuery();
            String nombre = "";
            while (rs.next()) {
                Paciente pac = new Paciente(rs.getString(5), rs.getString(4), rs.getInt(6), rs.getInt(1));
                Atencion atencion = new Atencion(rs.getString(3), pac);
                atenciones.add(atencion);
            }
            cerrarConexion();
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, sqle, "Alerta", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panBackground = new javax.swing.JPanel();
        pnlEncabezado = new javax.swing.JPanel();
        lblTituloEncabezado = new javax.swing.JLabel();
        lblArea = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        pnlTablaContenedora = new javax.swing.JPanel();
        pnlFila1 = new javax.swing.JPanel();
        pnlFila2 = new javax.swing.JPanel();
        pnlFila3 = new javax.swing.JPanel();
        pnlFila4 = new javax.swing.JPanel();
        pnlFila5 = new javax.swing.JPanel();
        pnlFila6 = new javax.swing.JPanel();
        pnlContenedorNotificaciones = new javax.swing.JPanel();
        pnlSlot1 = new javax.swing.JPanel();
        pnlSlot2 = new javax.swing.JPanel();
        pnlSlot3 = new javax.swing.JPanel();
        pnlContenedorImagen = new javax.swing.JPanel();
        pnlContenedorDirector = new javax.swing.JPanel();
        lblCampoPrincipal = new javax.swing.JLabel();
        lblCampoSecundario = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setUndecorated(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panBackground.setBackground(new java.awt.Color(255, 255, 255));

        pnlEncabezado.setBackground(new java.awt.Color(0, 102, 102));
        pnlEncabezado.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                pnlEncabezadoComponentResized(evt);
            }
        });

        lblTituloEncabezado.setBackground(new java.awt.Color(0, 102, 102));
        lblTituloEncabezado.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        lblTituloEncabezado.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloEncabezado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTituloEncabezado.setText("Proximos turnos:");

        lblArea.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblArea.setForeground(new java.awt.Color(255, 255, 255));
        lblArea.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArea.setText("LABORATORIO");

        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout pnlEncabezadoLayout = new javax.swing.GroupLayout(pnlEncabezado);
        pnlEncabezado.setLayout(pnlEncabezadoLayout);
        pnlEncabezadoLayout.setHorizontalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEncabezadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblTituloEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblArea, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlEncabezadoLayout.setVerticalGroup(
            pnlEncabezadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEncabezadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblArea, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(lblTituloEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTablaContenedora.setBackground(new java.awt.Color(204, 204, 204));
        pnlTablaContenedora.setName("tablaContenedora"); // NOI18N
        pnlTablaContenedora.setLayout(new java.awt.GridLayout(6, 1));

        pnlFila1.setBackground(new java.awt.Color(204, 204, 204));
        pnlFila1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 5));
        pnlFila1.setName("Fila1"); // NOI18N
        pnlFila1.setLayout(new java.awt.GridLayout(2, 0));
        pnlTablaContenedora.add(pnlFila1);

        pnlFila2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 5));
        pnlFila2.setName("Fila2"); // NOI18N
        pnlFila2.setLayout(new java.awt.GridLayout(2, 0));
        pnlTablaContenedora.add(pnlFila2);

        pnlFila3.setBackground(new java.awt.Color(204, 204, 204));
        pnlFila3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 5));
        pnlFila3.setName("Fila3"); // NOI18N
        pnlFila3.setLayout(new java.awt.GridLayout(2, 0));
        pnlTablaContenedora.add(pnlFila3);

        pnlFila4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 5));
        pnlFila4.setName("Fila4"); // NOI18N
        pnlFila4.setLayout(new java.awt.GridLayout(2, 0));
        pnlTablaContenedora.add(pnlFila4);

        pnlFila5.setBackground(new java.awt.Color(204, 204, 204));
        pnlFila5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 5));
        pnlFila5.setName("Fila5"); // NOI18N
        pnlFila5.setLayout(new java.awt.GridLayout(2, 0));
        pnlTablaContenedora.add(pnlFila5);

        pnlFila6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 5));
        pnlFila6.setName("Fila6"); // NOI18N
        pnlFila6.setLayout(new java.awt.GridLayout(2, 0));
        pnlTablaContenedora.add(pnlFila6);

        pnlContenedorNotificaciones.setLayout(new java.awt.GridLayout(1, 3, 1, 0));

        pnlSlot1.setBackground(new java.awt.Color(0, 0, 204));
        pnlSlot1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        pnlSlot1.setName("Slot 1"); // NOI18N
        pnlSlot1.setLayout(new java.awt.GridLayout(2, 1, 0, -50));
        pnlContenedorNotificaciones.add(pnlSlot1);

        pnlSlot2.setBackground(new java.awt.Color(0, 0, 204));
        pnlSlot2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        pnlSlot2.setName("Slot 2"); // NOI18N
        pnlSlot2.setLayout(new java.awt.GridLayout(2, 1, 0, -50));
        pnlContenedorNotificaciones.add(pnlSlot2);

        pnlSlot3.setBackground(new java.awt.Color(0, 0, 204));
        pnlSlot3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        pnlSlot3.setName("Slot 3"); // NOI18N
        pnlSlot3.setLayout(new java.awt.GridLayout(2, 1, 0, -50));
        pnlContenedorNotificaciones.add(pnlSlot3);

        pnlContenedorImagen.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                pnlContenedorImagenComponentResized(evt);
            }
        });

        javax.swing.GroupLayout pnlContenedorImagenLayout = new javax.swing.GroupLayout(pnlContenedorImagen);
        pnlContenedorImagen.setLayout(pnlContenedorImagenLayout);
        pnlContenedorImagenLayout.setHorizontalGroup(
            pnlContenedorImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1003, Short.MAX_VALUE)
        );
        pnlContenedorImagenLayout.setVerticalGroup(
            pnlContenedorImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 497, Short.MAX_VALUE)
        );

        pnlContenedorDirector.setBackground(new java.awt.Color(255, 255, 255));

        lblCampoPrincipal.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblCampoPrincipal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCampoPrincipal.setText("Hospital de Apoyo II - 1 Nuestra Señora de las  Mercedes - Paita");

        lblCampoSecundario.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblCampoSecundario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCampoSecundario.setText("Director del hospital");

        javax.swing.GroupLayout pnlContenedorDirectorLayout = new javax.swing.GroupLayout(pnlContenedorDirector);
        pnlContenedorDirector.setLayout(pnlContenedorDirectorLayout);
        pnlContenedorDirectorLayout.setHorizontalGroup(
            pnlContenedorDirectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContenedorDirectorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlContenedorDirectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCampoPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCampoSecundario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlContenedorDirectorLayout.setVerticalGroup(
            pnlContenedorDirectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContenedorDirectorLayout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(lblCampoPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCampoSecundario, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout panBackgroundLayout = new javax.swing.GroupLayout(panBackground);
        panBackground.setLayout(panBackgroundLayout);
        panBackgroundLayout.setHorizontalGroup(
            panBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBackgroundLayout.createSequentialGroup()
                .addComponent(pnlContenedorNotificaciones, javax.swing.GroupLayout.DEFAULT_SIZE, 1003, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(pnlEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panBackgroundLayout.createSequentialGroup()
                .addGroup(panBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlContenedorDirector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlContenedorImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(pnlTablaContenedora, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panBackgroundLayout.setVerticalGroup(
            panBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBackgroundLayout.createSequentialGroup()
                .addGroup(panBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlContenedorNotificaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlTablaContenedora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panBackgroundLayout.createSequentialGroup()
                        .addComponent(pnlContenedorImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(pnlContenedorDirector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized

    }//GEN-LAST:event_formComponentResized

    private void pnlContenedorImagenComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_pnlContenedorImagenComponentResized
        if (imagenFondoVersion != null) {
            imagenFondoVersion.redimension(evt.getComponent().getWidth(), evt.getComponent().getHeight());
            pnlContenedorImagen.revalidate();
            pnlContenedorImagen.repaint();
        }
    }//GEN-LAST:event_pnlContenedorImagenComponentResized

    private void pnlEncabezadoComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_pnlEncabezadoComponentResized
//        Font labelFont = lblTituloEncabezado.getFont();
//        String labelText = lblTituloEncabezado.getText();
//        int stringWidth = lblTituloEncabezado.getFontMetrics(labelFont).stringWidth(labelText);
//        int componentWidth = evt.getComponent().getWidth();
//        // Find out how much the font can grow in width.
//        double widthRatio = (double) componentWidth / (double) stringWidth;
//        int newFontSize = (int) (labelFont.getSize() * widthRatio);
//        int componentHeight = evt.getComponent().getHeight();
//        // Pick a new font size so it will not be larger than the height of label.
//        int fontSizeToUse = Math.min(newFontSize, componentHeight);
//        // Set the label's font size to the newly determined size.
//        lblTituloEncabezado.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
    }//GEN-LAST:event_pnlEncabezadoComponentResized

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // Cierra la conexion con el servidor.
        if (isConexion) {
            cliente.confirmarDesconexion();
        }
//        videoPlayer.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        cliente = null;
    }//GEN-LAST:event_formWindowClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblArea;
    private javax.swing.JLabel lblCampoPrincipal;
    private javax.swing.JLabel lblCampoSecundario;
    private javax.swing.JLabel lblTituloEncabezado;
    private javax.swing.JPanel panBackground;
    private javax.swing.JPanel pnlContenedorDirector;
    private javax.swing.JPanel pnlContenedorImagen;
    private javax.swing.JPanel pnlContenedorNotificaciones;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlFila1;
    private javax.swing.JPanel pnlFila2;
    private javax.swing.JPanel pnlFila3;
    private javax.swing.JPanel pnlFila4;
    private javax.swing.JPanel pnlFila5;
    private javax.swing.JPanel pnlFila6;
    private javax.swing.JPanel pnlSlot1;
    private javax.swing.JPanel pnlSlot2;
    private javax.swing.JPanel pnlSlot3;
    private javax.swing.JPanel pnlTablaContenedora;
    // End of variables declaration//GEN-END:variables

}
