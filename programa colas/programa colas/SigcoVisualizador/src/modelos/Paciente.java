/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

/**
  * La clase Paciente representa un paciente con su información personal y su estado preferencial.
 * 
 * Esta clase se utiliza para modelar a un paciente con detalles como su nombre, número de DNI,
 * estado preferencial y el identificador de su turno.
 * 
 * @author jnxd_
 */
public class Paciente {
    
    /**
     * Indica si el paciente es preferencial. 1 = preferencial, 0 = no preferencial.
     */
    private int preferencial; //preferencial = 1 | no preferencial = 0
    
    /**
     * Nombre del paciente.
     */
    private String nombre;
    
    /**
     * Número de DNI del paciente.
     */
    private String nroDni;
    
    /**
     * Identificador del turno del paciente.
     */
    private int idTurno;

    /**
     * Constructor de la clase Paciente.
     * 
     * @param nombre El nombre del paciente.
     * @param nroDni El número de DNI del paciente.
     * @param preferencial Indica si el paciente es preferencial. 1 = preferencial, 0 = no preferencial.
     * @param idTurno El identificador del turno del paciente.
     */
    public Paciente(String nombre, String nroDni, int preferencial,int idTurno) {
        this.nombre = nombre;
        this.nroDni = nroDni;
        this.preferencial = preferencial;
        this.idTurno=idTurno;
        
    }

    /**
     * Obtiene el identificador del turno del paciente.
     * 
     * @return El identificador del turno.
     */
    public int getIdTurno() {
        return idTurno;
    }

    /**
     * Establece el identificador del turno del paciente.
     * 
     * @param idTurno El nuevo identificador del turno.
     */
    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    /**
     * Obtiene si el paciente es preferencial.
     * 
     * @return 1 si el paciente es preferencial, 0 si no lo es.
     */
    public int getPreferencial() {
        return preferencial;
    }

    /**
     * Establece si el paciente es preferencial.
     * 
     * @param preferencial 1 para preferencial, 0 para no preferencial.
     */
    public void setPreferencial(int preferencial) {
        this.preferencial = preferencial;
    }
    
    /**
     * Obtiene el nombre del paciente.
     * 
     * @return El nombre del paciente.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del paciente.
     * 
     * @param nombre El nuevo nombre del paciente.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el número de DNI del paciente.
     * 
     * @return El número de DNI del paciente.
     */
    public String getNroDni() {
        return nroDni;
    }

    /**
     * Establece el número de DNI del paciente.
     * 
     * @param nroDni El nuevo número de DNI del paciente.
     */
    public void setNroDni(String nroDni) {
        this.nroDni = nroDni;
    }
}