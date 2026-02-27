/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

/**
  * La clase Atencion representa una atención en una ventanilla específica para un paciente.
 * 
 * Esta clase se utiliza para modelar las atenciones que se realizan en ventanillas,
 * cada atención está asociada a un paciente y una ventanilla específica.
 * 
 * @author jnxd_
 */
public class Atencion {
    
    /**
     * Nombre de la ventanilla donde se realiza la atención.
     */
    private String nombreVentanilla;
    
    /**
     * Paciente que recibe la atención.
     */
    private Paciente paciente;

    /**
     * Constructor de la clase Atencion.
     * 
     * @param nombreVentanilla El nombre de la ventanilla donde se realiza la atención.
     * @param paciente El paciente que recibe la atención.
     */
    public Atencion(String nombreVentanilla, Paciente paciente) {
        this.nombreVentanilla = nombreVentanilla;
        this.paciente = paciente;
    }

    /**
     * Obtiene el nombre de la ventanilla donde se realiza la atención.
     * 
     * @return El nombre de la ventanilla.
     */
    public String getNombreVentanilla() {
        return nombreVentanilla;
    }

    /**
     * Establece el nombre de la ventanilla donde se realiza la atención.
     * 
     * @param nombreVentanilla El nuevo nombre de la ventanilla.
     */
    public void setNombreVentanilla(String nombreVentanilla) {
        this.nombreVentanilla = nombreVentanilla;
    }

    /**
     * Obtiene el paciente que recibe la atención.
     * 
     * @return El paciente.
     */
    public Paciente getPaciente() {
        return paciente;
    }

    /**
     * Establece el paciente que recibe la atención.
     * 
     * @param paciente El nuevo paciente.
     */
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
}
