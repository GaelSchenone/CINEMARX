/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
//hola
package CINEMARX.M2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Pelicula
 * Gestiona todas las operaciones CRUD con la tabla Pelicula
 */
public class PeliculaDAO {
    
    // (INSERT, UPDATE, DELETE no cambian... omitidos por brevedad)
    // ...
    // ==========================================
    // CREAR (INSERT)
    // ==========================================
    
    public boolean insertar(Pelicula pelicula) {
        // --- MODIFICADO: Añadir Sinopsis al insertar ---
        String sql = "INSERT INTO Pelicula (Genero, Titulo, ClasificacionEdad, Estado, Imagen, Sinopsis) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, pelicula.getGenero());
            pstmt.setString(2, pelicula.getTitulo());
            pstmt.setString(3, pelicula.getClasificacionEdad());
            pstmt.setString(4, pelicula.getEstado());
            pstmt.setString(5, pelicula.getImagen());
            pstmt.setString(6, pelicula.getSinopsis()); // Añadido
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pelicula.setIdPelicula(rs.getInt(1));
                    }
                }
                System.out.println("✅ Película insertada: " + pelicula.getTitulo());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar película: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ==========================================
    // LEER (SELECT)
    // ==========================================
    
    /**
     * Obtiene todas las películas de la base de datos
     * @return Lista de películas
     */
    public List<Pelicula> obtenerTodas() {
        List<Pelicula> peliculas = new ArrayList<>();
        // --- MODIFICADO: Añadir Sinopsis ---
        String sql = "SELECT ID_Pelicula, Genero, Titulo, ClasificacionEdad, Estado, Imagen, Sinopsis " +
                     "FROM Pelicula ORDER BY Titulo";
        
        try (Connection conn = M2.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // --- MODIFICADO: Usar nuevo constructor ---
                Pelicula pelicula = new Pelicula(
                    rs.getInt("ID_Pelicula"),
                    rs.getString("Titulo"),
                    rs.getString("Genero"),
                    rs.getString("ClasificacionEdad"),
                    rs.getString("Estado"),
                    rs.getString("Imagen"),
                    rs.getString("Sinopsis") // Añadido
                );
                peliculas.add(pelicula);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener películas: " + e.getMessage());
        }
        return peliculas;
    }
    
    /**
     * Obtiene una película por su ID
     * @param idPelicula ID de la película
     * @return Objeto Pelicula o null si no existe
     */
    public Pelicula obtenerPorId(int idPelicula) {
        // --- MODIFICADO: Añadir Sinopsis ---
        String sql = "SELECT ID_Pelicula, Genero, Titulo, ClasificacionEdad, Estado, Imagen, Sinopsis " +
                     "FROM Pelicula WHERE ID_Pelicula = ?";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPelicula);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // --- MODIFICADO: Usar nuevo constructor ---
                    return new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("ClasificacionEdad"),
                        rs.getString("Estado"),
                        rs.getString("Imagen"),
                        rs.getString("Sinopsis") // Añadido
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener película por ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Busca películas por título (búsqueda parcial)
     * @param termino Término de búsqueda
     * @return Lista de películas que contienen el término en el título
     */
    public List<Pelicula> buscarPorTitulo(String termino) {
        List<Pelicula> peliculas = new ArrayList<>();
        // --- MODIFICADO: Añadir Sinopsis ---
        String sql = "SELECT ID_Pelicula, Genero, Titulo, ClasificacionEdad, Estado, Imagen, Sinopsis " +
                     "FROM Pelicula WHERE Titulo LIKE ? ORDER BY Titulo";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + termino + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // --- MODIFICADO: Usar nuevo constructor ---
                    Pelicula pelicula = new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("ClasificacionEdad"),
                        rs.getString("Estado"),
                        rs.getString("Imagen"),
                        rs.getString("Sinopsis") // Añadido
                    );
                    peliculas.add(pelicula);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar películas: " + e.getMessage());
        }
        return peliculas;
    }
    
    // ==========================================
    // ACTUALIZAR (UPDATE)
    // ==========================================
    
    public boolean actualizar(Pelicula pelicula) {
        // --- MODIFICADO: Añadir Sinopsis ---
        String sql = "UPDATE Pelicula SET Genero = ?, Titulo = ?, ClasificacionEdad = ?, " +
                     "Estado = ?, Imagen = ?, Sinopsis = ? WHERE ID_Pelicula = ?";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pelicula.getGenero());
            pstmt.setString(2, pelicula.getTitulo());
            pstmt.setString(3, pelicula.getClasificacionEdad());
            pstmt.setString(4, pelicula.getEstado());
            pstmt.setString(5, pelicula.getImagen());
            pstmt.setString(6, pelicula.getSinopsis()); // Añadido
            pstmt.setInt(7, pelicula.getIdPelicula());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar película: " + e.getMessage());
        }
        return false;
    }
    
    // (ELIMINAR y otros métodos auxiliares no cambian...)
    // ...
    // ==========================================
    // ELIMINAR (DELETE)
    // ==========================================
    public boolean eliminar(int idPelicula) {
        String sql = "DELETE FROM Pelicula WHERE ID_Pelicula = ?";
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPelicula);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar película: " + e.getMessage());
        }
        return false;
    }
    
    // ==========================================
    // MÉTODO PARA SECCIONES
    // ==========================================
    
    public List<Pelicula> obtenerPeliculasConFiltro(String genero, String estado, int limit) {
        List<Pelicula> peliculas = new ArrayList<>();
        // --- MODIFICADO: Añadir Sinopsis ---
        StringBuilder sql = new StringBuilder(
            "SELECT ID_Pelicula, Genero, Titulo, ClasificacionEdad, Estado, Imagen, Sinopsis FROM Pelicula WHERE 1=1"
        );
        
        if (genero != null && !genero.trim().isEmpty()) sql.append(" AND Genero = ?");
        if (estado != null && !estado.trim().isEmpty()) sql.append(" AND Estado = ?");
        sql.append(" ORDER BY Titulo"); 
        if (limit > 0) sql.append(" LIMIT ?");

        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (genero != null && !genero.trim().isEmpty()) pstmt.setString(paramIndex++, genero);
            if (estado != null && !estado.trim().isEmpty()) pstmt.setString(paramIndex++, estado);
            if (limit > 0) pstmt.setInt(paramIndex, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // --- MODIFICADO: Usar nuevo constructor ---
                    Pelicula pelicula = new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("ClasificacionEdad"),
                        rs.getString("Estado"),
                        rs.getString("Imagen"),
                        rs.getString("Sinopsis") // Añadido
                    );
                    peliculas.add(pelicula);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener películas con filtro: " + e.getMessage());
        }
        return peliculas;
    }

    // ==========================================
    // MÉTODO PARA SUGERENCIAS
    // ==========================================
    
    public List<String> obtenerTitulosQueCoinciden(String termino, int limit) {
        // (Este método no necesita cambios, solo busca títulos)
        List<String> titulos = new ArrayList<>();
        String sql = "SELECT Titulo FROM Pelicula WHERE Titulo LIKE ? ORDER BY Titulo LIMIT ?";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + termino + "%");
            pstmt.setInt(2, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    titulos.add(rs.getString("Titulo"));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener sugerencias de títulos: " + e.getMessage());
        }
        return titulos;
    }

    // ==========================================
    // MÉTODO "MÁS TAQUILLERAS"
    // ==========================================
    
    public List<Pelicula> obtenerPeliculasMasTaquilleras(int limit) {
        List<Pelicula> peliculas = new ArrayList<>();
        // --- MODIFICADO: Añadir Sinopsis ---
        String sql = "SELECT p.ID_Pelicula, p.Genero, p.Titulo, p.ClasificacionEdad, p.Estado, p.Imagen, p.Sinopsis, SUM(cb.Cantidad) AS TotalVentas "
                   + "FROM Pelicula p "
                   + "JOIN Funcion f ON p.ID_Pelicula = f.ID_Pelicula "
                   + "JOIN Boleto b ON f.ID_Funcion = b.ID_Funcion "
                   + "JOIN Comprobante_Boleto cb ON b.ID_Boleto = cb.ID_Boleto "
                   + "GROUP BY p.ID_Pelicula, p.Genero, p.Titulo, p.ClasificacionEdad, p.Estado, p.Imagen, p.Sinopsis "
                   + "ORDER BY TotalVentas DESC "
                   + "LIMIT ?";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // --- MODIFICADO: Usar nuevo constructor ---
                    Pelicula pelicula = new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("ClasificacionEdad"),
                        rs.getString("Estado"),
                        rs.getString("Imagen"),
                        rs.getString("Sinopsis") // Añadido
                    );
                    peliculas.add(pelicula);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener películas más taquilleras: " + e.getMessage());
        }
        return peliculas;
    }
    
    // ==========================================
    // *** NUEVO MÉTODO PARA "SPOTLIGHT" ***
    // ==========================================
    
    /**
     * Obtiene una única película aleatoria que esté "En Cartelera".
     * @return Un objeto Pelicula, o null si no se encuentra ninguna.
     */
    public Pelicula obtenerPeliculaAleatoriaEnCartelera() {
        // --- MODIFICADO: Añadir Sinopsis y usar RAND() ---
        String sql = "SELECT ID_Pelicula, Genero, Titulo, ClasificacionEdad, Estado, Imagen, Sinopsis " +
                     "FROM Pelicula WHERE Estado = 'En Cartelera' ORDER BY RAND() LIMIT 1";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // --- MODIFICADO: Usar nuevo constructor ---
                    return new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("ClasificacionEdad"),
                        rs.getString("Estado"),
                        rs.getString("Imagen"),
                        rs.getString("Sinopsis") // Añadido
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener película aleatoria: " + e.getMessage());
        }
        return null; // Retorna null si no hay películas "En Cartelera"
    }

    // (El método main() de prueba no necesita cambios)
    // ...
}