import java.util.Random;
import java.util.concurrent.Semaphore;

class Banheiro {
    private int pessoasNoBanheiro = 0;
    private String generoAtual = "";
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore capacidade = new Semaphore(3);

    public void entrarNoBanheiro(String genero, int id) throws InterruptedException {
        boolean entrou = false;

        while (!entrou) {
            mutex.acquire();

            if (pessoasNoBanheiro == 0 || generoAtual.equals(genero)) {
                generoAtual = genero;
                System.out.println("GENERO ATUAL NO BANHEIRO: " + generoAtual);
                capacidade.acquire();
                pessoasNoBanheiro++;
                System.out.println(genero + " " + id + " entrou no banheiro. Pessoas no banheiro: " + pessoasNoBanheiro);
                entrou = true;
            }
            mutex.release();
            if (!entrou) {
                System.out.println(genero + " " + id + " esperando. Pessoas no banheiro: " + pessoasNoBanheiro);
                Thread.sleep(50);
            }
        }
    }

    public void sairDoBanheiro(String genero, int id) throws InterruptedException {
        mutex.acquire();
        pessoasNoBanheiro--;
        System.out.println(genero + " " + id + " saiu do banheiro. Pessoas no banheiro: " + pessoasNoBanheiro);
        capacidade.release();
        if (pessoasNoBanheiro == 0) {
            generoAtual = "";
        }
        mutex.release();
    }
}

class Pessoa extends Thread {
    private final Banheiro banheiro;
    private final String genero;
    private final int id;

    public Pessoa(Banheiro banheiro, String genero, int id) {
        this.banheiro = banheiro;
        this.genero = genero;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            System.out.println(genero + " " + this.id + " chegou no banheiro.");
            banheiro.entrarNoBanheiro(genero, this.id);
            Thread.sleep(new Random().nextInt(1000));
            banheiro.sairDoBanheiro(genero, this.id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Questao6 {
    public static void main(String[] args) {
        Banheiro banheiro = new Banheiro();
        Random random = new Random();
        int NUM_REPETICAO = 100;
        Thread[] pessoas = new Thread[NUM_REPETICAO];

        for (int i = 0; i < NUM_REPETICAO; i++) {
            String genero = random.nextBoolean() ? "Mulher": "Homem";
            pessoas[i] = new Pessoa(banheiro, genero, i);
            pessoas[i].start();
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < NUM_REPETICAO; i++) {
            try {
                pessoas[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
