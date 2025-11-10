/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



package CINEMARX.M2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Pelicula
 * Gestiona todas las operaciones CRUD con la tabla Pelicula
 */
public class PeliculaDAO {
    
    // ==========================================
    // CREAR (INSERT)
    // ==========================================
    
    public boolean insertar(Pelicula pelicula) {
        String sql = "INSERT INTO Pelicula (Genero, Titulo, ClasificacionEdad, Estado, Imagen, Sinopsis) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, pelicula.getGenero());
            pstmt.setString(2, pelicula.getTitulo());
            pstmt.setString(3, pelicula.getClasificacionEdad());
            pstmt.setString(4, pelicula.getEstado());
            pstmt.setString(5, pelicula.getImagen());
            pstmt.setString(6, pelicula.getSinopsis());
            
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
    
    public List<Pelicula> obtenerTodas() {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT ID_Pelicula, Genero, Titulo, ClasificacionEdad, Estado, Imagen, Sinopsis " +
                     "FROM Pelicula ORDER BY Titulo";
        
        try (Connection conn = M2.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Pelicula pelicula = new Pelicula(
                    rs.getInt("ID_Pelicula"),
                    rs.getString("Titulo"),
                    rs.getString("Genero"),
                    rs.getString("ClasificacionEdad"),
                    rs.getString("Estado"),
                    rs.getString("Imagen"),
                    rs.getString("Sinopsis")
                );
                peliculas.add(pelicula);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener películas: " + e.getMessage());
        }
        return peliculas;
    }
    
    public Pelicula obtenerPorId(int idPelicula) {
        String sql = "SELECT ID_Pelicula, Genero, Titulo, ClasificacionEdad, Estado, Imagen, Sinopsis " +
                     "FROM Pelicula WHERE ID_Pelicula = ?";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPelicula);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("ClasificacionEdad"),
                        rs.getString("Estado"),
                        rs.getString("Imagen"),
                        rs.getString("Sinopsis")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener película por ID: " + e.getMessage());
        }
        return null;
    }
    
    public List<Pelicula> buscarPorTitulo(String termino) {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT ID_Pelicula, Genero, Titulo, ClasificacionEdad, Estado, Imagen, Sinopsis " +
                     "FROM Pelicula WHERE Titulo LIKE ? ORDER BY Titulo";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + termino + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Pelicula pelicula = new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("ClasificacionEdad"),
                        rs.getString("Estado"),
                        rs.getString("Imagen"),
                        rs.getString("Sinopsis")
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
        String sql = "UPDATE Pelicula SET Genero = ?, Titulo = ?, ClasificacionEdad = ?, " +
                     "Estado = ?, Imagen = ?, Sinopsis = ? WHERE ID_Pelicula = ?";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pelicula.getGenero());
            pstmt.setString(2, pelicula.getTitulo());
            pstmt.setString(3, pelicula.getClasificacionEdad());
            pstmt.setString(4, pelicula.getEstado());
            pstmt.setString(5, pelicula.getImagen());
            pstmt.setString(6, pelicula.getSinopsis());
            pstmt.setInt(7, pelicula.getIdPelicula());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar película: " + e.getMessage());
        }
        return false;
    }
    
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
    // MÉTODOS AUXILIARES
    // ==========================================
    
    public List<Pelicula> obtenerPeliculasConFiltro(String genero, String estado, int limit) {
        List<Pelicula> peliculas = new ArrayList<>();
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
                    Pelicula pelicula = new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("ClasificacionEdad"),
                        rs.getString("Estado"),
                        rs.getString("Imagen"),
                        rs.getString("Sinopsis")
                    );
                    peliculas.add(pelicula);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener películas con filtro: " + e.getMessage());
        }
        return peliculas;
    }

    public List<String> obtenerTitulosQueCoinciden(String termino, int limit) {
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

    public List<Pelicula> obtenerPeliculasMasTaquilleras(int limit) {
        List<Pelicula> peliculas = new ArrayList<>();
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
                    Pelicula pelicula = new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("ClasificacionEdad"),
                        rs.getString("Estado"),
                        rs.getString("Imagen"),
                        rs.getString("Sinopsis")
                    );
                    peliculas.add(pelicula);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener películas más taquilleras: " + e.getMessage());
        }
        return peliculas;
    }
    
    public Pelicula obtenerPeliculaAleatoriaEnCartelera() {
        String sql = "SELECT ID_Pelicula, Genero, Titulo, ClasificacionEdad, Estado, Imagen, Sinopsis " +
                     "FROM Pelicula WHERE Estado = 'En Cartelera' ORDER BY RAND() LIMIT 1";
        
        try (Connection conn = M2.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("ClasificacionEdad"),
                        rs.getString("Estado"),
                        rs.getString("Imagen"),
                        rs.getString("Sinopsis")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener película aleatoria: " + e.getMessage());
        }
        return null;
    }
    
    // ==========================================
    // NUEVOS MÉTODOS PARA FILTROS DINÁMICOS
    // ==========================================
    
    /**
     * Obtiene todos los géneros únicos de la BD
     */
    public List<String> obtenerGenerosUnicos() {
        List<String> generos = new ArrayList<>();
        String sql = "SELECT DISTINCT Genero FROM Pelicula WHERE Genero IS NOT NULL ORDER BY Genero";
        
        try (Connection conn = M2.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String genero = rs.getString("Genero");
                if (genero != null && !genero.trim().isEmpty()) {
                    generos.add(genero);
                }
            }
            System.out.println("✅ Géneros únicos obtenidos: " + generos.size());
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener géneros únicos: " + e.getMessage());
        }
        return generos;
    }
    
    /**
     * Obtiene todas las clasificaciones únicas de la BD
     */
    public List<String> obtenerClasificacionesUnicas() {
        List<String> clasificaciones = new ArrayList<>();
        String sql = "SELECT DISTINCT ClasificacionEdad FROM Pelicula WHERE ClasificacionEdad IS NOT NULL ORDER BY ClasificacionEdad";
        
        try (Connection conn = M2.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String clasif = rs.getString("ClasificacionEdad");
                if (clasif != null && !clasif.trim().isEmpty()) {
                    clasificaciones.add(clasif);
                }
            }
            System.out.println("✅ Clasificaciones únicas obtenidas: " + clasificaciones.size());
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener clasificaciones únicas: " + e.getMessage());
        }
        return clasificaciones;
    }
}
