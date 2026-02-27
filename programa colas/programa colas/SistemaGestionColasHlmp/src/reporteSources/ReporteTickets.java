package reporteSources;

/**
 * Clase que permite crear una instancia de ReporteTickets, 
 * importante para la generacion del reporte de los tickets.
 * 
 * @author FERNANDO
 */
public class ReporteTickets {

    /**
     * Variables atributos de la clase.
     */
    private String numeroticket;
    private String fechacreacion;
    private String estado;
    private String area;
    private String preferencial;
    private String inicio;
    private String fin;
    private String tiempo;

    /**
     * Constructor de la clase.
     * 
     * @param numero
     * @param fechacreacion
     * @param estado
     * @param area
     * @param preferencial
     * @param inicio
     * @param fin
     * @param tiempo 
     */
    public ReporteTickets(String numero,String fechacreacion,String estado, String area,  String preferencial, String inicio,  
            String fin, String tiempo) {
        this.numeroticket = numero;
        this.fechacreacion = fechacreacion;
        this.estado = estado;
        this.area = area;
        this.preferencial = preferencial;
        this.inicio = inicio;
        this.fin = fin;
        this.tiempo = tiempo;
    }
    
    // Metodos getters y setters de cada atributo.
    public String getNumeroticket() {
        return numeroticket;
    }

    public void setNumeroticket(String numeroticket) {
        this.numeroticket = numeroticket;
    }

    public String getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(String fechacreacion) {
        this.fechacreacion = fechacreacion;
    }

    public String getPreferencial() {
        return preferencial;
    }

    public void setPreferencial(String preferencial) {
        this.preferencial = preferencial;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
