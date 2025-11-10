/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package CINEMARX.M2;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Clase de dominio que representa una Función en el sistema CinemarX
 * Mapea la tabla: Funcion
 */
public class Funcion {
    
    // Atributos que coinciden con la BD
    private int idFuncion;
    private LocalTime horaFuncion;
    private LocalDate fechaFuncion;
    private String estado;
    private int idPelicula;
    private int idSala;
    private int idCartelera;
    
    // Atributos relacionados (para mostrar info completa)
    private Pelicula pelicula; // Objeto relacionado
    private Sala sala;         // Objeto relacionado
    
    // Constructores
    
    /**
     * Constructor vacío
     */
    public Funcion() {
        this.idCartelera = 1; // Por defecto hay una sola cartelera
    }
    
    /**
     * Constructor sin ID (para INSERT)
     */
    public Funcion(LocalTime horaFuncion, LocalDate fechaFuncion, String estado, 
                   int idPelicula, int idSala) {
        this.horaFuncion = horaFuncion;
        this.fechaFuncion = fechaFuncion;
        this.estado = estado;
        this.idPelicula = idPelicula;
        this.idSala = idSala;
        this.idCartelera = 1; // Por defecto
    }
    
    /**
     * Constructor completo (para SELECT)
     */
    public Funcion(int idFuncion, LocalTime horaFuncion, LocalDate fechaFuncion, 
                   String estado, int idPelicula, int idSala, int idCartelera) {
        this.idFuncion = idFuncion;
        this.horaFuncion = horaFuncion;
        this.fechaFuncion = fechaFuncion;
        this.estado = estado;
        this.idPelicula = idPelicula;
        this.idSala = idSala;
        this.idCartelera = idCartelera;
    }
    
    // Getters y Setters
    
    public int getIdFuncion() {
        return idFuncion;
    }
    
    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }
    
    public LocalTime getHoraFuncion() {
        return horaFuncion;
    }
    
    public void setHoraFuncion(LocalTime horaFuncion) {
        this.horaFuncion = horaFuncion;
    }
    
    public LocalDate getFechaFuncion() {
        return fechaFuncion;
    }
    
    public void setFechaFuncion(LocalDate fechaFuncion) {
        this.fechaFuncion = fechaFuncion;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public int getIdPelicula() {
        return idPelicula;
    }
    
    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }
    
    public int getIdSala() {
        return idSala;
    }
    
    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }
    
    public int getIdCartelera() {
        return idCartelera;
    }
    
    public void setIdCartelera(int idCartelera) {
        this.idCartelera = idCartelera;
    }
    
    public Pelicula getPelicula() {
        return pelicula;
    }
    
    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }
    
    public Sala getSala() {
        return sala;
    }
    
    public void setSala(Sala sala) {
        this.sala = sala;
    }
    
    // Métodos útiles
    
    /**
     * Obtiene la hora en formato String (HH:mm)
     */
    public String getHoraFormateada() {
        return horaFuncion != null ? horaFuncion.toString() : "00:00";
    }
    
    /**
     * Obtiene la fecha en formato String (dd/MM/yyyy)
     */
    public String getFechaFormateada() {
        if (fechaFuncion != null) {
            return String.format("%02d/%02d/%d", 
                fechaFuncion.getDayOfMonth(), 
                fechaFuncion.getMonthValue(), 
                fechaFuncion.getYear());
        }
        return "Sin fecha";
    }
    
    /**
     * Verifica si la función está disponible
     */
    public boolean estaDisponible() {
        return "Disponible".equalsIgnoreCase(estado);
    }
    
    @Override
    public String toString() {
        return "Funcion{" +
                "idFuncion=" + idFuncion +
                ", horaFuncion=" + horaFuncion +
                ", fechaFuncion=" + fechaFuncion +
                ", estado='" + estado + '\'' +
                ", idPelicula=" + idPelicula +
                ", idSala=" + idSala +
                ", idCartelera=" + idCartelera +
                '}';
    }
}