package CINEMARX.M5;/**
 * Clase que representa el resumen de una compra
 */
public class ResumenCompra {
    private String descripcion;
    private int cantidad;
    private double precio;
    
    public ResumenCompra(String descripcion, int cantidad, double precio) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
    }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    
    public double getSubtotal() {
        return cantidad * precio;
    }
    
    public String getLineaCompra() {
        return cantidad + "x " + descripcion;
    }
}
