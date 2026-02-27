/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

/**
 * Clase que permite crear una instancia de Paciente.
 * 
 * @author jnxd_
 */
public class Paciente {
  
    /**
     * Variable que almacena el valor numerico sobre si es preferencial o no.
     */
    private int preferencial; //preferencial = 1 | no preferencial = 0
    
    /**
     * Variable que almacena el nombre del paciente.
     */
    private String nombre;
    
    /**
     * Variable que almacena el numero de DNI del paciente.
     */
    private String nroDni;

    /**
     * Constructor de la clase.
     * 
     * @param nombre
     * @param nroDni
     * @param preferencial 
     */
    public Paciente(String nombre, String nroDni, int preferencial) {
        this.nombre = nombre;
        this.nroDni = nroDni;
        this.preferencial = preferencial;
    }

    /**
     * Metodo get de preferencial.
     * 
     * @return 
     */
    public int getPreferencial() {
        return preferencial;
    }

    /**
     * Metodo set de preferencial.
     * 
     * @param preferencial 
     */
    public void setPreferencial(int preferencial) {
        this.preferencial = preferencial;
    }
    
    /**
     * Metodo get de nombre.
     * 
     * @return 
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Metodo set de nombre.
     * 
     * @param nombre 
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Metodo get de nroDni.
     * 
     * @return 
     */
    public String getNroDni() {
        return nroDni;
    }

    /**
     * Metodo set de nroDni.
     * 
     * @param nroDni 
     */
    public void setNroDni(String nroDni) {
        this.nroDni = nroDni;
    }
}
