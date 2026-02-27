/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

/**
 * Clase para representar un turno en el sistema.
 * Contiene información sobre el paciente, el área, si es preferencial o no, y el ID del turno.
 * 
 * @author jnxd_
 */
public class Turno {
    private Integer preferencial;
    private Integer idArea;
    private String nroDni;
    private String nombrePaciente;
    private Integer IdPaciente;
    private Integer IdTurno;

    /**
     * Método para obtener el ID del paciente.
     * 
     * @return El ID del paciente.
     */
    public Integer getIdPaciente() {
        return IdPaciente;
    }

    /**
     * Método para establecer el ID del paciente.
     * 
     * @param IdPaciente El ID del paciente a establecer.
     */
    public void setIdPaciente(int IdPaciente) {
        this.IdPaciente = IdPaciente;
    }

    /**
     * Constructor por defecto de la clase Turno.
     */
    public Turno() {
    }

    /**
     * Método para verificar si el turno es preferencial.
     * 
     * @return 1 si el turno es preferencial, 0 si no lo es.
     */
    public int isPreferencial() {
        return preferencial;
    }

    /**
     * Método para establecer si el turno es preferencial o no.
     * 
     * @param preferencial 1 si el turno es preferencial, 0 si no lo es.
     */
    public void setPreferencial(int preferencial) {
        this.preferencial = preferencial;
    }

    /**
     * Método para obtener el ID del área.
     * 
     * @return El ID del área.
     */
    public Integer getIdArea() {
        return idArea;
    }

    /**
     * Método para establecer el ID del área.
     * 
     * @param idArea El ID del área a establecer.
     */
    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    /**
     * Método para obtener el número de DNI del paciente.
     * 
     * @return El número de DNI del paciente.
     */
    public String getNroDni() {
        return nroDni;
    }

    /**
     * Método para establecer el número de DNI del paciente.
     * 
     * @param nroDni El número de DNI del paciente a establecer.
     */
    public void setNroDni(String nroDni) {
        this.nroDni = nroDni;
    }

    /**
     * Método para obtener el nombre del paciente.
     * 
     * @return El nombre del paciente.
     */
    public String getNombrePaciente() {
        return nombrePaciente;
    }

    /**
     * Método para establecer el nombre del paciente.
     * 
     * @param nombrePaciente El nombre del paciente a establecer.
     */
    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    /**
     * Método para obtener el ID del turno.
     * 
     * @return El ID del turno.
     */
    public Integer getIdTurno() {
        return IdTurno;
    }

    /**
     * Método para establecer el ID del turno.
     * 
     * @param IdTurno El ID del turno a establecer.
     */
    public void setIdTurno(int IdTurno) {
        this.IdTurno = IdTurno;
    }
    
    /**
     * Método para vaciar los datos del turno.
     * Utilizado para limpiar los datos del turno cuando sea necesario.
     */
    public void vaciarDatos(){
        this.idArea=null;
        this.nombrePaciente=null;
        this.nroDni=null;
        this.preferencial=null;
        this.IdPaciente=null;
        this.IdTurno = null;
    }
}
