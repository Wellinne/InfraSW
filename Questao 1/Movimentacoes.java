//mudei as classes pra static assim tudo roda num so arquivo
//mudei os metodos que antes eu tava usando pq vc que tavam na lista de não permitidos (blockingqueue e linkedblockingqueue)
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Movimentacoes {
    public static void main(String[] args) {
        //cria a conta com saldo inicial de 0 e o hash vazio que vai armazenar os nomes dos membros da familia 
        Banco conta = new Banco(0);
        Map<String, Pessoa> pessoas_familia = new HashMap<>();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            //entrada do usuario vai consistir no nome, operacao que ele quer fazer e a quantidade, ou so o comando sair que para o programa e imprime o saldo final
            System.out.print("Digite o nome, a operacao (depositar/sacar) e o valor desejado (Formato: 'nome operacao valor').\nDigite 'sair' caso deseje finalizar a operacao.\n");
            String entrada = scanner.nextLine();
            if (entrada.equalsIgnoreCase("sair")) {
                break;
            }
            try {
                String[] entradaa = entrada.split(" ");
                String nome = entradaa[0];
                String operacao = entradaa[1];
                double valor = Double.parseDouble(entradaa[2]);
                //como é uma família consideramos que pessoas com o mesmo
                //nome sao as mesmas pessoas, entao ele verifica se o nome ja ta
                //no hash antes de criar uma pessoa (thread) nova
                if (operacao.equals("depositar") || operacao.equals("sacar")) {
                    Pessoa pessoa;
                    if (!pessoas_familia.containsKey(nome)) {
                        pessoa = new Pessoa(nome, conta);
                        pessoas_familia.put(nome, pessoa);
                        pessoa.start();
                    }
                    else {
                        pessoa = pessoas_familia.get(nome);
                    }
                    pessoa.addOperacao(new Operacao(operacao, valor));
                }
                //se a entrada for diferente do esperado (ex: erro de digitacao na hora de colocar a operacao, etc) aparece uma mensagem de erro na tela
                else {
                    System.out.println("Operação inválida. Tente novamente.\n");
                }
            }
            catch (Exception e) {
                System.out.println("Entrada inválida. Tente novamente.\n");
            }
        }

        //interrompe as threads quando o programa para de ser executado
        for (Pessoa pessoa : pessoas_familia.values()) {
            pessoa.interrupt();
            try {
                pessoa.join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        //printa o saldo final depois que o usuario diigta sair
        System.out.printf("Saldo final: %.2f%n", conta.getSaldo());
        //System.out.println(Pessoa.getContador()); contar quantas threads tavam sendo criadas
        scanner.close();
    }
    //mudei o nome da classe de sistemaBancario pra banco, mas continua tudo igual
    //adicionando uma condicao que da um sinal quando o saldo muda pra ajudar na sincronizacao
    static class Banco {
        private double saldo;
        private final Lock lock = new ReentrantLock();
        private final Condition mudou = lock.newCondition();

        public Banco(double saldoInicial) {
            this.saldo = saldoInicial;
        }
        //deposita o valor dado pelo usuario
        public void depositar(String nome, double valor) {
            lock.lock();
            try {
                saldo += valor;
                System.out.printf("Deposito realizado com sucesso!.\nSaldo atual: %.2f%n\n", saldo);
                mudou.signalAll();
            }
            finally {
                lock.unlock();
            }
        }
        //se o saldo for insuficiente o saque nao acontece e uma mensagem aparece na tela
        public void sacar(String nome, double valor) {
            lock.lock();
            try {
                if (saldo >= valor) {
                    saldo -= valor;
                    System.out.printf("Saque realizado com sucesso!\nSaldo atual: %.2f%n\n", saldo);
                }
                else {
                    System.out.printf("Saldo insuficiente.");
                }
            }
            finally {
                lock.unlock();
            }
        }
        //retorna o valor do saldo
        public double getSaldo() {
            return saldo;
        }
    }
    //retorna qual o tipo de movimentacao que vai ser feita (saque/deposito) e o valor
    //vai ser usado na classe pessoa pra ajudar na criacao da fila de operacoes em ordem e das threads
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

        public double getvalor() {
            return valor;
        }
    }
    //cria uma thread pra cada pessoa (contanto que os nomes sejam diferentes)
    //mais uma vez esse contador só foi usado pra ter certeza da quantidade de threads que estaavm sendo criadas
    //a ordem das operacoes é do tipo FIFO
    //condicao quando uma nova operacao eh adicionada na fila pra ajudar na sincronia
    static class Pessoa extends Thread {
        private static int contador = 0;
        private final String nome;
        private final Queue<Operacao> fila_operacoes;
        private final Banco conta_bancaria;
        private final Lock lock = new ReentrantLock();
        private final Condition novaOp = lock.newCondition();

        public Pessoa(String nome, Banco conta_bancaria) {
            this.nome = nome;
            this.conta_bancaria = conta_bancaria;
            this.fila_operacoes = new LinkedList<>();
            contador++;
        }
        //adiciona a operacao dessa pessoa na fila
        public void addOperacao(Operacao operacao) {
            lock.lock();
            try {
                fila_operacoes.add(operacao);
                novaOp.signal();
            }
            finally {
                lock.unlock();
            }
        }
        //contador de threads
        public static int getContador() {
            return contador;
        }

        @Override
        public void run() {
            while (true) {
                Operacao operacao = null;
                lock.lock();
                try {
                    while (fila_operacoes.isEmpty()) {
                        try {
                            novaOp.await();
                        }
                        catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    operacao = fila_operacoes.poll();
                } finally {
                    lock.unlock();
                }
                //pega o tipo de operacao que vai ser feita e depois chama uma funcao que vai pegar o valor isso tudo passando pra funcao que vai fazer a movimentacao
                if (operacao.getTipo().equals("depositar")) {
                    conta_bancaria.depositar(nome, operacao.getvalor());
                }
                else if (operacao.getTipo().equals("sacar")) {
                    conta_bancaria.sacar(nome, operacao.getvalor());
                }
            }
        }
    }
}
