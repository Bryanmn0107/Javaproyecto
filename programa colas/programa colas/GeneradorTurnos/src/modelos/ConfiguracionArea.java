/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

/**
 * @deprecated 
 * @author jnxd_
 */
public class ConfiguracionArea {

    // Columnas de la tabla ConfiguracionesAreaVentanillas
    private int idConfiguracion;
    private int idArea;
    private int limiteVentanillas;
    private int tiempoPromedioAtencion;
    private String ipServidorPantalla;
    private int puerto;
    private String colorHexAlert;
    private String colorHexFondo;
    private String colorHexTexto;
    private String colorHexFondoEncabezado;
    private String colorHexTextoEncabezado;

    public ConfiguracionArea(int idConfiguracion, int idArea, int limiteVentanillas, int tiempoPromedioAtencion, String ipServidorPantalla, int puerto, String colorHexAlert, String colorHexFondo, String colorHexTexto, String colorHexFondoEncabezado, String colorHexTextoEncabezado) {
        this.idConfiguracion = idConfiguracion;
        this.idArea = idArea;
        this.limiteVentanillas = limiteVentanillas;
        this.tiempoPromedioAtencion = tiempoPromedioAtencion;
        this.ipServidorPantalla = ipServidorPantalla;
        this.puerto = puerto;
        this.colorHexAlert = colorHexAlert;
        this.colorHexFondo = colorHexFondo;
        this.colorHexTexto = colorHexTexto;
        this.colorHexFondoEncabezado = colorHexFondoEncabezado;
        this.colorHexTextoEncabezado = colorHexTextoEncabezado;
    }

    public int getIdArea() {
        return idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }
    
    public int getIdConfiguracion() {
        return idConfiguracion;
    }

    public void setIdConfiguracion(int idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }

    public int getLimiteVentanillas() {
        return limiteVentanillas;
    }

    public void setLimiteVentanillas(int limiteVentanillas) {
        this.limiteVentanillas = limiteVentanillas;
    }

    public int getTiempoPromedioAtencion() {
        return tiempoPromedioAtencion;
    }

    public void setTiempoPromedioAtencion(int tiempoPromedioAtencion) {
        this.tiempoPromedioAtencion = tiempoPromedioAtencion;
    }

    public String getIpServidorPantalla() {
        return ipServidorPantalla;
    }

    public void setIpServidorPantalla(String ipServidorPantalla) {
        this.ipServidorPantalla = ipServidorPantalla;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public String getColorHexAlert() {
        return colorHexAlert;
    }

    public void setColorHexAlert(String colorHexAlert) {
        this.colorHexAlert = colorHexAlert;
    }

    public String getColorHexFondo() {
        return colorHexFondo;
    }

    public void setColorHexFondo(String colorHexFondo) {
        this.colorHexFondo = colorHexFondo;
    }

    public String getColorHexTexto() {
        return colorHexTexto;
    }

    public void setColorHexTexto(String colorHexTexto) {
        this.colorHexTexto = colorHexTexto;
    }

    public String getColorHexFondoEncabezado() {
        return colorHexFondoEncabezado;
    }

    public void setColorHexFondoEncabezado(String colorHexFondoEncabezado) {
        this.colorHexFondoEncabezado = colorHexFondoEncabezado;
    }

    public String getColorHexTextoEncabezado() {
        return colorHexTextoEncabezado;
    }

    public void setColorHexTextoEncabezado(String colorHexTextoEncabezado) {
        this.colorHexTextoEncabezado = colorHexTextoEncabezado;
    }
    
}
