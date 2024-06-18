import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//inicia a conta com saldo = 0
//usa fila salvar a ordem de operacoes conforme o usuario digite [first in first out]
//e hash pra associar um nome a uma só thread
public class Movimentacoes {
    public static void main(String[] args) {
        SistemaBancario conta = new SistemaBancario(0);
        BlockingQueue<Operacao> fila_operacoes = new LinkedBlockingQueue<>();
        Map<String, Pessoa> pessoa_familia = new HashMap<>();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Digite o nome, a operacao (depositar/sacar) e o valor desejado (Formato: 'nome operacao valor').\nDigite 'sair' caso deseje finalizar a operacao.\n");
            String entrada = scanner.nextLine();
            if (entrada.equalsIgnoreCase("sair")) {
                break;
            }
            try {
                String[] partes = entrada.split(" ");
                String nome = partes[0];
                String operacao = partes[1];
                double quantia = Double.parseDouble(partes[2]);
                //verifica se ja tem alguem com o mesmo nome antes de criar a thread
                //se tiver, apenas reutiliza a thread pré-criada, se não, cria uma nova
                if (operacao.equals("depositar") || operacao.equals("sacar")) {
                    Pessoa pessoa;
                    if (!pessoa_familia.containsKey(nome)) {
                        pessoa = new Pessoa(nome, fila_operacoes, conta);
                        pessoa_familia.put(nome, pessoa);
                        pessoa.start();
                    }
                    else {
                        pessoa = pessoa_familia.get(nome);
                    }
                    //coloca a operação e a quantidade na fila
                    fila_operacoes.put(new Operacao(operacao, quantia));
                }
                //operacao diferente das esperadas [depositar, sacar, sair]
                else {
                    System.out.println("Operacao invalida. Tente novamente.");
                }
            }
            //entrada num formato diferente do esperado
            catch (Exception e) {
                System.out.println("Entrada invalida. Tente novamente.");
            }
        }

        //espera todas as threads acabarem
        for (Pessoa pessoa : pessoa_familia.values()) {
            pessoa.interrupt();
            try {
                pessoa.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        //printa o saldo final em conta
        System.out.printf("Saldo final: %.2f%n", conta.getSaldo());
        scanner.close();
    }
}
