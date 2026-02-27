/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loginSources;

/**
 * Clase que permite crear una instancia de Usuario.
 * 
 * @author jnxd_
 */
public class Usuario {
    
    /**
     * Variable que almacena el id del usuario logeado.
     */
    private int idUusario;
    
    /**
     * Variable que almacena el nombre completo del usuario logeado.
     */
    private String nombreCompleto;
    
    /**
     * Variable que almacena el id del rol del usuario logeado.
     */
    private int idRol;

    /**
     * Constructor de la clase
     * 
     * @param idUusario
     * @param nombreCompleto
     * @param idRol 
     */
    public Usuario(int idUusario, String nombreCompleto, int idRol) {
        this.idUusario = idUusario;
        this.nombreCompleto = nombreCompleto;
        this.idRol = idRol;
    }
    
    /**
     * Metodo get de idUsuario.
     * @return 
     */
    public int getIdUusario() {
        return idUusario;
    }

    /**
     * Metodo set de idUsuario.
     * @param idUusario 
     */
    public void setIdUusario(int idUusario) {
        this.idUusario = idUusario;
    }

    /**
     * Metodo get de nombreCompleto.
     * @return 
     */
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    /**
     * Metodo set de nombreCompleto.
     * @param nombreCompleto 
     */
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    /**
     * Metodo get de idRol.
     * @return 
     */
    public int getIdRol() {
        return idRol;
    }

    /**
     * Metodo set de idRol.
     * @param idRol 
     */
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
    
    /**
     * Metodo que limpia los datos del usuario logeado.
     */
    public void limpiarDatos(){
        this.idUusario = 0;
        this.nombreCompleto = "";
        this.idRol = 0;
    }
}
