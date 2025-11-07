package CINEMARX.M3;

import CINEMARX.M3.BuffetFrame;
import CINEMARX.M3.ConexionBD;
import javax.swing.*;
import java.sql.Connection;

public class M3 {
    
    private void abrirModuloBuffet() {
        BuffetFrame buffetFrame = new BuffetFrame();
        buffetFrame.setVisible(true);
    }
    
    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }
        
        // Probar conexión al iniciar
        Connection testCon = ConexionBD.getConexion();
        if (testCon != null) {
            System.out.println("✅ Aplicación iniciada correctamente");
        } else {
            System.err.println("❌ No se pudo conectar a la base de datos");
            JOptionPane.showMessageDialog(null,
                "No se pudo conectar a la base de datos.\nVerifique la configuración de conexión.",
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE);
        }
        
        // Iniciar aplicación del buffet
        java.awt.EventQueue.invokeLater(() -> {
            BuffetFrame buffet = new BuffetFrame();
            buffet.setVisible(true);
        });
    }
}