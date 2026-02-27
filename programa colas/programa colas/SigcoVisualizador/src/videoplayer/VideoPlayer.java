/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videoplayer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import static videoplayer.EscogerCarpeta.videoPaths;

/**
 * La clase VideoPlayer permite reproducir videos utilizando JavaFX Media y MediaPlayer en un JPanel.
 * 
 * @author jnxd_
 */
public class VideoPlayer {

    private final JFXPanel jfxPanel = new JFXPanel();
    private int currentVideoIndex = 0;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private JPanel panelMultimedia;

    /**
     * Constructor de la clase VideoPlayer que inicializa el reproductor de video.
     * 
     * @param panel El JPanel donde se mostrará el reproductor de video.
     */
    public VideoPlayer(JPanel panel) {
        panelMultimedia = panel;
        jfxPanel.setPreferredSize(new Dimension(panel.getWidth(), panel.getHeight()));
        panel.setLayout(new BorderLayout());
        panel.add(jfxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(this::initFX);
    }

    /**
     * Inicializa la plataforma JavaFX y crea la escena para el reproductor de video.
     */
    private void initFX() {
        Scene scene = createScene();
        jfxPanel.setScene(scene);
    }

    /**
     * Crea la escena para el reproductor de video.
     * 
     * @return La escena creada.
     */
    private Scene createScene() {
        Group root = new Group();
        mediaView = new MediaView();
        mediaView.setPreserveRatio(false);
        root.getChildren().add(mediaView);
        Scene scene = new Scene(root);
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            mediaView.setFitWidth(newValue.doubleValue());
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            mediaView.setFitHeight(newValue.doubleValue());
        });
        return scene;
    }

    /**
     * Reproduce el video actualmente seleccionado.
     */
    public void play() {
        try {
            String currentVideoPath = videoPaths.get(currentVideoIndex);
            File mediaFile = new File(currentVideoPath);
            String mediaUrl = mediaFile.toURI().toString();
            Platform.runLater(() -> {
                if (mediaPlayer != null) {
                    mediaPlayer.dispose();
                }
                Media media = new Media(mediaUrl);
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnEndOfMedia(() -> {
                    currentVideoIndex = (currentVideoIndex + 1) % videoPaths.size();
                    play();
                });
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setAutoPlay(true);
                mediaView.setMediaPlayer(mediaPlayer);
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se encontraron videos", e.toString(), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Detiene la reproducción del video actual.
     */
    public void stop() {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });
    }

    /**
     * Libera los recursos utilizados por el reproductor de video.
     */
    public void dispose() {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }
        });
        jfxPanel.removeAll();
    }
    
    /**
     * Cambia las dimensiones del panel de JavaFX para que coincida con las dimensiones del panel contenedor.
     */
    public void changeDimensionsJfxPanel() {
        jfxPanel.setPreferredSize(new Dimension(panelMultimedia.getWidth(), panelMultimedia.getHeight()));
    }
}
