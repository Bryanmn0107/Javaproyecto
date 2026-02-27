package videoplayer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vistas.frmHandlerVisualizador;

/**
 * La clase EscogerCarpeta permite al usuario seleccionar una carpeta que contenga archivos de video MP4.
 * También maneja la actualización de una configuración JSON con la ruta de la carpeta seleccionada.
 * 
 * @autor FERNANDO
 */
public class EscogerCarpeta {

    public static String ruta; // Ruta de la carpeta seleccionada.
    public static ArrayList<String> videoPaths; // Lista de rutas de archivos de video en la carpeta seleccionada
    private Gson gson = new Gson();

    /**
     * Constructor de la clase EscogerCarpeta.
     */
    public EscogerCarpeta() {
        // Constructor vacío, se puede utilizar para inicializar recursos si es necesario
    }

    /**
     * Método que abre un diálogo para seleccionar una carpeta.
     */
    public void escogerC() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        setFileFilter(folderChooser);

        int result = folderChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            processSelectedFolder(folderChooser);
        }
    }

    /**
     * Configura el filtro de archivos para que solo acepte carpetas que contengan videos MP4.
     * 
     * @param folderChooser El JFileChooser al cual se le aplicará el filtro.
     */
    private void setFileFilter(JFileChooser folderChooser) {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File f : files) {
                        if (f.isFile() && f.getName().toLowerCase().endsWith(".mp4")) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Carpetas con videos MP4";
            }
        };
        folderChooser.setFileFilter(filter);
    }

    /**
     * Procesa la carpeta seleccionada, actualiza la configuración JSON con la nueva ruta y muestra un mensaje en la interfaz.
     * 
     * @param folderChooser El JFileChooser que contiene la carpeta seleccionada.
     */
    private void processSelectedFolder(JFileChooser folderChooser) {
        try {
            // new FileReader("configMedia.json");
            // new FileReader(this.getClass().getResource("/resources/configMedia.json").getPath());
            File selectedFolder = folderChooser.getSelectedFile();
            ruta = selectedFolder.getAbsolutePath();
            FileReader readerArchivoJsonConfig = new FileReader(this.getClass().getResource("/resources/configMedia.json").getPath());
            Map datos = gson.fromJson(readerArchivoJsonConfig, Map.class);
            readerArchivoJsonConfig.close();
            datos.put("rutaVideo", ruta);
            FileWriter writerArchivoJsonConfig = new FileWriter(this.getClass().getResource("/resources/configMedia.json").getPath());
            writerArchivoJsonConfig.write(gson.toJson(datos));
            writerArchivoJsonConfig.close();
            frmHandlerVisualizador.txaTerminal.append("Carpeta seleccionada: " + ruta + "\n");
        } catch (IOException ex) {
            Logger.getLogger(EscogerCarpeta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Configura la lista de rutas de videos en la carpeta seleccionada.
     * 
     * @param ruta La ruta de la carpeta que contiene los videos.
     */
    public static void setVideoPaths(String ruta) {
        File selectedFolder = new File(ruta);
        File[] filesInFolder = selectedFolder.listFiles();
        videoPaths = new ArrayList<>();
        if (filesInFolder != null) {
            addVideoPaths(filesInFolder);
        }
    }

    /**
     * Añade las rutas de los archivos de video MP4 en la carpeta seleccionada a la lista de rutas de videos.
     * 
     * @param filesInFolder Los archivos en la carpeta seleccionada.
     */
    private static void addVideoPaths(File[] filesInFolder) {
        for (File f : filesInFolder) {
            if (f.isFile() && f.getName().toLowerCase().endsWith(".mp4")) {
                videoPaths.add(f.getAbsolutePath());
            }
        }
        // Ahora 'videoPaths' contiene las rutas de todos los videos MP4 en la carpeta seleccionada
        for (String path : videoPaths) {
            System.out.println("Video encontrado: " + path);
            // Aquí podrías manejar cómo utilizar los caminos de los videos en tu interfaz
        }
    }
}
