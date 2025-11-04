package cinecomida;

public class Producto {
    private int id;
    private String categoria;
    private String nombre;
    private String descripcion;
    private double precio;
    private String imagenRuta;
    private int stock;
    
    // Constructor completo
    public Producto(int id, String categoria, String nombre, String descripcion, 
                    double precio, String imagenRuta, int stock) {
        this.id = id;
        this.categoria = categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenRuta = imagenRuta;
        this.stock = stock;
    }
    
    // Constructor sin descripción (para compatibilidad)
    public Producto(int id, String nombre, double precio, String imagenRuta, int stock) {
        this.id = id;
        this.categoria = "General";
        this.nombre = nombre;
        this.descripcion = "";
        this.precio = precio;
        this.imagenRuta = imagenRuta;
        this.stock = stock;
    }
    
    // Getters
    public int getId() { 
        return id; 
    }
    
    public String getCategoria() { 
        return categoria; 
    }
    
    public String getNombre() { 
        return nombre; 
    }
    
    public String getDescripcion() { 
        return descripcion; 
    }
    
    public double getPrecio() { 
        return precio; 
    }
    
    public String getImagenRuta() { 
        return imagenRuta; 
    }
    
    public int getStock() { 
        return stock; 
    }
    
    // Setters
    public void setId(int id) { 
        this.id = id; 
    }
    
    public void setCategoria(String categoria) { 
        this.categoria = categoria; 
    }
    
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }
    
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }
    
    public void setPrecio(double precio) { 
        this.precio = precio; 
    }
    
    public void setImagenRuta(String imagenRuta) { 
        this.imagenRuta = imagenRuta; 
    }
    
    public void setStock(int stock) { 
        this.stock = stock; 
    }
    
    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", categoria='" + categoria + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                '}';
    }
}


