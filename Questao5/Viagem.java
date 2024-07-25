//nao ta verificando se a pessoa chega durante o embarque
//arrumar um jeito do onibus so sair se tiver 50 pessoas sentadas ou se so ficar na parada quem chegou durante o embarque

import java.util.concurrent.*;
import java.util.Random;

public class Viagem {
    //passageiros cadeiras e passageiros na espera
    private static final int CADEIRAS = 50;
    private static final int NUM_PASSAGEIROS = 100;
    private static volatile int esperando = 0;
    //private static volatile int sentados = 0;

    private static final Random random = new Random();
    //semaforo do onibus e booleano pra ajudar na sincronizacao
    private static final Semaphore sema_onibus = new Semaphore(CADEIRAS);
    private static volatile boolean chegou = false;
    //private static volatile boolean cheio = false;


    public static void main(String[] args) {
        Onibus onibus = new Onibus();
        onibus.start();

        //cria e inicializa os passageiros 
        Passageiro[] passageiros = new Passageiro[NUM_PASSAGEIROS];
        for (int i = 0; i < NUM_PASSAGEIROS; i++) {
            passageiros[i] = new Passageiro("Passageiro " + (i+1));
            passageiros[i].start();
            esperando++;
            try {
                //eles chegam em intervalos aleatorios de 0 a 500
                TimeUnit.MILLISECONDS.sleep(random.nextInt(500));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Onibus extends Thread {
        @Override
        public void run() {
            while (true) {

                try {
                    //calcula quando o poximo onibus vai chegar
                    TimeUnit.SECONDS.sleep(1 + random.nextInt(3));
                    if ( esperando == 0) {
                        System.out.println("Não há mais passageiros esperando.");
                        break;
                    }

                    //quando um onibus chega tem uma mensagem
                    chegou = true;
                    System.out.println("O ônibus chegou.");
                    

                    //embarcando mesmo os que chegaram na hora do embarque
                    //while (sema_onibus.availablePermits() > 0 && chegou ){
                    //    TimeUnit.MILLISECONDS.sleep(100);
                    //}

                    //while (!cheio){
                    //    sentados++;
                    //    if (sentados == 50){
                    //        cheio = true;
                    //        sentados = 0;
                    //    }
                    //}

                    //tempo de embarque
                    TimeUnit.SECONDS.sleep(3);

                    //reseta o onibus depois que ele vai embra
                    chegou = false;
                    System.out.println("O ônibus partiu.");
                    sema_onibus.release(CADEIRAS);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static class Passageiro extends Thread {
        private String nome;
        private Passageiro(String nome) {
            this.nome = nome;
        }

        @Override
        public void run() {
            try {
                //espera
                while (!chegou) {
                    //printa isso a cada 0,1s ate que o passageiro entre 
                    System.out.println(nome + " está esperando o ônibus.");
                    Thread.sleep(100);
                }
                
                //quando o onibus chega tenta entrar
                if (sema_onibus.tryAcquire()) {
                    System.out.println(nome + " embarcou.");
                    esperando--;
                }
                //se nao consegue imprime essa mensagem
                else {
                    System.out.println(nome + " não conseguiu embarcar e está esperando o próximo.");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
