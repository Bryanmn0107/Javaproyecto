
package reporteSources;

/**
 * Clase que permite crear una instancia de Reporte, 
 * importante para la generacion del reporte
 * 
 * @author jnxd_
 */
public class Reporte {
    
    /**
     * Variables atributos de la clase.
     */
    private String nombre;
    private String area;
    private String ventanilla;
    private String ticketsatendidos;
    private String ticketsnoatendidos;
    private String estado;
    private String fecha;
    private String horaapertura;
    private String horaclausura;
    private String tiempoinactivo;

    /**
     * Constructor de la clase.
     * 
     * @param nombre
     * @param area
     * @param ventanilla
     * @param ticketsatendidos
     * @param ticketsnoatendidos
     * @param estado
     * @param fecha
     * @param horaapertura
     * @param horaclausura
     * @param tiempoinactivo 
     */
    public Reporte(String nombre, String area, String ventanilla, String ticketsatendidos, String ticketsnoatendidos, String estado, String fecha, String horaapertura, String horaclausura, String tiempoinactivo) {
        this.nombre = nombre;
        this.area = area;
        this.ventanilla = ventanilla;
        this.ticketsatendidos = ticketsatendidos;
        this.ticketsnoatendidos = ticketsnoatendidos;
        this.estado = estado;
        this.fecha = fecha;
        this.horaapertura = horaapertura;
        this.horaclausura = horaclausura;
        this.tiempoinactivo = tiempoinactivo;
    }
    
    // Metodos getters y setters de cada atributo.
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getVentanilla() {
        return ventanilla;
    }

    public void setVentanilla(String ventanilla) {
        this.ventanilla = ventanilla;
    }

    public String getTicketsatendidos() {
        return ticketsatendidos;
    }

    public void setTicketsatendidos(String ticketsatendidos) {
        this.ticketsatendidos = ticketsatendidos;
    }

    public String getTicketsnoatendidos() {
        return ticketsnoatendidos;
    }

    public void setTicketsnoatendidos(String ticketsnoatendidos) {
        this.ticketsnoatendidos = ticketsnoatendidos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraapertura() {
        return horaapertura;
    }

    public void setHoraapertura(String horaapertura) {
        this.horaapertura = horaapertura;
    }

    public String getHoraclausura() {
        return horaclausura;
    }

    public void setHoraclausura(String horaclausura) {
        this.horaclausura = horaclausura;
    }

    public String getTiempoinactivo() {
        return tiempoinactivo;
    }

    public void setTiempoinactivo(String tiempoinactivo) {
        this.tiempoinactivo = tiempoinactivo;
    }

}
