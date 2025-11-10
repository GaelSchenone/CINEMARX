package CINEMARX.M5;/**
 * Clase que representa un método de pago (tarjeta de crédito/débito)
 * Basada en el diagrama de clases
 */
public class MetodoPago {
    private int idMetodo;
    private String empresa; // VISA, MasterCard, American Express, etc.
    private String tipo; // Crédito o Débito
    private String numero; // Últimos 4 dígitos
    private String numeroCompleto; // Número completo (16 dígitos)
    private String pin;
    private String nombreTitular;
    private String fechaCaducidad;
    
    public MetodoPago(String empresa, String tipo, String numero, String pin) {
        this.empresa = empresa;
        this.tipo = tipo;
        this.numero = numero;
        this.pin = pin;
    }
    
    // Getters y Setters
    public int getIdMetodo() { return idMetodo; }
    public void setIdMetodo(int idMetodo) { this.idMetodo = idMetodo; }
    
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public String getNumeroCompleto() { return numeroCompleto; }
    public void setNumeroCompleto(String numeroCompleto) { 
        this.numeroCompleto = numeroCompleto;
        if (numeroCompleto != null && numeroCompleto.length() >= 4) {
            this.numero = numeroCompleto.substring(numeroCompleto.length() - 4);
        }
    }
    
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    
    public String getNombreTitular() { return nombreTitular; }
    public void setNombreTitular(String nombreTitular) { this.nombreTitular = nombreTitular; }
    
    public String getFechaCaducidad() { return fechaCaducidad; }
    public void setFechaCaducidad(String fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }
    
    /**
     * Representación en texto para mostrar en el ComboBox
     */
    @Override
    public String toString() {
        return empresa + " - " + tipo + " - *" + numero;
    }
    
    /**
     * Descripción completa del método de pago
     */
    public String getDescripcionCompleta() {
        return empresa + " " + tipo + " terminada en " + numero;
    }
}