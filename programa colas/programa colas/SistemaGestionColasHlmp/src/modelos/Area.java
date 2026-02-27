/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

/**
 * Clase que permite crear una instancia de Area.
 * 
 * @author jnxd_
 */
public class Area {
    
    /**
     * Variables que referencian a las columnas de la tabla AreasVentanillas.
     */
    // Columnas de la tabla AreasVentanillas
    private int idArea;
    private String nombre;

    /**
     * Constructor de la clase.
     * 
     * @param idArea
     * @param nombre 
     */
    public Area(int idArea, String nombre) {
        this.idArea = idArea;
        this.nombre = nombre;
    }

    /**
     * Metodo get de idArea.
     * @return 
     */
    public int getIdArea() {
        return idArea;
    }

    /**
     * Metodo set de idArea.
     * @param idArea 
     */
    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    /**
     * Metodo get de nombre.
     * @return 
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Metodo set de nombre.
     * @param nombre 
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Metodo que permite pasar a String la instancia, devolviendo el nombre del área.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return nombre; //To change body of generated methods, choose Tools | Templates.
    }
}
