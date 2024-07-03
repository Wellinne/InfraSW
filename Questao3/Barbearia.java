import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//sleeps com numeros aleatorios pq se nao ficava muito uniforme

//clientes organizados em fila por ordem de chegada
//5 cadeiras na espera e 100 clientes
public class Barbearia {
    private static final Lock lock = new ReentrantLock();

    //semaforos pra sinalizar as cadeiras ocupadas/vazias da sala de espera e do barbeiro
    //barbeiro começa "dormindo"
    public static Semaphore sema_cad = new Semaphore(5);
    public static Semaphore sema_barb = new Semaphore(0);

    //fila dos clientes por ordem de chegada
    private  static final List<String> fila = new ArrayList<>();

    public static void main(String[] args) {
        //cria e inicia o barbeiro
        //como daemon pq ele continuava executando mesmo quando nao havia mais cientes
        Barbeiro barbeiro = new Barbeiro();
        barbeiro.setDaemon(true);
        barbeiro.start();

        //cria e inicia os clientes em intervalos aleatorios
        Cliente[] thread_clientes = new Cliente[100];
        for (int i = 0; i < 100; i++) {
            
            thread_clientes[i] = new Cliente("Cliente " + (i));
            thread_clientes[i].start();

            try {
                Thread.sleep(new Random().nextInt(300) + 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //termina os clientes
        for (Thread thread_Clientes : thread_clientes) {
            try {
                thread_Clientes.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("A barbearia fechou.");
    }

    static class Barbeiro extends Thread {
        @Override
        public void run() {
            while (true) { 
                try {
                    sema_barb.acquire();
                    String nome_cliente;
                    lock.lock();
                    try {
                        //barbeiro pega o primeiro da fila de espera
                        nome_cliente = fila.remove(0);
                        System.out.println("O barbeiro está cortando o cabelo do " + nome_cliente + ". Clientes esperando: " + fila.size());
                        sema_cad.release(); 
                    } finally {
                        lock.unlock();
                    }

                    //duracao do corte varia de cliente pra cliente
                    Thread.sleep(new Random().nextInt(600) + 100);
                    System.out.println("O barbeiro terminou de cortar o cabelo do " + nome_cliente);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Cliente extends Thread{
        private final String nome;

        public Cliente(String nome){
            this.nome = nome;
        }

        @Override
        public void run() {
            try {
                //tempo de chegada do cliente aleatorio
                Thread.sleep(new Random().nextInt(400) + 100);
                lock.lock();

                try {
                    //verifica se todas as cadeiras estao ocupadas quando um cliente novo chega
                    //se tiver alguma cadeira vazia, o cliente senta e espera
                    if (fila.size() < 5) {
                        fila.add(nome);
                        System.out.println(nome + " entrou. Clientes esperando: " + fila.size());

                        sema_cad.acquire();
                        sema_barb.release();                        
                    }
                    //se nao, ele so da meia volta e vai embora
                    else {
                        System.out.println(nome + " não pôde entrar porque não há cadeiras vazias na sala de espera.");
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}