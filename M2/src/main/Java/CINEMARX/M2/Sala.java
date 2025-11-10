/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package CINEMARX.M2;

/**
 * Clase de dominio que representa una Sala en el sistema CinemarX
 * Mapea la tabla: Sala
 * Nota: Esta clase es principalmente para uso del Módulo 3, 
 * pero la necesitamos para mostrar info de formato (2D/3D)
 */
public class Sala {
    
    // Atributos que coinciden con la BD
    private int idSala;
    private int numero;
    private int cantButacas;
    private String tipoDeSala; // "2D", "3D", "4DX", "IMAX", etc.
    private int idCine;
    
    // Constructores
    
    /**
     * Constructor vacío
     */
    public Sala() {
    }
    
    /**
     * Constructor sin ID (para INSERT)
     */
    public Sala(int numero, int cantButacas, String tipoDeSala, int idCine) {
        this.numero = numero;
        this.cantButacas = cantButacas;
        this.tipoDeSala = tipoDeSala;
        this.idCine = idCine;
    }
    
    /**
     * Constructor completo (para SELECT)
     */
    public Sala(int idSala, int numero, int cantButacas, String tipoDeSala, int idCine) {
        this.idSala = idSala;
        this.numero = numero;
        this.cantButacas = cantButacas;
        this.tipoDeSala = tipoDeSala;
        this.idCine = idCine;
    }
    
    // Getters y Setters
    
    public int getIdSala() {
        return idSala;
    }
    
    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }
    
    public int getNumero() {
        return numero;
    }
    
    public void setNumero(int numero) {
        this.numero = numero;
    }
    
    public int getCantButacas() {
        return cantButacas;
    }
    
    public void setCantButacas(int cantButacas) {
        this.cantButacas = cantButacas;
    }
    
    public String getTipoDeSala() {
        return tipoDeSala;
    }
    
    public void setTipoDeSala(String tipoDeSala) {
        this.tipoDeSala = tipoDeSala;
    }
    
    public int getIdCine() {
        return idCine;
    }
    
    public void setIdCine(int idCine) {
        this.idCine = idCine;
    }
    
    // Métodos útiles
    
    /**
     * Obtiene el nombre completo de la sala (ej: "Sala 1 - 2D")
     */
    public String getNombreCompleto() {
        return "Sala " + numero + " - " + tipoDeSala;
    }
    
    /**
     * Verifica si es una sala 3D
     */
    public boolean es3D() {
        return tipoDeSala != null && tipoDeSala.toUpperCase().contains("3D");
    }
    
    /**
     * Verifica si es una sala 2D
     */
    public boolean es2D() {
        return tipoDeSala != null && tipoDeSala.toUpperCase().contains("2D");
    }
    
    @Override
    public String toString() {
        return "Sala{" +
                "idSala=" + idSala +
                ", numero=" + numero +
                ", cantButacas=" + cantButacas +
                ", tipoDeSala='" + tipoDeSala + '\'' +
                ", idCine=" + idCine +
                '}';
    }
}