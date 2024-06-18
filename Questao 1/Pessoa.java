import java.util.concurrent.BlockingQueue;
//cria a thread da pessoa
//pessoas com nomes iguais sao consideradas as mesmas pessoas
class Pessoa extends Thread {
    private final String nome;
    private final BlockingQueue<Operacao> operacoes;
    private final SistemaBancario conta_bancaria;

    public Pessoa(String nome, BlockingQueue<Operacao> operacoes, SistemaBancario conta_bancaria) {
        this.nome = nome;
        this.operacoes = operacoes;
        this.conta_bancaria = conta_bancaria;
    }
    //chama a operacao que vai ser feita na classe SistemaBancario
    @Override
    public void run() {
        try {
            while (true) {
                Operacao operacao = operacoes.take();
                if (operacao.getTipo().equals("depositar")) {
                    conta_bancaria.depositar(nome, operacao.getQuantia());
                }
                else if (operacao.getTipo().equals("sacar")) {
                    conta_bancaria.sacar(nome, operacao.getQuantia());
                }
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}