/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

/**
 * Clase que permite crear una instancia de Atencion.
 * 
 * @author jnxd_
 */
public class Atencion {
    
    /**
     * Atributo que almacena el nombre de la ventanilla de la atencion.
     */
    private String nombreVentanilla;
    
    /**
     * Atributo que almacena la instancia del paciente de la atencion.
     */
    private Paciente paciente;

    /**
     * Constructor de la clase.
     * 
     * @param nombreVentanilla
     * @param paciente 
     */
    public Atencion(String nombreVentanilla, Paciente paciente) {
        this.nombreVentanilla = nombreVentanilla;
        this.paciente = paciente;
    }

    /**
     * Metodo get de nombreVentanilla.
     * 
     * @return 
     */
    public String getNombreVentanilla() {
        return nombreVentanilla;
    }

    /**
     * Metodo set de nombreVentanilla.
     * 
     * @param nombreVentanilla 
     */
    public void setNombreVentanilla(String nombreVentanilla) {
        this.nombreVentanilla = nombreVentanilla;
    }

    /**
     * Metodo get de paciente.
     * 
     * @return 
     */
    public Paciente getPaciente() {
        return paciente;
    }

    /**
     * Metodo set de paciente.
     * 
     * @param paciente 
     */
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
}
