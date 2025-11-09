package CINEMARX.M6;

/**
 * Archivo que contiene todas las clases auxiliares para items de ComboBox
 */

class SalaItem {
    private int id;
    private int numero;
    private String tipo;
    private int cantButacas;
    private String cineNombre;
    
    public SalaItem(int id, int numero, String tipo, int cantButacas, String cineNombre) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.cantButacas = cantButacas;
        this.cineNombre = cineNombre;
    }
    
    public int getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return cineNombre + " - Sala " + numero + " (" + tipo + ")";
    }
}

class PeliculaItem {
    private int id;
    private String titulo;

    public PeliculaItem(int id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return titulo;
    }
}

class CarteleraItem {
    private int id;
    private String fechaInicio;
    private String fechaFin;

    public CarteleraItem(int id, String fechaInicio, String fechaFin) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Cartelera " + id + " (" + fechaInicio + " - " + fechaFin + ")";
    }
}

class FuncionItem {
    private int id;
    private String titulo;
    private String fecha;
    private String hora;
    private int numeroSala;

    public FuncionItem(int id, String titulo, String fecha, String hora, int numeroSala) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.hora = hora;
        this.numeroSala = numeroSala;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return titulo + " - " + fecha + " " + hora + " (Sala " + numeroSala + ")";
    }
}

class CineItem {
    private int id;
    private String nombre;

    public CineItem(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return nombre;
    }
}