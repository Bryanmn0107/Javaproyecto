/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

import java.net.Socket;

/**
 * @deprecated
 * @author jnxd_
 */
public class Ventanilla {
    //private int idVentanilla;
    private int numero;

    public Ventanilla(int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
}
