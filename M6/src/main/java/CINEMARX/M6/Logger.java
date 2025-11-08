package CINEMARX.M6;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Logger {

    public static void log(Connection connection, String description) {
        String query = "INSERT INTO Logs (Descripcion) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // In a real application, you might want to handle this error more gracefully
        }
    }
}
