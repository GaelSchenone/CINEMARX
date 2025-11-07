package CINEMARX.M1;

public class RolVIP {
    private String nivel;
    private String beneficios;
    private int puntosAcumulados;
    private String fechaExpiracion;

    public RolVIP(String nivel, String beneficios, int puntosAcumulados, String fechaExpiracion) {
        this.nivel = nivel;
        this.beneficios = beneficios;
        this.puntosAcumulados = puntosAcumulados;
        this.fechaExpiracion = fechaExpiracion;
    }

    @Override
    public String toString() {
        return "Nivel: " + nivel + "\nBeneficios: " + beneficios +
               "\nPuntos: " + puntosAcumulados + "\nExpira: " + fechaExpiracion;
    }
}


