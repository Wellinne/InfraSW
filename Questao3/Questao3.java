import java.util.concurrent.Semaphore;

class Restaurante extends Thread {
    private String cliente;
    private static final int TEMPO_JANTA = 1000;
    private static final Semaphore semaphore = new Semaphore(5);

    public Restaurante(String cliente) {
        this.cliente = cliente;
    }

    public void clienteChegou() {
        System.out.println(cliente + " chegou e está em espera.");
    }

    public void clienteJantandoSaiu() {
        try {
            semaphore.acquire();
            System.out.println(cliente + " está jantando.");
            Thread.sleep(TEMPO_JANTA);
            System.out.println(cliente + " saiu.");
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        clienteChegou();
        clienteJantandoSaiu();
    }
}

public class Questao3 {
    public static void main(String[] args) {
        Restaurante cliente1 = new Restaurante("Cliente 1");
        Restaurante cliente2 = new Restaurante("Cliente 2");
        Restaurante cliente3 = new Restaurante("Cliente 3");
        Restaurante cliente4 = new Restaurante("Cliente 4");
        Restaurante cliente5 = new Restaurante("Cliente 5");
        Restaurante cliente6 = new Restaurante("Cliente 6");
        Restaurante cliente7 = new Restaurante("Cliente 7");
        Restaurante cliente8 = new Restaurante("Cliente 8");
        Restaurante cliente9 = new Restaurante("Cliente 9");
        Restaurante cliente10 = new Restaurante("Cliente 10");
        Restaurante cliente11 = new Restaurante("Cliente 11");
        Restaurante cliente12 = new Restaurante("Cliente 12");

        cliente1.start();
        cliente2.start();
        cliente3.start();
        cliente4.start();
        cliente5.start();
        cliente6.start();
        cliente7.start();
        cliente8.start();
        cliente9.start();
        cliente10.start();
        cliente11.start();
        cliente12.start();

        try {
            cliente1.join();
            cliente2.join();
            cliente3.join();
            cliente4.join();
            cliente5.join();
            cliente6.join();
            cliente7.join();
            cliente8.join();
            cliente9.join();
            cliente10.join();
            cliente11.join();
            cliente12.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
