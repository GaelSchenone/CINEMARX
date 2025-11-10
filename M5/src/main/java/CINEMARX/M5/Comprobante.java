package CINEMARX.M5;/**
 * Clase que representa un Comprobante de compra
 * Basada en el diagrama de clases
 */
public class Comprobante {
    private int idComprobante;
    private int cantComprada;
    private String numComprobante;
    private double precioTotal;
    
    public Comprobante() {
    }
    
    public Comprobante(int cantComprada, String numComprobante, double precioTotal) {
        this.cantComprada = cantComprada;
        this.numComprobante = numComprobante;
        this.precioTotal = precioTotal;
    }
    
    // Getters y Setters
    public int getIdComprobante() {
        return idComprobante;
    }
    
    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }
    
    public int getCantComprada() {
        return cantComprada;
    }
    
    public void setCantComprada(int cantComprada) {
        this.cantComprada = cantComprada;
    }
    
    public String getNumComprobante() {
        return numComprobante;
    }
    
    public void setNumComprobante(String numComprobante) {
        this.numComprobante = numComprobante;
    }
    
    public double getPrecioTotal() {
        return precioTotal;
    }
    
    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }
    
    /**
     * Modifica el precio del comprobante
     */
    public void modificarPrecio(double nuevoPrecio) {
        this.precioTotal = nuevoPrecio;
    }
    
    /**
     * Modifica la cantidad comprada
     */
    public void modificarCantidad(int nuevaCantidad) {
        this.cantComprada = nuevaCantidad;
    }
    
    /**
     * Entrega el comprobante al cliente
     */
    public void entregarCliente() {
        System.out.println("Comprobante " + numComprobante + " entregado");
    }
    
    @Override
    public String toString() {
        return "Comprobante " + numComprobante + " - Total: $" + precioTotal;
    }
}