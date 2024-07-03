import java.util.concurrent.Semaphore;

class Restaurante extends Thread {
    private String cliente;
    private static final int TEMPO_JANTA = 1000;
    private static final Semaphore semaphore = new Semaphore(5, true);

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

public class Questao4 {
    public static void main(String[] args) {
        int NUM_REPETICAO = 100;
        Restaurante[] clientes = new Restaurante[NUM_REPETICAO];

        for (int i = 0; i < NUM_REPETICAO; i++) {
            clientes[i] = new Restaurante("Cliente " + (i + 1));
            clientes[i].start();
        }

        for (int i = 0; i < NUM_REPETICAO; i++) {
            try {
                clientes[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
