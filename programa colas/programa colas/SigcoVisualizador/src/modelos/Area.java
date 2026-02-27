/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

/**
  * La clase Area representa un área con un identificador y un nombre.
 * 
 * Esta clase se utiliza para modelar las áreas dentro de una tabla llamada AreasVentanillas.
 * Cada área tiene un identificador único y un nombre.
 * @author jnxd_
 */
public class Area {
    
    // Columnas de la tabla AreasVentanillas
    
    /**
     * Identificador único del área.
     */
    private int idArea;
    
    /**
     * Nombre del área.
     */
    private String nombre;

    /**
     * Constructor de la clase Area.
     * 
     * @param idArea El identificador del área.
     * @param nombre El nombre del área.
     */
    public Area(int idArea, String nombre) {
        this.idArea = idArea;
        this.nombre = nombre;
    }

    /**
     * Obtiene el identificador del área.
     * 
     * @return El identificador del área.
     */
    public int getIdArea() {
        return idArea;
    }

    /**
     * Establece el identificador del área.
     * 
     * @param idArea El nuevo identificador del área.
     */
    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    /**
     * Obtiene el nombre del área.
     * 
     * @return El nombre del área.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del área.
     * 
     * @param nombre El nuevo nombre del área.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve una representación en forma de cadena del área.
     * 
     * @return El nombre del área.
     */
    @Override
    public String toString() {
        return nombre; //To change body of generated methods, choose Tools | Templates.
    }
}
