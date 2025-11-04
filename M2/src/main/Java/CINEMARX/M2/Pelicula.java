/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CINEMARX.M2;

/**
 * Clase de dominio que representa una Película en el sistema CinemarX
 * Mapea la tabla: Pelicula
 */
/**
 * Clase de dominio que representa una Película en el sistema CinemarX
 * Mapea la tabla: Pelicula
 */
public class Pelicula {
    
    // Atributos que coinciden con la BD
    private int idPelicula;
    private String titulo;
    private String genero;
    private String clasificacionEdad;
    private String estado;
    private String imagen; // Ruta de la imagen del poster (campo "Imagen" en BD)
    
    // Constructores
    
    /**
     * Constructor vacío
     */
    public Pelicula() {
    }
    
    /**
     * Constructor sin ID (para INSERT)
     */
    public Pelicula(String titulo, String genero, String clasificacionEdad, String estado) {
        this.titulo = titulo;
        this.genero = genero;
        this.clasificacionEdad = clasificacionEdad;
        this.estado = estado;
        this.imagen = null; // Por defecto null hasta que se asigne
    }
    
    /**
     * Constructor completo sin imagen (para cuando se lee de BD actual)
     */
    public Pelicula(int idPelicula, String titulo, String genero, String clasificacionEdad, String estado) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.genero = genero;
        this.clasificacionEdad = clasificacionEdad;
        this.estado = estado;
        this.imagen = null;
    }
    
    /**
     * Constructor completo con imagen
     */
    public Pelicula(int idPelicula, String titulo, String genero, String clasificacionEdad, 
                    String estado, String imagen) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.genero = genero;
        this.clasificacionEdad = clasificacionEdad;
        this.estado = estado;
        this.imagen = imagen;
    }
    
    // Getters y Setters
    
    public int getIdPelicula() {
        return idPelicula;
    }
    
    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getGenero() {
        return genero;
    }
    
    public void setGenero(String genero) {
        this.genero = genero;
    }
    
    public String getClasificacionEdad() {
        return clasificacionEdad;
    }
    
    public void setClasificacionEdad(String clasificacionEdad) {
        this.clasificacionEdad = clasificacionEdad;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getImagen() {
        return imagen;
    }
    
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    
    // Métodos útiles
    
    /**
     * Verifica si la película tiene una imagen asignada
     */
    public boolean tieneImagen() {
        return imagen != null && !imagen.trim().isEmpty();
    }
    
    /**
     * Verifica si la película está en cartelera actualmente
     */
    public boolean estaEnCartelera() {
        return "En Cartelera".equalsIgnoreCase(estado);
    }
    
    /**
     * Verifica si la película es próximo estreno
     */
    public boolean esProximamente() {
        return "Próximamente".equalsIgnoreCase(estado);
    }
    
    @Override
    public String toString() {
        return "Pelicula{" +
                "idPelicula=" + idPelicula +
                ", titulo='" + titulo + '\'' +
                ", genero='" + genero + '\'' +
                ", clasificacionEdad='" + clasificacionEdad + '\'' +
                ", estado='" + estado + '\'' +
                ", imagen='" + (imagen != null ? imagen : "Sin imagen") + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pelicula pelicula = (Pelicula) o;
        return idPelicula == pelicula.idPelicula;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idPelicula);
    }
}