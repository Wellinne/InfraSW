import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//saldo comeca em 50
public class Movimentacoes {
    public static void main(String[] args) {
        Banco conta = new Banco(50);
        List<Pessoa> pessoas_familia = new ArrayList<>();

        final int NUM_PESSOAS = 5;
        Random random = new Random();

        //define o nome das pessoas e adiciona na lista
        for (int i = 0; i < NUM_PESSOAS; i++) {
            String nome = "Pessoa " + i;
            Pessoa pessoa = new Pessoa(nome, conta);
            pessoas_familia.add(pessoa);
            pessoa.start();
        }

        //roda no maximo por 10 segundos
        long startTime = System.currentTimeMillis();
        long runTime = 10000;

        //valores aleatorios de 1 a 100
        //operacoes aleatorias sao adicionadas a fila de operacoes de cada pessoa
        while (System.currentTimeMillis() - startTime < runTime) {
            String[] fila_operacoes = {"depositar", "sacar"};
            String operacao = fila_operacoes[random.nextInt(fila_operacoes.length)];
            double valor = 1 + (random.nextDouble() * 99);

            //seleciona aleatoriamente as pessoas na lista e adiciona uma operacao na lista dela
            Pessoa pessoa = pessoas_familia.get(random.nextInt(pessoas_familia.size()));
            pessoa.addOperacao(new Operacao(operacao, valor));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        //sinaliza quando for pra elas pararem
        for (Pessoa pessoa : pessoas_familia) {
            pessoa.parar();
        }

        //espera as threads terminarem
        for (Pessoa pessoa : pessoas_familia) {
            try {
                pessoa.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        //printa o saldo final depois que todas as operacoes sao finalizadas
        System.out.printf("O saldo final foi de %.2f%n", conta.getSaldo());
    }
    //as operacoes sao feitas aqui
    static class Banco {
        private double saldo;
        private final Lock lock = new ReentrantLock();

        public Banco(double saldoInicial) {
            this.saldo = saldoInicial;
        }
        //deposita o valor 
        public void depositar(String nome, double valor) {
            lock.lock();
            try {
                saldo += valor;
                System.out.printf("%s depositou %.2f com sucesso. Saldo atual: %.2f%n", nome, valor, saldo);
            }
            finally {
                lock.unlock();
            }
        }
        //verifica se o saldo eh suficiente ou nao antes de fazer a operacao
        public void sacar(String nome, double valor) {
            lock.lock();
            try {
                if (saldo >= valor) {
                    saldo -= valor;
                    System.out.printf("%s sacou %.2f com sucesso. Saldo atual: %.2f%n", nome, valor, saldo);
                }
                else {
                    System.out.printf("Saque de %.2f nao foi possivel de ser realizado, %s. Motivo: saldo insuficiente. Saldo atual: %.2f%n", valor, nome, saldo);
                }
            }
            finally {
                lock.unlock();
            }
        }
        //retorna o saldo
        public double getSaldo() {
            return saldo;
        }
    }
    //auxilia na hora de pegar o tipo ou o valor das operacoes
    static class Operacao {
        private final String tipo;
        private final double valor;

        public Operacao(String tipo, double valor) {
            this.tipo = tipo;
            this.valor = valor;
        }

        public String getTipo() {
            return tipo;
        }

        public double getValor() {
            return valor;
        }
    }
    //cria threads pra cada pessoa
    //bool ativa ajuda a saber quais threads ainda estao rodando e
    //quais ja pararam quando ela for falsa o metodo parar eh ativado
    //vai ser verdadeira ate que a lista de operacoes daquela pessoa esteja vazia
    static class Pessoa extends Thread {
        private final String nome;
        private final Banco conta_bancaria;
        private final List<Operacao> fila_operacoes;
        private boolean ativa;
        private final Lock lock2 = new ReentrantLock();

        public Pessoa(String nome, Banco conta_bancaria) {
            this.nome = nome;
            this.conta_bancaria = conta_bancaria;
            this.fila_operacoes = new ArrayList<>();
            this.ativa = true;
        }
        //adiciona operacoes na fila de operacoes
        public void addOperacao(Operacao operacao) {
            lock2.lock();
            try {
                fila_operacoes.add(operacao);
            } finally {
                lock2.unlock();
            }
        }

        public void parar() {
            ativa = false;
        }

        //roda na hora que a thread eh criada
        @Override
        public void run() {
            while (ativa || !fila_operacoes.isEmpty()) {
                Operacao operacao = null;
                lock2.lock();
                try {
                    if (!fila_operacoes.isEmpty()) {
                        operacao = fila_operacoes.remove(0);
                    }
                } finally {
                    lock2.unlock();
                }

                if (operacao != null) {
                    if (operacao.getTipo().equals("depositar")) {
                        conta_bancaria.depositar(nome, operacao.getValor());
                    } else if (operacao.getTipo().equals("sacar")) {
                        conta_bancaria.sacar(nome, operacao.getValor());
                    }
                }
                else {
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            //processa todas as operacoes antes de parar
            while (!fila_operacoes.isEmpty()) {
                Operacao operacao = null;
                lock2.lock();
                try {
                    if (!fila_operacoes.isEmpty()) {
                        operacao = fila_operacoes.remove(0);
                    }
                }
                finally {
                    lock2.unlock();
                }

                if (operacao != null) {
                    if (operacao.getTipo().equals("depositar")) {
                        conta_bancaria.depositar(nome, operacao.getValor());
                    }
                    else if (operacao.getTipo().equals("sacar")) {
                        conta_bancaria.sacar(nome, operacao.getValor());
                    }
                }
            }
        }
    }
}
