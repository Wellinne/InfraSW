import java.util.Scanner;
import java.util.concurrent.Semaphore;

class Carro extends Thread{
    private String direcao;
    private Boolean sincronia;
    private String numero;
    private static final int TEMPO_TRAVESSIA = 1000;
    private static final Semaphore semaphore = new Semaphore(1);

    public Carro(String direcao, Boolean sincronia, String numero) {
        this.direcao = direcao;
        this.sincronia = sincronia;
        this.numero = numero;
    }

    @Override
    public void run() {
        try {
            if(sincronia) {
                semaphore.acquire();
                System.out.println("Carro " + numero + " vindo da " + direcao + " está entrando na ponte.");
                Thread.sleep(TEMPO_TRAVESSIA);
                System.out.println("Carro "  + numero + " vindo da " + direcao + " atravessou a ponte.");
                semaphore.release();
            } else {
                System.out.println("Carro " + numero + " vindo da " + direcao + " está entrando na ponte.");
                Thread.sleep(TEMPO_TRAVESSIA);
                System.out.println("Carro "  + numero + " vindo da " + direcao + " atravessou a ponte.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Questao2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o número 1 para carro sincronizado ou o número 2 para carro sem sincronia: ");
        String entrada = scanner.nextLine();
        scanner.close();

        if(entrada.equals("1")){
            Carro carro1 = new Carro("Direita", true, "1");
            Carro carro2 = new Carro("Direita", true, "2");
            Carro carro3 = new Carro("Esquerda", true, "3");
            Carro carro4 = new Carro("Direita", true, "4");

            carro1.start();
            carro2.start();
            carro3.start();
            carro4.start();

            try {
                carro1.join();
                carro2.join();
                carro3.join();
                carro4.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (entrada.equals("2")){
            Carro carro1 = new Carro("Direita", false, "1");
            Carro carro2 = new Carro("Direita", false, "2");
            Carro carro3 = new Carro("Esquerda", false, "3");
            Carro carro4 = new Carro("Direita", false, "4");

            carro1.start();
            carro2.start();
            carro3.start();
            carro4.start();

            try {
                carro1.join();
                carro2.join();
                carro3.join();
                carro4.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}