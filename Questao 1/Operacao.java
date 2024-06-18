//retorna a operacao e o valor a ser sacado/depositado
class Operacao {
    private final String tipo;
    private final double quantia;

    public Operacao(String tipo, double quantia) {
        this.tipo = tipo;
        this.quantia = quantia;
    }

    public String getTipo() {
        return tipo;
    }

    public double getQuantia() {
        return quantia;
    }
}