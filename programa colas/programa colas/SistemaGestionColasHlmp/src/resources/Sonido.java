package resources;

import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.JOptionPane;

/**
 * Clase que permite crear una instancia de Sonido,
 * importante para la reproduccion de un sonido en las notificaciones.
 * 
 * @author FERNANDO
 */
public class Sonido {

    /**
     * obtiene la ruta del archivo convierte en un archivo temporal lee el archivo y lo retorna 
     * @return 
     */
    private String obtenerRutaSonido() {
        try {
            // Obtén la ruta relativa del paquete "updaters"
            String rutaPaquete = "/resources/";

            // Nombre del archivo de Sonido
            String nombreArchivo = "timbre.wav";

            // Construye la ruta completa al archivo de Sonido
            String rutaCompleta = rutaPaquete + nombreArchivo;

            // Usa ClassLoader para obtener el recurso como un flujo de entrada
            InputStream inputStream = getClass().getResourceAsStream(rutaCompleta);
            //System.out.println(rutaCompleta);
            // Crea un archivo temporal para el Sonido
            // Esto puede variar dependiendo de tus necesidades, aquí se usa un archivo temporal en el sistema
            // Pero podrías ajustarlo según tus necesidades
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("sonido", ".wav");

            // Copia el contenido del flujo de entrada al archivo temporal
            java.nio.file.Files.copy(inputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Devuelve la ruta al archivo temporal
            return tempFile.toString();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener la ruta del sonido", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Crea un hilo para reproducir el sonido una sola vez, sin que se quede estatico.
     */
    public void reproducirSonido() {
        // Crea un nuevo hilo para reproducir el sonido
        Thread thread = new Thread(() -> {
            try {
                // Obtén la ruta del Sonido
                String filePath = obtenerRutaSonido();

                if (filePath != null) {
                    // Crea un flujo de entrada de audio desde el archivo
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new java.io.File(filePath));

                    // Obtiene un Clip de Sonido
                    Clip clip = AudioSystem.getClip();

                    // Abre el flujo de audio y carga los datos del Sonido en el Clip
                    clip.open(audioInputStream);

                    // Agrega un listener para detectar cuando el sonido ha terminado
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            // Cierra el clip después de reproducir el sonido
                            clip.close();
                        }
                    });

                    // Reproduce el Sonido
                    clip.start();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al reproducir el sonido", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });

        // Inicia el hilo
        thread.start();
    }
}
