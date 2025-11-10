/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package CINEMARX.M2;

import java.time.LocalDate;
import java.util.List;

/**
 * Clase de dominio que representa la Cartelera en el sistema CinemarX
 * Mapea la tabla: Cartelera
 * Nota: Solo hay UNA cartelera (ID = 1) según el diseño de la BD
 */
public class Cartelera {
    
    // Atributos que coinciden con la BD
    private int idCartelera;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    // Atributos relacionados (para info completa)
    private List<Funcion> funciones; // Todas las funciones de esta cartelera
    
    // Constructores
    
    /**
     * Constructor vacío
     */
    public Cartelera() {
        this.idCartelera = 1; // Por defecto siempre 1
    }
    
    /**
     * Constructor con fechas (para UPDATE)
     */
    public Cartelera(LocalDate fechaInicio, LocalDate fechaFin) {
        this.idCartelera = 1;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
    
    /**
     * Constructor completo (para SELECT)
     */
    public Cartelera(int idCartelera, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idCartelera = idCartelera;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
    
    // Getters y Setters
    
    public int getIdCartelera() {
        return idCartelera;
    }
    
    public void setIdCartelera(int idCartelera) {
        this.idCartelera = idCartelera;
    }
    
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    public List<Funcion> getFunciones() {
        return funciones;
    }
    
    public void setFunciones(List<Funcion> funciones) {
        this.funciones = funciones;
    }
    
    // Métodos útiles
    
    /**
     * Verifica si la cartelera está activa actualmente
     */
    public boolean estaActiva() {
        LocalDate hoy = LocalDate.now();
        return !hoy.isBefore(fechaInicio) && !hoy.isAfter(fechaFin);
    }
    
    /**
     * Verifica si la cartelera está próxima a iniciar
     */
    public boolean esProxima() {
        LocalDate hoy = LocalDate.now();
        return hoy.isBefore(fechaInicio);
    }
    
    /**
     * Verifica si la cartelera ya finalizó
     */
    public boolean yaFinalizo() {
        LocalDate hoy = LocalDate.now();
        return hoy.isAfter(fechaFin);
    }
    
    /**
     * Obtiene el período de la cartelera en formato legible
     */
    public String getPeriodoFormateado() {
        if (fechaInicio != null && fechaFin != null) {
            return String.format("%02d/%02d/%d - %02d/%02d/%d",
                fechaInicio.getDayOfMonth(), fechaInicio.getMonthValue(), fechaInicio.getYear(),
                fechaFin.getDayOfMonth(), fechaFin.getMonthValue(), fechaFin.getYear());
        }
        return "Sin fechas definidas";
    }
    
    /**
     * Obtiene la cantidad de días que dura la cartelera
     */
    public long getDuracionDias() {
        if (fechaInicio != null && fechaFin != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "Cartelera{" +
                "idCartelera=" + idCartelera +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", estado=" + (estaActiva() ? "ACTIVA" : (esProxima() ? "PRÓXIMA" : "FINALIZADA")) +
                '}';
    }
}
