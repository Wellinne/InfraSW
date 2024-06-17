import java.util.Scanner;
import java.util.concurrent.Semaphore;

class CarroSincronizado extends Thread{
    private String direcao;
    private static final int TEMPO_TRAVESSIA = 1000;
    private static final Semaphore semaphore = new Semaphore(1);

    public CarroSincronizado(String direcao) {
        this.direcao = direcao;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            System.out.println("Carro vindo da " + direcao + " está entrando na ponte.");
            Thread.sleep(TEMPO_TRAVESSIA);
            System.out.println("Carro vindo da " + direcao + " atravessou a ponte.");
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class CarroSemSicronia extends Thread {
    private String direcao;
    private static final int TEMPO_TRAVESSIA = 1000;

    public CarroSemSicronia(String direcao) {
        this.direcao = direcao;
    }

    @Override
    public void run() {
        try {
            System.out.println("Carro sem sicronia vindo da " + direcao + " está entrando na ponte.");
            Thread.sleep(TEMPO_TRAVESSIA);
            System.out.println("Carro sem sicronia vindo da " + direcao + " atravessou a ponte.");
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
            CarroSincronizado carro1 = new CarroSincronizado("Direita");
            CarroSincronizado carro2 = new CarroSincronizado("Direita");
            CarroSincronizado carro3 = new CarroSincronizado("Esquerda");
            CarroSincronizado carro4 = new CarroSincronizado("Direita");

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
            CarroSemSicronia carro1 = new CarroSemSicronia("Direita");
            CarroSemSicronia carro2 = new CarroSemSicronia("Direita");
            CarroSemSicronia carro3 = new CarroSemSicronia("Esquerda");
            CarroSemSicronia carro4 = new CarroSemSicronia("Direita");

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

