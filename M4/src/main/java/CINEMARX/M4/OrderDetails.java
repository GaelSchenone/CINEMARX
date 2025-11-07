package CINEMARX.M4;

import java.util.Set;
import java.util.HashSet;

public class OrderDetails {
    private int idFuncion;
    private Set<Boleto> boletos;

    public OrderDetails(int idFuncion) {
        this.idFuncion = idFuncion;
        this.boletos = new HashSet<>();
    }

    public void addBoleto(Boleto boleto) {
        this.boletos.add(boleto);
    }

    public Set<Boleto> getBoletos() {
        return boletos;
    }

    public int getIdFuncion() {
        return idFuncion;
    }
}