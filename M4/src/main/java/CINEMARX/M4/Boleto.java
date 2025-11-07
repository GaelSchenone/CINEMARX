package CINEMARX.M4;

public class Boleto {
    private String numeroButaca;
    private int idFuncion;
    private Integer idCliente;

    public Boleto(String numeroButaca, int idFuncion, Integer idCliente) {
        this.numeroButaca = numeroButaca;
        this.idFuncion = idFuncion;
        this.idCliente = idCliente;
    }

    public String getNumeroButaca() {
        return numeroButaca;
    }

    public int getIdFuncion() {
        return idFuncion;
    }

    public Integer getIdCliente() {
        return idCliente;
    }
}
